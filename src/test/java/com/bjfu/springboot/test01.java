package com.bjfu.springboot;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import java.lang.reflect.Array;
import java.util.Arrays;

public class test01  {
    public static void main(String[] args) {
        RConnection c = null;
        try {
            // 连接到本地的 Rserve 服务器，默认端口为 6311
            c = new RConnection();

            // 在 R 服务器上执行 R 代码
            c.eval("library(\"DESeq2\")");
            c.eval("merged_counts <- read.csv(\"C:/Users/19605/Desktop/bioinformation/merged_counts_matrix.csv\",row.names = 1)");
            c.eval("mymeta <- read.csv(\"C:/Users/19605/Desktop/bioinformation/mymeta.csv\",stringsAsFactors = T)");
            c.eval("dds <- DESeqDataSetFromMatrix(countData = merged_counts,colData = mymeta,design = ~dex)");
            c.eval("print(dds)");
            c.eval("dds <- DESeq(dds)");

            c.eval("res <- results(dds)");
            c.eval("png(\"C:/Users/19605/Desktop/bioinformation/MA_plot4.png\", width=800, height=600)\n" +
                    "plotMA(res, ylim=c(-5,5))\n" +
                    "dev.off()");
            String[] res = c.eval("print(colnames(merged_counts))").asStrings();
            System.out.println(res[1]);

        } catch (RserveException e) {
            e.printStackTrace();
        } catch (REXPMismatchException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close(); // 关闭 Rserve 连接
            }
        }
    }
    }

