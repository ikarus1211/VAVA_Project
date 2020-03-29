package com.mikpuk.vava_project;

public class Item {

    private long id;
    private String name;
    private String description;
    private float longtitude;
    private float latitude;
    private long  used_id;
    private boolean accepted;
    private long type_id;

    public Item(){}

    public Item(long id, String name, String description, float longtitude, float latitude, long used_id, boolean accepted, long type_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.used_id = used_id;
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

    public float getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(float longtitude) {
        this.longtitude = longtitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public long getUsed_id() {
        return used_id;
    }

    public void setUsed_id(long used_id) {
        this.used_id = used_id;
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
}
