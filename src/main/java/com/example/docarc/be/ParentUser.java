package com.example.docarc.be;

public class ParentUser {

    protected int id;
    protected String username;
    protected String password;

    public ParentUser(int id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId(){
        return this.id;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    @Override
    public String toString(){
        return this.username;
    }
}
