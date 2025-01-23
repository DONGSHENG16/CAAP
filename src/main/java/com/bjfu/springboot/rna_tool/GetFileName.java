package com.bjfu.springboot.rna_tool;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class GetFileName {
    public String getSeqName(String sequence_file1) {
        // Original file name
        String originalFileName = sequence_file1;
        // Extract the main part of the file name (excluding the extension)
        String fileName = originalFileName.split("\\.")[0];
        // Print result
        System.out.println("FileName: " + fileName);
        return fileName;
    }
}
