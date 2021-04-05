package com.trkpo.ptinder;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.activity.NavigationActivity;
import com.trkpo.ptinder.adapter.FeedCardAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FeedsScenarioTest {
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
    public void testUserCanGet15Feeds() throws InterruptedException {
        testUtils.registration(activityActivityTestRule.getActivity());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_feed));
        Thread.sleep(5000);
        RecyclerView recyclerView = (RecyclerView) navigationActivityActivityTestRule.getActivity().findViewById(R.id.feed_cards_recycle_view);
        FeedCardAdapter adapter = (FeedCardAdapter) recyclerView.getAdapter();
        int feedsAmount = adapter.getItemCount();
        assertThat(feedsAmount, is(15));
        assertFalse(adapter.getFeeds().get(0).getTitle().isEmpty());
        assertFalse(adapter.getFeeds().get(0).getScore().isEmpty());
        assertFalse(adapter.getFeeds().get(0).getAuthor().isEmpty());
    }
}
