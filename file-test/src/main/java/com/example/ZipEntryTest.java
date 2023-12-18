package com.example;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/11/24 14:10
 */
public class ZipEntryTest {

    public static void main(String[] args) throws Exception{
        FileOutputStream fileOutputStream = new FileOutputStream("/Users/demussong/Documents/txt/out.zip");

        String fileName1 = "/Users/demussong/Documents/txt/12.txt";
        FileInputStream fileInputStream1 = new FileInputStream(fileName1);
        String fileName2 = "/Users/demussong/Documents/txt/34.txt";
        FileInputStream fileInputStream2 = new FileInputStream(fileName2);

        try (ZipOutputStream zos = new ZipOutputStream(fileOutputStream)) {
            zos.putNextEntry(new ZipEntry("12.txt"));
            copy(fileInputStream1, zos);
            zos.closeEntry();
            zos.flush();

            zos.putNextEntry(new ZipEntry("34.txt"));
            copy(fileInputStream2, zos);
            zos.closeEntry();
            zos.flush();

            zos.putNextEntry(new ZipEntry("下载详情.txt"));
            ByteArrayInputStream bais = new ByteArrayInputStream("两个文件".getBytes());
            copy(bais, zos);
            zos.closeEntry();
            zos.flush();
        }


    }

    public static int copy(InputStream in, OutputStream out) throws IOException {


        int byteCount = 0;
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }
}
