package com.trkpo.ptinder.pojo;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PetInfo implements Serializable {
    private Long id;
    private String name;
    private String breed;
    private String age;
    private String gender;
    private String animalType;
    private String purpose;
    private String comment;
    private List<Bitmap> icons;
    private boolean isFavourite;

    private String currentUserId;

    private String ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerIconURL;

    // if dir = 1, then we want to go from user profile fragment to pet card
    // if dir = 2, then we want to go from search fragment to pet card
    // if dir = 3, then we want to go from favourite fragment to pet card
    // if dir = 4, then we want to go from other user profile fragment to pet card
    private int direction;

    public PetInfo(Long id, String name, String breed, String age, String gender, String animalType, String purpose, String comment, List<Bitmap> icons, int direction, boolean isFavourite) {
        this.id = id;
        this.name = name.isEmpty() ? "-" : name;
        this.breed = breed.isEmpty() ? "-" : breed;
        this.age = age.isEmpty() ? "-" : age;
        this.gender = gender.isEmpty() ? "-" : gender;
        this.animalType = animalType.isEmpty() ? "-" : animalType;
        this.purpose = purpose.isEmpty() ? "-" : purpose;
        this.comment = comment.isEmpty() ? "-" : comment;
        this.icons = new ArrayList<>(icons);
        this.direction = direction;
        this.isFavourite = isFavourite;
    }

    public PetInfo() {
        this.id = Long.valueOf("0");
        this.name = "";
        this.breed = "";
        this.age = "";
        this.gender = "";
        this.animalType = "";
        this.purpose = "";
        this.comment = "";
        this.icons = new ArrayList<>();
        this.direction = 0;
        this.isFavourite = false;
    }

    public void setCurrentUserInfo(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setOwnerInfo(String ownerId, String ownerName, String ownerEmail, String ownerIconURL) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.ownerIconURL = ownerIconURL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Bitmap> getIcons() {
        return icons;
    }

    public void setIcons(List<Bitmap> icons) {
        this.icons = icons;
    }

    public int getIconsAmount() {
        return icons.size();
    }

    public Bitmap getIcon(int i) {
        return icons.get(i);
    }

    public void addIcon(Bitmap icon) {
        icons.add(icon);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getOwnerIconURL() {
        return ownerIconURL;
    }

    public void setOwnerIconURL(String ownerIconURL) {
        this.ownerIconURL = ownerIconURL;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
}
