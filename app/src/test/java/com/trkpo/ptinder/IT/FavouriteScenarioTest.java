package com.trkpo.ptinder.IT;

import android.os.Build;
import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.pojo.PetInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class FavouriteScenarioTest {
    TestUtils utils;

    @Before
    public void setUp() {
        utils = new TestUtils();
    }

    @After
    public void clean() {
        utils.deleteAllPets();
        utils.deleteTestUser();
    }

    @Test
    public void addToFavouriteTest() {
        utils.addUserWithGoogleId("1");
        utils.addUserWithGoogleId("2");
        utils.addPetForUser("2");
        Long petId = utils.getPetsForUser("2").get(0).getId();

        try {
            String url = FAVOURITE_PATH + "/" + petId + "/user/1";
            new PostRequest().execute(new PostRequestParams(url, "")).get();

            List<PetInfo> favourite = utils.getFavouriteForUser("1");
            assertEquals(favourite.size(), 1);
            assertEquals(favourite.get(0).getId(), petId);
        } catch (ExecutionException | InterruptedException error) {
            Log.d("VOLLEY", "Post request (add to favourite) error: " + error.toString());
        }
    }

    @Test
    public void deleteFromFavouriteTest() {
        utils.addUserWithGoogleId("1");
        utils.addUserWithGoogleId("2");
        utils.addPetForUser("2");
        Long petId = utils.getPetsForUser("2").get(0).getId();

        try {
            String url = FAVOURITE_PATH + "/" + petId + "/user/1";
            new PostRequest().execute(new PostRequestParams(url, "")).get();
            url = FAVOURITE_PATH + "/" + petId + "/user/1";
            new DeleteRequest().execute(url).get();

            List<PetInfo> favourite = utils.getFavouriteForUser("1");
            assertEquals(favourite.size(), 0);
        } catch (ExecutionException | InterruptedException error) {
            Log.d("VOLLEY", "Delete request (delete from favourite) error: " + error.toString());
        }
    }
}
