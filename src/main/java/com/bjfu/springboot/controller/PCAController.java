package com.bjfu.springboot.controller;

import com.bjfu.springboot.rna_dea.dimensionality_reduction.PCAanalyze;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pca")
public class PCAController {


    @PostMapping("/perform")
    public Map<String, String> performPCA(@RequestBody PCARequest pcaRequest) {
        PCAanalyze pcAanalyze = new PCAanalyze();
        try {
            pcAanalyze.PCA(pcaRequest.getExpressionMatrix(), pcaRequest.getOutputPath());
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

    public static class PCARequest {

        private String expressionMatrix;
        private String outputPath;

        public String getExpressionMatrix() {
            return expressionMatrix;
        }

        public void setExpressionMatrix(String expressionMatrix) {
            this.expressionMatrix = expressionMatrix;
        }

        public String getOutputPath() {
            return outputPath;
        }

        public void setOutputPath(String outputPath) {
            this.outputPath = outputPath;
        }
    }
}
