package com.bnk.fund_api_project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // JPA Entity로 지정
@Getter @Setter
@NoArgsConstructor
public class FundCode {

    // DART 고유번호 (8자리), Primary Key
    @Id
    private String corpCode;

    // 펀드명 또는 운용사명 (DART에서 제공)
    private String corpName;

    // 종목코드 (상장된 펀드일 경우)
    private String stockCode;

    // 생성자 (데이터 파싱 후 사용)
    public FundCode(String corpCode, String corpName, String stockCode) {
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.stockCode = stockCode;
    }
}