package com.bnk.fund_api_project.service;

import com.bnk.fund_api_project.component.DartApiClient;
import com.bnk.fund_api_project.component.KofiaApiClient;
import com.bnk.fund_api_project.entity.FundCode;
import com.bnk.fund_api_project.entity.KofiaStatistic;
import com.bnk.fund_api_project.repository.FundCodeRepository;
import com.bnk.fund_api_project.repository.KofiaStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.scheduler.Schedulers;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성 (의존성 주입)
public class FundDataService {

    private final DartApiClient dartApiClient;
    private final FundCodeRepository fundCodeRepository;
    private final KofiaApiClient kofiaApiClient;
    private final KofiaStatisticRepository kofiaStatisticRepository;

    /**
     * DART API에서 고유번호를 가져와 DB에 저장하는 메서드
     */
    public void saveAllCorpCodes() {
        System.out.println("---- DART 고유번호 수집 시작 ----");

        // 1. DART API 호출하여 고유번호 리스트를 가져옴
        List<FundCode> fundCodes = dartApiClient.fetchCorpCodes();

        if (fundCodes.isEmpty()) {
            System.out.println("API 호출 실패 또는 파싱된 데이터가 없습니다.");
            return;
        }

        // 2. 펀드 (집합투자기구) 데이터만 필터링 (필요 시)
        // DART 고유번호 리스트에는 일반 기업 정보가 대부분이므로,
        // 펀드에 해당하는 항목만 필터링하는 로직이 필요합니다.

        // 3. DB에 일괄 저장
        fundCodeRepository.saveAll(fundCodes);

        System.out.println("---- 총 " + fundCodes.size() + "개의 고유번호를 DB에 저장 완료 ----");
    }

    // ⚠️ 펀드 순자산, 수익률 등 다른 데이터 수집 로직은 여기에 추가됩니다.
    @Transactional
    public Mono<List<KofiaStatistic>> saveKofiaFundStatistics(LocalDate date) {
        String targetDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        System.out.println("---- KOFIA 펀드 통계 수집 시작: " + targetDate + " ----");

        // 1. KOFIA API 호출 (Mono<List<DTO>> 반환)
        return kofiaApiClient.fetchFundStatistics(targetDate)
                // 2. 데이터가 도착하면 (flatMap), DB 저장 로직 실행
                .flatMap(dtoList -> {
                    if (dtoList.isEmpty()) {
                        System.out.println("KOFIA API에서 통계 데이터를 가져오지 못했습니다.");
                        return Mono.just(Collections.emptyList());
                    }

                    // DTO를 Entity로 변환
                    List<KofiaStatistic> entities = dtoList.stream()
                            .map(dto -> new KofiaStatistic(
                                    dto.getFundType(),
                                    dto.getNetAssetAmount(),
                                    date))
                            .collect(Collectors.toList());

                    // 3. DB에 저장 (블로킹 작업이므로 Mono.fromCallable 사용)
                    return Mono.fromCallable(() -> kofiaStatisticRepository.saveAll(entities))
                            // 저장 완료 시 로그 출력
                            .doOnSuccess(savedEntities ->
                                    System.out.println("---- 총 " + savedEntities.size() + "개의 KOFIA 통계 데이터를 DB에 저장 완료 ----")
                            )
                            // DB 저장은 I/O 작업이므로, 별도의 스레드 풀에서 실행되도록 지정
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }
}