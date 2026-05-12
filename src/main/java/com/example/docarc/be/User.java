package com.example.docarc.be;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends ParentUser{
    private String specialInfo;
    private List<Profile> profiles;

    public User(int id, String username, String password, String specialInfo){
        super(id, username, password);
        this.specialInfo = specialInfo;
        this.profiles = new ArrayList<>();
    }

    public User(int id, String username, String password){
        super(id, username, password);
        this.profiles = new ArrayList<>();
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

    @Override
    public Role getRole() {
        return Role.USER;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
}
