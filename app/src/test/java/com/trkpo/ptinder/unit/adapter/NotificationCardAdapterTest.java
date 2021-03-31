package com.trkpo.ptinder.unit.adapter;

import android.os.Build;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.adapter.NotificationCardAdapter;
import com.trkpo.ptinder.pojo.Notification;
import com.trkpo.ptinder.ui.NotificationsFragment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})

public class NotificationCardAdapterTest {
    List<Notification> notifications = Arrays.asList(
            new Notification("11", "CONTACT_INFO_REQUEST", "", false, "123", "456"),
            new Notification("22", "NEW_PET", "", false, "123", "456"),
            new Notification("33", "EDIT_PET", "", false, "123", "456"),
            new Notification("14", "EDIT_FAVOURITE", "", true, "123", "456"),
            new Notification("32", "CONTACT_INFO_ANSWER", "", false, "123", "456")
    );
    List<String> expectedTitles = Arrays.asList("Новый запрос доступа к контактной информации",
            "Новые питомцы у пользователей, на которых вы подписаны",
            "Изменения в анкетах питомцев пользователей, на которых вы подписаны",
            "Изменения в анкетах Ваших избранных питомцев",
            "Ответ на Ваш запрос доступа к контактной информации");
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
    public void setItemsAndGetNotificationsIsCorrect() {
        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            NotificationCardAdapter adapter = (NotificationCardAdapter) fragment.getNotificationCardRecycleView().getAdapter();
            adapter.setItems(notifications);

            Assert.assertEquals(5, adapter.getItemCount());
            Assert.assertTrue(adapter.getNotifications().equals(notifications));
        });
    }

    @Test
    public void ClearNotificationsIsCorrect() {
        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            NotificationCardAdapter adapter = (NotificationCardAdapter) fragment.getNotificationCardRecycleView().getAdapter();
            adapter.setItems(notifications);
            adapter.clearItems();

            Assert.assertEquals(0, adapter.getItemCount());
            Assert.assertTrue(adapter.getNotifications().isEmpty());
        });
    }

    @Test
    public void deleteNotificationByIdIsCorrect() {
        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            NotificationCardAdapter adapter = (NotificationCardAdapter) fragment.getNotificationCardRecycleView().getAdapter();
            adapter.setItems(notifications);

            adapter.deleteNotificationById("9876");

            Assert.assertEquals(5, adapter.getItemCount());
            Assert.assertTrue(adapter.getNotifications().equals(notifications));

            adapter.deleteNotificationById("33");

            Assert.assertEquals(4, adapter.getItemCount());
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(0)));
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(1)));
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(3)));
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(4)));
            Assert.assertFalse(adapter.getNotifications().contains(notifications.get(2)));
        });
    }

    @Test
    public void markAsReadIdIsCorrect() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            NotificationCardAdapter adapter = (NotificationCardAdapter) fragment.getNotificationCardRecycleView().getAdapter();
            adapter.setOptUrlAndConnectionPermission(url, true);
            adapter.setItems(notifications);
            adapter.markAsRead(fragment.getContext(), notifications.get(1));

            try {
                RecordedRequest requestToServer = server.takeRequest();
                Assert.assertEquals("POST", requestToServer.getMethod());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Assert.assertEquals(4, adapter.getItemCount());
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(0)));
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(2)));
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(3)));
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(4)));
            Assert.assertFalse(adapter.getNotifications().contains(notifications.get(1)));
        });
    }

    @Test
    public void responseUserContactsIsCorrect() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            NotificationCardAdapter adapter = (NotificationCardAdapter) fragment.getNotificationCardRecycleView().getAdapter();
            adapter.setOptUrlAndConnectionPermission(url, true);
            adapter.setItems(notifications);
            adapter.responseUserContacts(fragment.getContext(), notifications.get(0));

            try {
                RecordedRequest requestToServer = server.takeRequest();
                Assert.assertEquals("POST", requestToServer.getMethod());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Assert.assertEquals(4, adapter.getItemCount());
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(1)));
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(2)));
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(3)));
            Assert.assertTrue(adapter.getNotifications().contains(notifications.get(4)));
            Assert.assertFalse(adapter.getNotifications().contains(notifications.get(0)));
        });
    }

    @Test
    public void formTitleIsCorrect() {
        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            NotificationCardAdapter adapter = (NotificationCardAdapter) fragment.getNotificationCardRecycleView().getAdapter();
            for (int i = 0; i < 5; i++) {
                Assert.assertEquals(expectedTitles.get(i), adapter.formTitle(notifications.get(i)));
            }
        });
    }

    @Test
    public void noConnectionTest() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        FragmentScenario<NotificationsFragment> uf = FragmentScenario.launch(NotificationsFragment.class);
        uf.onFragment(fragment -> {
            NotificationCardAdapter adapter = (NotificationCardAdapter) fragment.getNotificationCardRecycleView().getAdapter();
            adapter.setOptUrlAndConnectionPermission(url, false);
            adapter.markAsRead(fragment.getContext(), notifications.get(1));
            adapter.responseUserContacts(fragment.getContext(), notifications.get(0));
            assertEquals(0, server.getRequestCount());
        });
    }
}
