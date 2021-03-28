package com.trkpo.ptinder;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.activity.NavigationActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

//@RunWith(AndroidJUnit4.class)
public class NewsScenarioTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public ActivityTestRule<NavigationActivity> navigationActivityActivityTestRule = new ActivityTestRule<>(NavigationActivity.class);

    TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        testUtils = new TestUtils();
    }

    @Test
    public void testUserCanGet15Feeds() throws InterruptedException {
        onView(withId(R.id.sign_in_button)).perform(click());
        testUtils.registration();
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_feed));
        Thread.sleep(5000);
        RecyclerView recyclerView = (RecyclerView) navigationActivityActivityTestRule.getActivity().findViewById(R.id.feed_cards_recycle_view);
        int feedsAmount = recyclerView.getAdapter().getItemCount();
        assertThat(feedsAmount, is(15));
        testUtils.deleteTestUser();
    }
}
