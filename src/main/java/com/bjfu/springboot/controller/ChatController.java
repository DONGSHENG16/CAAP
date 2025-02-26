package com.bjfu.springboot.controller;

import com.bjfu.springboot.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class ChatController {

    private final ApiService apiService;

    @Autowired
    public ChatController(ApiService apiService) {
        this.apiService = apiService;
    }

    @PostMapping("/ask")
    public String askQuestion(@RequestParam String LLM,@RequestParam String model, @RequestBody String question) {
        try {
            String apiKey = apiService.getApiKey(LLM,model);
            System.out.println("LLM" + question + "---model" + model);
            return apiService.getResponse(apiKey, question, model,LLM);
        } catch (IOException e) {
            return "出错了：" + e.getMessage();
        }
    }
}
