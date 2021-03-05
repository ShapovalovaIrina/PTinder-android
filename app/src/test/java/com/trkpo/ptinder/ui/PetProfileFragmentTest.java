package com.trkpo.ptinder.ui;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.PetInfo;

import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static com.trkpo.ptinder.utils.PetInfoUtils.getPetsFromJSON;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PetProfileFragmentTest {
    private Bundle bundle;
    PetInfo petInfo;
    final private String petJson =
            "[{" +
                "\"petId\":101," +
                "\"name\":\"CoOlNaMe\"," +
                "\"breed\":\"Breed1\"," +
                "\"age\":5," +
                "\"gender\":\"MALE\"," +
                "\"animalType\":{" +
                    "\"id\":102," +
                    "\"type\":\"Собака\"" +
                "}," +
                "\"purpose\":\"FRIENDSHIP\"," +
                "\"comment\":\"comment12345\"," +
                "\"petPhotos\": []," +
                "\"owner\":{" +
                    "\"googleId\":11," +
                    "\"firstName\":\"Will\"," +
                    "\"lastName\":\"Smith\"," +
                    "\"email\":\"email\"," +
                    "\"photoUrl\":\"https://clck.ru/TYpNV\"" +
                "}" +
            "}]";
    private final MockWebServer server = new MockWebServer();

    @Before
    public void initAndServerSetupUp() throws IOException {
        server.start(8080);

        bundle = new Bundle();
        try {
            petInfo = ((List<PetInfo>) getPetsFromJSON(petJson, null, "1", 1)).get(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putSerializable("petInfo", petInfo);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void addToFavouriteIsCorrect() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        FragmentScenario<PetProfileFragment> uf = FragmentScenario.launch(PetProfileFragment.class, bundle, R.style.AppTheme, null);
        uf.onFragment(fragment -> fragment.addToFavourite(url));
        try {
            RecordedRequest requestToServer = server.takeRequest();
            String strRequest = requestToServer.getBody().readString(StandardCharsets.UTF_8);
            Assert.assertEquals("POST", requestToServer.getMethod());
            Assert.assertEquals("", strRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteFromFavouriteIsCorrect() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        try {
            FragmentScenario<PetProfileFragment> uf = FragmentScenario.launch(PetProfileFragment.class, bundle, R.style.AppTheme, null);
            uf.onFragment(fragment -> fragment.deleteFromFavourite(url));
            RecordedRequest requestToServer = server.takeRequest();
            String strRequest = requestToServer.getBody().readString(StandardCharsets.UTF_8);
            Assert.assertEquals("DELETE", requestToServer.getMethod());
            Assert.assertEquals("", strRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void petInfoIsCorrect() {
        FragmentScenario<PetProfileFragment> uf = FragmentScenario.launch(PetProfileFragment.class, bundle, R.style.AppTheme, null);
        uf.onFragment(fragment -> {
            Assert.assertEquals(petInfo.getName(), fragment.getPetName().getText());
            Assert.assertEquals(petInfo.getAge(), fragment.getPetAge().getText());
            Assert.assertEquals(petInfo.getAnimalType(), fragment.getPetType().getText());
            Assert.assertEquals(petInfo.getBreed(), fragment.getPetBreed().getText());
            Assert.assertEquals(petInfo.getComment(), fragment.getPetComment().getText());
            Assert.assertEquals(petInfo.getPurpose(), fragment.getPetPurpose().getText());
        });
    }

    @Test
    public void noConnectionTest() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        FragmentScenario<PetProfileFragment> uf = FragmentScenario.launch(PetProfileFragment.class, bundle, R.style.AppTheme, null);
        uf.onFragment(fragment -> {
            fragment.addToFavourite(url, "false");
            fragment.deleteFromFavourite(url, "false");
            assertEquals(0, server.getRequestCount());
        });
    }
}
