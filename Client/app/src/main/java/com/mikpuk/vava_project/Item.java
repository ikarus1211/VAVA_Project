package com.mikpuk.vava_project;

public class Item {

    private long id;
    private String name;
    private String description;
    private double longitude;
    private double latitude;
    private long  user_id;
    private String user_name;
    private boolean accepted;
    private long type_id;
    private double distance;

    public Item(){}

    public Item(long id, String name, String description, float longtitude, float latitude, long user_id, boolean accepted, long type_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.longitude = longtitude;
        this.latitude = latitude;
        this.user_id = user_id;
        this.accepted = accepted;
        this.type_id = type_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLongtitude() {
        return longitude;
    }

    public void setLongtitude(double longtitude) {
        this.longitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public long getUsed_id() {
        return user_id;
    }

    public void setUsed_id(long used_id) {
        this.user_id = used_id;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public long getType_id() {
        return type_id;
    }

    public void setType_id(long type_id) {
        this.type_id = type_id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
