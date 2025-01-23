package com.bjfu.springboot.controller;

import org.springframework.web.bind.annotation.*;
import com.bjfu.springboot.rna_dea.DifferentialExpressionAnalysis;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dea")
public class DEAController {

    @PostMapping("/deseq2")
    public Map<String, String> handleDeseq2Request(@RequestBody DEARequest deaRequest) {
        DifferentialExpressionAnalysis dea = new DifferentialExpressionAnalysis();
        try {
            dea.Deseq2(deaRequest.getExpressionMatrix(), deaRequest.getGroupFile(), deaRequest.getVisualization(),deaRequest.getOutputPath());
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    @PostMapping("/edger")
    public Map<String, String> handleEdgerRequest(@RequestBody DEARequest deaRequest) {
        DifferentialExpressionAnalysis dea = new DifferentialExpressionAnalysis();
        try {
            dea.Edger(deaRequest.getExpressionMatrix(), deaRequest.getGroupFile(),deaRequest.getOutputPath(),deaRequest.getVisualization());
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    public static class DEARequest {
        private String expressionMatrix;
        private String groupFile;
        private String outputPath; // 新增字段
        private String visualization;

        public String getExpressionMatrix() {
            return expressionMatrix;
        }

        public void setExpressionMatrix(String expressionMatrix) {
            this.expressionMatrix = expressionMatrix;
        }

        public String getGroupFile() {
            return groupFile;
        }

        public void setGroupFile(String groupFile) {
            this.groupFile = groupFile;
        }

        public String getOutputPath() {
            return outputPath;
        }

        public void setOutputPath(String outputPath) {
            this.outputPath = outputPath;
        }

        public String getVisualization() {
            return visualization;
        }

        public void setVisualization(String visualization) {
            this.visualization = visualization;
        }
    }
}
