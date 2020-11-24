package com.trkpo.ptinder.pojo;

public class PetInfo {
    private String petName;
    private String petBreed;
    private String petAge;
//    Set to url?
//    private int petIcon;
//    private int petGender;
    public PetInfo(String petName, String petBreed, String petAge) {
        this.petName = petName;
        this.petBreed = petBreed;
        this.petAge = petAge;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }

    public String getPetAge() {
        return petAge;
    }

    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }
}
