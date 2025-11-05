package com.bnk.fund_api_project.controller;

import com.bnk.fund_api_project.service.FundDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono; // 추가된 import

import java.time.LocalDate;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataApiController {

    private final FundDataService fundDataService;

    @GetMapping("/collect/kofia")
    public Mono<String> collectKofiaData() { // 반환 타입을 Mono<String>으로 변경
        LocalDate today = LocalDate.now().minusDays(7);

        // 서비스의 Mono를 반환하고, 최종적으로 문자열 메시지로 변환합니다.
        return fundDataService.saveKofiaFundStatistics(today)
                .map(savedList -> "KOFIA 통계 데이터 수집 및 DB 저장이 완료되었습니다. (저장 건수: " + savedList.size() + ")")
                .onErrorReturn("KOFIA 통계 데이터 수집 중 오류가 발생했습니다.");
    }
}