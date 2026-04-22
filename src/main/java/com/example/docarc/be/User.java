package com.example.docarc.be;

import java.util.Objects;

public class User extends ParentUser{
    private String specialInfo;

    public User(int id, String username, String password, String specialInfo){
        super(id, username, password);
        this.specialInfo = specialInfo;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof User){
            User comp = (User) obj;
            return this.id == comp.id && Objects.equals(this.username, comp.username) && Objects.equals(this.password, comp.password) && Objects.equals(this.specialInfo, comp.specialInfo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public String getInfo(){
        return this.specialInfo;
    }
}
