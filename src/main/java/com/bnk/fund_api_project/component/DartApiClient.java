package com.bnk.fund_api_project.component;

import com.bnk.fund_api_project.entity.FundCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;
// (⚠️ DART API는 XML을 반환하며, XML 파싱 로직(예: JAXB 또는 Jackson XML)이 필요합니다.)

@Component
public class DartApiClient {

    private final WebClient webClient;

    // application.yml에서 설정한 값 주입
    @Value("${api.key.dart}")
    private String dartApiKey;
    @Value("${api.url.dart}")
    private String dartApiBaseUrl;

    public DartApiClient(WebClient.Builder webClientBuilder) {
        // DART API 기본 URL을 설정하여 WebClient 인스턴스 생성
        this.webClient = webClientBuilder.baseUrl(dartApiBaseUrl).build();
    }

    /**
     * DART API에서 모든 공시 대상의 고유번호 목록을 가져오는 메서드
     */
    public List<FundCode> fetchCorpCodes() {
        String url = "/corpCode.xml"; // 고유번호 파일 API 엔드포인트

        // WebClient 요청 빌드
        String responseXml = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam("crtfc_key", dartApiKey) // 인증키 파라미터
                        .build())
                .retrieve()
                .bodyToMono(String.class) // 응답을 String으로 받음 (XML)
                .block();

        // ⚠️ 이 부분에 XML 파싱 및 FundCode 리스트로 변환하는 로직이 필요합니다.
        // DART는 XML 파일을 반환하며, 이 파일을 압축 해제 후 파싱해야 합니다.
        // 복잡한 로직이므로, 이 예시에서는 빈 리스트를 반환한다고 가정합니다.

        System.out.println("DART API 응답 (XML): " + responseXml.substring(0, Math.min(responseXml.length(), 200)) + "...");

        // 실제 구현 시, XML 파싱 라이브러리(예: JAXB, Jackson XML)를 사용하여 FundCode 객체 리스트를 만듭니다.
        return List.of();
    }
}