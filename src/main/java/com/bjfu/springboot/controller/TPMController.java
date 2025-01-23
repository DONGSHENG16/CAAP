package com.bjfu.springboot.controller;

import org.rosuda.REngine.Rserve.RserveException;
import org.springframework.web.bind.annotation.*;
import com.bjfu.springboot.rna_dea.normalization.TPM;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TPMController {

    @PostMapping("/api/tpm")
    public Map<String, String> handleTPMRequest(@RequestBody Map<String, String> form) throws RserveException {
        String expressionMatrix = form.get("expressionMatrix");
        String annotationFile = form.get("annotationFile");
        String outputDir = form.get("outputDir");

        // Save the paths in variables and print them
        String expressionMatrixVariable = expressionMatrix;
        String annotationFileVariable = annotationFile;
        String outputDirVariable = outputDir;

        System.out.println("Expression Matrix File Path: " + expressionMatrixVariable);
        System.out.println("Annotation File Path: " + annotationFileVariable);
        System.out.println("Output Directory: " + outputDirVariable);

        // Process TPM logic
        TPM tpm = new TPM();
        tpm.TPM_EC(expressionMatrixVariable, annotationFileVariable, outputDirVariable);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Execution successful");
        return response;
    }
}
