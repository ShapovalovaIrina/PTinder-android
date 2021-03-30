//package com.trkpo.ptinder;
//
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.test.espresso.contrib.NavigationViewActions;
//import androidx.test.rule.ActivityTestRule;
//
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.trkpo.ptinder.activity.LoginActivity;
//import com.trkpo.ptinder.activity.NavigationActivity;
//import com.trkpo.ptinder.adapter.PetCardAdapter;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//
//import static androidx.test.espresso.Espresso.onData;
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.swipeUp;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.contrib.DrawerActions.open;
//import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
//import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.hasToString;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.startsWith;
//
//public class PetsScenariosTest {
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
//    public void testCreateNewPetScenario() {
//        onView(withId(R.id.sign_in_button)).perform(click());
//        testUtils.registration();
//        onView(withId(R.id.add_pet)).perform(click());
//        onView(withId(R.id.pet_name)).perform(typeText("Barsik"));
//        onView(withId(R.id.pet_age)).perform(typeText("3"));
//        onView(withId(R.id.radio_button_male_pet)).perform(click());
//        onView(withId(R.id.type_spinner)).perform(click());
//        onData(hasToString(startsWith("Кот"))).perform(click());
//        onView(withId(R.id.pet_registration_scroll_view)).perform(swipeUp(), click());
//        onView(withId(R.id.pet_registration_scroll_view)).perform(swipeUp(), click());
//        onView(withId(R.id.save_pet)).perform(click());
//        RecyclerView recyclerView = navigationActivityActivityTestRule.getActivity().findViewById(R.id.pet_cards_recycle_view);
//        PetCardAdapter adapter = (PetCardAdapter) recyclerView.getAdapter();
//        int feedsAmount = adapter.getItemCount();
//        assertThat(feedsAmount, is(1));
//        assertThat("Barsik", equalTo(adapter.getPetsList().get(0).getName()));
//        testUtils.deleteAllPets();
//        testUtils.deleteTestUser();
//    }
//
//    @Test
//    public void testUpdatePetProfileScenario() {
//        onView(withId(R.id.sign_in_button)).perform(click());
//        testUtils.registration();
//        String googleId = "";
//        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(navigationActivityActivityTestRule.getActivity());
//        if (signInAccount != null) {
//            googleId = signInAccount.getId();
//        }
//        testUtils.addPetForUser(googleId);
//        onView(withId(R.id.drawer_layout)).perform(open());
//        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_user_profile));
//        onView(withId(R.id.pet_cards_recycle_view)).perform(actionOnItemAtPosition(0, click()));
//        onView(withId(R.id.full_pet_info_name)).check(matches(withText("Barsik")));
//        onView(withId(R.id.full_pet_info_type)).check(matches(withText("Кот")));
//        onView(withId(R.id.full_pet_info_breed)).check(matches(withText("-")));
//        onView(withId(R.id.full_pet_info_purpose)).check(matches(withText("Переливание крови")));
//        onView(withId(R.id.full_pet_info_age)).check(matches(withText("3 года")));
//
//        testUtils.deleteAllPets();
//        testUtils.deleteTestUser();
//    }
//}
