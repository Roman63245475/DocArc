package com.example.docarc.bll;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ExportService {
    private File folder = new File(System.getProperty("user.home"),"exported/");
    public void singlePage(BufferedImage[] bufferedImages, int boxid, String ProfileName) {

        if (!folder.exists()) {
            folder.mkdir();
        }

        for (BufferedImage t : bufferedImages) {

            String f = folder.getAbsolutePath()+"/"+ProfileName+boxid+getDate()+".tiff";
            try {
                ImageIO.write(t, "TIFF", new File(f));
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    public void multiPage(BufferedImage[] bufferedImages, int boxid, String ProfileName) {

        if (!folder.exists()) {
            folder.mkdir();
        }


        Iterator<ImageWriter> writers =
                ImageIO.getImageWritersByFormatName("TIFF");
        ImageWriter writer = writers.next();
        File outputFile = new File(folder + "/"+ProfileName+boxid+getDate()+".tiff");
        try {
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile);
            writer.setOutput(ios);
            writer.prepareWriteSequence(null);

            for (BufferedImage t : bufferedImages) {

                IIOImage IOImg = new IIOImage(t, null, null);
                writer.writeToSequence(IOImg, null);
            }
            writer.endWriteSequence();

            ios.close();
            writer.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

        String timestamp = LocalDateTime.now().format(formatter);
        return timestamp;
    }
}