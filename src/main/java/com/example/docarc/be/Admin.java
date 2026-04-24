package com.example.docarc.be;

import java.util.Objects;

public class Admin extends ParentUser{

    public Admin(int id, String username, String password){
        super(id, username, password);
    }


    @Override
    public boolean equals(Object obj){
        if (obj instanceof Admin){
            Admin comp = (Admin) obj;
            return this.id == comp.id && Objects.equals(this.username, comp.username) && Objects.equals(this.password, comp.password);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public Role getRole(){
        return Role.ADMIN;
    }

}
