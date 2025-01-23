package com.bjfu.springboot.rna_dea.dimensionality_reduction;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PCAanalyze {

    public void PCA(String expressionMatrixFilePath, String outputPath) {
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
            c.eval("library(readxl)");
            c.eval("library(ggplot2)");
            c.eval("library(svglite)");

            System.out.println("Reading expression matrix...");
            c.eval("expression_matrix <- read_excel(\"" + expressionMatrixFilePath + "\", sheet = 1, col_names = TRUE)");

            c.eval("rownames(expression_matrix) <- expression_matrix[[1]]");
            c.eval("expression_matrix <- expression_matrix[, -1]");

            c.eval("logCPM <- log2(as.matrix(expression_matrix) + 1)");

            c.eval("logCPM_filtered <- logCPM[, apply(logCPM, 2, var) != 0]");
            c.eval("logCPM_filtered <- logCPM_filtered[apply(logCPM_filtered, 1, var) != 0, ]");
            c.eval("logCPM_filtered <- logCPM_filtered[, colSums(logCPM_filtered) != 0]");
            c.eval("logCPM_filtered <- logCPM_filtered[rowSums(logCPM_filtered) != 0, ]");

            System.out.println("Performing PCA...");
            c.eval("pca <- prcomp(t(logCPM_filtered), scale. = TRUE)");
            c.eval("pca_data <- as.data.frame(pca$x)");

            System.out.println("Creating PCA plot...");
            c.eval("pca_plot <- ggplot(pca_data, aes(x = PC1, y = PC2)) + " +
                    "geom_point(size = 4) + " +
                    "theme_minimal() + " +
                    "labs(x = paste0('PC1: ', round(summary(pca)$importance[2, 1] * 100, 1), '% variance'), " +
                    "y = paste0('PC2: ', round(summary(pca)$importance[2, 2] * 100, 1), '% variance')) + " +
                    "theme(text = element_text(family = 'Arial', size = 30))");

            String resultFilePath = outputPath + "/" + formattedDateTime + "_pca_plot.svg";
            c.eval("ggsave(\"" + resultFilePath + "\", plot = pca_plot, width = 13, height = 7, device = 'svg')");

            System.out.println("PCA plot saved to " + resultFilePath);

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
