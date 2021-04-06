package com.trkpo.ptinder;

import androidx.test.rule.ActivityTestRule;

import com.trkpo.ptinder.activity.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.trkpo.ptinder.MatchersUtils.withRecyclerView;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.startsWith;

public class PetsScenarios {
    @Rule
    public ActivityTestRule<LoginActivity> loginActivityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    public AndroidTestUtils testUtils;

    @Before
    public void setUp() {
        testUtils = new AndroidTestUtils();
        testUtils.deleteTestUser();
        testUtils.deleteAllPets();
    }

    @After
    public void clean() {
        testUtils.deleteTestUser();
        testUtils.deleteAllPets();
    }

    @Test
    public void testCreateNewPetScenario() {
        testUtils.registration(loginActivityActivityTestRule.getActivity());
        onView(withId(R.id.sign_in_button)).perform(click());

        // add pet
        onView(withId(R.id.add_pet)).perform(click());
        onView(withId(R.id.pet_name)).perform(typeText("Barsik"));
        onView(withId(R.id.pet_age)).perform(typeText("3"));
        onView(withId(R.id.radio_button_male_pet)).perform(click());
        onView(withId(R.id.type_spinner)).perform(click());
        onData(hasToString(startsWith("Кот"))).perform(click());
        onView(withId(R.id.pet_registration_scroll_view)).perform(swipeUp(), click());
        onView(withId(R.id.comment)).perform(typeText("Eto kot. Pyshistii kotik."));
        onView(withId(R.id.pet_registration_scroll_view)).perform(swipeUp(), click());
        onView(withId(R.id.comment)).perform(closeSoftKeyboard());
        onView(withId(R.id.save_pet)).perform(click());
        onView(withId(R.id.pet_cards_recycle_view)).check(new RecyclerViewItemCountAssertion(1));

        onView(withRecyclerView(R.id.pet_cards_recycle_view)
                .atPositionOnView(0, R.id.card_pet_name))
                .check(matches(withText("Barsik")));
        onView(withRecyclerView(R.id.pet_cards_recycle_view)
                .atPositionOnView(0, R.id.card_pet_age))
                .check(matches(withText("3 года")));
        onView(withRecyclerView(R.id.pet_cards_recycle_view)
                .atPositionOnView(0, R.id.card_pet_breed))
                .check(matches(withText("Кот")));

        // go to pets card
        onView(withId(R.id.pet_cards_recycle_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.full_pet_info_name)).check(matches(withText("Barsik")));
        onView(withId(R.id.full_pet_info_type)).check(matches(withText("Кот")));
//        onView(withId(R.id.pet_profile_scroll_view)).perform(swipeUp(), click());
        onView(withId(R.id.full_pet_info_breed)).check(matches(withText("-")));
        onView(withId(R.id.full_pet_info_age)).check(matches(withText("3 года")));
//        onView(withId(R.id.pet_profile_scroll_view)).perform(swipeUp(), click());
//        onView(withId(R.id.full_pet_info_purpose)).check(matches(withText("-")));
//        onView(withId(R.id.full_pet_info_comment)).check(matches(withText("Eto kot. Pyshistii kotik.")));
//        onView(withId(R.id.full_pet_info_owner_name)).check(matches(withText("Name Surname")));
    }
}
