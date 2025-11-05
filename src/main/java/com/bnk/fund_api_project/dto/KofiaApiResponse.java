package com.bnk.fund_api_project.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class KofiaApiResponse {
    private Response response;

    @Getter @Setter
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter @Setter
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter @Setter
    public static class Body {
        private Items items;
    }

    @Getter @Setter
    public static class Items {
        // 이 리스트의 요소가 실제 통계 데이터입니다.
        private List<KofiaFundStatsDto> item;
    }
}