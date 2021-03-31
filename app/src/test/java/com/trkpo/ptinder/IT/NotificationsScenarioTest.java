package com.trkpo.ptinder.IT;

import android.os.Build;
import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.HTTP.PutRequest;
import com.trkpo.ptinder.pojo.Notification;
import com.trkpo.ptinder.utils.NotificationUtils;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.CONTACT_PATH;
import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.NOTIFICATIONS_PATH;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SUBSCRIPTION_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})

public class NotificationsScenarioTest {
    TestUtils utils;

    @Before
    public void setUp() {
        utils = new TestUtils();
        utils.addUserWithGoogleId("1");
        utils.addUserWithGoogleId("2");
        utils.addUserWithGoogleId("3");
        utils.addPetForUser("2");

        try {
            String url = SUBSCRIPTION_PATH + "/" + "1";
            JSONObject requestObject = new JSONObject();
            requestObject.put("googleId", "2");
            new PostRequest().execute(new PostRequestParams(url, requestObject.toString())).get();
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.e("VOLLEY", "Post request (subscribe) error: " + error.toString());
        }

        utils.addPetForUser("3");
        Long petId = utils.getPetsForUser("3").get(0).getId();
        try {
            String url = FAVOURITE_PATH + "/" + petId + "/user/1";
            new PostRequest().execute(new PostRequestParams(url, "")).get();
        } catch (ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Post request (add to favourite) error: " + error.toString());
        }
    }

    @After
    public void clean() {
        utils.deleteAllPets();
        utils.deleteTestUser();
    }

    @Test
    public void newPetNotificationTest() {
        utils.addPetForUser("2");
        try {
            String url = NOTIFICATIONS_PATH + "/1";
            String response = new GetRequest().execute(url).get();
            List<Notification> notifications = (List<Notification>) NotificationUtils.getNotificationsFromJSON(response);
            assertEquals(1, notifications.size());
            assertEquals("NEW_PET", notifications.get(0).getTitle());
            assertFalse(notifications.get(0).isRead());
            assertEquals("1", notifications.get(0).getAddresseeGoogleId());
            assertEquals("Пользователь Name добавил нового питомца!", notifications.get(0).getText());
        } catch (JSONException | ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Get request (get notifications) error: " + error.toString());
        }
    }

    @Test
    public void editPetNotificationTest() {
        try {
            JSONObject requestObject = PetInfoUtils.setPetToJSON(
                    "NotBarsik",
                    "3",
                    "MALE",
                    "Кот",
                    "",
                    "DONORSHIP",
                    "",
                    null,
                    "2"
            );
            final String requestBody = requestObject.toString();
            Long petId = utils.getPetsForUser("2").get(0).getId();
            String url = PETS_PATH + "/" + petId;
            new PutRequest().execute(new PostRequestParams(url, requestBody)).get();
        } catch (ExecutionException | InterruptedException | IllegalStateException error) {
            Log.e("VOLLEY", "Put request (update pet) error: " + error.toString());
        }

        try {
            String url = NOTIFICATIONS_PATH + "/1";
            String response = new GetRequest().execute(url).get();
            List<Notification> notifications = (List<Notification>) NotificationUtils.getNotificationsFromJSON(response);
            assertEquals(1, notifications.size());
            assertEquals("EDIT_PET", notifications.get(0).getTitle());
            assertFalse(notifications.get(0).isRead());
            assertEquals("1", notifications.get(0).getAddresseeGoogleId());
            assertEquals("Пользователь Name обновил информацию о питомце NotBarsik", notifications.get(0).getText());
        } catch (JSONException | ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Get request (get notifications) error: " + error.toString());
        }
    }

