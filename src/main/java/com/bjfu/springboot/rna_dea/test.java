package com.bjfu.springboot.rna_dea;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
/**
 * @author DONGSHENG
 * @version 1.0
 */
public class test {
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();

        long startMemory = runtime.totalMemory() - runtime.freeMemory();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        double startCpuLoad = osBean.getProcessCpuLoad();

        long startTime = System.nanoTime();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000000; // 毫微秒
        System.out.println("Time taken: " + duration + "秒");

        long endMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = (endMemory - startMemory)/1000000;
        System.out.println("Memory used: " + memoryUsed + " MB");

        double endCpuLoad = osBean.getProcessCpuLoad();
        System.out.println("CPU Load: " + (endCpuLoad - startCpuLoad) * 100 + " %");
    }
}
