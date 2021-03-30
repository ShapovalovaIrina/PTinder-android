package com.trkpo.ptinder.IT;

import android.os.Build;

import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.pojo.Feed;
import com.trkpo.ptinder.utils.FeedUtils;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.NEWS_PATH;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class FeedsScenarioTest {
    private TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        testUtils = new TestUtils();
    }

    @Test
    public void testUserCanGet15Feeds() throws InterruptedException, ExecutionException, JSONException {
        String response = new GetRequest().execute(NEWS_PATH).get();
        List<Feed> feeds = (List<Feed>) FeedUtils.getNewsFromJSON(response);
        assertEquals(15, feeds.size());
    }
}
