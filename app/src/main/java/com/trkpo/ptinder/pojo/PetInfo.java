package com.trkpo.ptinder.pojo;

import android.graphics.Bitmap;

public class PetInfo {
    private String name;
    private String breed;
    private String age;
    private String gender;
    private String purpose;
    private String comment;
    private Bitmap icon;

    public PetInfo(String name, String breed, String age, String gender, String purpose, String comment) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.purpose = purpose;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String gender) {
        this.purpose = purpose;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
