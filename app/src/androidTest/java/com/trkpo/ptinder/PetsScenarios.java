package com.trkpo.ptinder;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.rule.ActivityTestRule;

import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.activity.NavigationActivity;
import com.trkpo.ptinder.adapter.PetCardAdapter;

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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class PetsScenarios {
    @Rule
    public ActivityTestRule<LoginActivity> loginActivityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

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
        RecyclerView recyclerView = navigationActivityActivityTestRule.getActivity().findViewById(R.id.pet_cards_recycle_view);
        PetCardAdapter adapter = (PetCardAdapter) recyclerView.getAdapter();
        int petsAmount = adapter.getItemCount();
        assertThat(petsAmount, is(1));
        assertThat("Barsik", equalTo(adapter.getPetsList().get(0).getName()));
        assertThat("3 года", equalTo(adapter.getPetsList().get(0).getAge()));
        assertThat("Кот", equalTo(adapter.getPetsList().get(0).getAnimalType()));

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
