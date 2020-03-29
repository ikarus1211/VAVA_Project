package com.mikpuk.vava_project;
/*
 Neviem ako budeme to budeme riesit ale zatial som si vytvoril vlastnu klasu ktorou si odskusam UI
 */
public class Person {
    private String name;
    private String adress;
    private String itemName;
    private String description;

    public Person(String name, String adress, String itemName, String description) {
        this.name = name;
        this.adress = adress;
        this.itemName = itemName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
