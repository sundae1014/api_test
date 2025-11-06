package com.bnk.fund_api_project.service;


import com.bnk.fund_api_project.dto.KofiaResponseDTO;
import com.bnk.fund_api_project.entity.KofiaStat;
import com.bnk.fund_api_project.repository.KofiaStatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class KofiaStatService {

    private final KofiaStatRepository repository;

    @Value("${kofia.api.service-key}")
    private String serviceKey;

    public KofiaStatService(KofiaStatRepository repository) {
        this.repository = repository;
    }

    public void fetchAndSave() throws Exception {
        String url = "https://apis.data.go.kr/1160100/service/GetKofiaStatisticsInfoService/getFundTotalNetEssetInfo"
                + "?serviceKey=" + URLEncoder.encode(serviceKey, StandardCharsets.UTF_8)
                + "&numOfRows=10&pageNo=1"; // _type=json 제거

        // XML 요청
        RestTemplate restTemplate = new RestTemplate();
        String xmlResponse = restTemplate.getForObject(url, String.class);

        // 파싱
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlResponse.getBytes()));

        NodeList items = doc.getElementsByTagName("item");

        for (int i = 0; i < items.getLength(); i++) {
            var item = items.item(i);

            String basDt = item.getChildNodes().item(1).getTextContent();
            String ctg = item.getChildNodes().item(3).getTextContent();
            String tstMthdCtg = item.getChildNodes().item(5).getTextContent();
            String nPptTotAmt = item.getChildNodes().item(7).getTextContent();

            KofiaStat stat = new KofiaStat();
            stat.setBasDt(basDt);
            stat.setCtg(ctg);
            stat.setTstMthdCtg(tstMthdCtg);
            stat.setNPptTotAmt(new BigDecimal(nPptTotAmt));

            repository.save(stat);
        }

        System.out.println("✅ XML 데이터 저장 완료 (" + items.getLength() + "건)");
    }
}