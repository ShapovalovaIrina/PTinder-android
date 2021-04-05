package com.trkpo.ptinder;

import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.activity.NavigationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class PetSettingsScenarioTest {
    @Rule
    public ActivityTestRule<LoginActivity> loginActivityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public ActivityTestRule<NavigationActivity> navigationActivityActivityTestRule = new ActivityTestRule<>(NavigationActivity.class);

    public AndroidTestUtils testUtils;

    @Before
    public void setUp() {
        testUtils = new AndroidTestUtils();
    }

    @After
    public void clean() {
        testUtils.deleteAllPets();
        testUtils.deleteTestUser();
    }

    @Test
    public void petSettingsTest() {
        String name = "Barsik";
        String age = "3 года";
        String type = "Кот";
        String breed = "-";
        String purpose = "Переливание крови";
        String comment = "-";
        String updatedComment = "Eto kot. Pyshistii kotik.";

        testUtils.registration(loginActivityActivityTestRule.getActivity());
        testUtils.addPetForUser(loginActivityActivityTestRule.getActivity());
        onView(withId(R.id.sign_in_button)).perform(click());

        /* check initial pet info */
        onView(withId(R.id.pet_cards_recycle_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.full_pet_info_name)).check(matches(withText(name)));
        onView(withId(R.id.full_pet_info_age)).check(matches(withText(age)));
        onView(withId(R.id.full_pet_info_type)).check(matches(withText(type)));
        onView(withId(R.id.full_pet_info_breed)).check(matches(withText(breed)));
        onView(withId(R.id.full_pet_info_purpose)).check(matches(withText(purpose)));
        onView(withId(R.id.full_pet_info_comment)).check(matches(withText(comment)));

        /* go back to user profile */
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_user_profile));

        /* go to pet settings */
        onView(withId(R.id.settings_icon)).perform(click());
        onView(withId(R.id.user_profile_settings_linear_2)).perform(swipeUp());
        onView(withId(R.id.small_pet_cards_recycle_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.comment)).perform(clearText(), typeText(updatedComment));
        closeSoftKeyboard();
        onView(withId(R.id.pet_settings_linear_2)).perform(swipeUp());
        onView(withId(R.id.update_pet)).perform(click());

        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_user_profile));

        /* check updated pet info */
        onView(withId(R.id.pet_cards_recycle_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.full_pet_info_name)).check(matches(withText(name)));
        onView(withId(R.id.full_pet_info_age)).check(matches(withText(age)));
        onView(withId(R.id.full_pet_info_type)).check(matches(withText(type)));
        onView(withId(R.id.full_pet_info_breed)).check(matches(withText(breed)));
        onView(withId(R.id.full_pet_info_purpose)).check(matches(withText(purpose)));
        onView(withId(R.id.full_pet_info_comment)).check(matches(withText(updatedComment)));
    }
}
