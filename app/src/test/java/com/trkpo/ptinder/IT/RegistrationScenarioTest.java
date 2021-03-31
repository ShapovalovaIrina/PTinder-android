package com.trkpo.ptinder.IT;

import android.os.Build;
import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.USERS_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class RegistrationScenarioTest {
    TestUtils utils = new TestUtils();

    @After
    public void clean() {
        utils.deleteTestUser();
    }

    @Test
    public void userCanRegisterTest() {
        try {
            JSONObject postData = new JSONObject();
            postData.put("firstName", "Ivan");
            postData.put("lastName", "Ivanov");
            postData.put("gender", "MALE");
            postData.put("number", "88005553535");
            postData.put("address", "Karaganda");
            postData.put("email", "ii@m.com");
            postData.put("photoUrl", "");
            postData.put("contactInfoPublic", false);
            postData.put("googleId", "1234");

            new PostRequest().execute(new PostRequestParams(USERS_PATH, postData.toString())).get();

            JSONArray jArray = new JSONArray(utils.getAllUsersInJson());
            JSONObject jsonUser = jArray.getJSONObject(0);
            assertEquals("Ivan", jsonUser.getString("firstName"));
            assertEquals("Ivanov", jsonUser.getString("lastName"));
            assertEquals("MALE", jsonUser.getString("gender"));
            assertEquals("88005553535", jsonUser.getString("number"));
            assertEquals("Karaganda", jsonUser.getString("address"));
            assertEquals("ii@m.com", jsonUser.getString("email"));
            assertEquals("", jsonUser.getString("photoUrl"));
            assertFalse(jsonUser.getBoolean("contactInfoPublic"));
            assertEquals("1234", jsonUser.getString("googleId"));
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.d("VOLLEY", "Not Success response: " + error.toString());
        }
    }

    @Test
    public void testCantAddExistingUser() {
        try {
            JSONObject postData = new JSONObject();
            postData.put("firstName", "Ivan");
            postData.put("lastName", "Ivanov");
            postData.put("gender", "MALE");
            postData.put("number", "88005553535");
            postData.put("address", "Karaganda");
            postData.put("email", "ii@m.com");
            postData.put("photoUrl", "");
            postData.put("contactInfoPublic", false);
            postData.put("googleId", "1234");

            new PostRequest().execute(new PostRequestParams(USERS_PATH, postData.toString())).get();
            new PostRequest().execute(new PostRequestParams(USERS_PATH, postData.toString())).get();

            JSONArray jArray = new JSONArray(utils.getAllUsersInJson());
            assertEquals(1, jArray.length());
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.d("VOLLEY", "Not Success response: " + error.toString());
        }
    }

    @Test
    public void deleteUserTest() {
        utils.addUserWithGoogleId("1234");
        try {
            String url = USERS_PATH + "/" + "1234";
            new DeleteRequest().execute(url).get();
            JSONArray jArray = new JSONArray(utils.getAllUsersInJson());
            assertEquals(0, jArray.length());
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.d("VOLLEY", "Not Success response: " + error.toString());
        }
    }
}
