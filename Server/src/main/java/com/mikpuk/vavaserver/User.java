package com.mikpuk.vavaserver;

//Classa, ktora v sebe udrziava udaje o pouzivatelovi
public class User {

    private long id;
    private String username;
    private int reputation;

    public User() {}

    public User(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }
}
