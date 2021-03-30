//package com.trkpo.ptinder;
//
//import androidx.test.espresso.contrib.NavigationViewActions;
//import androidx.test.rule.ActivityTestRule;
//
//import com.trkpo.ptinder.activity.LoginActivity;
//import com.trkpo.ptinder.activity.NavigationActivity;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.clearText;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static androidx.test.espresso.action.ViewActions.pressBack;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.contrib.DrawerActions.open;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//
//public class UsersScenariosTest {
//    @Rule
//    public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
//
//    @Rule
//    public ActivityTestRule<NavigationActivity> navigationActivityActivityTestRule = new ActivityTestRule<>(NavigationActivity.class);
//    public TestUtils testUtils;
//
//    @Before
//    public void setUp() throws Exception {
//        testUtils = new TestUtils();
//    }
//
//    @Test
//    public void testCanChangeUserSettingsFromProfile() {
//        onView(withId(R.id.sign_in_button)).perform(click());
//        testUtils.registration();
//
//        onView(withId(R.id.settings_icon)).perform(click());
//        checkUpdatedSettings();
//        onView(withId(R.id.username)).check(matches(withText("Name Surname")));
//
//        testUtils.deleteAllPets();
//        testUtils.deleteTestUser();
//    }
//
//    @Test
//    public void testCanChangeUserSettingsFromMenu() {
//        onView(withId(R.id.sign_in_button)).perform(click());
//        testUtils.registration();
//        onView(withId(R.id.drawer_layout)).perform(open());
//        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));
//
//        checkUpdatedSettings();
//        onView(withId(R.id.username)).check(matches(withText("Name Surname")));
//
//        testUtils.deleteAllPets();
//        testUtils.deleteTestUser();
//    }
//
//    private void checkUpdatedSettings() {
//        onView(withId(R.id.user_first_name)).perform(clearText());
//        onView(withId(R.id.user_first_name)).perform(typeText("Name"));
//        onView(withId(R.id.user_last_name)).perform(clearText());
//        onView(withId(R.id.user_last_name)).perform(typeText("Surname"));
//        onView(withId(R.id.user_last_name)).perform(closeSoftKeyboard());
//        onView(withId(R.id.update_user)).perform(click());
//        onView(withId(R.id.update_user)).perform(pressBack());
//    }
//}
