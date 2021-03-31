package com.trkpo.ptinder.unit.pojo;

import android.graphics.Bitmap;

import com.trkpo.ptinder.pojo.PetInfo;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PetInfoTest {
    @Test
    public void petInfoEmptyConstructor() {
        PetInfo petInfo = new PetInfo();
        assertEquals(Long.valueOf("0"), petInfo.getId());
        assertEquals("", petInfo.getName());
        assertEquals("", petInfo.getBreed());
        assertEquals("", petInfo.getAge());
        assertEquals("", petInfo.getGender());
        assertEquals("", petInfo.getAnimalType());
        assertEquals("", petInfo.getPurpose());
        assertEquals("", petInfo.getComment());
        assertEquals(new ArrayList<>(), petInfo.getIcons());
        assertEquals(0, petInfo.getDirection());
        assertFalse(petInfo.isFavourite());
    }

    @Test
    public void petInfoParametrizedConstructor() {
        Long id = Long.valueOf("1");
        String name = "Name";
        String breed = "Breed";
        String age = "Age";
        String gender = "Gender";
        String animalType = "AnimalType";
        String purpose = "Purpose";
        String comment = "Comment";
        List<Bitmap> icons = new ArrayList<>();
        Bitmap mockBitmap = Mockito.mock(Bitmap.class);
        icons.add(mockBitmap);
        int direction = 1;
        boolean isFavourite = false;
        PetInfo petInfo = new PetInfo(id, name, breed, age, gender, animalType, purpose, comment, icons, direction, isFavourite);
        assertEquals(id, petInfo.getId());
        assertEquals(name, petInfo.getName());
        assertEquals(breed, petInfo.getBreed());
        assertEquals(age, petInfo.getAge());
        assertEquals(gender, petInfo.getGender());
        assertEquals(animalType, petInfo.getAnimalType());
        assertEquals(purpose, petInfo.getPurpose());
        assertEquals(comment, petInfo.getComment());
        assertEquals(icons, petInfo.getIcons());
        assertEquals(direction, petInfo.getDirection());
        assertEquals(isFavourite, petInfo.isFavourite());
    }

    @Test
    public void correctSetPetInfoParameters() {
        Long id = Long.valueOf("1");
        String name = "Name";
        String breed = "Breed";
        String age = "Age";
        String gender = "Gender";
        String animalType = "AnimalType";
        String purpose = "Purpose";
        String comment = "Comment";
        List<Bitmap> icons = new ArrayList<>();
        Bitmap mockBitmap = Mockito.mock(Bitmap.class);
        icons.add(mockBitmap);
        int direction = 1;
        boolean isFavourite = false;

        String ownerId = "ownerId";
        String ownerName = "ownerName";
        String ownerEmail = "ownerEmail";
        String ownerIconURL = "ownerIconURL";

        PetInfo petInfo = new PetInfo();

        petInfo.setId(id);
        petInfo.setName(name);
        petInfo.setBreed(breed);
        petInfo.setAge(age);
        petInfo.setGender(gender);
        petInfo.setAnimalType(animalType);
        petInfo.setPurpose(purpose);
        petInfo.setComment(comment);
        petInfo.setIcons(icons);
        petInfo.setDirection(direction);
        petInfo.setFavourite(isFavourite);
        petInfo.setCurrentUserId(ownerId);
        petInfo.setOwnerIconURL(ownerIconURL);
        petInfo.setOwnerInfo(ownerId, ownerName, ownerEmail, ownerIconURL);

        assertEquals(id, petInfo.getId());
        assertEquals(name, petInfo.getName());
        assertEquals(breed, petInfo.getBreed());
        assertEquals(gender, petInfo.getGender());
        assertEquals(animalType, petInfo.getAnimalType());
        assertEquals(purpose, petInfo.getPurpose());
        assertEquals(comment, petInfo.getComment());
        assertEquals(icons, petInfo.getIcons());
        assertEquals(direction, petInfo.getDirection());
        assertEquals(isFavourite, petInfo.isFavourite());
        assertEquals(mockBitmap, petInfo.getIcon(0));

        assertEquals(ownerId, petInfo.getOwnerId());
        assertEquals(ownerName, petInfo.getOwnerName());
        assertEquals(ownerEmail, petInfo.getOwnerEmail());
        assertEquals(ownerIconURL, petInfo.getOwnerIconURL());
        assertEquals(ownerId, petInfo.getCurrentUserId());

        petInfo.addIcon(mockBitmap);
    }
}