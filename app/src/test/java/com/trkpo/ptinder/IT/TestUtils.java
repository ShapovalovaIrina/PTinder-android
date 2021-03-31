package com.trkpo.ptinder.IT;

import android.util.Log;

import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.pojo.PetInfo;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SERVER_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class TestUtils {
    private final String UTILS_PATH = SERVER_PATH + "/utils";

    public void deleteTestUser() {
        String url = UTILS_PATH + "/users";
        try {
            String response = new DeleteRequest().execute(url).get();
            if (!response.equals("")) {
                Log.d("VOLLEY", "Success response (delete all pets)");
            }
        } catch (ExecutionException | InterruptedException error) {
            Log.d("VOLLEY", "Not Success response (delete from favourite): " + error.toString());
        }
    }

    public void deleteAllPets() {
        String url = UTILS_PATH + "/pets";
        try {
            String response = new DeleteRequest().execute(url).get();
            if (!response.equals("")) {
                Log.d("VOLLEY", "Success response (delete all pets)");
            }
        } catch (ExecutionException | InterruptedException error) {
            Log.d("VOLLEY", "Not Success response (delete from favourite): " + error.toString());
        }
    }

    public void addPetForUser(String googleId) {
        JSONObject requestObject = PetInfoUtils.setPetToJSON(
                "Barsik",
                "3",
                "MALE",
                "Кот",
                "",
                "DONORSHIP",
                "",
                null,
                googleId
        );
        final String requestBody = requestObject.toString();
        String url =  PETS_PATH ;
        try {
            String response = new PostRequest().execute(new PostRequestParams(url, requestBody)).get();
            if (!response.equals("")) {
                Log.i("VOLLEY", response);
            }
        } catch (ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Making post request (save pet): request error - " + error.toString());
        }
    }

    public void addUserWithGoogleId(String googleId) {
        try {
            JSONObject postData = new JSONObject();
            postData.put("firstName", "Name");
            postData.put("lastName", "Surname");
            postData.put("gender", "MALE");
            postData.put("number", "-");
            postData.put("address", "Peter");
            postData.put("email", "-");
            postData.put("photoUrl", "https://i.picsum.photos/id/260/200/300.jpg?hmac=_VpBxDn0zencTyMnssCV14LkW80zG7vw2rw7WCQ2uVo");
            postData.put("contactInfoPublic", false);
            postData.put("googleId", googleId);

            String response = new PostRequest().execute(new PostRequestParams(USERS_PATH, postData.toString())).get();
            Log.d("VOLLEY", "Success response: " + response);
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.d("VOLLEY", "Not Success response: " + error.toString());
        }
    }

    public List<PetInfo> getPetsForUser(String googleId) {
        String url =  PETS_PATH + "/owner/" + googleId ;
        try {
            String response = new GetRequest().execute(url).get();
            return (List<PetInfo>) PetInfoUtils.getPetsFromJSON(response, null, googleId, 1);
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<PetInfo> getFavouriteForUser(String googleId) {
        String url = FAVOURITE_PATH + "/user/full/" + googleId;
        try {
            String response = new GetRequest().execute(url).get();
            return (List<PetInfo>) PetInfoUtils.getPetsFromJSON(response, null, googleId, 1);
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String getAllUsersInJson() {
        try {
            return new GetRequest().execute(USERS_PATH).get();
        } catch (ExecutionException | InterruptedException error) {
            error.printStackTrace();
        }
        return "";
    }
}
