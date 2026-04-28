package com.example.docarc.bll;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

public class FileOperations {
    private String DestinationFolder = "unzipped";
    //private String zipFilePath;

    public void unzipFile(String zipFilePath) {
        File dir = new File(DestinationFolder);
        if (!dir.exists()) dir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(DestinationFolder, entry.getName());

                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // Create parent directories
                    new File(newFile.getParent()).mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }

                zis.closeEntry();
            }

            System.out.println("Unzipped successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<File> checkForBarCodes() {
        File dir = new File(DestinationFolder);
        File[] files = dir.listFiles();

        ArrayList<File> barcodes = new ArrayList<>();
        //if (files == null) return;

        Arrays.stream(files)
                .parallel() // 🔥 parallel processing
                .forEach(file -> {

                    try {
                        BufferedImage image = ImageIO.read(file);
                        if (image == null) return;

                        LuminanceSource source = new BufferedImageLuminanceSource(image);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        MultiFormatReader reader = new MultiFormatReader();

                        Result result = reader.decode(bitmap);

                        barcodes.add(file);
                    } catch (NotFoundException e) {
                        // No barcode → ignore
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
        return barcodes;
    }

}


