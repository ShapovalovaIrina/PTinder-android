package com.trkpo.ptinder.IT;

import android.os.Build;
import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.USERS_PATH;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class RegistrationScenarioTest {

    @After
    public void clean() {
        new TestUtils().deleteTestUser();
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

            String url = USERS_PATH + "/" + "1234";
            String response = new GetRequest().execute(url).get();
            JSONObject jsonResponse = new JSONObject(response);
            assertEquals("Ivan", jsonResponse.getString("firstName"));
            assertEquals("Ivanov", jsonResponse.getString("lastName"));
            assertEquals("MALE", jsonResponse.getString("gender"));
            assertEquals("88005553535", jsonResponse.getString("number"));
            assertEquals("Karaganda", jsonResponse.getString("address"));
            assertEquals("ii@m.com", jsonResponse.getString("email"));
            assertEquals("", jsonResponse.getString("photoUrl"));
            assertEquals(false, jsonResponse.getBoolean("contactInfoPublic"));
            assertEquals("1234", jsonResponse.getString("googleId"));
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.d("VOLLEY", "Not Success response: " + error.toString());
        }
    }
}
