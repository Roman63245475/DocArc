package com.example.docarc.be;

public abstract class ParentUser {

    protected int id;
    protected String username;
    protected String password;
    private boolean isActive;

    public ParentUser(int id, String username, String password, boolean isActive){
        this.id = id;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
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

    public boolean isUserActive() {
        return isActive;
    }

    @Override
    public String toString(){
        return this.username;
    }

    public abstract Role getRole();
}
