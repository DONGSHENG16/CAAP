package com.bjfu.springboot.controller;

import com.bjfu.springboot.rna_dea.dimensionality_reduction.PCAanalyze;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DONGSHENG
 * @version 1.0
 */
@RestController
@RequestMapping("/pcoa")
public class PCoAController {
    @PostMapping("/perform")
    public Map<String, String> performPCA(@RequestBody PCoAController.PCoARequest pcoaRequest) {
        PCAanalyze pcAanalyze = new PCAanalyze();
        try {
            pcAanalyze.PCA(pcoaRequest.getExpressionMatrix(), pcoaRequest.getOutputPath());
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

    public static class PCoARequest {

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
