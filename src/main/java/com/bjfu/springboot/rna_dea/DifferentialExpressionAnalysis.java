package com.bjfu.springboot.rna_dea;

import org.junit.Test;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class DifferentialExpressionAnalysis {
    @Test
    public void Deseq2(String expressionMatrixFilePath, String groupFilePath, String option,String outputPath) {

        RConnection c = null;
        try {
            System.out.println(option);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String formattedDateTime = now.format(formatter);
            expressionMatrixFilePath = expressionMatrixFilePath.replace("\\", "/");
            groupFilePath = groupFilePath.replace("\\", "/");
            outputPath = outputPath.replace("\\", "/");


            System.out.println("Connecting to Rserve...");
            c = new RConnection("127.0.0.1", 6311);
            System.out.println("Connected to Rserve");

            System.out.println("Loading DESeq2 library...");
            c.eval("library(\"DESeq2\")");

            System.out.println("Reading merged counts matrix...");
            c.eval("merged_counts <- read.csv(\"" + expressionMatrixFilePath + "\", row.names = 1)");

            System.out.println("Reading metadata...");
            c.eval("mymeta <- read.csv(\"" + groupFilePath + "\", stringsAsFactors = TRUE)");

            c.eval("merged_counts <- round(as.matrix(merged_counts))");

            System.out.println("Creating DESeqDataSet...");
            c.eval("dds <- DESeqDataSetFromMatrix(countData = merged_counts, colData = mymeta, design = ~dex)");

            System.out.println("Running DESeq...");
            c.eval("dds <- DESeq(dds)");

            System.out.println("Generating results...");
            c.eval("res <- results(dds)");

            switch (option) {
                case "MA":
                    System.out.println("Creating MA plot...");

                    c.eval("png(\""+outputPath+formattedDateTime+"_MA_plot.png\", width=800, height=600)");
                    c.eval("plotMA(res, ylim=c(-5,5))");
                    c.eval("dev.off()");
                    System.out.println("MA plot created.");
                    break;
                case "EnhancedVolcano":
                    System.out.println("Loading EnhancedVolcano library...");
                    c.eval("library(EnhancedVolcano)");
                    System.out.println("Creating volcano plot...");
                    c.eval("volcano <- EnhancedVolcano(res, lab = rownames(res), x = 'log2FoldChange', y = 'pvalue', title = 'Volcano plot of differential expression', pCutoff = 0.05, FCcutoff = 1.5)");
                    c.eval("png(\""+outputPath+formattedDateTime+"_volcano.png\", width = 800, height = 600)");
                    c.eval("print(volcano)");
                    c.eval("dev.off()");
                    System.out.println("Volcano plot created.");
                    break;
                case "Heatmap":
                    System.out.println("Loading pheatmap library...");
                    c.eval("library(pheatmap)");
                    System.out.println("Creating heatmap...");
                    c.eval("vsd <- vst(dds, blind=FALSE)");
                    c.eval("pheatmap(assay(vsd), cluster_rows=TRUE, show_rownames=FALSE, cluster_cols=TRUE, annotation_col=colData(vsd))");
                    c.eval("png(\""+outputPath+formattedDateTime+"_heatmap.png\", width = 800, height = 600)");
                    c.eval("dev.off()");
                    System.out.println("Heatmap created.");
                    break;
                case "Boxplot":
                    c.eval("significant_genes <- res[which(res_DESeq2$padj < 0.05), ]");
                    c.eval("logCPM <- log2(merged_counts + 1)");
                    c.eval("logCPM_df <- as.data.frame(logCPM)");
                    c.eval("logCPM_df$Gene <- rownames(logCPM_df)");
                    c.eval("logCPM_df_significant <- logCPM_df[rownames(logCPM_df) %in% rownames(significant_genes), ]");
                    c.eval("logCPM_long <- melt(logCPM_df_significant, id.vars = 'Gene', variable.name = 'Sample', value.name = 'Expression')");
                    c.eval("logCPM_long$Group <- mymeta$dex[match(logCPM_long$Sample, mymeta$id)]");
                    c.eval("png(\""+outputPath+formattedDateTime+"_boxplot.png\", width = 800, height = 600)");
                    c.eval("boxplot <- ggplot(logCPM_long, aes(x = Group, y = Expression, fill = Group)) +");
                    c.eval("  geom_boxplot(outlier.size = 1, outlier.colour = 'red') +");
                    c.eval("  theme_minimal(base_family = 'Arial', base_size = 30) +");
                    c.eval("  labs(x = 'Group', y = 'Log2(CPM + 1)') +");
                    c.eval("  theme(plot.title = element_blank(), axis.title.x = element_text(size = 28),");
                    c.eval(" axis.title.y = element_text(size = 28), axis.text.x = element_text(size = 35),");
                    c.eval(" axis.text.y = element_text(size = 40))");
                    c.eval("print(boxplot)");
                    c.eval("dev.off()");
                    break;
                case "beeswarm":
                    c.eval("library(ggplot2)");
                    c.eval("library(ggbeeswarm)");
                    c.eval("library(reshape2)");
                    c.eval("library(svglite)");
                    c.eval("significant_genes <- res_DESeq2[which(res_DESeq2$padj < 0.05), ]");
                    c.eval("logCPM <- log2(merged_counts + 1)");
                    c.eval("logCPM_significant <- logCPM[rownames(logCPM) %in% rownames(significant_genes), ]");
                    c.eval("logCPM_df <- as.data.frame(logCPM_significant)");
                    c.eval("logCPM_df$Gene <- rownames(logCPM_df)");
                    c.eval("logCPM_long <- melt(logCPM_df, id.vars = 'Gene', variable.name = 'Sample', value.name = 'Expression')");
                    c.eval("logCPM_long$Group <- mymeta$dex[match(logCPM_long$Sample, mymeta$id)]");
                    c.eval("beeswarm_plot <- ggplot(logCPM_long, aes(x = Group, y = Expression, color = Group)) + "
                            + "geom_quasirandom() + "
                            + "theme_minimal() + "
                            + "labs(x = 'Group', y = 'Log2(CPM + 1)') + "
                            + "theme(text = element_text(family = 'Arial', size = 20), "
                            + "axis.title.x = element_text(size = 20), "
                            + "axis.title.y = element_text(size = 20), "
                            + "axis.text.x = element_text(size = 15), "
                            + "axis.text.y = element_text(size = 15))");
                    c.eval("ggsave('" + outputPath+formattedDateTime + "', plot = beeswarm_plot, width = 13, height = 7, device = 'png')");
                    c.eval("dev.off()");
                    break;
                case "violin":
                    c.eval("significant_genes <- res[which(res$padj < 0.05), ]");
                    c.eval("logCPM <- log2(normalized_counts_matrix + 1)");
                    c.eval("logCPM_df <- as.data.frame(logCPM)");
                    c.eval("logCPM_df$Gene <- rownames(logCPM_df)");
                    c.eval("logCPM_df_significant <- logCPM_df[rownames(logCPM_df) %in% rownames(significant_genes), ]");
                    c.eval("logCPM_long <- melt(logCPM_df_significant, id.vars = 'Gene', variable.name = 'Sample', value.name = 'Expression')");
                    c.eval("logCPM_long$Group <- metadata$dex[match(logCPM_long$Sample, metadata$id)]");
                    c.eval("violin_plot <- ggplot(logCPM_long, aes(x = Group, y = Expression, fill = Group)) + " +
                            "geom_violin(trim = FALSE) + " +
                            "geom_jitter(height = 0, width = 0.2, alpha = 0.6, size = 1) + " +
                            "theme_minimal() + " +
                            "labs(x = 'Groups', y = 'Log2(CPM + 1)') + " +
                            "theme(axis.title.x = element_text(family = 'Arial', size = 25), " +
                            "axis.title.y = element_text(family = 'Arial', size = 30), " +
                            "axis.text.x = element_text(family = 'Arial', size = 30), " +
                            "axis.text.y = element_text(family = 'Arial', size = 25), " +
                            "legend.title = element_text(family = 'Arial', size = 28), " +
                            "legend.text = element_text(family = 'Arial', size = 28))");

                    c.eval("print(violin_plot)");
                    c.eval("ggsave('" + outputPath+formattedDateTime + "', " +
                            "plot = violin_plot, width = 13, height = 7, device = 'svg')");
                    c.eval("dev.off()");
                    break;
                default:
                    System.out.println("Invalid option provided.");
                    break;
            }

            String[] resCols = c.eval("colnames(merged_counts)").asStrings();
            System.out.println("Columns in merged counts matrix: " + String.join(", ", resCols));

        } catch (RserveException e) {
            e.printStackTrace();
            System.out.println("RserveException occurred: " + e.getMessage());
        } catch (REXPMismatchException e) {
            e.printStackTrace();
            System.out.println("REXPMismatchException occurred: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
                System.out.println("Rserve connection closed.");
            }
        }
    }


    public void Edger(String expressionMatrixFilePath, String groupFilePath, String option, String outputPath) {
        RConnection c = null;
        try {
            System.out.println("Expression Matrix File Path: " + expressionMatrixFilePath);
            System.out.println("Group File Path: " + groupFilePath);
            System.out.println("Output Path: " + outputPath);


            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String formattedDateTime = now.format(formatter);

            expressionMatrixFilePath = expressionMatrixFilePath.replace("\\", "/");
            groupFilePath = groupFilePath.replace("\\", "/");
            outputPath = outputPath.replace("\\", "/");

            System.out.println("Connecting to Rserve...");
            c = new RConnection("127.0.0.1", 6311);
            System.out.println("Connected to Rserve");


            System.out.println("Loading edgeR library...");
            c.eval("library(edgeR)");


            c.eval("normalized_counts_matrix <- read.csv(\"" + expressionMatrixFilePath + "\", row.names = 1)");
            c.eval("metadata <- read.csv(\"" + groupFilePath + "\")");


            c.eval("sample_order <- colnames(normalized_counts_matrix)");
            c.eval("metadata <- metadata[match(sample_order, metadata$id), ]");

            c.eval("group <- factor(metadata$dex)");


            c.eval("y <- DGEList(counts = normalized_counts_matrix, group = group)");

            c.eval("y <- estimateDisp(y)");

            c.eval("design <- model.matrix(~ group)");
            c.eval("fit <- glmFit(y, design)");
            c.eval("lrt <- glmLRT(fit)");
            c.eval("results <- topTags(lrt, n = Inf)");

            switch (option) {
                case "MA":
                    System.out.println("Creating MA plot...");
                    try {
                        c.eval("png(\"" + outputPath + "_MA_plot.png\", width=800, height=600)");
                        c.eval("plotMA(results, ylim=c(-5,5))");
                        c.eval("dev.off()");
                        System.out.println("MA plot created.");
                    } catch (RserveException e) {
                        e.printStackTrace();
                        System.out.println("Error in creating MA plot: " + e.getMessage());
                    }
                    break;
                case "EnhancedVolcano":
                    System.out.println("Loading EnhancedVolcano library...");
                    try {
                        c.eval("library(EnhancedVolcano)");
                        System.out.println("Creating volcano plot...");
                        c.eval("volcano <- EnhancedVolcano(results, lab = rownames(results), x = 'logFC', y = 'PValue', title = 'Volcano plot of differential expression', pCutoff = 0.05, FCcutoff = 1.5)");
                        c.eval("png(\"" + outputPath + "_volcano_plot.png\", width = 800, height = 600)");
                        c.eval("print(volcano)");
                        c.eval("dev.off()");
                        System.out.println("Volcano plot created.");
                    } catch (RserveException e) {
                        e.printStackTrace();
                        System.out.println("Error in creating volcano plot: " + e.getMessage());
                    }
                    break;
                case "Heatmap":
                    System.out.println("Loading pheatmap library...");
                    try {
                        c.eval("library(pheatmap)");
                        System.out.println("Creating heatmap...");
                        c.eval("vsd <- vst(y, blind=FALSE)");
                        c.eval("pheatmap(assay(vsd), cluster_rows=TRUE, show_rownames=FALSE, cluster_cols=TRUE)");
                        c.eval("png(\"" + outputPath + "_heatmap.png\", width = 800, height = 600)");
                        c.eval("dev.off()");
                        System.out.println("Heatmap created.");
                    } catch (RserveException e) {
                        e.printStackTrace();
                        System.out.println("Error in creating heatmap: " + e.getMessage());
                    }
                    break;
                case "Boxplot":
                    System.out.println("Creating Boxplot...");
                    c.eval("significant_genes <- results[which(results$padj < 0.05), ]");
                    c.eval("logCPM <- log2(normalized_counts_matrix + 1)");
                    c.eval("logCPM_df <- as.data.frame(logCPM)");
                    c.eval("logCPM_df$Gene <- rownames(logCPM_df)");
                    c.eval("logCPM_df_significant <- logCPM_df[rownames(logCPM_df) %in% rownames(significant_genes), ]");
                    c.eval("logCPM_long <- melt(logCPM_df_significant, id.vars = 'Gene', variable.name = 'Sample', value.name = 'Expression')");
                    c.eval("logCPM_long$Group <- metadata$dex[match(logCPM_long$Sample, metadata$id)]");

                    // 绘制Boxplot
                    c.eval("png(\"" + outputPath + formattedDateTime + "_boxplot.png\", width = 800, height = 600)");
                    c.eval("boxplot <- ggplot(logCPM_long, aes(x = Group, y = Expression, fill = Group)) + geom_boxplot(outlier.size = 1, outlier.colour = 'red') + theme_minimal(base_family = 'Arial', base_size = 30) + labs(x = 'Group', y = 'Log2(CPM + 1)') + theme(plot.title = element_blank(), axis.title.x = element_text(size = 28), axis.title.y = element_text(size = 28), axis.text.x = element_text(size = 35), axis.text.y = element_text(size = 40))");
                    c.eval("print(boxplot)");
                    c.eval("dev.off()");
                    System.out.println("Boxplot created.");
                    break;

                case "beeswarm":
                    System.out.println("Creating Beeswarm plot...");
                    c.eval("library(ggplot2)");
                    c.eval("library(ggbeeswarm)");
                    c.eval("library(reshape2)");
                    c.eval("significant_genes <- results[which(results$padj < 0.05), ]");
                    c.eval("logCPM <- log2(normalized_counts_matrix + 1)");
                    c.eval("logCPM_significant <- logCPM[rownames(logCPM) %in% rownames(significant_genes), ]");
                    c.eval("logCPM_df <- as.data.frame(logCPM_significant)");
                    c.eval("logCPM_df$Gene <- rownames(logCPM_df)");
                    c.eval("logCPM_long <- melt(logCPM_df, id.vars = 'Gene', variable.name = 'Sample', value.name = 'Expression')");
                    c.eval("logCPM_long$Group <- metadata$dex[match(logCPM_long$Sample, metadata$id)]");

                    // 绘制Beeswarm plot
                    c.eval("beeswarm_plot <- ggplot(logCPM_long, aes(x = Group, y = Expression, color = Group)) + geom_quasirandom() + theme_minimal() + labs(x = 'Group', y = 'Log2(CPM + 1)') + theme(text = element_text(family = 'Arial', size = 20), axis.title.x = element_text(size = 20), axis.title.y = element_text(size = 20), axis.text.x = element_text(size = 15), axis.text.y = element_text(size = 15))");
                    c.eval("ggsave('" + outputPath + formattedDateTime + "_beeswarm.png', plot = beeswarm_plot, width = 13, height = 7, device = 'png')");
                    c.eval("dev.off()");
                    System.out.println("Beeswarm plot created.");
                    break;

                case "violin":
                    System.out.println("Creating Violin plot...");
                    c.eval("significant_genes <- results[which(results$padj < 0.05), ]");
                    c.eval("logCPM <- log2(normalized_counts_matrix + 1)");
                    c.eval("logCPM_df <- as.data.frame(logCPM)");
                    c.eval("logCPM_df$Gene <- rownames(logCPM_df)");
                    c.eval("logCPM_df_significant <- logCPM_df[rownames(logCPM_df) %in% rownames(significant_genes), ]");
                    c.eval("logCPM_long <- melt(logCPM_df_significant, id.vars = 'Gene', variable.name = 'Sample', value.name = 'Expression')");
                    c.eval("logCPM_long$Group <- metadata$dex[match(logCPM_long$Sample, metadata$id)]");

                    // 绘制Violin plot
                    c.eval("violin_plot <- ggplot(logCPM_long, aes(x = Group, y = Expression, fill = Group)) + geom_violin(trim = FALSE) + geom_jitter(height = 0, width = 0.2, alpha = 0.6, size = 1) + theme_minimal() + labs(x = 'Groups', y = 'Log2(CPM + 1)') + theme(axis.title.x = element_text(family = 'Arial', size = 25), axis.title.y = element_text(family = 'Arial', size = 30), axis.text.x = element_text(family = 'Arial', size = 30), axis.text.y = element_text(family = 'Arial', size = 25), legend.title = element_text(family = 'Arial', size = 28), legend.text = element_text(family = 'Arial', size = 28))");
                    c.eval("ggsave('" + outputPath + formattedDateTime + "_violin.png', plot = violin_plot, width = 13, height = 7, device = 'png')");
                    c.eval("dev.off()");
                    System.out.println("Violin plot created.");
                    break;

                default:
                    System.out.println("Invalid option provided.");
                    break;
            }

            String[] resCols = c.eval("colnames(normalized_counts_matrix)").asStrings();
            System.out.println("Columns in normalized counts matrix: " + String.join(", ", resCols));

        } catch (RserveException e) {
            e.printStackTrace();
            System.out.println("RserveException occurred: " + e.getMessage());
        } catch (REXPMismatchException e) {
            e.printStackTrace();
            System.out.println("REXPMismatchException occurred: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close(); // 关闭 Rserve 连接
                System.out.println("Rserve connection closed.");
            }
        }
    }


}
