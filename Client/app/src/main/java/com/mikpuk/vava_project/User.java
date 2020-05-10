package com.mikpuk.vava_project;

import java.io.Serializable;

/**
 * This class is use to store data about user
 */
public class User implements Serializable {

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
