package com.bjfu.springboot.rna_dea.dimensionality_reduction;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UMAPanalyze {

    public void UMAP(String expressionMatrixFilePath, String groupinfo, String outputPath) {
        RConnection c = null;
        try {
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String formattedDateTime = now.format(formatter);

            // 将反斜杠 \ 替换为正斜杠 /
            expressionMatrixFilePath = expressionMatrixFilePath.replace("\\", "/");
            groupinfo = groupinfo.replace("\\", "/");
            outputPath = outputPath.replace("\\", "/");

            // 连接到本地的 Rserve 服务器
            System.out.println("Connecting to Rserve...");
            c = new RConnection("127.0.0.1", 6311);
            System.out.println("Connected to Rserve");

            // 加载 R 包和执行 UMAP 分析
            System.out.println("Loading necessary libraries...");
            c.eval("if (!requireNamespace('uwot', quietly = TRUE)) { install.packages('uwot') }");
            c.eval("library(uwot)");
            c.eval("library(ggplot2)");

            // 读取归一化计数数据文件
            System.out.println("Reading normalized counts matrix...");
            c.eval("normalized_counts_matrix <- read.csv(\"" + expressionMatrixFilePath + "\", row.names = 1)");

            // 读取样本元数据文件
            System.out.println("Reading metadata...");
            c.eval("metadata <- read.csv(\"" + groupinfo + "\")");

            // 确保元数据中的样本顺序与计数数据中的样本顺序一致
            c.eval("sample_order <- colnames(normalized_counts_matrix)");
            c.eval("metadata <- metadata[match(sample_order, metadata$id), ]");

            // 执行 UMAP 降维分析，设置 n_neighbors 为 3
            System.out.println("Performing UMAP...");
            c.eval("umap_results <- umap(t(normalized_counts_matrix), n_neighbors = 3)");

            // 创建数据框以便绘图
            c.eval("umap_data <- as.data.frame(umap_results)");
            c.eval("colnames(umap_data) <- c('UMAP1', 'UMAP2')");
            c.eval("umap_data$Group <- metadata$dex");

            // 绘制 UMAP 图
            System.out.println("Creating UMAP plot...");
            c.eval("umap_plot <- ggplot(umap_data, aes(x = UMAP1, y = UMAP2, color = Group)) + " +
                    "geom_point(size = 4) + " +
                    "theme_minimal() + " +
                    "theme(text = element_text(family = 'Arial', size = 20))");

            // 保存 UMAP 图为 SVG 格式
            String resultFilePath = outputPath + "/" + formattedDateTime + "_umap_plot.svg";
            c.eval("ggsave(\"" + resultFilePath + "\", plot = umap_plot, width = 13, height = 7, device = 'svg')");

            System.out.println("UMAP plot saved to " + resultFilePath);

        } catch (RserveException e) {
            e.printStackTrace();
            System.out.println("RserveException occurred: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();  // 关闭 Rserve 连接
                System.out.println("Rserve connection closed.");
            }
        }
    }
}
