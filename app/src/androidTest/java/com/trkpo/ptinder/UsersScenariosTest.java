package com.trkpo.ptinder;

import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.activity.NavigationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class UsersScenariosTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public ActivityTestRule<NavigationActivity> navigationActivityActivityTestRule = new ActivityTestRule<>(NavigationActivity.class);
    public AndroidTestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        testUtils = new AndroidTestUtils();
    }

    @After
    public void clean() {
        testUtils.deleteAllPets();
        testUtils.deleteTestUser();
    }

    @Test
    public void testCanChangeUserSettingsFromProfile() {
        testUtils.registration(activityActivityTestRule.getActivity());
        onView(withId(R.id.sign_in_button)).perform(click());

        onView(withId(R.id.settings_icon)).perform(click());
        checkUpdatedSettings(2);
        onView(withId(R.id.username)).check(matches(withText("NewName NewSurname")));
        onView(withId(R.id.user_phone)).check(matches(withText("15-90-11")));
    }

    @Test
    public void testCanChangeUserSettingsFromMenu() {
        testUtils.registration(activityActivityTestRule.getActivity());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));

        checkUpdatedSettings(1);
        onView(withId(R.id.username)).check(matches(withText("NewName NewSurname")));
        onView(withId(R.id.user_email)).check(matches(withText("sof@mail.ru")));
    }

    private void checkUpdatedSettings(int testN) {
        onView(withId(R.id.user_first_name)).perform(clearText());
        onView(withId(R.id.user_first_name)).perform(typeText("NewName"));
        onView(withId(R.id.user_last_name)).perform(clearText());
        onView(withId(R.id.user_last_name)).perform(typeText("NewSurname"));
        onView(withId(R.id.user_last_name)).perform(closeSoftKeyboard());
        if (testN == 1) {
            onView(withId(R.id.user_email)).perform(clearText());
            onView(withId(R.id.user_email)).perform(typeText("sof@mail.ru"));
        } else {
            onView(withId(R.id.user_phone)).perform(clearText());
            onView(withId(R.id.user_phone)).perform(typeText("15-90-11"));
        }
        onView(withId(R.id.user_phone)).perform(closeSoftKeyboard());
        onView(withId(R.id.update_user)).perform(click());
        onView(withId(R.id.update_user)).perform(pressBack());
    }
}