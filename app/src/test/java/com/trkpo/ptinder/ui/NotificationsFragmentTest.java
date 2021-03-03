package com.trkpo.ptinder.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.adapter.NotificationCardAdapter;
import com.trkpo.ptinder.config.Constants;
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
public class NotificationsFragmentTest {
    private String notificationBody = "[" +
                "{ " +
                    "\"type\": \"type\"," +
                    "\"id\": \"1\"," +
                    "\"text\": \"text\"," +
                    "\"read\": false," +
                    "\"addressee\": {\"googleId\" : \"123\"}," +
                    "\"addresseeFromId\": \"1234\"" +
                "}," +
                "{ " +
                    "\"type\": \"type\"," +
                    "\"id\": \"2\"," +
                    "\"text\": \"text\"," +
                    "\"read\": false," +
                    "\"addressee\": {\"googleId\" : \"123\"}," +
                    "\"addresseeFromId\": \"1234\"" +
                "}" +
            "]";
    private String googleId = "1";
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
    public void loadNotificationsIsCorrect() {
        server.enqueue(new MockResponse().setBody(notificationBody));
        String url = server.url("/").toString();

        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadNotifications(googleId, url);
            NotificationCardAdapter adapter = fragment.getNotificationCardAdapter();
            List<Notification> notificationList = adapter.getNotifications();

            /* Little adapter test*/
            assertEquals(adapter.getItemCount(), 2);
            adapter.deleteNotificationById("1");
            assertEquals(adapter.getItemCount(), 1);
            adapter.clearItems();
            assertEquals(adapter.getItemCount(), 0);
            adapter.setItems(notificationList);
        });
    }

    @Test
    public void notificationFragmentCreatingWithBundle() {
        server.enqueue(new MockResponse().setBody(notificationBody));
        String url = server.url("/").toString();

        Bundle bundle = new Bundle();
        bundle.putSerializable("googleId", googleId);
        bundle.putSerializable("optUrl", url);
        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class, bundle);
        uf.onFragment(fragment -> {
            NotificationCardAdapter adapter = fragment.getNotificationCardAdapter();
            assertEquals(adapter.getItemCount(), 2);
        });
    }

    @Test
    public void incorrectJsonBody() {
        server.enqueue(new MockResponse().setBody("[{\"id\": \"1\"}]"));
        String url = server.url("/").toString();

        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadNotifications(googleId, url);
            NotificationCardAdapter adapter = fragment.getNotificationCardAdapter();
            assertEquals(adapter.getItemCount(), 0);
        });
    }

    @Test
    public void noConnectionTest() {
        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            fragment.loadNotifications(googleId, "", "false");
            NotificationCardAdapter adapter = fragment.getNotificationCardAdapter();
            assertEquals(adapter.getItemCount(), 0);
        });
    }
}