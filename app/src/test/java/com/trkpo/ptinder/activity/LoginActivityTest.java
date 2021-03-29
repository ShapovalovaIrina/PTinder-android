package com.trkpo.ptinder.activity;

import android.os.Build;

import androidx.test.core.app.ActivityScenario;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

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
public class LoginActivityTest {
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
    public void onClickAction() {
        FirebaseUser user = Mockito.mock(FirebaseUser.class);

        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            activity.setOptUrlAndConnectionPermission("", true);

            activity.onClickAction();

            activity.setCurrentUser(user);
            activity.onClickAction();
        });
    }

    @Test
    public void isUserExist() {
        server.enqueue(new MockResponse().setBody("true"));
        server.enqueue(new MockResponse().setBody("false"));
        server.enqueue(new MockResponse().setBody("other response"));

        String url = server.url("/").toString();

        GoogleSignInAccount signInAccount = Mockito.mock(GoogleSignInAccount.class);
        Mockito.when(signInAccount.getId()).thenReturn("1");

        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            activity.setOptUrlAndConnectionPermission(url, true);
            activity.isUserExists(signInAccount.getId());
            /* If opt url (which was set previously to url value) is null, that means postUserInfo method was called */
            assertNull(activity.getOptUrl());

            activity.setOptUrlAndConnectionPermission(url, true);
            activity.isUserExists(signInAccount.getId());
            /* If opt url (which was set previously to url value) is null, that means postUserInfo method was called */
            assertNull(activity.getOptUrl());

            activity.setOptUrlAndConnectionPermission(url, true);
            activity.isUserExists(signInAccount.getId());
            /* If opt url (which was set previously to url value) is null, that means postUserInfo method was called */
            assertNull(activity.getOptUrl());
        });
    }

    @Test
    public void noConnection() {
        ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);
        scenario.onActivity(activity -> {
            activity.setOptUrlAndConnectionPermission("", false);
            activity.onClickAction();
            /* If opt url (which was set previously to url value) is null, that means postUserInfo method was called
             * In our case it should not be called*/
            assertEquals("", activity.getOptUrl());

            activity.setOptUrlAndConnectionPermission("", false);
            activity.isUserExists("1");
            /* If opt url (which was set previously to url value) is null, that means postUserInfo method was called
             * In our case it should not be called*/
            assertEquals("", activity.getOptUrl());
        });
    }
}