package com.bnk.fund_api_project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class KofiaFundStatsDto {
    private String fundType; // 펀드 유형 (예: 주식형, 채권형)
    private Long netAssetAmount; // 순자산 총액 (가장 중요한 데이터)
    // API 명세서에 있는 다른 필드들을 여기에 추가합니다.
}