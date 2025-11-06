package com.bnk.fund_api_project.controller;


import com.bnk.fund_api_project.service.KofiaStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kofia")
@RequiredArgsConstructor
public class KofiaController {

    private final KofiaStatService service;

    @GetMapping("/sync")
    public String sync() throws Exception {
        service.fetchAndSave();
        return "KOFIA API 동기화 완료 ✅";
    }
}
