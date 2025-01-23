package com.bjfu.springboot.rna_dea.expression_matrix;

import org.junit.Test;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class ExpressionMatrixR {
    @Test
    public void FeatureCountsConstruct(String quantFile1Variable, String quantFile2Variable, String outputDirVariable) {

        RConnection c = null;
        try {
            // Get the current time
            LocalDateTime now = LocalDateTime.now();
            // Define the date-time format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            // Format the current time as a string
            String formattedDateTime = now.format(formatter);
            System.out.println("Current DateTime: " + formattedDateTime);

            // Replace backslashes with forward slashes
            quantFile1Variable = quantFile1Variable.replace("\\", "/");
            quantFile2Variable = quantFile2Variable.replace("\\", "/");
            outputDirVariable = outputDirVariable.replace("\\", "/");

            // Connect to the local Rserve server, default port is 6311
            System.out.println("Connecting to Rserve...");
            c = new RConnection("127.0.0.1", 6311);
            System.out.println("Connected to Rserve");

            // Execute R code on the R server
            System.out.println("Reading and merging counts files...");
            System.out.println(quantFile1Variable);
            String commad1 = "gene_counts1 <- read.table(\"" + quantFile1Variable + "\", header = TRUE, skip = 1, fill = TRUE)";
            System.out.println("commad1:" + commad1);
            c.eval(commad1);
            String commad2 = "counts2 <- read.table(\"" + quantFile2Variable + "\", header = TRUE, skip = 1, fill = TRUE)";
            System.out.println("commad2:" + commad2);
            c.eval(commad2);
            String commad3 = "gene_counts1_selected <- gene_counts1[, c(1, 7)]";
            c.eval(commad3);
            c.eval("counts2_selected <- counts2[, 7, drop = FALSE]");

            c.eval("merged_counts <- cbind(gene_counts1_selected, counts2_selected)");

            c.eval("colnames(merged_counts) <- c(\"genid\", \"sample1\", \"sample2\")");

            //c.eval("merged_counts <- merged_counts[!(merged_counts[, 2] == 0 & merged_counts[, 3] == 0), ]");

            //c.eval("write.csv(merged_counts, \"" + outputDirVariable + "merged_counts_featurecounts" + formattedDateTime + ".csv\", row.names = FALSE");
            c.eval("write.csv(merged_counts, \"" + outputDirVariable + "merged_counts_featurecounts" + formattedDateTime + ".csv\", row.names = FALSE)");
            System.out.println("Counts files merged and saved to merged_counts_" + formattedDateTime + ".csv");
            System.out.println("Successfully merged columns and saved to merged_counts.csv");

        } catch (RserveException e) {
            System.err.println("RserveException caught: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (c != null) {
                System.out.println("Closing Rserve connection...");
                c.close(); // Close the Rserve connection
                System.out.println("Rserve connection closed.");
            }
        }
    }

    public void StringtieConstruct(String quantFile1Variable, String quantFile2Variable, String outputDirVariable, String option) {
        RConnection c = null;
        try {
            // Get the current time
            LocalDateTime now = LocalDateTime.now();
            // Define the date-time format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            // Format the current time as a string
            String formattedDateTime = now.format(formatter);
            System.out.println("Current DateTime: " + formattedDateTime);

            // Replace backslashes with forward slashes
            quantFile1Variable = quantFile1Variable.replace("\\", "/");
            quantFile2Variable = quantFile2Variable.replace("\\", "/");
            outputDirVariable = outputDirVariable.replace("\\", "/");

            // Connect to the local Rserve server, default port is 6311
            System.out.println("Connecting to Rserve...");
            c = new RConnection("127.0.0.1", 6311);
            System.out.println("Connected to Rserve");

            // Execute R code on the R server
            System.out.println("Reading counts1.txt...");
            String command1 = "counts1 <- read.table(\"" + quantFile1Variable + "\", header = TRUE, skip = 1, fill = TRUE)";
            c.eval(command1);
            System.out.println("Reading counts2.txt...");
            String command2 = "counts2 <- read.table(\"" + quantFile2Variable + "\", header = TRUE, skip = 1, fill = TRUE)";
            c.eval(command2);

            System.out.println("Processing selected columns based on option...");
            switch (option) {
                case "FPKM":
                    c.eval("counts1_selected <- counts1[, c(1, ncol(counts1) - 1)]");
                    c.eval("counts2_selected <- counts2[, ncol(counts2) - 1, drop = FALSE]");
                    System.out.println("Option FPKM selected.");
                    break;
                case "TPM":
                    c.eval("counts1_selected <- counts1[, c(1, ncol(counts1))]");
                    c.eval("counts2_selected <- counts2[, ncol(counts2), drop = FALSE]");
                    System.out.println("Option TPM selected.");
                    break;
                default:
                    System.err.println("Invalid option selected.");
                    return;
            }

            System.out.println("Merging selected columns...");
            String command3 = "merged_counts <- cbind(counts1_selected, counts2_selected)";
            c.eval(command3);
            c.eval("colnames(merged_counts) <- c(\"genid\", \"sample1\", \"sample2\")");
            System.out.println("Saving merged counts to CSV file...");
            c.eval("write.csv(merged_counts, \"" + outputDirVariable + "merged_counts" + formattedDateTime + ".csv\", row.names = FALSE)");
            System.out.println("Merged counts saved to merged_counts" + formattedDateTime + ".csv");

        } catch (RserveException e) {
            System.err.println("RserveException caught: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (c != null) {
                System.out.println("Closing Rserve connection...");
                c.close(); // Close the Rserve connection
                System.out.println("Rserve connection closed.");
            }
        }
    }

    public void HtseqConstruct(String quantFile1Variable, String quantFile2Variable, String outputDirVariable) {
        RConnection c = null;
        try {
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            // 定义日期时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            // 将当前时间格式化为字符串
            String formattedDateTime = now.format(formatter);
            System.out.println("Current DateTime: " + formattedDateTime);

            // 将反斜杠 \ 替换为正斜杠 /
            quantFile1Variable = quantFile1Variable.replace("\\", "/");
            quantFile2Variable = quantFile2Variable.replace("\\", "/");
            outputDirVariable = outputDirVariable.replace("\\", "/");

            // 连接到本地的 Rserve 服务器，默认端口为 6311
            System.out.println("Connecting to Rserve...");
            c = new RConnection("127.0.0.1", 6311);
            System.out.println("Connected to Rserve");

            // 构建读取第一个定量文件的R命令
            String command1 = "gene_counts1 <- read.table(\"" + quantFile1Variable + "\", header = TRUE, skip = 1, fill = TRUE)";
            // 在 R 服务器上执行读取第一个定量文件的R代码
            c.eval(command1);

            // 构建读取第二个定量文件的R命令
            String command2 = "gene_counts2 <- read.table(\"" + quantFile2Variable + "\", header = TRUE, skip = 1, fill = TRUE)";
            // 在 R 服务器上执行读取第二个定量文件的R代码
            c.eval(command2);

            // 从第一个定量文件中选择前两列
            c.eval("gene_counts1_selected <- gene_counts1[, c(1, 2)]");

            // 从第二个定量文件中选择第二列，并保持为数据框格式
            c.eval("gene_counts2_selected <- gene_counts2[, 2, drop = FALSE]");

            // 合并两个定量文件的数据
            c.eval("merged_counts <- cbind(gene_counts1_selected, gene_counts2_selected)");

            // 设置合并后的列名
            c.eval("colnames(merged_counts) <- c(\"genid\", \"sample1\", \"sample2\")");

            // 移除样本1和样本2都为0的行
            //c.eval("merged_counts <- merged_counts[!(merged_counts[, 2] == 0 & merged_counts[, 3] == 0), ]");

            // 将合并后的数据写入CSV文件
            c.eval("write.csv(merged_counts, \"" + outputDirVariable + "merged_counts_htseq" + formattedDateTime + ".csv\", row.names = FALSE)");

        } catch (RserveException e) {
            System.err.println("RserveException caught: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (c != null) {
                System.out.println("Closing Rserve connection...");
                c.close(); // 关闭 Rserve 连接
                System.out.println("Rserve connection closed.");
            }
        }
    }
}
