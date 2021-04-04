package com.trkpo.ptinder;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SERVER_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class AndroidTestUtils {
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

    public void addPetForUser(Activity activity) {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        String googleId = "";
        if (signInAccount != null) {
            googleId = signInAccount.getId();
        }
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

    public void registration(Activity activity) {
        try {
            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
            String googleId = "";
            if (signInAccount != null) {
                googleId = signInAccount.getId();
            }
            JSONObject postData = new JSONObject();
            postData.put("firstName", "Name");
            postData.put("lastName", "Surname");
            postData.put("gender", "MALE");
            postData.put("number", "-");
            postData.put("address", "Peter");
            postData.put("email", "-");
            postData.put("username", "testUser");
            postData.put("photoUrl", "https://i.picsum.photos/id/260/200/300.jpg?hmac=_VpBxDn0zencTyMnssCV14LkW80zG7vw2rw7WCQ2uVo");
            postData.put("contactInfoPublic", false);
            postData.put("googleId", googleId);

            String response = new PostRequest().execute(new PostRequestParams(USERS_PATH, postData.toString())).get();
            Log.d("VOLLEY", "Success response: " + response);
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.d("VOLLEY", "Not Success response: " + error.toString());
        }
    }
}
