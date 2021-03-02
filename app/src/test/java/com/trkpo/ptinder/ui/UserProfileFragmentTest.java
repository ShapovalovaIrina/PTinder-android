package com.trkpo.ptinder.ui;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UserProfileFragmentTest {
    private String userBody = "{ " +
                "\"firstName\": \"first Name\"," +
                "\"lastName\": \"last Name\"," +
                "\"address\": \"location\"," +
                "\"email\": \"email\"," +
                "\"number\": \"123\"" +
            "}";
    private String favouriteBody = "[1, 2, 3]";
    private String petBody =
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
              "}" +
            "]";
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
    public void showInfoGetRequestIsCorrect() {
        String googleId = "1";
        server.enqueue(new MockResponse().setBody(userBody));
        String url = server.url("/").toString();

        FragmentScenario<UserProfileFragment> uf = FragmentScenario.launch(UserProfileFragment.class);
        uf.onFragment(fragment -> fragment.showInfo(googleId, url));
        uf.onFragment(fragment -> {
            assertEquals(fragment.getUsername(), "first Name last Name");
            assertEquals(fragment.getLocation(), "location");
            assertEquals(fragment.getEmail(), "email");
            assertEquals(fragment.getPhone(), "123");
        });
    }

    @Test
    public void loadFavouritePetsAndPetsIsCorrect() {
        String googleId = "1";
        server.enqueue(new MockResponse().setBody(favouriteBody));
        server.enqueue(new MockResponse().setBody(petBody));
        String url = server.url("/").toString();

        FragmentScenario<UserProfileFragment> uf = FragmentScenario.launch(UserProfileFragment.class);
        uf.onFragment(fragment -> fragment.loadFavouriteId(googleId, url));
        uf.onFragment(fragment -> {
            assertEquals(fragment.getPetCardAdapter().getItemCount(), 1);
            assertTrue(fragment.getPetCardAdapter().getPetsList().get(0).isFavourite());
        });
    }

    @Test
    public void noConnectionTest() {
        String googleId = "1";

        FragmentScenario<UserProfileFragment> uf = FragmentScenario.launch(UserProfileFragment.class);
        uf.onFragment(fragment -> {
            fragment.showInfo(googleId, "", "false");
            fragment.loadFavouriteId(googleId, "", "false");
            fragment.loadPets(googleId, new ArrayList<>(), "", "false");
            assertEquals(fragment.getPetCardAdapter().getItemCount(), 0);
        });
    }

    @Test
    public void incorrectJson() {
        String googleId = "1";
        server.enqueue(new MockResponse().setBody("Incorrect text"));
        server.enqueue(new MockResponse().setBody("Incorrect text"));
        server.enqueue(new MockResponse().setBody("Incorrect text"));
        String url = server.url("/").toString();

        FragmentScenario<UserProfileFragment> uf = FragmentScenario.launch(UserProfileFragment.class);
        uf.onFragment(fragment -> fragment.loadFavouriteId(googleId, url));
        uf.onFragment(fragment -> {
            fragment.showInfo(googleId, url);
            fragment.loadFavouriteId(googleId, url);
            fragment.loadPets(googleId, new ArrayList<>(), url);
            assertEquals(fragment.getPetCardAdapter().getItemCount(), 0);
        });
    }
}