    @Test
    public void editFavouriteNotificationTest() {
        try {
            JSONObject requestObject = PetInfoUtils.setPetToJSON(
                    "NotBarsik",
                    "3",
                    "MALE",
                    "Кот",
                    "",
                    "DONORSHIP",
                    "",
                    null,
                    "3"
            );
            final String requestBody = requestObject.toString();
            Long petId = utils.getPetsForUser("3").get(0).getId();
            String url = PETS_PATH + "/" + petId;
            new PutRequest().execute(new PostRequestParams(url, requestBody)).get();
        } catch (ExecutionException | InterruptedException | IllegalStateException error) {
            Log.e("VOLLEY", "Put request (update pet) error: " + error.toString());
        }

        try {
            String url = NOTIFICATIONS_PATH + "/1";
            String response = new GetRequest().execute(url).get();
            List<Notification> notifications = (List<Notification>) NotificationUtils.getNotificationsFromJSON(response);
            assertEquals(1, notifications.size());
            assertEquals("EDIT_FAVOURITE", notifications.get(0).getTitle());
            assertFalse(notifications.get(0).isRead());
            assertEquals("1", notifications.get(0).getAddresseeGoogleId());
            assertEquals("Информация о Вашем избранном питомце Barsik была обновлена!", notifications.get(0).getText());
        } catch (JSONException | ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Get request (get notifications) error: " + error.toString());
        }
    }

    @Test
    public void requestContactNotificationTest() {
        try {
            String url = CONTACT_PATH + "/request/1/2";
            new PostRequest().execute(new PostRequestParams(url, null)).get();
        } catch (ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Not Success response (request user info): " + error.toString());
        }

        try {
            String url = NOTIFICATIONS_PATH + "/2";
            String response = new GetRequest().execute(url).get();
            List<Notification> notifications = (List<Notification>) NotificationUtils.getNotificationsFromJSON(response);
            assertEquals(1, notifications.size());
            assertEquals("CONTACT_INFO_REQUEST", notifications.get(0).getTitle());
            assertFalse(notifications.get(0).isRead());
            assertEquals("1", notifications.get(0).getAddresseeFromGoogleId());
            assertEquals("2", notifications.get(0).getAddresseeGoogleId());
            assertEquals("Пользователь Name Surname запрашивает информацию о ваших личных данных для связи. Разрешить?", notifications.get(0).getText());
        } catch (JSONException | ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Get request (get notifications) error: " + error.toString());
        }
    }

    @Test
    public void requestContactAnswerNotificationTest() {
        try {
            String url = CONTACT_PATH + "/request/1/2";
            new PostRequest().execute(new PostRequestParams(url, null)).get();
            url = NOTIFICATIONS_PATH + "/2";
            String response = new GetRequest().execute(url).get();
            List<Notification> notifications = (List<Notification>) NotificationUtils.getNotificationsFromJSON(response);
            url = CONTACT_PATH + "/response/" +
                    notifications.get(0).getAddresseeGoogleId() + "/" +
                    notifications.get(0).getAddresseeFromGoogleId() + "/" +
                    notifications.get(0).getId();
            new PostRequest().execute(new PostRequestParams(url, "")).get();
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.e("VOLLEY", "Not Success response (request user info): " + error.toString());
        }

        try {
            String url = NOTIFICATIONS_PATH + "/1";
            String response = new GetRequest().execute(url).get();
            List<Notification> notifications = (List<Notification>) NotificationUtils.getNotificationsFromJSON(response);
            assertEquals(1, notifications.size());
            assertEquals("CONTACT_INFO_ANSWER", notifications.get(0).getTitle());
            assertFalse(notifications.get(0).isRead());
            assertEquals("2", notifications.get(0).getAddresseeFromGoogleId());
            assertEquals("1", notifications.get(0).getAddresseeGoogleId());
            assertEquals("Пользователь Name Surname предоставил вам информацию о своих личных данных для связи.\n" +
                    "Адрес электронной почты: -\n" +
                    "Номер мобильного телефона: -", notifications.get(0).getText());
        } catch (JSONException | ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Get request (get notifications) error: " + error.toString());
        }
    }

    @Test
    public void readNotificationTest() {
        utils.addPetForUser("2");
        try {
            String url = NOTIFICATIONS_PATH + "/1";
            String response = new GetRequest().execute(url).get();
            Notification not = ((List<Notification>) NotificationUtils.getNotificationsFromJSON(response)).get(0);
            url = NOTIFICATIONS_PATH + "/" + not.getId();
            new PostRequest().execute(new PostRequestParams(url, "")).get();

            url = NOTIFICATIONS_PATH + "/1";
            response = new GetRequest().execute(url).get();
            List<Notification> notifications = (List<Notification>) NotificationUtils.getNotificationsFromJSON(response);
            assertEquals(0, notifications.size());
        } catch (JSONException | ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Get request (get notifications) error: " + error.toString());
        }
    }
}
