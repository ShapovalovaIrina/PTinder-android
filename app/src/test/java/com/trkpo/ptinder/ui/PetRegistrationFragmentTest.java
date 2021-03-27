package com.trkpo.ptinder.ui;

import android.os.Build;
import android.widget.ArrayAdapter;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class PetRegistrationFragmentTest {
    String googleId = "1";
    final String typesBody =
            "[" +
                "{\"id\":1, \"type\":type1}," +
                "{\"id\":2, \"type\":anotherType}," +
                "{\"id\":3, \"type\":newType}," +
                "{\"id\":4, \"type\":coolType}," +
                "{\"id\":5, \"type\":popularType}," +
                "{\"id\":6, \"type\":nextType}" +
            "]";
    private final MockWebServer server = new MockWebServer();

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void setTypesIsCorrect() {
        server.enqueue(new MockResponse().setBody(typesBody));
        String url = server.url("/").toString();

        FragmentScenario<PetRegistrationFragment> uf = FragmentScenario.launch(PetRegistrationFragment.class);
        uf.onFragment(fragment -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(fragment.getActivity(), android.R.layout.simple_spinner_item);
            fragment.setTypes(adapter, url);
            Assert.assertEquals(6, fragment.getPetTypeAdapter().getCount());
            Assert.assertEquals("type1", fragment.getPetTypeAdapter().getItem(0));
            Assert.assertEquals("anotherType", fragment.getPetTypeAdapter().getItem(1));
            Assert.assertEquals("newType", fragment.getPetTypeAdapter().getItem(2));
            Assert.assertEquals("coolType", fragment.getPetTypeAdapter().getItem(3));
            Assert.assertEquals("popularType", fragment.getPetTypeAdapter().getItem(4));
            Assert.assertEquals("nextType", fragment.getPetTypeAdapter().getItem(5));
        });
    }

    @Test
    public void savePetIsCorrect() {
        server.enqueue(new MockResponse().setBody(typesBody));
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        String name = "ПрекрасноеИмя";
        String age = "5";
        String gender = "MALE";
        String type = "type1";
        String breed = "breed1";
        String purpose = "DONORSHIP";
        String comment = "comment1";

        FragmentScenario<PetRegistrationFragment> uf = FragmentScenario.launch(PetRegistrationFragment.class);
        uf.onFragment(fragment -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(fragment.getActivity(), android.R.layout.simple_spinner_item);
            fragment.setTypes(adapter, url);
            fragment.getPetName().setText(name);
            fragment.getPetAge().setText(age);
            fragment.getRgGender().check(fragment.getRgGender().getChildAt(0).getId());
            fragment.getPetType().setSelection(0);
            fragment.getPetBreed().setText(breed);
            fragment.getPetPurpose().setSelection(3);
            fragment.getPetComment().setText(comment);
            fragment.savePet(googleId, url);
        });
        try {
            RecordedRequest requestToServer = server.takeRequest();
            requestToServer = server.takeRequest();
            String strRequest = requestToServer.getBody().readString(StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(strRequest);
            Assert.assertEquals("POST", requestToServer.getMethod());
            Assert.assertEquals(name, jsonObject.getString("name"));
            Assert.assertEquals(age, jsonObject.getString("age"));
            Assert.assertEquals(gender, jsonObject.getString("gender"));
            Assert.assertEquals(type, jsonObject.getString("type"));
            Assert.assertEquals(breed, jsonObject.getString("breed"));
            Assert.assertEquals(purpose, jsonObject.getString("purpose"));
            Assert.assertEquals(comment, jsonObject.getString("comment"));
            Assert.assertEquals(googleId, jsonObject.getString("googleId"));
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noConnectionTest() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        FragmentScenario<PetRegistrationFragment> uf = FragmentScenario.launch(PetRegistrationFragment.class);
        uf.onFragment(fragment -> {
            fragment.setTypes(null,url, "false");
            fragment.savePet(googleId, url, "false");
            assertEquals(0, server.getRequestCount());
        });
    }
}
