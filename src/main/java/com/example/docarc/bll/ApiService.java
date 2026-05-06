package com.example.docarc.bll;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ApiService {
    private final String tempZipDocuments = "zipDocuments";
    private final String destinationFolder = "unzippedFiles";
    private final String stringUrl = "https://studentiffapi-production.up.railway.app/getRandomFile";

    public File unzipFile(File fetchedZipFile) {

        File dir = new File(destinationFolder);
        if (!dir.exists()) dir.mkdirs();


        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(fetchedZipFile.toPath()))) {
            ZipEntry entry;
            File newFile = null;
            while ((entry = zis.getNextEntry()) != null) {
                 newFile = new File(destinationFolder, entry.getName());


                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                }

                zis.closeEntry();

            }
            zis.close();
            return newFile;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void deleteZip(File zipFile) {
        String fileName = zipFile.getName();
        Path filePath = zipFile.toPath();
        try {

            Files.delete(filePath);

            System.out.println(fileName + " was deleted");

        } catch (Exception ex) {
            zipFile.delete();
        }
    }

    public BufferedImage convert(File file) throws IOException {

        //System.out.println("Open Resource File: " + file.getAbsolutePath());
        return null;
    }


        public File getZip() {
            try {
                File file = new File(this.tempZipDocuments);
                if  (!file.exists()) {
                    file.mkdirs();
                }
                InputStream in = new URL(this.stringUrl).openStream();
                Path tempFile = Files.createTempFile(Paths.get(this.tempZipDocuments), "fetched_file", ".zip");
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                return tempFile.toFile();
            } catch (MalformedURLException e) {
                System.out.println("http protocol is not secure");
                e.printStackTrace();
                System.out.println("this");
                throw new RuntimeException(e);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("this");
                throw new RuntimeException(e);
            }
        }


//        public String getFile(){
//            return filename;
//        }

    private boolean hasBarCode(BufferedImage img){
        LuminanceSource source = new BufferedImageLuminanceSource(img);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            new MultiFormatReader().decode(bitmap);
            return true;
        }
        catch(NotFoundException e) {
            return false;
        }
    }

    public List<BufferedImage> loadFiles() {
        boolean barCodeFound = false;
        List<BufferedImage> files = new ArrayList<>();
        try {
            while (!barCodeFound){
                File fetchedZipFile = getZip();
                File unzippedFile = unzipFile(fetchedZipFile);
                deleteZip(fetchedZipFile);
                BufferedImage convertedFile = ImageIO.read(unzippedFile);
                files.add(convertedFile);
                barCodeFound = hasBarCode(convertedFile);
            }
            return files;
        }
        catch (IOException ex){
            throw new RuntimeException(ex);
        }

    }

}

