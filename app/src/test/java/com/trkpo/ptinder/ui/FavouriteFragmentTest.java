package com.trkpo.ptinder.ui;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.adapter.PetCardAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FavouriteFragmentTest {
    private String body =
            "[" +
                "{" +
                    "\"petId\":1," +
                    "\"name\":\"Tina\"," +
                    "\"breed\":\"Beagle\"," +
                    "\"age\":2," +
                    "\"gender\":\"MALE\"," +
                    "\"animalType\":{" +
                        "\"id\":102," +
                        "\"type\":\"Собака\"" +
                    "}," +
                    "\"purpose\":\"BREEDING\"," +
                    "\"comment\":\"-\"," +
                    "\"petPhotos\": [" +
                        "{\"id\": 11, " +
                        "\"photo\": \"dygifskjdnf324rref\"" +
                    "}]," +
                    "\"owner\":{" +
                        "\"googleId\":1," +
                        "\"firstName\":\"first Name\"," +
                        "\"lastName\":\"last Name\"," +
                        "\"email\":\"email\"," +
                        "\"photoUrl\":\"photo Url\"" +
                    "}" +
                "}," +
                 "{" +
                    "\"petId\":2," +
                    "\"name\":\"Tina\"," +
                    "\"breed\":\"Beagle\"," +
                    "\"age\":2," +
                    "\"gender\":\"MALE\"," +
                    "\"animalType\":{" +
                        "\"id\":102," +
                        "\"type\":\"Собака\"" +
                    "}," +
                    "\"purpose\":\"BREEDING\"," +
                    "\"comment\":\"-\"," +
                    "\"petPhotos\": [" +
                        "{\"id\": 11, " +
                        "\"photo\": \"dygifskjdnf324rref\"" +
                    "}]," +
                    "\"owner\":{" +
                        "\"googleId\":1," +
                        "\"firstName\":\"first Name\"," +
                        "\"lastName\":\"last Name\"," +
                        "\"email\":\"email\"," +
                        "\"photoUrl\":\"photo Url\"" +
                    "}" +
                 "}" +
            "]";
    private String googleId = "1";
    private MockWebServer server = new MockWebServer();

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void loadPetsTest() {
        server.enqueue(new MockResponse().setBody(body));
        String url = server.url("/").toString();

        FragmentScenario<FavouriteFragment> uf = FragmentScenario.launch(FavouriteFragment.class);
        uf.onFragment(fragment -> fragment.loadPets(googleId, url));
        uf.onFragment(fragment -> {
            assertEquals(fragment.getPetCardAdapter().getItemCount(), 2);
            fragment.getPetCardAdapter().deletePetById(Long.valueOf("1"));
            assertEquals(fragment.getPetCardAdapter().getItemCount(), 1);
            fragment.getPetCardAdapter().clearItems();
            assertEquals(fragment.getPetCardAdapter().getItemCount(), 0);
        });
    }

    @Test
    public void incorrectJsonBody() {
        server.enqueue(new MockResponse().setBody("[{\"petId\": 1}]"));
        String url = server.url("/").toString();

        FragmentScenario<FavouriteFragment> uf = FragmentScenario.launch(FavouriteFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadPets(googleId, url);
            assertEquals(fragment.getPetCardAdapter().getItemCount(), 0);
        });
    }


    @Test
    public void noConnectionTest() {
        FragmentScenario<FavouriteFragment> uf = FragmentScenario.launch(FavouriteFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadPets(googleId, "", "false");
            PetCardAdapter adapter = fragment.getPetCardAdapter();
            assertEquals(adapter.getItemCount(), 0);
        });
    }
}
