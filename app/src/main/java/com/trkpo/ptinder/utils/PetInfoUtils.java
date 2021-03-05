package com.trkpo.ptinder.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.trkpo.ptinder.pojo.PetInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.trkpo.ptinder.utils.BitmapUtils.getByteArrayFromBitmap;

public class PetInfoUtils {
    public static JSONObject setPetToJSON(
            String petName,
            String petAge,
            String petGender,
            String petType,
            String petBreed,
            String petPurpose,
            String petComment,
            List<Bitmap> imagesBitmap,
            String googleId) {
        JSONObject requestObject = new JSONObject();
        JSONObject jsonBodyWithPet = new JSONObject();
        try {
            jsonBodyWithPet.put("name", petName);
            jsonBodyWithPet.put("age", Integer.parseInt(petAge));
            jsonBodyWithPet.put("gender", petGender);
            jsonBodyWithPet.put("type", petType);
            jsonBodyWithPet.put("breed", petBreed);
            jsonBodyWithPet.put("purpose", PetInfoUtils.formatPurposeToEnum(petPurpose));
            jsonBodyWithPet.put("comment", petComment);

            JSONArray jsonWithPhotos = new JSONArray();
            if (imagesBitmap != null) {
                for (Bitmap image : imagesBitmap) {
                    byte[] imageBytes = getByteArrayFromBitmap(image);
                    String imageStr = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    JSONObject jsonPhoto = new JSONObject();
                    jsonPhoto.put("photo", imageStr);
                    jsonWithPhotos.put(jsonPhoto);
                }
            }
            requestObject.put("photos", jsonWithPhotos);
            requestObject.put("type", petType);
            requestObject.put("googleId", googleId);
            requestObject.put("pet", jsonBodyWithPet);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestObject;
    }

    public static Collection<PetInfo> getPetsFromJSON(
            String jsonArrayString,
            List<Long> favouritePetsId,
            String googleId,
            Integer direction) throws JSONException {
        Log.d("getPetsFromJSON", jsonArrayString);
        Collection<PetInfo> pets = new ArrayList<>();
        JSONArray jArray = new JSONArray(jsonArrayString);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonObject = jArray.getJSONObject(i);
            Long id = jsonObject.getLong("petId");
            String name = jsonObject.getString("name");
            String breed = jsonObject.getString("breed");
            String age = formAge(jsonObject.getInt("age"));
            String gender = jsonObject.getString("gender");
            String animalType = jsonObject.getJSONObject("animalType").getString("type");
            String purpose = formatPurposeFromEnum(jsonObject.getString("purpose"));
            String comment = jsonObject.getString("comment");
            boolean isFavourite;
            if (favouritePetsId == null) {
                isFavourite = true;
            } else {
                isFavourite = favouritePetsId.contains(id);
            }

            List<Bitmap> icons = new ArrayList<>();
            JSONArray images = jsonObject.getJSONArray("petPhotos");
            for (int j = 0; j < images.length(); j++) {
                String imageStr = images.getJSONObject(j).getString("photo");
                icons.add(BitmapUtils.getBitmapFromByteArray(imageStr));
            }
            PetInfo petInfo = new PetInfo(id, name, breed, age, gender, animalType, purpose, comment, icons, direction, isFavourite);
            pets.add(petInfo);

            JSONObject ownerInfo = jsonObject.getJSONObject("owner");
            String ownerId = ownerInfo.getString("googleId");
            String ownerName = ownerInfo.getString("firstName") + " " + ownerInfo.getString("lastName");
            String ownerEmail = ownerInfo.getString("email");
            String ownerIconURL = ownerInfo.getString("photoUrl");
            petInfo.setOwnerInfo(ownerId, ownerName, ownerEmail, ownerIconURL);

            petInfo.setCurrentUserInfo(googleId);
        }
        return pets;
    }

    public static PetInfo getPetFromJSON(
            String jsonObjectString,
            int direction,
            boolean isFavourite) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonObjectString);
        Long id = jsonObject.getLong("petId");
        String name = jsonObject.getString("name");
        String breed = jsonObject.getString("breed");
        String age = String.valueOf(jsonObject.getInt("age"));
        String gender = jsonObject.getString("gender");
        String animalType = jsonObject.getJSONObject("animalType").getString("type");
        String purpose = jsonObject.getString("purpose");
        String comment = jsonObject.getString("comment");

        List<Bitmap> icons = new ArrayList<>();
        JSONArray images = jsonObject.getJSONArray("petPhotos");
        if (images != null) {
            for (int j = 0; j < images.length(); j++) {
                String imageStr = images.getJSONObject(j).getString("photo");
                byte[] imageBytes = Base64.decode(imageStr, Base64.DEFAULT);
                Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                icons.add(image);
            }
        }
        return new PetInfo(id, name, breed, age, gender, animalType, purpose, comment, icons, direction, isFavourite);
    }

    public static String formAge(Integer age) {
        String year = "";
        switch (age) {
            case 1:
                year = " год";
                break;
            case 2:
            case 3:
            case 4:
                year = " года";
                break;
            default:
                year = " лет";
        }
        return age + year;
    }

    public static String formatPurposeFromEnum(String purpose) {
        switch (purpose) {
            case "NOTHING":
                purpose = "Не указана";
                break;
            case "WALKING":
                purpose = "Прогулка";
                break;
            case "FRIENDSHIP":
                purpose = "Дружба";
                break;
            case "DONORSHIP":
                purpose = "Переливание крови";
                break;
            case "BREEDING":
                purpose = "Вязка";
                break;
        }
        return purpose;
    }

    public static String formatPurposeToEnum(String purpose) {
        switch (purpose) {
            case "Прогулка":
                purpose = "WALKING";
                break;
            case "Вязка":
                purpose = "BREEDING";
                break;
            case "Переливание крови":
                purpose = "DONORSHIP";
                break;
            case "Дружба":
                purpose = "FRIENDSHIP";
                break;
            case "-":
                purpose = "NOTHING";
                break;
        }
        return purpose;
    }
}
