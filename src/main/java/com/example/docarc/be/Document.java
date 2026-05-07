package com.example.docarc.be;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private int id;
    private String name;
    private int box_reference;
    private List<Tiff> files;

    public Document(int id, String name, int box_id) {
        this.id = id;
        this.name = name;
        this.box_reference = box_id;
        this.files = new ArrayList<>();
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public int getBoxId(){
        return this.box_reference;
    }

    public List<Tiff> getFiles(){
        return this.files;
    }


    @Override
    public String toString() {
        return name;
    }
}
