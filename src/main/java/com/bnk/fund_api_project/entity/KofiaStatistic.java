package com.bnk.fund_api_project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
public class KofiaStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fundType; // 펀드 유형 (예: 주식형)
    private Long netAssetAmount; // 순자산 총액
    private LocalDate statisticDate; // 통계 기준 날짜

    public KofiaStatistic(String fundType, Long netAssetAmount, LocalDate statisticDate) {
        this.fundType = fundType;
        this.netAssetAmount = netAssetAmount;
        this.statisticDate = statisticDate;
    }
}