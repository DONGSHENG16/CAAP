package com.bjfu.springboot;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import java.lang.reflect.Array;
import java.util.Arrays;

public class test01  {
    public static void main(String[] args) {
        RConnection c = null;
        try {
            
            c = new RConnection();

            c.eval("library(\"DESeq2\")");
 
        } catch (RserveException e) {
            e.printStackTrace();
        } catch (REXPMismatchException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
        }
    }
    }

