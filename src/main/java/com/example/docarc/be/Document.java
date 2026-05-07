package com.example.docarc.be;

import java.util.ArrayList;
import java.util.List;

public class Document extends Data implements IDataSettable<Tiff> {
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

    @Override
    public void setData(List<Tiff> data) {
        this.files = data;
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

    public String getCreatedDate(){
        return "07/05/2026";
    }


}
