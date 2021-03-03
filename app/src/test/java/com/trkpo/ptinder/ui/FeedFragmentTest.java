package com.trkpo.ptinder.ui;

import android.os.Build;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.adapter.FeedCardAdapter;
import com.trkpo.ptinder.pojo.Feed;

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

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class FeedFragmentTest {
    private MockWebServer server = new MockWebServer();

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    private String feedBody = "[\n" +
            "  {\n" +
            "    \"author\":\"Rick\",\n" +
            "    \"content\":\"https://fraufluger.ru/wp-content/uploads/2020/09/kosh.jpg\",\n" +
            "    \"score\":\"111\",\n" +
            "    \"title\":\"Test title\"\n" +
            "  }, {\n" +
            "    \"author\":\"Jon\",\n" +
            "    \"content\":\"https://fraufluger.ru/wp-content/uploads/2020/09/kosh.jpg\",\n" +
            "    \"score\":\"222\",\n" +
            "    \"title\":\"Test title\"\n" +
            "  }, {\n" +
            "    \"author\":\"Bob\",\n" +
            "    \"content\":\"https://fraufluger.ru/wp-content/uploads/2020/09/kosh.jpg\",\n" +
            "    \"score\":\"333\",\n" +
            "    \"title\":\"Test title\"\n" +
            "  }\n" +
            "  ]";

    @Test
    public void loadFeedsIsCorrect() {
        server.enqueue(new MockResponse().setBody(feedBody));
        String url = server.url("/").toString();

        FragmentScenario<FeedFragment> uf = FragmentScenario.launch(FeedFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadFeeds(url);
            FeedCardAdapter adapter = fragment.getFeedCardAdapter();
            List<Feed> feeds = adapter.getFeeds();

            assertEquals(3, adapter.getItemCount());
            adapter.clearItems();
            assertEquals(0, adapter.getItemCount());
            adapter.setItems(feeds);
        });
    }

    @Test
    public void testIncorrectJson() {
        server.enqueue(new MockResponse().setBody("[{\"id\": \"1\"}]"));
        String url = server.url("/").toString();

        FragmentScenario<FeedFragment> uf = FragmentScenario.launch(FeedFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadFeeds(url);
            FeedCardAdapter adapter = fragment.getFeedCardAdapter();
            assertEquals(0, adapter.getItemCount());
        });
    }

    @Test
    public void testNoConnection() {
        FragmentScenario<FeedFragment> uf = FragmentScenario.launch(FeedFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadFeeds( "", "false");
            FeedCardAdapter adapter = fragment.getFeedCardAdapter();
            assertEquals(0, adapter.getItemCount());
        });
    }

}