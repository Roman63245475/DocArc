package com.example.docarc.be;

import org.apache.commons.logging.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tiff {
    private int id;
    private String fileName;
    private int reference_id;
    private File fileContent;

    public Tiff(String fileName, int reference_id, File fileContent) {
        this.fileName = fileName;
        this.reference_id = reference_id;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public int getReference_id() {
        return reference_id;
    }

    public BufferedImage getConvertedBufferedImage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }
}
