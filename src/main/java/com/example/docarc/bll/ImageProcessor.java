package com.example.docarc.bll;

import com.example.docarc.be.Profile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageProcessor {

    public static BufferedImage applyProfileSettings(BufferedImage originalImage, Profile profile) {
        if (originalImage == null || profile == null) {
            return originalImage;
        }

        BufferedImage processedImage = originalImage;

        // Применяем grayscale если нужно
        if (profile.getGrayscale() != null && profile.getGrayscale()) {
            processedImage = applyGrayscale(processedImage);
        }

        // Применяем яркость
        processedImage = applyBrightness(processedImage, profile.getBrightness());

        // Применяем контраст
        processedImage = applyContrast(processedImage, profile.getContrast());

        return processedImage;
    }

    private static BufferedImage applyGrayscale(BufferedImage image) {
        BufferedImage grayImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );

        Graphics g = grayImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return grayImage;
    }

    private static BufferedImage applyBrightness(BufferedImage image, double brightness) {
        if (brightness == 0.0) {
            return image;
        }

        BufferedImage brightImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                image.getType()
        );

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));

                int red = Math.min(255, Math.max(0, (int) (color.getRed() + brightness * 255)));
                int green = Math.min(255, Math.max(0, (int) (color.getGreen() + brightness * 255)));
                int blue = Math.min(255, Math.max(0, (int) (color.getBlue() + brightness * 255)));

                Color newColor = new Color(red, green, blue);
                brightImage.setRGB(x, y, newColor.getRGB());
            }
        }

        return brightImage;
    }

    private static BufferedImage applyContrast(BufferedImage image, double contrast) {
        if (contrast == 0.0) {
            return image;
        }

        BufferedImage contrastImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                image.getType()
        );

        double factor = (259 * (contrast + 255)) / (255 * (259 - contrast));

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));

                int red = Math.min(255, Math.max(0, (int) (factor * (color.getRed() - 128) + 128)));
                int green = Math.min(255, Math.max(0, (int) (factor * (color.getGreen() - 128) + 128)));
                int blue = Math.min(255, Math.max(0, (int) (factor * (color.getBlue() - 128) + 128)));

                Color newColor = new Color(red, green, blue);
                contrastImage.setRGB(x, y, newColor.getRGB());
            }
        }

        return contrastImage;
    }

//    private static BufferedImage applyRotation(BufferedImage image, double rotation) {
//        if (rotation == 0.0) {
//            return image;
//        }
//
//        double radians = Math.toRadians(rotation);
//
//        // Вычисляем новые размеры изображения после поворота
//        double sin = Math.abs(Math.sin(radians));
//        double cos = Math.abs(Math.cos(radians));
//
//        int newWidth = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
//        int newHeight = (int) Math.floor(image.getWidth() * sin + image.getHeight() * cos);
//
//        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, image.getType());
//
//        Graphics2D g2d = rotatedImage.createGraphics();
//
//        // Устанавливаем качество рендеринга
//        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        // Применяем трансформацию
//        AffineTransform transform = new AffineTransform();
//        transform.translate(newWidth / 2.0, newHeight / 2.0);
//        transform.rotate(radians);
//        transform.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);
//
//        g2d.drawImage(image, transform, null);
//        g2d.dispose();
//
//        return rotatedImage;
//    }
}
