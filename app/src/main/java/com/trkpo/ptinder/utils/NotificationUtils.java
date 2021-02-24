package com.trkpo.ptinder.utils;

import com.trkpo.ptinder.pojo.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NotificationUtils {
    public static Collection<Notification> getNotificationsFromJSON(String response) throws JSONException {
        List<Notification> notifications = new ArrayList<>();
        JSONArray jArray = new JSONArray(response);

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonNotification = jArray.getJSONObject(i);
            String title = jsonNotification.getString("type");
            String id = jsonNotification.getString("id");
            String text = jsonNotification.getString("text");
            boolean isRead = jsonNotification.getBoolean("read");
            String addresseeGoogleId = jsonNotification.getJSONObject("addressee").getString("googleId");
            String addresseeFromGoogleId = jsonNotification.getString("addresseeFromId");
            notifications.add(new Notification(id, title, text, isRead, addresseeGoogleId, addresseeFromGoogleId));
        }
        return notifications;
    }
}
