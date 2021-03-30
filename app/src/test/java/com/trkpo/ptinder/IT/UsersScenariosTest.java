package com.trkpo.ptinder.IT;

import android.os.Build;
import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.HTTP.PutRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.SUBSCRIPTION_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class UsersScenariosTest {
    private TestUtils testUtils;

    @Before
    public void setUp() {
        testUtils = new TestUtils();
    }

    @After
    public void clean() {
        testUtils.deleteAllPets();
        testUtils.deleteTestUser();
    }

    @Test
    public void testUserCanUpdateProfileInfoScenario() {
        testUtils.addUserWithGoogleId("1");
        String url = USERS_PATH + "/1";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstName", "NewName");
            jsonObject.put("lastName", "NewSurname");
            jsonObject.put("gender", "MALE");
            jsonObject.put("email", "new@mail");
            jsonObject.put("number", "-");
            jsonObject.put("address", "Peter");
            String response = new PutRequest().execute(new PostRequestParams(url, jsonObject.toString()))
                    .get();
            if (!response.equals("")) {
                JSONObject respObj = new JSONObject(response);
                assertEquals("NewName", respObj.getString("firstName"));
                assertEquals("NewSurname", respObj.getString("lastName"));
                assertEquals("MALE", respObj.getString("gender"));
                assertEquals("Peter", respObj.getString("address"));
                assertEquals("new@mail", respObj.getString("email"));
            }
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUserCanSubscribeScenario() {
        testUtils.addUserWithGoogleId("1");
        testUtils.addUserWithGoogleId("2");
        String url = SUBSCRIPTION_PATH + "/" + "1";
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("googleId", "2");
            String request = new PostRequest().execute(new PostRequestParams(url, requestObject.toString())).get();
            String response = new GetRequest().execute(SUBSCRIPTION_PATH + "/check/1/2").get();
            assertTrue(Boolean.parseBoolean(response));
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.e("VOLLEY", error.toString());
        }
    }

    @Test
    public void testUserCanUnsubscribeScenario() {
        testUtils.addUserWithGoogleId("1");
        testUtils.addUserWithGoogleId("2");
        String url = SUBSCRIPTION_PATH + "/" + "1";
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("googleId", "2");
            String request = new PostRequest().execute(new PostRequestParams(url, requestObject.toString())).get();
            String response = new GetRequest().execute(SUBSCRIPTION_PATH + "/check/1/2").get();
            assertTrue(Boolean.parseBoolean(response));
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.e("VOLLEY", error.toString());
        }

        // unsubscribe
        url = SUBSCRIPTION_PATH + "/" + "1" + "/" + "2";
        try {
            String request = new DeleteRequest().execute(url).get();
            String response = new GetRequest().execute(SUBSCRIPTION_PATH + "/check/1/2").get();
            assertFalse(Boolean.parseBoolean(response));
        } catch (ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", error.toString());
        }

    }

}
