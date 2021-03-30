package com.trkpo.ptinder;

import android.util.Log;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SERVER_PATH;

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
}
