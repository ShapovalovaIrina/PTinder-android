package com.trkpo.ptinder.activity;

import android.net.Uri;
import android.os.Build;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.trkpo.ptinder.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class NavigationActivityTest {
    @Test
    public void setUserInfo() {
        GoogleSignInAccount signInAccount = Mockito.mock(GoogleSignInAccount.class);
        Mockito.when(signInAccount.getPhotoUrl()).thenReturn(Uri.parse("https://raw.githubusercontent.com/avdosev/github_avatars_generator/master/examples/test2.png"));
        Mockito.when(signInAccount.getDisplayName()).thenReturn("Name Surname");

        ActivityScenario<NavigationActivity> scenario = ActivityScenario.launch(NavigationActivity.class);
        scenario.onActivity(activity -> {
            NavigationView navigationView = activity.findViewById(R.id.nav_view);
            activity.setUserInfo(navigationView.getHeaderView(0), signInAccount);

            TextView username = activity.findViewById(R.id.username_nav_header);
            assertEquals("Name Surname", username.getText().toString());
        });
    }

    @Test
    public void menuItemClick() {
        GoogleSignInAccount signInAccount = Mockito.mock(GoogleSignInAccount.class);
        Mockito.when(signInAccount.getId()).thenReturn("1");

        ActivityScenario<NavigationActivity> scenario = ActivityScenario.launch(NavigationActivity.class);
        scenario.onActivity(activity -> {
            activity.menuItemClick(activity, signInAccount);
        });
    }
}