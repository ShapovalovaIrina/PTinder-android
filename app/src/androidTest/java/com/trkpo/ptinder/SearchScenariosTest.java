package com.trkpo.ptinder;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.activity.NavigationActivity;
import com.trkpo.ptinder.adapter.FeedCardAdapter;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.pojo.PetInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.startsWith;

public class SearchScenariosTest {
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
    public void testEmptySearchScenario() {
        testUtils.registration(activityActivityTestRule.getActivity());
        testUtils.addPetForUser(activityActivityTestRule.getActivity());
        testUtils.registration("111");
        testUtils.addPetForUser("111");
        testUtils.registration("222");
        testUtils.addPetForUser("222");

        prepareForSearch();

        onView(withId(R.id.search)).perform(click());
        RecyclerView recyclerView = (RecyclerView) navigationActivityActivityTestRule.getActivity().findViewById(R.id.pet_cards_recycle_view);
        PetCardAdapter adapter = (PetCardAdapter) recyclerView.getAdapter();
        assertThat(adapter.getItemCount(), is(3));
    }


    @Test
    public void testSearchWithParameters() {
        testUtils.registration(activityActivityTestRule.getActivity());
        testUtils.addPetForUser(activityActivityTestRule.getActivity());
        testUtils.registrationWithDifferentCityAndAge("111", "Peter");
        testUtils.addPetForUser("111");
        List<PetInfo> minorUserPets = testUtils.getPetsForUser("111");
        testUtils.updatePetAge("111", minorUserPets.get(0).getId(), "10");
        testUtils.registrationWithDifferentCityAndAge("222", "Moscow");
        testUtils.addPetForUser("222");

        String googleId = "";
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activityActivityTestRule.getActivity());
        if (signInAccount != null) {
            googleId = signInAccount.getId();
        }

        prepareForSearch();

        // search with sex and age
        onView(withId(R.id.radio_m)).perform(click());
        onView(withId(R.id.min_pet_age)).perform(typeText("1"));
        onView(withId(R.id.max_pet_age)).perform(typeText("11"));
        onView(withId(R.id.search)).perform(click());
        RecyclerView recyclerView = (RecyclerView) navigationActivityActivityTestRule.getActivity().findViewById(R.id.pet_cards_recycle_view);
        PetCardAdapter adapter = (PetCardAdapter) recyclerView.getAdapter();
        assertThat(adapter.getItemCount(), is(3));

        // search with city
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_search));
        onView(withId(R.id.location_spinner)).perform(click());
        onData(hasToString(startsWith("Peter"))).perform(click());
        onView(withId(R.id.search)).perform(click());
        assertThat(adapter.getItemCount(), is(3));

        // search with age
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_search));
        onView(withId(R.id.min_pet_age)).perform(typeText("5"));
        onView(withId(R.id.max_pet_age)).perform(typeText("11"));
        onView(withId(R.id.search)).perform(click());
        assertThat(adapter.getItemCount(), is(3));
    }

    private void prepareForSearch() {
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_search));
    }

}
