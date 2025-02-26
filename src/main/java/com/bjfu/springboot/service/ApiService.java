package com.bjfu.springboot.service;

import okhttp3.*;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
public class ApiService {

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Value("${chatgpt.api.key}")
    private String chatGptApiKey;

    @Value("${deepseek.api.key}")
    private String deepSeekApiKey;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${grok.api.key}")
    private String grokApiKey;

    public String getApiKey(String LLM,String model) {
        switch (LLM) {
            case "chatgpt":
                System.out.println("-------case chatgpt:--------");
                return chatGptApiKey;
            case "deepseek":
                return deepSeekApiKey;
            case "gemini":
                return geminiApiKey;
            case "grok":
                return grokApiKey;
            default:
                throw new IllegalArgumentException("Unknown model: " + model);
        }
    }

    public String getResponse(String apiKey, String prompt, String model,String LLM) throws IOException {
        String apiUrl = getApiUrl(LLM);  

        
        ModelRequest.Message message = ModelRequest.Message.builder()
                .role("user")
                .content(prompt)
                .build();

        ModelRequest requestBody = ModelRequest.builder()
                .model(model)
                .messages(Collections.singletonList(message))
                .build();

        
        String json = gson.toJson(requestBody);
        System.out.println("String json = gson.toJson(requestBody);" + json);

       
        RequestBody body = RequestBody.create(json.getBytes(StandardCharsets.UTF_8), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

       
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
           
            String responseBody = response.body().string();
            JSONObject responseObject = new JSONObject(responseBody);
            JSONArray choices = responseObject.getJSONArray("choices");
            String generatedMessage = choices.getJSONObject(0).getJSONObject("message").getString("content");
            return generatedMessage.trim();
        }
    }

    private String getApiUrl(String LLM) {
        switch (LLM) {
            case "chatgpt":
                return "configure URLL";
            case "deepseek":
                return "configure URL";
            case "gemini":
                return "configure URL";
            case "grok":
                return "configure URL";
            default:
                throw new IllegalArgumentException("Unknown model: " + LLM);
        }
    }

    
    @lombok.Data
    @lombok.Builder
    public static class ModelRequest {
        private String model;
        private java.util.List<Message> messages;

        @lombok.Data
        @lombok.Builder
        public static class Message {
            private String role;
            private String content;
        }
    }
}
