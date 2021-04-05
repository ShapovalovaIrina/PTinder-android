package com.trkpo.ptinder;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.activity.NavigationActivity;
import com.trkpo.ptinder.adapter.PetCardAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class UserRegistrationScenarioTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public ActivityTestRule<NavigationActivity> navigationActivityActivityTestRule = new ActivityTestRule<>(NavigationActivity.class);
    public AndroidTestUtils testUtils;

    @Before
    public void setUp() {
        testUtils = new AndroidTestUtils();
        testUtils.deleteAllPets();
        testUtils.deleteTestUser();
    }

    @After
    public void clean() {
        testUtils.deleteAllPets();
        testUtils.deleteTestUser();
    }

    @Test
    public void userRegistrationTest() {
        onView(withId(R.id.sign_in_button)).perform(click());

        onView(withId(R.id.radio_button_female)).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.login_submit_button)).perform(click());
        onView(withText("Необходимо указать все поля, которые отмечены *")).
                inRoot(withDecorView(not(is(activityActivityTestRule.getActivity().getWindow().getDecorView())))).
                check(matches(isDisplayed()));

        testUtils.registrationUI();

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(navigationActivityActivityTestRule.getActivity());
        String email = "";
        String username = "";
        if (signInAccount != null) {
            email = signInAccount.getEmail();
            username = signInAccount.getDisplayName();
        }

        onView(withId(R.id.username)).check(matches(withText(username)));
        onView(withId(R.id.location)).check(matches(withText("Saint-Petersburg")));
        onView(withId(R.id.user_phone)).check(matches(withText("81234567890")));
        onView(withId(R.id.user_email)).check(matches(withText(email)));

        RecyclerView recyclerView = (RecyclerView) navigationActivityActivityTestRule.getActivity().findViewById(R.id.pet_cards_recycle_view);
        PetCardAdapter adapter = (PetCardAdapter) recyclerView.getAdapter();
        int notificationAmount = adapter.getItemCount();
        assertThat(notificationAmount, is(0));

        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_favourite));

        recyclerView = (RecyclerView) navigationActivityActivityTestRule.getActivity().findViewById(R.id.pet_cards_recycle_view);
        adapter = (PetCardAdapter) recyclerView.getAdapter();
        notificationAmount = adapter.getItemCount();
        assertThat(notificationAmount, is(0));
    }

}
