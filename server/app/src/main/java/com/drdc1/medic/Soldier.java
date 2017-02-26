package com.drdc1.medic;

import java.util.List;

/**
 * Represents a soldier
 */

public class Soldier {
    private String name;
    private String id;
    private String gender;
    private String bodyOrientation;

    private int overallWellnessIndex;

    // I used List for now, I am thinking List<Integer> will work well with graphes. If not, change it to whatever type that works well..
    private List<Integer> heartRate;
    private List<Integer> breathingRate;
    private List<Integer> skinTmp;
    private List<Integer> coreTmp;
    private int fatigueLevel;

    public Soldier(String name, String id, String gender) {
        this.name = name;
        this.id = id;
        this.gender = gender;
    }

    /*********************
     * getters & setters
     * Free free to reorder them
     **********************/
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

    public List<Integer> getHeartRate() {
        return heartRate;
    }

    public List<Integer> getBreathingRate() {
        return breathingRate;
    }

    public List<Integer> getSkinTmp() {
        return skinTmp;
    }

    public List<Integer> getCoreTmp() {
        return coreTmp;
    }

    public int getFatigueLevel() {
        return fatigueLevel;
    }
}
