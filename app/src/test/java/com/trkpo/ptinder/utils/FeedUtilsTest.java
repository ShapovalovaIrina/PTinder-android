package com.trkpo.ptinder.utils;

import android.graphics.Bitmap;

import com.trkpo.ptinder.pojo.Feed;
import com.trkpo.ptinder.utils.FeedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FeedUtilsTest {
    private String author;
    private String score;
    private String title;
    private Bitmap content;

    @Before
    public void init() {
        author = "Mike";
        score = "2000";
        title = "Definitely cool title";
        content = Mockito.mock(Bitmap.class);
    }

    @Test
    public void setUpIsCorrect() {
        Feed feed = new Feed("", "", "", null);
        feed.setAuthor(author);
        feed.setScore(score);
        feed.setTitle(title);
        feed.setContent(content);
        assertEquals(feed.getAuthor(), author);
        assertEquals(feed.getScore(), score);
        assertEquals(feed.getTitle(), title);
        assertEquals(feed.getContent(), content);
    }

    @Test
    public void newsFromJSONIsCorrect() throws JSONException {
        JSONObject news = new JSONObject();
        news.put("author", author);
        news.put("score", score);
        news.put("title", title);
        news.put("content", content);

        JSONArray newsArray = new JSONArray();
        newsArray.put(news);

        Collection<Feed> result = FeedUtils.getNewsFromJSON(newsArray.toString());
        assertEquals(result.size(), 1);
        for (Feed f : result) {
            assertEquals(f.getAuthor(), author);
            assertEquals(f.getScore(), score);
            assertEquals(f.getTitle(), title);
            assertNull(f.getContent());
        }
    }
}
