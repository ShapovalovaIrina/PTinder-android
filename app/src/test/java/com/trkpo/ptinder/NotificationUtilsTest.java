package com.trkpo.ptinder;

import com.trkpo.ptinder.pojo.Notification;
import com.trkpo.ptinder.utils.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class NotificationUtilsTest {
    private String id;
    private String title;
    private String text;
    private boolean isRead;
    private String addresseeGoogleId;
    private String addresseeFromGoogleId;

    @Before
    public void init() {
        id = "1";
        title = "Some cool title";
        text = "Even more cool text";
        isRead = false;
        addresseeGoogleId = "2";
        addresseeFromGoogleId = "3";
    }

    @Test
    public void constructorIsCorrect() {
        Notification notification = new Notification(id, title, text, isRead, addresseeGoogleId, addresseeFromGoogleId);
        assertEquals(notification.getId(), id);
        assertEquals(notification.getTitle(), title);
        assertEquals(notification.getText(), text);
        assertEquals(notification.isRead(), isRead);
        assertEquals(notification.getAddresseeGoogleId(), addresseeGoogleId);
        assertEquals(notification.getAddresseeFromGoogleId(), addresseeFromGoogleId);
    }

    @Test
    public void setUpValuesIsCorrect() {
        Notification notification = new Notification("", "", "", true, "", "");
        notification.setId(id);
        assertEquals(notification.getId(), id);
        notification.setTitle(title);
        assertEquals(notification.getTitle(), title);
        notification.setText(text);
        assertEquals(notification.getText(), text);
        notification.setRead(isRead);
        assertEquals(notification.isRead(), isRead);
        notification.setAddresseeGoogleId(addresseeGoogleId);
        assertEquals(notification.getAddresseeGoogleId(), addresseeGoogleId);
        notification.setAddresseeFromGoogleId(addresseeFromGoogleId);
        assertEquals(notification.getAddresseeFromGoogleId(), addresseeFromGoogleId);
    }

    @Test
    public void notificationsFromJSONIsCorrect() throws JSONException {
        JSONObject notificationJSON = new JSONObject();
        JSONObject addresseeGoogleIdJSON = new JSONObject();
        notificationJSON.put("id", id);
        notificationJSON.put("type", title);
        notificationJSON.put("text", text);
        notificationJSON.put("read", isRead);
        addresseeGoogleIdJSON.put("googleId", addresseeGoogleId);
        notificationJSON.put("addressee", addresseeGoogleIdJSON);
        notificationJSON.put("addresseeFromId", addresseeFromGoogleId);

        JSONArray notifications = new JSONArray();
        notifications.put(notificationJSON);

        Collection<Notification> result = NotificationUtils.getNotificationsFromJSON(notifications.toString());
        assertEquals(result.size(), 1);
        for (Notification n : result) {
            assertEquals(n.getId(), id);
            assertEquals(n.getTitle(), title);
            assertEquals(n.getText(), text);
            assertEquals(n.isRead(), isRead);
            assertEquals(n.getAddresseeGoogleId(), addresseeGoogleId);
            assertEquals(n.getAddresseeFromGoogleId(), addresseeFromGoogleId);
        }
    }
}
