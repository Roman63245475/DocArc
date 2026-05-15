package com.example.docarc.be;

public class Profile {
    private Integer id;
    private String name;
    private double brightness;
    private double contrast;
    private Boolean grayscale;


    public Profile(int id, String name, double brightness, double contrast, Boolean greyscale) {
        this.id = id;
        this.name = name;
        this.brightness = brightness;
        this.contrast = contrast;
        this.grayscale = greyscale;
    }

    public Profile(String name){
        this.id = null;
        this.name = name;
    }

    public Profile(int id, String name){
        this.id = id;
        this.name = name;
    }

    public Profile(String name, double brightness, double contrast, Boolean greyscale) {
        this.name = name;
        this.brightness = brightness;
        this.contrast = contrast;
        this.grayscale = greyscale;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRawBrightness() {
        return brightness;
    }

    public double getBrightness() {
        return brightness / 100;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    public double getRawContrast() {
        return contrast;
    }

    public double getContrast() {
        return contrast / 100;
    }

    public void setContrast(double contrast) {
        this.contrast = contrast;
    }

    public Boolean getGrayscale() {
        return grayscale;
    }

    public void setGreyscale(Boolean grayscale) {
        this.grayscale = grayscale;
    }

    @Override
    public String toString() {
        return this.name;
    }
}