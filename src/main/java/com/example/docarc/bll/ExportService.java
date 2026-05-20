package com.example.docarc.bll;

import com.example.docarc.be.Tiff;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

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
    public void singlePage(double rotation, Tiff[] tiffs) {

        if (!folder.exists()) {
            folder.mkdir();
        }
        for (Tiff t : tiffs) {

            String f = folder.getAbsolutePath()+"/"+t.getReference_id()+".tiff";
            BufferedImage image = t.getConvertedBufferedImage();
            Image fximage =  SwingFXUtils.toFXImage(image, null);
            ImageView imageView = new ImageView(fximage);
            imageView.setRotate(rotation);

            WritableImage writableImage = imageView.snapshot(new SnapshotParameters(), null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
            try {
                ImageIO.write(bufferedImage, "TIFF", new File(f));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void multiPage(double rotation, Tiff[] tiffs) {

        if (!folder.exists()) {
            folder.mkdir();
        }


        Iterator<ImageWriter> writers =
                ImageIO.getImageWritersByFormatName("TIFF");
        ImageWriter writer = writers.next();
        File outputFile = new File(folder + "/" + tiffs[tiffs.length - 1] + "-" + tiffs[0] + ".tiff");
        try {
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile);
            writer.setOutput(ios);
            writer.prepareWriteSequence(null);

            for (Tiff t : tiffs) {
                BufferedImage image = t.getConvertedBufferedImage();
                Image fximage = SwingFXUtils.toFXImage(image, null);
                ImageView imageView = new ImageView(fximage);
                imageView.setRotate(rotation);

                WritableImage writableImage = imageView.snapshot(new SnapshotParameters(), null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                IIOImage IOImg = new IIOImage(bufferedImage, null, null);
                writer.writeToSequence(IOImg, null);
            }
            writer.endWriteSequence();

            ios.close();
            writer.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}