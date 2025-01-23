package com.bjfu.springboot.rna_dea.dimensionality_reduction;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PCoAAnalyze {

    public void PCoA(String expressionMatrixFilePath, String outputPath) {
        RConnection c = null;
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String formattedDateTime = now.format(formatter);

            expressionMatrixFilePath = expressionMatrixFilePath.replace("\\", "/");
            outputPath = outputPath.replace("\\", "/");

            System.out.println("Connecting to Rserve...");
            c = new RConnection("127.0.0.1", 6311);
            System.out.println("Connected to Rserve");

            System.out.println("Loading necessary libraries...");
            c.eval("library(vegan)");
            c.eval("library(ggplot2)");
            c.eval("library(readxl)");

            System.out.println("Reading expression matrix...");
            c.eval("normalized_counts_matrix <- read_excel(\"" + expressionMatrixFilePath + "\", sheet = 1)");

            c.eval("rownames(normalized_counts_matrix) <- normalized_counts_matrix[,1]");
            c.eval("normalized_counts_matrix <- normalized_counts_matrix[,-1]");

            c.eval("normalized_counts_matrix <- as.data.frame(lapply(normalized_counts_matrix, as.numeric))");

            c.eval("logCPM_filtered <- normalized_counts_matrix[, apply(normalized_counts_matrix, 2, var) != 0]");
            c.eval("logCPM_filtered <- logCPM_filtered[apply(logCPM_filtered, 1, var) != 0, ]");
            c.eval("logCPM_filtered <- logCPM_filtered[, colSums(logCPM_filtered) != 0]");
            c.eval("logCPM_filtered <- logCPM_filtered[rowSums(logCPM_filtered) != 0, ]");

            c.eval("bray_curtis_dist <- vegdist(t(logCPM_filtered), method = \"bray\")");

            System.out.println("Performing PCoA analysis...");
            c.eval("pcoa_result <- cmdscale(bray_curtis_dist, eig = TRUE, k = 2)");

            c.eval("pcoa_scores <- as.data.frame(pcoa_result$points)");
            c.eval("colnames(pcoa_scores) <- c('PCoA1', 'PCoA2')");

            System.out.println("Creating PCoA plot...");
            c.eval("pcoa_plot <- ggplot(pcoa_scores, aes(x = PCoA1, y = PCoA2)) + " +
                    "geom_point(size = 5) + " +
                    "theme_minimal() + " +
                    "labs(x = paste0('PCoA1: ', round(pcoa_result$eig[1] / sum(pcoa_result$eig) * 100, 1), '% variance'), " +
                    "y = paste0('PCoA2: ', round(pcoa_result$eig[2] / sum(pcoa_result$eig) * 100, 1), '% variance')) + " +
                    "theme(text = element_text(family = 'Arial', size = 30), " +
                    "axis.title = element_text(size = 30), " +
                    "axis.text = element_text(size = 24))");

            String resultFilePath = outputPath + "/" + formattedDateTime + "_pcoa_plot.svg";
            c.eval("ggsave(\"" + resultFilePath + "\", plot = pcoa_plot, width = 13, height = 7, device = 'svg')");

            System.out.println("PCoA plot saved to " + resultFilePath);

        } catch (RserveException e) {
            e.printStackTrace();
            System.out.println("RserveException occurred: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
                System.out.println("Rserve connection closed.");
            }
        }
    }
}
