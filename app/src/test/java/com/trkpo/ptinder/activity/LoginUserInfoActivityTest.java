package com.trkpo.ptinder.activity;

import android.net.Uri;
import android.os.Build;

import androidx.test.core.app.ActivityScenario;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class LoginUserInfoActivityTest {
    private final MockWebServer server = new MockWebServer();

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void correctCreation() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        GoogleSignInAccount signInAccount = Mockito.mock(GoogleSignInAccount.class);
        Mockito.when(signInAccount.getDisplayName()).thenReturn("Name Surname");
        Mockito.when(signInAccount.getEmail()).thenReturn("Email@email.com");
        Mockito.when(signInAccount.getPhotoUrl()).thenReturn(Uri.parse("https://fraufluger.ru/wp-content/uploads/2020/09/kosh.jpg"));
        Mockito.when(signInAccount.getId()).thenReturn("1");

        ActivityScenario<LoginUserInfoActivity> scenario = ActivityScenario.launch(LoginUserInfoActivity.class);
        scenario.onActivity(activity -> {
            activity.setGoogleSignInAccount(signInAccount);
            activity.setOptUrlAndConnectionPermission(url, true);
            activity.getRg().check(R.id.radio_button_female);
            activity.getRg().check(R.id.radio_button_male);
            activity.getCityEdit().setText("Saint-Petersburg");

            activity.getSubmitButton().performClick();

            /* If opt url (which was set previously to url value) is null, that means postUserInfo method was called */
            assertNull(activity.getOptUrl());
        });
    }

    @Test
    public void emptyGender() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        ActivityScenario<LoginUserInfoActivity> scenario = ActivityScenario.launch(LoginUserInfoActivity.class);
        scenario.onActivity(activity -> {
            activity.setOptUrlAndConnectionPermission(url, true);
            activity.getCityEdit().setText("Saint-Petersburg");

            activity.getSubmitButton().performClick();

            /* If opt url (which was set previously to url value) is null, that means postUserInfo method was called
            * In our case it should not be called*/
            assertEquals(url, activity.getOptUrl());
        });
    }

    @Test
    public void noConnection() {
        String url = server.url("/").toString();
        ActivityScenario<LoginUserInfoActivity> scenario = ActivityScenario.launch(LoginUserInfoActivity.class);
        scenario.onActivity(activity -> {
            activity.setOptUrlAndConnectionPermission(url, false);
            activity.postUserInfo();

            /* If opt url (which was set previously to url value) is null, that means postUserInfo method was called
             * In our case it should not be called*/
            assertEquals(url, activity.getOptUrl());
        });
    }
}