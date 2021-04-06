package com.trkpo.ptinder;

import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import com.trkpo.ptinder.activity.LoginActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.trkpo.ptinder.MatchersUtils.withRecyclerView;
import static org.hamcrest.Matchers.not;

public class FeedsScenarioTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

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
    public void testUserCanGet15Feeds() throws InterruptedException {
        testUtils.registration(activityActivityTestRule.getActivity());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_feed));
        Thread.sleep(5000);

        onView(withId(R.id.feed_cards_recycle_view)).check(new RecyclerViewItemCountAssertion(15));
        onView(withRecyclerView(R.id.feed_cards_recycle_view)
                .atPositionOnView(0, R.id.feed_title))
                .check(matches(withText(not("Title"))));
        onView(withRecyclerView(R.id.feed_cards_recycle_view)
                .atPositionOnView(0, R.id.feed_score))
                .check(matches(withText(not("Score"))));
        onView(withRecyclerView(R.id.feed_cards_recycle_view)
                .atPositionOnView(0, R.id.feed_author))
                .check(matches(withText(not("Author"))));
    }
}
