package com.bjfu.springboot.controller;

import com.bjfu.springboot.rna_dea.normalization.CPM;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CPMController {

    @PostMapping("/cpm")
    public Map<String, String> handleCPMRequest(@RequestBody Map<String, String> form) {
        String matrixFilePath = form.get("matrixFilePath");
        String outputDir = form.get("outputDir");

        // 将路径保存在变量中并打印
        String matrixFilePathVariable = matrixFilePath;
        String outputDirVariable = outputDir;

        System.out.println("Matrix File Path: " + matrixFilePathVariable);
        System.out.println("Output Directory: " + outputDirVariable);

        // 处理CPM逻辑
        CPM cpm = new CPM();
        cpm.CPM_EC(matrixFilePathVariable,outputDirVariable);

        Map<String, String> response = new HashMap<>();
        response.put("message", "执行成功");
        return response;
    }
}
