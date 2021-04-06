package com.trkpo.ptinder;

import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.pojo.PetInfo;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class NotificationScenarioTest {
    @Rule
    public ActivityTestRule<LoginActivity> loginActivityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    public AndroidTestUtils testUtils;

    @Before
    public void setUp() {
        testUtils = new AndroidTestUtils();
    }

    @After
    public void clean() {
        testUtils.deleteTestUser();
        testUtils.deleteAllPets();
    }

    @Test
    public void getAndReadNotificationTest() {
        String name = "Barsik";
        String age = "3 года";
        String type = "Кот";
        String breed = "-";
        String purpose = "Переливание крови";
        String comment = "-";
        String updatedName = "Pyshistii kotik";

        /* add major user */
        testUtils.registration(loginActivityActivityTestRule.getActivity());

        /* add minor user */
        testUtils.registration("1234567890");
        testUtils.addPetForUser("1234567890");
        List<PetInfo> minorUserPets = testUtils.getPetsForUser("1234567890");

        /* sign in with major user */
        onView(withId(R.id.sign_in_button)).perform(click());

        /* add favourite pet for major user */
        testUtils.addPetInFavouriteForCurrentUser(loginActivityActivityTestRule.getActivity(), minorUserPets.get(0).getId());

        /* go back to favourite pets */
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_favourite));

        /* check initial pet info */
        onView(withId(R.id.pet_cards_recycle_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.full_pet_info_name)).check(matches(withText(name)));
        onView(withId(R.id.full_pet_info_age)).check(matches(withText(age)));
        onView(withId(R.id.full_pet_info_type)).check(matches(withText(type)));
        onView(withId(R.id.full_pet_info_breed)).check(matches(withText(breed)));
        onView(withId(R.id.full_pet_info_purpose)).check(matches(withText(purpose)));
        onView(withId(R.id.full_pet_info_comment)).check(matches(withText(comment)));

        /* update pet */
        testUtils.updatePetName("1234567890", minorUserPets.get(0).getId(), updatedName);

        /* go to notifications, check them, read */
        onView(withId(R.id.notification)).perform(click());

        onView(withId(R.id.notification_cards_recycle_view)).check(new RecyclerViewItemCountAssertion(1));
        onView(allOf(withId(R.id.notification_title))).check(matches(withText("Изменения в анкетах Ваших избранных питомцев")));
        onView(allOf(withId(R.id.notification_text))).check(matches(withText("Информация о Вашем избранном питомце Barsik была обновлена!")));

        onView(allOf(withId(R.id.accept_btn))).perform(click());

        onView(withId(R.id.notification_cards_recycle_view)).check(new RecyclerViewItemCountAssertion(0));
    }
}
