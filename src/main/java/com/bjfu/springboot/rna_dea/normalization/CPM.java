package com.bjfu.springboot.rna_dea.normalization;

import org.junit.Test;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class CPM {
    @Test
    public void CPM_EC(String matrixFilePathVariable, String outputDirVariable) {

        // Replace backslashes with forward slashes
        matrixFilePathVariable = matrixFilePathVariable.replace("\\", "/");
        outputDirVariable = outputDirVariable.replace("\\", "/");

        RConnection c = null;
        try {
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
            System.out.println("Loading edgeR...");
            c.eval("library(edgeR)");

            System.out.println("Reading counts matrix...");
            String command = "counts <- read.csv(\"" + matrixFilePathVariable + "\", row.names = 1)";
            System.out.println(command);
            c.eval(command);
            // Handle missing values (replace NA with 0)
            c.eval("counts[is.na(counts)] <- 0");

            System.out.println("Creating DGEList...");
            c.eval("dge <- DGEList(counts = counts)");

            System.out.println("Calculating CPM...");
            c.eval("cpm_values <- cpm(dge)");

            System.out.println("Saving normalized matrix...");
            c.eval("write.csv(cpm_values, file = \"" + outputDirVariable + "normalized_cpm_matrix" + formattedDateTime + ".csv\")");
            System.out.println("Completed CPM normalization");

        } catch (RserveException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close(); // Close the Rserve connection
            }
        }
    }
}
