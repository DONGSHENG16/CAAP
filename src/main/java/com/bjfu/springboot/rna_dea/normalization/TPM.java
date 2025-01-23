package com.bjfu.springboot.rna_dea.normalization;

import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class TPM {
    @Test
    public void TPM_EC(String expressionMatrixFilePath, String annotationFilePath, String outputDir) {
        RConnection c = null;
        try {
            // Replace backslashes with forward slashes
            expressionMatrixFilePath = expressionMatrixFilePath.replace("\\", "/");
            annotationFilePath = annotationFilePath.replace("\\", "/");
            outputDir = outputDir.replace("\\", "/");

            System.out.println("Starting TPM calculation...");

            // Get the current time
            LocalDateTime now = LocalDateTime.now();
            // Define the date-time format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            // Format the current time as a string
            String formattedDateTime = now.format(formatter);

            // Connect to the local Rserve server, default port is 6311
            System.out.println("Connecting to Rserve...");
            c = new RConnection("127.0.0.1", 6311);
            System.out.println("Connected to Rserve");

            // Execute R code on the R server
            System.out.println("Loading GenomicFeatures library...");
            REXP x = c.eval("library(GenomicFeatures)");
            if (x == null) {
                throw new RserveException(c, "Failed to load GenomicFeatures library");
            }

            // Create TxDb object from GTF file
            System.out.println("Creating TxDb object from GTF file...");
            x = c.eval("gtf_file <- \"" + annotationFilePath + "\"\n" +
                    "txdb <- makeTxDbFromGFF(gtf_file, format = \"gtf\")");
            if (x == null) {
                throw new RserveException(c, "Failed to create TxDb object from GTF file");
            }

            // Extract gene length information
            System.out.println("Extracting gene length information...");
            x = c.eval("gene_lengths <- transcriptLengths(txdb, with.cds_len = TRUE)\n" +
                    "gene_lengths <- data.frame(gene_lengths[, c(\"gene_id\", \"tx_len\")])\n" +
                    "colnames(gene_lengths) <- c(\"GeneID\", \"Length\")");
            if (x == null) {
                throw new RserveException(c, "Failed to extract gene length information");
            }

            // Read expression matrix
            System.out.println("Reading expression matrix...");
            x = c.eval("counts <- read.csv(\"" + expressionMatrixFilePath + "\", row.names = 1)");
            if (x == null) {
                throw new RserveException(c, "Failed to read expression matrix");
            }

            // Ensure gene lengths match the order of genes in the expression matrix
            System.out.println("Matching gene lengths with expression matrix...");
            x = c.eval("gene_lengths <- gene_lengths[match(rownames(counts), gene_lengths$GeneID), ]");
            if (x == null) {
                throw new RserveException(c, "Failed to match gene lengths with expression matrix");
            }

            // Calculate RPK and TPM
            System.out.println("Calculating RPK...");
            x = c.eval("rpk <- counts / gene_lengths$Length");
            if (x == null) {
                throw new RserveException(c, "Failed to calculate RPK");
            }

            System.out.println("Summing RPK values...");
            x = c.eval("rpk_sum <- colSums(rpk)");
            if (x == null) {
                throw new RserveException(c, "Failed to sum RPK values");
            }

            System.out.println("Calculating TPM...");
            x = c.eval("tpm <- t(t(rpk) / rpk_sum) * 1e6");
            if (x == null) {
                throw new RserveException(c, "Failed to calculate TPM");
            }

            // Filter non-zero genes
            System.out.println("Filtering non-zero genes...");
            x = c.eval("nonzero_genes <- rowSums(tpm) > 0\n" +
                    "tpm_filtered <- tpm[nonzero_genes, ]");
            if (x == null) {
                throw new RserveException(c, "Failed to filter non-zero genes");
            }

            // Save the normalized expression matrix
            System.out.println("Saving normalized TPM matrix...");
            x = c.eval("write.csv(tpm, file = \"" + outputDir + "normalized_tpm_matrix_" + formattedDateTime + ".csv\")");
            if (x == null) {
                throw new RserveException(c, "Failed to save normalized TPM matrix");
            }

            System.out.println("TPM calculation completed successfully.");

        } catch (RserveException e) {
            e.printStackTrace();
            System.out.println("An error occurred during TPM calculation: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close(); // Close the Rserve connection
                System.out.println("Rserve connection closed.");
            }
        }
    }
}
