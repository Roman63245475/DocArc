package com.example.docarc.be;

public class User extends ParentUser{
    private String specialInfo;

    public User(int id, String username, String password, String specialInfo){
        super(id, username, password);
        this.specialInfo = specialInfo;
    }

    public String getInfo(){
        return this.specialInfo;
    }
}
