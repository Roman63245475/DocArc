package com.example.docarc.be;

import java.util.List;

public class Client {
    private int id;
    private String name;
    private String country;
    private String city;
    private int amountOfEmployees;
    private List<ParentUser> users;

    public Client(int id, String name, String country, String city, int amountOfEmployees){
        this.id = id;
        this.name = name;
        this.country = country;
        this.city = city;
        this.amountOfEmployees = amountOfEmployees;
    }

    public Client(int id, String name){
        this.id = id;
        this.name = name;
    }

    public Client(String name, String country, String city){
        this.name = name;
        this.country = country;
        this.city = city;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getCountry(){
        return this.country;
    }

    public String getCity(){
        return this.city;
    }

    public int getAmountOfEmployees() {
        return this.amountOfEmployees;
    }

    @Override
    public String toString(){
        return this.name;
    }

    public void setUsers(List<ParentUser> usrs){
        this.users = usrs;
    }

    public List<ParentUser> getUsers(){
        return this.users;
    }

}
