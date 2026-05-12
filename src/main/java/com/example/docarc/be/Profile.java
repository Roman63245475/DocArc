package com.example.docarc.be;

public class Profile {
    private int id;
    private String name;
    private double brightness;
    private double contrast;
    private double rotation;
    private Boolean grayscale;


    public Profile(int id, String name, double brightness, double contrast, double rotation, Boolean greyscale) {
        this.id = id;
        this.name = name;
        this.brightness = brightness;
        this.contrast = contrast;
        this.rotation = rotation;
        this.grayscale = greyscale;
    }

    public Profile(String name, double brightness, double contrast, double rotation, Boolean greyscale) {
        this.name = name;
        this.brightness = brightness;
        this.contrast = contrast;
        this.rotation = rotation;
        this.grayscale = greyscale;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    public double getContrast() {
        return contrast;
    }

    public void setContrast(double contrast) {
        this.contrast = contrast;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public Boolean getGrayscale() {
        return grayscale;
    }

    public void setGreyscale(Boolean grayscale) {
        this.grayscale = grayscale;
    }
}