package com.example.docarc.be;

import java.util.ArrayList;
import java.util.List;

public class Document extends Data implements IDataSettable<Tiff> {
    private int id;
    private String name;
    private int box_reference;
    private int amountOfFiles;
    private List<Tiff> files;

    public Document(int id, String name, int box_id, int amountOfFiles) {
        this.id = id;
        this.name = name;
        this.box_reference = box_id;
        this.amountOfFiles = amountOfFiles;
        this.files = new ArrayList<>();
    }

    public Document(String fileName, int boxId, List<Tiff> files){
        this.name = fileName;
        this.box_reference = boxId;
        this.files = files;
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

    public int getAmountOfFiles(){
        return this.amountOfFiles;
    }


}
