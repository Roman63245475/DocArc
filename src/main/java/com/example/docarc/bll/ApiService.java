package com.example.docarc.bll;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ApiService {

    private final String destinationFolder = "zipDocuments";
    private final String stringUrl = "https://studentiffapi-production.up.railway.app/getRandomFile";

    public String unzipFile(String zipFilePath) throws IOException {

        File dir = new File(destinationFolder);
        String filename = "dst/";
        if (!dir.exists()) dir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destinationFolder, entry.getName());
                filename += entry.getName();


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


            System.out.println("Unzipped successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public void deleteZip(String zip) throws IOException {

        Path zipFile = Paths.get(zip);
        try {
            Files.delete(zipFile);

            System.out.println("ZIP deleted");

        } catch (Exception ex) {
            System.out.println("Failed to delete ZIP");
            System.out.println(ex.getMessage());
        }
    }

    public void read(File file) throws IOException {

        //System.out.println("Open Resource File: " + file.getAbsolutePath());
        BufferedImage image = ImageIO.read(file);

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result result = new MultiFormatReader().decode(bitmap);
            System.out.println("Barcode text: " + result.getText());
            System.out.println("Format: " + result.getBarcodeFormat());
            System.out.println(file.getName());
        }
        catch(NotFoundException e) {
            System.out.println("No Barcode found");
        }
    }


        public void getZip() {
            try {
                InputStream in = new URL(this.stringUrl).openStream();
                Path tempFile = Files.createTempFile(Paths.get(this.destinationFolder), "fetched_file", ".zip");
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (MalformedURLException e) {
                System.out.println("http protocol is not secure");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


//        public String getFile(){
//            return filename;
//        }

    public void loadFiles(){
        boolean barCodeFound = false;
         while (!barCodeFound){

         }
    }

}

