package com.trkpo.ptinder.ui;

import android.graphics.Bitmap;
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
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static com.trkpo.ptinder.utils.PetInfoUtils.getPetsFromJSON;

@RunWith(AndroidJUnit4.class)
public class PetProfileFragmentTest {
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
                "\"petPhotos\": [{" +
                    "\"id\": 1, " +
                    "\"photo\": \"" + Mockito.mock(Bitmap.class).toString() + "\"" +
                "}]," +
                "\"owner\":{" +
                    "\"googleId\":11," +
                    "\"firstName\":\"Will\"," +
                    "\"lastName\":\"Smith\"," +
                    "\"email\":\"email\"," +
                    "\"photoUrl\":\"https://clck.ru/TYpNV\"" +
                "}" +
            "}]";
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
    public void addToFavouriteIsCorrect() {
        server.enqueue(new MockResponse().setBody("response"));
        String url = server.url("/").toString();

        Bundle bundle = new Bundle();
        PetInfo petInfo = null;
        try {
            petInfo = ((List<PetInfo>) getPetsFromJSON(petJson, null, "1", 1)).get(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putSerializable("petInfo", petInfo);
        FragmentScenario<PetProfileFragment> uf = FragmentScenario.launch(PetProfileFragment.class, bundle, R.style.AppTheme, null);
        uf.onFragment(fragment -> {
            fragment.addToFavourite(url);
        });
    }

    @Test
    public void deleteFromFavouriteIsCorrect() {
        server.enqueue(new MockResponse().setBody("response"));
        String url = server.url("/").toString();

        Bundle bundle = new Bundle();
        PetInfo petInfo = null;
        try {
            petInfo = ((List<PetInfo>) getPetsFromJSON(petJson, null, "1", 1)).get(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putSerializable("petInfo", petInfo);
        FragmentScenario<PetProfileFragment> uf = FragmentScenario.launch(PetProfileFragment.class, bundle, R.style.AppTheme, null);
        uf.onFragment(fragment -> {
            fragment.deleteFromFavourite(url);
        });
    }

    @Test
    public void petInfoIsCorrect() {
        Bundle bundle = new Bundle();
        try {
            PetInfo petInfo = ((List<PetInfo>) getPetsFromJSON(petJson, null, "1", 1)).get(0);
            bundle.putSerializable("petInfo", petInfo);
            FragmentScenario<PetProfileFragment> uf = FragmentScenario.launch(PetProfileFragment.class, bundle, R.style.AppTheme, null);
            uf.onFragment(fragment -> {
                Assert.assertEquals(petInfo.getName(), fragment.getPetName().getText());
                Assert.assertEquals(petInfo.getAge(), fragment.getPetAge().getText());
                Assert.assertEquals(petInfo.getAnimalType(), fragment.getPetType().getText());
                Assert.assertEquals(petInfo.getBreed(), fragment.getPetBreed().getText());
                Assert.assertEquals(petInfo.getComment(), fragment.getPetComment().getText());
                Assert.assertEquals(petInfo.getPurpose(), fragment.getPetPurpose().getText());
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
