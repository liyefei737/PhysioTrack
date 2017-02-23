package com.drdc1.medic;

/**
 * Represents a soldier
 */

public class Soldier {
    private String name;
    private String id;
    private String gender;
    private String bodyOrientation;

    public Soldier(String name, String id, String gender, String bodyOrientation) {
        this.name = name;
        this.id = id;
        this.gender = gender;
        this.bodyOrientation = bodyOrientation;
    }

    //getters & setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBodyOrientation() {
        return bodyOrientation;
    }

    public void setBodyOrientation(String bodyOrientation) {
        this.bodyOrientation = bodyOrientation;
    }
}
