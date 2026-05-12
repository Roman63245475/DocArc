package com.example.docarc.be;

import org.apache.commons.logging.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Tiff extends Data{
    private int id;
    private String fileName;
    private int documentId;
    private int reference_id;
    private byte[] fileContent;
    private File file;
    private final String destinationFolder = "unzippedFiles";

    public Tiff(int id, String fileName, int documentId, int referenceId, byte[] fileContent) {
        this.id = id;
        this.fileName = fileName;
        this.documentId = documentId;
        this.reference_id = referenceId;
        this.fileContent = fileContent;
        convertFileContentToFile();
    }

    private void convertFileContentToFile(){
        this.file = new File(destinationFolder, fileName);
        try (FileOutputStream fos = new FileOutputStream(this.file)) {
            fos.write(fileContent);

        } catch (IOException e) {
            System.out.println("needs to be logged");
        }

    }


    public Tiff(String fileName, int referenceId, File file) {
        this.fileName = fileName;
        this.reference_id = referenceId;
        this.file = file;
    }

    public Tiff(String fileName, int referenceId, BufferedImage processedImage) {
        this.fileName = fileName;
        this.reference_id = referenceId;
        this.file = createFileFromBufferedImage(processedImage, fileName);
    }

    private File createFileFromBufferedImage(BufferedImage image, String fileName) {
        try {
            File imageFile = new File(destinationFolder, fileName + "_processed.png");
            javax.imageio.ImageIO.write(image, "png", imageFile);
            return imageFile;
        } catch (IOException e) {
            System.out.println("Failed to create file from BufferedImage: " + e.getMessage());
            return new File(destinationFolder, fileName);
        }
    }
    public int getId() {
        return id;
    }

    public File getFile() {
        return file;
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

    public void setReference_id(int reference_id) {
        this.reference_id = reference_id;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public byte[] getFileContent() {
        return fileContent;
    }
}
