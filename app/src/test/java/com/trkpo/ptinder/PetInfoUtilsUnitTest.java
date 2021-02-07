package com.trkpo.ptinder;

import com.trkpo.ptinder.pojo.PetInfo;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static com.trkpo.ptinder.utils.PetInfoUtils.formAge;
import static com.trkpo.ptinder.utils.PetInfoUtils.formatPurposeFromEnum;
import static com.trkpo.ptinder.utils.PetInfoUtils.formatPurposeToEnum;
import static org.junit.Assert.*;

public class PetInfoUtilsUnitTest {
    private Long id;
    private String name;
    private String breed;
    private Integer age;
    private String gender;
    private String animalType;
    private String purpose;
    private String comment;
    private boolean isFavourite;
    private String currentUserId;
    private String ownerId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerEmail;
    private String ownerIconURL;
    private int direction;

    @Before
    public void init() {
        id = Long.valueOf("1");
        name = "Clark";
        breed = "Beagle";
        age = 3;
        gender = "MALE";
        animalType = "Кот";
        purpose = "FRIENDSHIP";
        comment = "No comment";
        isFavourite = true;
        currentUserId = "123456789";
        ownerId = "123456789";
        ownerFirstName = "Vanda";
        ownerLastName = "Maximoff";
        ownerEmail = "User email";
        ownerIconURL = "User icon url";
        direction = 1;
    }

    @Test
    public void formatPurposeFromEnumIsCorrect() {
        assertEquals(formatPurposeFromEnum("NOTHING"), "Не указана");
        assertEquals(formatPurposeFromEnum("WALKING"), "Прогулка");
        assertEquals(formatPurposeFromEnum("FRIENDSHIP"), "Дружба");
        assertEquals(formatPurposeFromEnum("DONORSHIP"), "Переливание крови");
        assertEquals(formatPurposeFromEnum("BREEDING"), "Вязка");
    }

    @Test
    public void formatPurposeToEnumIsCorrect() {
        assertEquals(formatPurposeToEnum("-"), "NOTHING");
        assertEquals(formatPurposeToEnum("Прогулка"), "WALKING");
        assertEquals(formatPurposeToEnum("Дружба"), "FRIENDSHIP");
        assertEquals(formatPurposeToEnum("Переливание крови"), "DONORSHIP");
        assertEquals(formatPurposeToEnum("Вязка"), "BREEDING");
    }

    @Test
    public void formAgeIsCorrect() {
        Integer age = 1;
        assertEquals(formAge(age), age + " год");
        age = 3;
        assertEquals(formAge(age), age + " года");
        age = 20;
        assertEquals(formAge(age), age + " лет");
    }

    @Test
    public void getPetsFromJSONIsCorrect() throws JSONException {
        JSONObject animalTypeJSON = new JSONObject();
        animalTypeJSON.put("id", 1);
        animalTypeJSON.put("type", animalType);

        JSONArray petPhotosJSONArray = new JSONArray();

        JSONObject ownerJSON = new JSONObject();
        ownerJSON.put("googleId", ownerId);
        ownerJSON.put("firstName", ownerFirstName);
        ownerJSON.put("lastName", ownerLastName);
        ownerJSON.put("email", ownerEmail);
        ownerJSON.put("photoUrl", ownerIconURL);

        JSONObject pet = new JSONObject();
        pet.put("petId", id);
        pet.put("name", name);
        pet.put("age", age);
        pet.put("gender", gender);
        pet.put("animalType", animalTypeJSON);
        pet.put("breed", breed);
        pet.put("purpose", purpose);
        pet.put("comment", comment);
        pet.put("petPhotos", petPhotosJSONArray);
        pet.put("owner", ownerJSON);

        JSONArray pets = new JSONArray();
        pets.put(pet);
        Collection<PetInfo> result = PetInfoUtils.getPetsFromJSON(pets.toString(), null, ownerId, direction);
        assertEquals(result.size(), 1);

        for (PetInfo p : result) {
            assertEquals(p.getId(), id);
            assertEquals(p.getName(), name);
            assertEquals(p.getAge(), formAge(age));
            assertEquals(p.getGender(), gender);
            assertEquals(p.getAnimalType(), animalType);
            assertEquals(p.getBreed(), breed);
            assertEquals(p.getPurpose(), formatPurposeFromEnum(purpose));
            assertEquals(p.getComment(), comment);
            assertEquals(p.isFavourite(), isFavourite);

            assertEquals(p.getCurrentUserId(), currentUserId);
            assertEquals(p.getOwnerId(), ownerId);
            assertEquals(p.getOwnerName(), ownerFirstName + " " + ownerLastName);
            assertEquals(p.getOwnerEmail(), ownerEmail);
            assertEquals(p.getOwnerIconURL(), ownerIconURL);

            assertEquals(p.getDirection(), direction);
        }
    }

    @Test
    public void setPetToJSONIsCorrect() throws JSONException {
        JSONObject requestJSON = PetInfoUtils.setPetToJSON(
                name,
                String.valueOf(age),
                gender,
                animalType,
                breed,
                purpose,
                comment,
                null,
                currentUserId);
        JSONObject petJSON = requestJSON.getJSONObject("pet");
        JSONArray photosJSON = requestJSON.getJSONArray("photos");

        assertEquals(requestJSON. getString("type"), animalType);
        assertEquals(requestJSON.getString("googleId"), currentUserId);
        assertEquals(petJSON.getString("name"), name);
        assertEquals(petJSON.getInt("age"), (int)age);
        assertEquals(petJSON.getString("gender"), gender);
        assertEquals(petJSON.getString("type"), animalType);
        assertEquals(petJSON.getString("breed"), breed);
        assertEquals(petJSON.getString("purpose"), purpose);
        assertEquals(petJSON.getString("comment"), comment);
        assertEquals(photosJSON.length(), 0);
    }

    @Test
    public void setPetInfoIsCorrect() {
        PetInfo pet = new PetInfo();
        pet.setId(id);
        pet.setName(name);
        pet.setBreed(breed);
        pet.setAge(formAge(age));
        pet.setGender(gender);
        pet.setAnimalType(animalType);
        pet.setPurpose(formatPurposeFromEnum(purpose));
        pet.setComment(comment);
        pet.setFavourite(isFavourite);
        pet.setDirection(direction);
        pet.setCurrentUserId(currentUserId);
        pet.setOwnerIconURL(ownerIconURL);

        assertEquals(pet.getId(), id);
        assertEquals(pet.getName(), name);
        assertEquals(pet.getAge(), formAge(age));
        assertEquals(pet.getGender(), gender);
        assertEquals(pet.getAnimalType(), animalType);
        assertEquals(pet.getBreed(), breed);
        assertEquals(pet.getPurpose(), formatPurposeFromEnum(purpose));
        assertEquals(pet.getComment(), comment);
        assertEquals(pet.isFavourite(), isFavourite);
        assertEquals(pet.getDirection(), direction);
        assertEquals(pet.getIconsAmount(), 0);

        assertEquals(pet.getCurrentUserId(), currentUserId);
        assertEquals(pet.getOwnerIconURL(), ownerIconURL);
    }
}
