package com.trkpo.ptinder;

import android.util.Log;

import com.trkpo.ptinder.HTTP.DeleteRequest;

import java.util.concurrent.ExecutionException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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

    public void registration() {
        onView(withId(R.id.editPersonCity)).perform(typeText("Peter"));
        onView(withId(R.id.radio_button_female)).perform(click());
        onView(withId(R.id.login_user_info_activity)).perform(swipeUp(), click());
        onView(withId(R.id.editPersonPhone)).perform(typeText("8-800-555-35-35"));
        onView(withId(R.id.login_user_info_activity)).perform(swipeUp(), click());
        onView(withId(R.id.login_submit_button)).perform(click());
    }
}
