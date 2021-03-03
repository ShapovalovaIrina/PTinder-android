package com.trkpo.ptinder.ui;

import android.os.Build;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.adapter.NotificationCardAdapter;
import com.trkpo.ptinder.pojo.Notification;

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
            "    \"content\":\"Test content\",\n" +
            "    \"score\":\"111\",\n" +
            "    \"title\":\"Test title\"\n" +
            "  }, {\n" +
            "    \"author\":\"Jon\",\n" +
            "    \"content\":\"Test content\",\n" +
            "    \"score\":\"222\",\n" +
            "    \"title\":\"Test title\"\n" +
            "  }, {\n" +
            "    \"author\":\"Bob\",\n" +
            "    \"content\":\"Test content\",\n" +
            "    \"score\":\"333\",\n" +
            "    \"title\":\"Test title\"\n" +
            "  }\n" +
            "  ]";

    @Test
    public void loadNotificationsIsCorrect() {
        server.enqueue(new MockResponse().setBody(feedBody));
        String url = server.url("/").toString();

        FragmentScenario<FeedFragment> uf = FragmentScenario.launch(FeedFragment.class);
        uf.onFragment(fragment -> {

        });
    }

}