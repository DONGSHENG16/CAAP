package com.bjfu.springboot.controller;

import com.bjfu.springboot.rna_dea.dimensionality_reduction.PCAanalyze;
import com.bjfu.springboot.rna_dea.dimensionality_reduction.UMAPanalyze;
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
@RequestMapping("/umap")
public class UMAPController {
    @PostMapping("/perform")
    public Map<String, String> performPCA(@RequestBody UMAPController.UMAPRequest umapRequest) {
        UMAPanalyze umapAanalyze = new UMAPanalyze();
        try {
            umapAanalyze.UMAP(umapRequest.getExpressionMatrix(), umapRequest.getGroupInfo(), umapRequest.getOutputPath());
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


    public static class UMAPRequest {
        private String expressionMatrix;
        private String groupInfo;  // 新增的字段，用于接收分组信息
        private String outputPath;

        // Getters and Setters
        public String getExpressionMatrix() {
            return expressionMatrix;
        }

        public void setExpressionMatrix(String expressionMatrix) {
            this.expressionMatrix = expressionMatrix;
        }

        public String getGroupInfo() {
            return groupInfo;
        }

        public void setGroupInfo(String groupInfo) {
            this.groupInfo = groupInfo;
        }

        public String getOutputPath() {
            return outputPath;
        }

        public void setOutputPath(String outputPath) {
            this.outputPath = outputPath;
        }
    }

}
