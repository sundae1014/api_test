package com.bnk.fund_api_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "KOFIA_STATS")
public class KofiaStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String basDt;
    private String ctg;
    private String tstMthdCtg;
    private BigDecimal nPptTotAmt;
}
