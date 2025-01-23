package com.bjfu.springboot.rna_tool;

/**
 * @author DONGSHENG
 * @version 1.0
 */

/**
 * RNA-seq Analysis Automation Tool V1.0!
 *
 */
public class StartAutomation {
    // Entry function
    public void runTool(String gc_content, String alignment_rate, String alignment_tool,
                        String quantification_tool, String sequence_file1, String sequence_file2,
                        String annotation_fileName, String reference_genome) {
        System.out.println("Executing the entry function of the project");

        AutoTool autoTool = new AutoTool();
        // Execute
        autoTool.runAutoTool(gc_content, alignment_rate, alignment_tool, quantification_tool,
                sequence_file1, sequence_file2, annotation_fileName, reference_genome);
    }
}
