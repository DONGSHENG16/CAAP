package com.bjfu.springboot.rna_tool;

import com.bjfu.springboot.rna_tool.Trimmomatic.Trimmomatic;
import com.bjfu.springboot.rna_tool.comparison.*;
import com.bjfu.springboot.rna_tool.fastqc.AnalyzeFile;
import com.bjfu.springboot.rna_tool.fastqc.FastqcCheck;
import com.bjfu.springboot.rna_tool.initializtion.InitializtionFolder;
import com.bjfu.springboot.rna_tool.initializtion.LoadingFiles;
import com.bjfu.springboot.rna_tool.ration.*;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class AutoTool {
    public void runAutoTool(String gc_content, String alignment_rate, String alignment_tool, String quantification_tool, String sequence_file1, String sequence_file2, String annotation_fileName, String reference_genome) {
        // Get the name of the sequencing file
        GetFileName getFileName = new GetFileName();
        String seqFileName = getFileName.getSeqName(sequence_file1);

        int GC_count = Integer.parseInt(gc_content);
        // 1. Initialize - Create folders, files, and configure environment
        InitializtionFolder initializtionFolder = new InitializtionFolder();
        String ProjectFolder = initializtionFolder.mkdirFolder();
        String toolPath = "/home/RSAP_Data/" + ProjectFolder;

        // 2. Load fastq files, reference genome, annotation files ----- Simulation
        LoadingFiles loadingFiles = new LoadingFiles();
        loadingFiles.uploadFiles(toolPath, sequence_file1, sequence_file2, annotation_fileName, reference_genome);

        // 3. Fastqc processing
        // 3.1 Check
        FastqcCheck fastqcCheck = new FastqcCheck();
        fastqcCheck.checkFile(toolPath, sequence_file1, seqFileName);

        // 3.2 File analysis - and perform Trimmomatic data preprocessing
        AnalyzeFile analyzeFile = new AnalyzeFile();
        int GC = analyzeFile.SeqGCcount(toolPath, seqFileName); // GC content read
        // Check if GC content is acceptable
        if (GC_count < GC) {
            analyzeFile.readAnyStr(toolPath, seqFileName, sequence_file1, sequence_file2);
            // 3.3 Data preprocessing
            Trimmomatic trimmomatic = new Trimmomatic();
            trimmomatic.cpCleandata(toolPath, sequence_file1, sequence_file2);
            System.out.println("==================Alignment Consumption===================");

            // 1. Get Operating System MXBean and Thread MXBean instances
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            Runtime runtime = Runtime.getRuntime();

            // 2. Get initial time, memory, and CPU usage
            long startTime = System.nanoTime();
            long startMemory = runtime.totalMemory() - runtime.freeMemory();
            long startCpuTime = osBean.getProcessCpuTime();

            if (alignment_tool.equals("STAR")) {
                STAR_BuiltIndex STAR_builtIndex = new STAR_BuiltIndex();
                STAR_builtIndex.SATRsetIndex(toolPath, seqFileName, annotation_fileName, reference_genome);
                // 4.2.1 STAR
                STAR_ExecuteComparison STAR_executeComparison = new STAR_ExecuteComparison();
                STAR_executeComparison.SATRcomparison(toolPath, sequence_file1, sequence_file2, seqFileName);
                // Rename
                RenameSTARSortBam renameSTARSortBam = new RenameSTARSortBam();
                renameSTARSortBam.rename(toolPath);
            } else {
                // 4. Alignment
                switch (alignment_tool) {
                    case "Hisat2":
                        // 4.1.2 Hisat2
                        HISAT2_BuiltIndex HISAT2_builtIndex = new HISAT2_BuiltIndex();
                        HISAT2_builtIndex.HISAT2setIndex(toolPath, reference_genome);
                        // 4.2.1 Hisat2
                        HISAT2_ExecuteComparison hisat2ExecuteComparison = new HISAT2_ExecuteComparison();
                        hisat2ExecuteComparison.HISAT2comparison(toolPath, sequence_file1, sequence_file2);
                        break;
                    case "BWA":
                        // 4.1.2 BWA
                        BWA_BuiltIndex bWA_builtIndex = new BWA_BuiltIndex();
                        bWA_builtIndex.BWAsetIndex(toolPath, reference_genome);
                        // 4.2.1 BWA
                        BWA_ExecuteComparison bWAExecuteComparison = new BWA_ExecuteComparison();
                        bWAExecuteComparison.BWAcomparison(toolPath, sequence_file1, sequence_file2);
                        break;
                    case "Bowtie2":
                        // 4.1.2 Bowtie2
                        Bowtie2_BuiltIndex bowtie2_builtIndex = new Bowtie2_BuiltIndex();
                        bowtie2_builtIndex.Bowtie2setIndex(toolPath, reference_genome);
                        // 4.2.1 Bowtie2
                        Bowtie2_ExecuteComparison bowtie2_ExecuteComparison = new Bowtie2_ExecuteComparison();
                        bowtie2_ExecuteComparison.Bowtie2comparison(toolPath, sequence_file1, sequence_file2);
                        break;
                    case "Minimap2":
                        // 4.1.2 Minimap2
                        Minimap2_BuiltIndex minimap2_builtIndex = new Minimap2_BuiltIndex();
                        minimap2_builtIndex.Minimap2setIndex(toolPath, reference_genome);
                        // 4.2.1 Minimap2
                        Minimap2_ExecuteComparison minimap2_ExecuteComparison = new Minimap2_ExecuteComparison();
                        minimap2_ExecuteComparison.Minimap2comparison(toolPath, sequence_file1, sequence_file2);
                        break;
                }
                // Build index
                SamtoolsTransform samtoolsTransform = new SamtoolsTransform();
                samtoolsTransform.Samtools(toolPath);
                // Sorting
                BamToSort bamToSort = new BamToSort();
                bamToSort.Samtools_sort(toolPath);
            }

        }
    }
}
