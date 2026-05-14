package com.example.docarc.be;

import java.util.List;

public class Box implements IDataSettable<Document>{
    private int id;
    private String name;
    private User owner;
    List<Document> documents;
    private Profile profile;

    public Box(int id, String name, User owner, Profile profile) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.profile = profile;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public void setData(List<Document> data) {
        this.documents = data;
    }

    public int getId(){
        return this.id;
    }

    public User getOwner(){
        return this.owner;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Document> getDocuments(){
        return this.documents;
    }

}
