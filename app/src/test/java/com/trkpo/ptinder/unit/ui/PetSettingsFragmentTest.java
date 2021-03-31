package com.trkpo.ptinder.unit.ui;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.ui.PetSettingsFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.app.Activity.RESULT_OK;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class PetSettingsFragmentTest {
    private MockWebServer server = new MockWebServer();
    private static final int GALLERY_REQUEST = 1;

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void correctLoadPet() {
        String petBody =
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
                        "}";
        server.enqueue(new MockResponse().setBody(petBody));
        String url = server.url("/").toString();
        Long petId = Long.valueOf("1");

        FragmentScenario<PetSettingsFragment> uf = FragmentScenario.launch(PetSettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.loadPet(petId);
            assertEquals("Tina", fragment.getPetName());
            assertEquals("Beagle", fragment.getPetBreed());
            assertEquals("2", fragment.getPetAge());
            assertEquals("-", fragment.getPetComment());
            assertEquals(1, fragment.getPetImagesCount());

            /* On click actions*/
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.getDeletePet().performClick();
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.getUpdatePet().performClick();
        });
    }

    @Test
    public void incorrectLoadPetJson() {
        server.enqueue(new MockResponse().setBody("Incorrect json"));
        String url = server.url("/").toString();
        Long petId = Long.valueOf("1");

        FragmentScenario<PetSettingsFragment> uf = FragmentScenario.launch(PetSettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.loadPet(petId);
        });
    }

    @Test
    public void correctDeletePet() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();
        Long petId = Long.valueOf("1");

        FragmentScenario<PetSettingsFragment> uf = FragmentScenario.launch(PetSettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.deletePet(petId);
        });
    }

    @Test
    public void correctUpdatePet() {
        server.enqueue(new MockResponse().setBody("Not empty response"));
        String url = server.url("/").toString();
        Long petId = Long.valueOf("1");

        FragmentScenario<PetSettingsFragment> uf = FragmentScenario.launch(PetSettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.getPetAgeObject().setText("1");
            fragment.updatePet(petId);
        });
    }

    @Test
    public void noConnectionTest() {
        FragmentScenario<PetSettingsFragment> uf = FragmentScenario.launch(PetSettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission("", false);
            fragment.loadPet(Long.valueOf("1"));

            fragment.setOptUrlAndConnectionPermission("", false);
            fragment.deletePet(Long.valueOf("1"));

            fragment.setOptUrlAndConnectionPermission("", false);
            fragment.updatePet(Long.valueOf("1"));
        });
    }

    @Test
    public void onActivityResult() {
        Intent intent = new Intent();
        ClipData.Item item = new ClipData.Item(Uri.parse("https://fraufluger.ru/wp-content/uploads/2020/09/kosh.jpg"));
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.setClipData(new ClipData("photos", mimeTypes, item));
        FragmentScenario<PetSettingsFragment> uf = FragmentScenario.launch(PetSettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.onActivityResult(GALLERY_REQUEST, RESULT_OK, intent);
        });
    }

    @Test
    public void clickOnImage() {
        FragmentScenario<PetSettingsFragment> uf = FragmentScenario.launch(PetSettingsFragment.class);
        uf.onFragment(PetSettingsFragment::performOnImageClickListener);
    }
}