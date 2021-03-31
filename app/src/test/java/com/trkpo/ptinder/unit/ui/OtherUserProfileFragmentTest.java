package com.trkpo.ptinder.unit.ui;

import android.os.Build;
import android.widget.ImageView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.pojo.PetInfo;
import com.trkpo.ptinder.ui.OtherUserProfileFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class OtherUserProfileFragmentTest {
    private String currentUserGoogleId = "1";
    private String userGoogleId = "2";
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
    public void correctFragmentCreation() {
        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
    }

    @Test
    public void correctRequestUserContacts() {
        server.enqueue(new MockResponse().setBody("Not empty response"));
        String url = server.url("/").toString();

        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.requestUserContacts(currentUserGoogleId, userGoogleId);
        });
    }

    @Test
    public void correctSubscribeOnUser() {
        server.enqueue(new MockResponse().setBody("Not empty response"));
        String url = server.url("/").toString();

        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.subscribeOnUser(currentUserGoogleId, userGoogleId);
            assertTrue(fragment.isSubscr());
        });
    }

    @Test
    public void correctUnsubscribeOnUser() {
        server.enqueue(new MockResponse().setBody("Not empty response"));
        String url = server.url("/").toString();

        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.unsubscribeOnUser(currentUserGoogleId, userGoogleId);
            assertFalse(fragment.isSubscr());
        });
    }

    @Test
    public void correctCheckSubscription() {
        String checkBodyTrue = "true";
        String checkBodyFalse = "false";
        server.enqueue(new MockResponse().setBody(checkBodyTrue));
        server.enqueue(new MockResponse().setBody(checkBodyFalse));
        String url = server.url("/").toString();

        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.checkSubscription(currentUserGoogleId, userGoogleId);
            assertTrue(fragment.isSubscr());
        });
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.checkSubscription(currentUserGoogleId, userGoogleId);
            assertFalse(fragment.isSubscr());
        });
    }

    @Test
    public void correctShowInfo() {
        String userBody = "{ " +
                "\"firstName\": \"first Name\"," +
                "\"lastName\": \"last Name\"," +
                "\"address\": \"location\"," +
                "\"email\": \"email\"," +
                "\"number\": \"123\"," +
                "\"contactInfoPublic\" : true," +
                "\"photoUrl\" : \"https://ru.wikipedia.org/wiki/Кошка#/media/Файл:AfricanWildCat.jpg\"" +
                "}";
        server.enqueue(new MockResponse().setBody(userBody));
        String url = server.url("/").toString();

        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.showInfo(currentUserGoogleId, userGoogleId);});
        uf.onFragment(fragment -> {
            assertEquals("first Name last Name", fragment.getUsername());
            assertEquals("location", fragment.getLocation());
            assertEquals("email", fragment.getEmail());
            assertEquals("123", fragment.getPhone());
        });
    }

    @Test
    public void correctLoadFavouriteIdAndPets() {
        String favouriteBody = "[1, 2, 3]";
        String petBody =
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
        server.enqueue(new MockResponse().setBody(favouriteBody));
        server.enqueue(new MockResponse().setBody(petBody));
        String url = server.url("/").toString();

        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.loadFavouriteId(currentUserGoogleId, userGoogleId);
            assertEquals(1, fragment.getPetCardAdapter().getItemCount());
            List<PetInfo> list = fragment.getPetCardAdapter().getPetsList();
            assertTrue(list.get(0).isFavourite());
        });
    }

    @Test
    public void noConnectionTest() {
        String url = server.url("/").toString();
        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, false);
            fragment.requestUserContacts(currentUserGoogleId, userGoogleId);

            fragment.setOptUrlAndConnectionPermission(url, false);
            fragment.subscribeOnUser(currentUserGoogleId, userGoogleId);

            fragment.setOptUrlAndConnectionPermission(url, false);
            fragment.unsubscribeOnUser(currentUserGoogleId, userGoogleId);

            fragment.setOptUrlAndConnectionPermission(url, false);
            fragment.checkSubscription(currentUserGoogleId, userGoogleId);

            fragment.setOptUrlAndConnectionPermission(url, false);
            fragment.showInfo(currentUserGoogleId, userGoogleId);

            fragment.setOptUrlAndConnectionPermission(url, false);
            fragment.loadFavouriteId(currentUserGoogleId, userGoogleId);

            fragment.setOptUrlAndConnectionPermission(url, false);
            fragment.loadPets(new ArrayList<>(), currentUserGoogleId, userGoogleId);
        });
    }

    @Test
    public void clickUserSubscribeImageView() {
        server.enqueue(new MockResponse().setBody("Not empty response (1)"));
        server.enqueue(new MockResponse().setBody("Not empty response (2)"));
        String url = server.url("/").toString();
        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            ImageView imageView = fragment.getUserSubscribe();
            assertFalse(fragment.isSubscr());

            fragment.setOptUrlAndConnectionPermission(url, true);
            imageView.performClick();
            assertTrue(fragment.isSubscr());

            fragment.setOptUrlAndConnectionPermission(url, true);
            imageView.performClick();
            assertFalse(fragment.isSubscr());
        });
    }

    @Test
    public void clickRequestUserContactsImageView() {
        server.enqueue(new MockResponse().setBody("Not empty response"));
        String url = server.url("/").toString();
        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            ImageView imageView = fragment.getRequestUserContacts();
            fragment.setOptUrlAndConnectionPermission(url, true);
            imageView.performClick();
        });
    }
}