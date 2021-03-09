package com.trkpo.ptinder.adapter;

import android.os.Build;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.pojo.Feed;
import com.trkpo.ptinder.ui.FeedFragment;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class FeedCardAdapterTest {
    List<Feed> feed = Arrays.asList(
            new Feed("author1", "666", "title1", null),
            new Feed("author2", "777", "title2", null)
    );

    @Test
    public void getFeedsAndSetItemsIsCorrect() {
        FragmentScenario<FeedFragment> uf = FragmentScenario.launch(FeedFragment.class);
        uf.onFragment(fragment -> {
            FeedCardAdapter adapter = (FeedCardAdapter) fragment.getFeedCardRecycleView().getAdapter();
            adapter.setItems(feed);
            Assert.assertEquals(2, adapter.getItemCount());
            Assert.assertArrayEquals(feed.toArray(), adapter.getFeeds().toArray());
        });
    }

    @Test
    public void clearItemsIsCorrect() {
        FragmentScenario<FeedFragment> uf = FragmentScenario.launch(FeedFragment.class);
        uf.onFragment(fragment -> {
            FeedCardAdapter adapter = (FeedCardAdapter) fragment.getFeedCardRecycleView().getAdapter();
            adapter.setItems(feed);

            adapter.clearItems();
            Assert.assertEquals(0, adapter.getItemCount());
            Assert.assertTrue(adapter.getFeeds().isEmpty());
        });
    }
}
