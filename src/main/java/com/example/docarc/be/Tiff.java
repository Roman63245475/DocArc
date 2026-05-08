package com.example.docarc.be;

import org.apache.commons.logging.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tiff extends Data{
    private int id;
    private String fileName;
    private int documentId;
    private int reference_id;
    private String fileContent;
    private File file;

    public Tiff(int id, String fileName, int documentId, int referenceId, String fileContent) {
        this.id = id;
        this.fileName = fileName;
        this.documentId = documentId;
        this.reference_id = referenceId;
        this.fileContent = fileContent;
    }

    public Tiff(String fileName, int referenceId, File file) {
        this.fileName = fileName;
        this.reference_id = referenceId;
        this.file = file;
    }
    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public int getReference_id() {
        return reference_id;
    }

    public BufferedImage getConvertedBufferedImage() {
        try {
            BufferedImage image = ImageIO.read(this.file);
            return image;
        }
        catch (IOException e) {
            System.out.println("file couldn't have been converted to bufferedImage");
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
    }

    public int getDocumentId() {
        return documentId;
    }

    @Override
    public String toString() {
        return this.fileName;
    }

}
