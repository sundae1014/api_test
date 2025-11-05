package com.bnk.fund_api_project.component;

import com.bnk.fund_api_project.dto.KofiaApiResponse;
import com.bnk.fund_api_project.dto.KofiaFundStatsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode; // 추가
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component
public class KofiaApiClient {

    private final WebClient webClient;
    private final String encodedApiKey;
    private final String baseUrl; // 🚨 Base URL을 필드에 저장

    public KofiaApiClient(WebClient.Builder webClientBuilder,
                          @Value("${api.url.kofia}") String baseUrl,
                          @Value("${api.key.kofia}") String kofiaApiKey) {

        // 🚨 WebClient에는 기본 URL을 설정하지 않고, 필드에 문자열로 저장합니다.
        this.webClient = webClientBuilder.build();
        this.baseUrl = baseUrl;

        this.encodedApiKey = URLEncoder.encode(kofiaApiKey, StandardCharsets.UTF_8);
    }

    public Mono<List<KofiaFundStatsDto>> fetchFundStatistics(String targetDate) {

        final String SERVICE_ID = "GetKofiaStatisticsInfoService";

        // 🚨 수정: UriComponentsBuilder를 사용하여 완전한 URL 문자열을 직접 구성합니다.
        String fullUrl = UriComponentsBuilder.fromUriString(this.baseUrl)
                // Base URL (GetKofiaStatisticsInfoService) 뒤에 오퍼레이션 이름을 붙입니다.
                .path("/service/" + SERVICE_ID + "/getFundNetAssetAmount")
                .queryParam("serviceKey", encodedApiKey) // 이미 인코딩된 키 사용
                .queryParam("_type", "json")
                .queryParam("schDate", targetDate)
                .queryParam("numOfRows", 100)
                .toUriString();

        System.out.println("Final Request URL: " + fullUrl); // 🚨 최종 URL 확인 로그

        return webClient.get()
                // 🚨 완성된 전체 URL 문자열을 WebClient에 직접 전달합니다.
                .uri(fullUrl)
                .retrieve()

                // HTTP 상태 코드 오류 발생 시 로그 출력 및 예외 처리
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    System.err.println("🚨 KOFIA API HTTP Error: Status Code " + clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(body -> {
                                System.err.println("Error Response Body: " + body);
                                return Mono.error(new RuntimeException("API HTTP Error: " + clientResponse.statusCode()));
                            });
                })

                // JSON 문자열을 DTO로 자동 파싱
                .bodyToMono(KofiaApiResponse.class)
                .map(response -> {
                    if (response != null &&
                            "00".equals(response.getResponse().getHeader().getResultCode()) &&
                            response.getResponse().getBody().getItems().getItem() != null) {

                        return response.getResponse().getBody().getItems().getItem();
                    }

                    // ResultCode가 00이 아닌 경우 로그 출력
                    String resultCode = response != null && response.getResponse() != null
                            && response.getResponse().getHeader() != null
                            ? response.getResponse().getHeader().getResultCode() : "NULL";

                    System.err.println("🚫 KOFIA API 내부 오류 코드 확인: ResultCode = " + resultCode);
                    System.err.println("KOFIA API에서 통계 데이터를 가져오지 못했습니다.");
                    return Collections.<KofiaFundStatsDto>emptyList();
                })
                .onErrorReturn(Collections.emptyList());
    }
}