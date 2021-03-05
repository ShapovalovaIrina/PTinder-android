package com.trkpo.ptinder.ui;

import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.adapter.FeedCardAdapter;
import com.trkpo.ptinder.adapter.SmallPetAdapter;
import com.trkpo.ptinder.pojo.Feed;
import com.trkpo.ptinder.pojo.PetInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class SettingsFragmentTest {
    private MockWebServer server = new MockWebServer();

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    private String userBody = "{ " +
            "\"firstName\": \"first Name\"," +
            "\"lastName\": \"last Name\"," +
            "\"address\": \"location\"," +
            "\"gender\": \"female\"," +
            "\"email\": \"email\"," +
            "\"number\": \"123\"" +
            "}";

    private String petsBody = "[{\n" +
            "\t\"petId\": \"1\",\n" +
            "\t\"name\": \"pet1\",\n" +
            "\t\"breed\": \"breed\",\n" +
            "\t\"age\": \"1\",\n" +
            "\t\"gender\": \"male\",\n" +
            "\t\"animalType\": {\n" +
            "\t\t\"type\": \"DOG\"\n" +
            "\t},\n" +
            "\t\"purpose\": \"BREEDING\",\n" +
            "\t\"comment\": \"Test title\",\n" +
            "   \"petPhotos\": [] \n" +
            "}, {\n" +
            "\t\"petId\": \"2\",\n" +
            "\t\"name\": \"pet2\",\n" +
            "\t\"breed\": \"breed\",\n" +
            "\t\"age\": \"1\",\n" +
            "\t\"gender\": \"male\",\n" +
            "\t\"animalType\": {\n" +
            "\t\t\"type\": \"DOG\"\n" +
            "\t},\n" +
            "\t\"purpose\": \"BREEDING\",\n" +
            "\t\"comment\": \"Test title\",\n" +
            "   \"petPhotos\":[]\n" +
            "}, {\n" +
            "\t\"petId\": \"3\",\n" +
            "\t\"name\": \"pet3\",\n" +
            "\t\"breed\": \"breed\",\n" +
            "\t\"age\": \"1\",\n" +
            "\t\"gender\": \"male\",\n" +
            "\t\"animalType\": {\n" +
            "\t\t\"type\": \"DOG\"\n" +
            "\t},\n" +
            "\t\"purpose\": \"BREEDING\",\n" +
            "\t\"comment\": \"Test title\",\n" +
            "   \"petPhotos\":[]\n" +
            "}]";

    @Test
    public void showInfoGetRequestIsCorrect() {
        String googleId = "1";
        server.enqueue(new MockResponse().setBody(userBody));
        String url = server.url("/").toString();

        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> fragment.showInfo(googleId, url));
        uf.onFragment(fragment -> {
            assertEquals(fragment.firstName.getText().toString(), "first Name");
            assertEquals(fragment.location.getText().toString(), "location");
            assertEquals(fragment.email.getText().toString(), "email");
            assertEquals(fragment.phone.getText().toString(), "123");
        });
    }

    @Test
    public void testNoConnection() {
        String googleId = "1";

        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> fragment.setOptUrlAndConnectionPermission("", false));
        uf.onFragment(fragment -> fragment.showInfo(googleId, "", "false"));
        uf.onFragment(SettingsFragment::deleteUser);
        uf.onFragment(SettingsFragment::updateUser);
        uf.onFragment(fragment -> fragment.loadPets("", "false"));
        uf.onFragment(fragment -> {
            assertEquals(fragment.smallPetAdapter.getItemCount(), 0);
        });
    }

    @Test
    public void testCanUpdateUser() {
        server.enqueue(new MockResponse().setBody(userBody));
        String url = server.url("/").toString();

        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.firstName.setText("name");
            fragment.email.setText("e@mail");
            fragment.phone.setText("123");
        });
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.updateUser();
        });
        uf.onFragment(fragment -> {
            assertEquals(fragment.firstName.getText().toString(), "name");
            assertEquals(fragment.email.getText().toString(), "e@mail");
            assertEquals(fragment.phone.getText().toString(), "123");
        });
    }

    @Test
    public void testCanDeleteUser() {
        server.enqueue(new MockResponse().setBody(userBody));
        String url = server.url("/").toString();

        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            fragment.firstName.setText("name");
            fragment.email.setText("e@mail");
            fragment.phone.setText("123");
        });
        uf.onFragment(fragment -> fragment.deleteUser());
    }

    @Test
    public void testLoadPetsIsCorrect() {
        server.enqueue(new MockResponse().setBody(petsBody));
        String url = server.url("/").toString();

        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadPets(url);
            SmallPetAdapter adapter = fragment.smallPetAdapter;
            List<PetInfo> pets = adapter.getItems();

            assertEquals(3, adapter.getItemCount());
            adapter.clearItems();
            assertEquals(0, adapter.getItemCount());
            adapter.setItems(pets);
        });
    }

    @Test
    public void testIncorrectJson() {
        server.enqueue(new MockResponse().setBody("{\"j\":\"d\"}"));
        server.enqueue(new MockResponse().setBody("{\"j\":\"d\"}"));
        String url = server.url("/").toString();
        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadPets(url);
            SmallPetAdapter adapter = fragment.smallPetAdapter;
            assertEquals(0, adapter.getItemCount());
        });
        uf.onFragment(fragment -> {
            fragment.showInfo("1", url);
            assertEquals("", fragment.firstName.getText().toString());
        });
    }

    @Test
    public void testSaveOnClick() {
        server.enqueue(new MockResponse().setBody(userBody));
        server.enqueue(new MockResponse().setBody(userBody));
        String url = server.url("/").toString();
        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            Button saveBtn = fragment.updateUser;
            saveBtn.performClick();
            fragment.showInfo("1", url);
            assertEquals(fragment.firstName.getText().toString(), "first Name");
            assertEquals(fragment.email.getText().toString(), "email");
            assertEquals(fragment.phone.getText().toString(), "123");
        });
    }

    @Test
    public void testDeleteOnClick() {
        server.enqueue(new MockResponse().setBody(userBody));
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();
        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> {
            fragment.setOptUrlAndConnectionPermission(url, true);
            Button saveBtn = fragment.deleteUser;
            saveBtn.performClick();
            fragment.showInfo("1", url);
            assertEquals(fragment.firstName.getText().toString(), "");
        });
    }


}