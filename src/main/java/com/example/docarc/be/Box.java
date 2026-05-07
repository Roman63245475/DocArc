package com.example.docarc.be;

public class Box {
    private int id;
    private String name;
    private User owner;

    public Box(int id, String name, User owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public String getName(){
        return this.name;
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

}
