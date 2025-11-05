package com.bnk.fund_api_project.repository;
import com.bnk.fund_api_project.entity.FundCode;
import org.springframework.data.jpa.repository.JpaRepository;

// <Entity 타입, PK 타입>
public interface FundCodeRepository extends JpaRepository<FundCode, String> {
    // 추가적인 사용자 정의 쿼리 메서드를 여기에 정의할 수 있습니다.
}