package com.bjfu.springboot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.util.FileCopyUtils.BUFFER_SIZE;

/**
 * @author DONGSHENG
 * @version 1.0
 */
public class ZipUTils {

    public static void toZip(List<File> srcFiles, File zipFile) throws IOException {
        if(zipFile == null){
            return;
        }
        if(!zipFile.getName().endsWith(".zip")){
            return;
        }
        ZipOutputStream zos = null;
        FileOutputStream out = new FileOutputStream(zipFile);
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;

                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                    zos.flush();
                }
                in.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (zos != null) {
                zos.close();
            }
        }
    }
}
