package com.bnk.fund_api_project.dto;

import lombok.Data;
import java.util.List;

@Data
public class KofiaResponseDTO {
    private Response response;

    @Data
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body {
        private List<Item> items;
    }

    @Data
    public static class Item {
        private String STAT_CODE;
        private String STAT_NAME;
        private String STAT_UNIT;
        private String STAT_DATE;
    }
}
