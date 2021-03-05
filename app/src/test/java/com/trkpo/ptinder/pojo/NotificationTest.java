package com.trkpo.ptinder.pojo;

import org.junit.Test;

import static org.junit.Assert.*;

public class NotificationTest {
    @Test
    public void parametrizedConstructor() {
        Notification notification = new Notification(
                "",
                "",
                "",
                false,
                "",
                "");
        assertEquals("", notification.getId());
        assertEquals("", notification.getTitle());
        assertEquals("", notification.getText());
        assertEquals(false, notification.isRead());
        assertEquals("", notification.getAddresseeGoogleId());
        assertEquals("", notification.getAddresseeFromGoogleId());
    }

    @Test
    public void notificationSetParameters() {
        String id = "1";
        String title = "Title";
        String text = "Text";
        boolean isRead = true;
        String addresseeGoogleId = "123";
        String addresseeFromGoogleId = "456";
        Notification notification = new Notification(
                "",
                "",
                "",
                false,
                "",
                "");

        notification.setId(id);
        notification.setTitle(title);
        notification.setText(text);
        notification.setRead(isRead);
        notification.setAddresseeGoogleId(addresseeGoogleId);
        notification.setAddresseeFromGoogleId(addresseeFromGoogleId);

        assertEquals(id, notification.getId());
        assertEquals(title, notification.getTitle());
        assertEquals(text, notification.getText());
        assertEquals(isRead, notification.isRead());
        assertEquals(addresseeGoogleId, notification.getAddresseeGoogleId());
        assertEquals(addresseeFromGoogleId, notification.getAddresseeFromGoogleId());
    }
}