package com.trkpo.ptinder.ui;

import android.graphics.Bitmap;
import android.widget.RadioGroup;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SearchFragmentTest {
    final private String addressBody = "[\"Москва\",\"Санкт-Петербург\",\"Мурманск\",\"Кировск\"]";
    final private String favouriteBody = "[\"4\", \"8\", \"15\", \"16\", \"23\", \"42\"]";
    final private String typesBody =
            "[" +
                "{\"id\":1, \"type\":type1}," +
                "{\"id\":2, \"type\":anotherType}," +
                "{\"id\":6, \"type\":nextType}" +
            "]";
    final private String resultsBody =
            "[" +
                "{" +
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
                    "\"petPhotos\": [{" +
                        "\"id\": 1, " +
                        "\"photo\": \"" + Mockito.mock(Bitmap.class).toString() + "\"" +
                    "}]," +
                    "\"owner\":{" +
                        "\"googleId\":11," +
                        "\"firstName\":\"Will\"," +
                        "\"lastName\":\"Smith\"," +
                        "\"email\":\"email\"," +
                        "\"photoUrl\":\"url\"" +
                    "}" +
                "}," +
                "{" +
                    "\"petId\":102," +
                    "\"name\":\"Sharik\"," +
                    "\"breed\":\"Breed2\"," +
                    "\"age\":1," +
                    "\"gender\":\"MALE\"," +
                    "\"animalType\":{" +
                        "\"id\":102," +
                        "\"type\":\"Собака\"" +
                    "}," +
                    "\"purpose\":\"FRIENDSHIP\"," +
                    "\"comment\":\"Good boy\"," +
                    "\"petPhotos\": [{" +
                        "\"id\": 155, " +
                        "\"photo\": \"" + Mockito.mock(Bitmap.class).toString() + "\"" +
                    "}]," +
                    "\"owner\":{" +
                        "\"googleId\":12," +
                        "\"firstName\":\"John\"," +
                        "\"lastName\":\"Smith\"," +
                        "\"email\":\"email\"," +
                        "\"photoUrl\":\"url\"" +
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
    public void setAddressIsCorrect() {
        server.enqueue(new MockResponse().setBody(addressBody));
        String url = server.url("/").toString();

        FragmentScenario<SearchFragment> uf = FragmentScenario.launch(SearchFragment.class);
        uf.onFragment(fragment -> fragment.setAddresses(url));
        uf.onFragment(fragment -> {
            assertEquals(5, fragment.getAddressAdapter().getCount());
            assertEquals("-", fragment.getAddressAdapter().getItem(0));
            assertEquals("Москва", fragment.getAddressAdapter().getItem(1));
            assertEquals("Санкт-Петербург", fragment.getAddressAdapter().getItem(2));
            assertEquals("Мурманск", fragment.getAddressAdapter().getItem(3));
            assertEquals("Кировск", fragment.getAddressAdapter().getItem(4));
        });
    }

    @Test
    public void showResultsIsCorrect() {
        server.enqueue(new MockResponse().setBody(resultsBody));
        String url = server.url("/").toString();

        FragmentScenario<SearchFragment> uf = FragmentScenario.launch(SearchFragment.class);
        uf.onFragment(fragment -> fragment.showResults("1", url));
        uf.onFragment(fragment -> {
            assertEquals(2, fragment.getPetCardAdapter().getItemCount());
        });
    }

    @Test
    public void setTypesIsCorrect() {
        server.enqueue(new MockResponse().setBody(typesBody));
        String url = server.url("/").toString();

        FragmentScenario<SearchFragment> uf = FragmentScenario.launch(SearchFragment.class);
        uf.onFragment(fragment -> fragment.setTypes(url));
        uf.onFragment(fragment -> {
            assertEquals(4, fragment.getTypeAdapter().getCount());
            assertEquals("-", fragment.getTypeAdapter().getItem(0));
            assertEquals("type1", fragment.getTypeAdapter().getItem(1));
            assertEquals("anotherType", fragment.getTypeAdapter().getItem(2));
            assertEquals("nextType", fragment.getTypeAdapter().getItem(3));
        });
    }

    @Test
    public void loadFavouriteIdIsCorrect() {
        server.enqueue(new MockResponse().setBody(favouriteBody));
        String url = server.url("/").toString();
        List<Long> expected = Arrays.asList(4L, 8L, 15L, 16L, 23L, 42L);

        FragmentScenario<SearchFragment> uf = FragmentScenario.launch(SearchFragment.class);
        uf.onFragment(fragment -> fragment.loadFavouriteId("1", url));
        uf.onFragment(fragment -> {
            assertEquals(6, fragment.getFavouritePetsId().size());
            assertArrayEquals(expected.toArray(), fragment.getFavouritePetsId().toArray());
        });
    }

    @Test
    public void translateGenderIsCorrect() {
        FragmentScenario<SearchFragment> uf = FragmentScenario.launch(SearchFragment.class);
        uf.onFragment(fragment -> {
            RadioGroup gender = fragment.getRadioGroup();
            gender.check(gender.getChildAt(0).getId());
            assertEquals("MALE", fragment.translateGender(gender));
            gender.check(gender.getChildAt(1).getId());
            assertEquals("FEMALE", fragment.translateGender(gender));
            gender.check(0);
            assertEquals("", fragment.translateGender(gender));
        });
    }

    @Test
    public void getAddrFromJSONIsCorrect() {
        List<String> expected = Arrays.asList("-", "Москва", "Санкт-Петербург", "Мурманск", "Кировск");

        FragmentScenario<SearchFragment> uf = FragmentScenario.launch(SearchFragment.class);
        uf.onFragment(fragment -> {
            assertArrayEquals(expected.toArray(), fragment.getAddrFromJSON(addressBody).toArray());
        });
    }

    @Test
    public void noConnectionTest() {
        String googleId = "1";

        FragmentScenario<SearchFragment> uf = FragmentScenario.launch(SearchFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadFavouriteId(googleId, "", "false");
            fragment.setAddresses("", "false");
            fragment.setTypes("", "false");
            fragment.showResults(googleId, "", "false");
            assertEquals(0, fragment.getPetCardAdapter().getItemCount());
            assertEquals(0, fragment.getFavouritePetsId().size());
            assertEquals(0, fragment.getTypeAdapter().getCount());
            assertEquals(0, fragment.getAddressAdapter().getCount());
        });
    }

    @Test
    public void incorrectJson() {
        String googleId = "1";
        server.enqueue(new MockResponse().setBody("Incorrect json"));
        server.enqueue(new MockResponse().setBody("Incorrect json"));
        server.enqueue(new MockResponse().setBody("Incorrect json"));
        server.enqueue(new MockResponse().setBody("Incorrect json"));
        String url = server.url("/").toString();

        FragmentScenario<SearchFragment> uf = FragmentScenario.launch(SearchFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadFavouriteId(googleId, url);
            fragment.setTypes(url);
            fragment.showResults(googleId, url);
            assertEquals(0, fragment.getPetCardAdapter().getItemCount());
            assertEquals(0, fragment.getFavouritePetsId().size());
            assertEquals(0, fragment.getTypeAdapter().getCount());
        });
    }
}
