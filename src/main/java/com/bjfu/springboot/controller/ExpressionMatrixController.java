package com.bjfu.springboot.controller;

import com.bjfu.springboot.rna_dea.expression_matrix.ExpressionMatrixR;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/expMatrix")
public class ExpressionMatrixController {

    @PostMapping("/featurecounts")
    public Map<String, String> handleFeatureCountsRequest(@RequestBody Map<String, String> form) {
        String quantFile1 = form.get("quantFile1");
        String quantFile2 = form.get("quantFile2");
        String outputDir = form.get("outputDir");

        // 将路径保存在变量中并打印
        String quantFile1Variable = quantFile1;
        String quantFile2Variable = quantFile2;
        String outputDirVariable = outputDir;

        System.out.println("Quant File 1: " + quantFile1Variable);
        System.out.println("Quant File 2: " + quantFile2Variable);
        System.out.println("Output Directory: " + outputDirVariable);

        // 处理FeatureCounts逻辑
        ExpressionMatrixR expressionMatrixR = new ExpressionMatrixR();
        expressionMatrixR.FeatureCountsConstruct(quantFile1Variable,quantFile2Variable,outputDirVariable);

        Map<String, String> response = new HashMap<>();
        response.put("message", "执行成功");
        return response;
    }

    @PostMapping("/Htseq")
    public Map<String, String> handleHtseqRequest(@RequestBody Map<String, String> form) {
        String quantFile1 = form.get("quantFile1");
        String quantFile2 = form.get("quantFile2");
        String outputDir = form.get("outputDir");

        // 将路径保存在变量中并打印
        String quantFile1Variable = quantFile1;
        String quantFile2Variable = quantFile2;
        String outputDirVariable = outputDir;

        System.out.println("Quant File 1: " + quantFile1Variable);
        System.out.println("Quant File 2: " + quantFile2Variable);
        System.out.println("Output Directory: " + outputDirVariable);

        // 处理FeatureCounts逻辑
        ExpressionMatrixR expressionMatrixR = new ExpressionMatrixR();
        expressionMatrixR.HtseqConstruct(quantFile1Variable,quantFile2Variable,outputDirVariable);

        Map<String, String> response = new HashMap<>();
        response.put("message", "执行成功");
        return response;
    }

    @PostMapping("/stringtie")
    public Map<String, String> handleStringtieMatrix(@RequestBody Map<String, Object> payload) {
        String quantFile1 = (String) payload.get("quantFile1");
        String quantFile2 = (String) payload.get("quantFile2");
        String outputDir = (String) payload.get("outputDir");
        String normalization = (String) payload.get("normalization");

        // 打印出传递过来的值
        System.out.println("Quant File 1: " + quantFile1);
        System.out.println("Quant File 2: " + quantFile2);
        System.out.println("Output Directory: " + outputDir);
        System.out.println("Normalization: " + normalization);

        // 在这里处理这些值，例如调用相应的处理逻辑
        // processStringtieMatrix(quantFile1, quantFile2, outputDir, normalization);
        ExpressionMatrixR expressionMatrixR = new ExpressionMatrixR();
        expressionMatrixR.StringtieConstruct(quantFile1, quantFile2, outputDir, normalization);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Stringtie矩阵构建成功");
        return response;
    }

}
