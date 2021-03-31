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

public class NotificationsScenarioTest {
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

    }
}
