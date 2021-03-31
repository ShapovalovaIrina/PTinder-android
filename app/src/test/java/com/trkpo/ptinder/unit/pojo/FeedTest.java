package com.trkpo.ptinder.unit.pojo;

import android.graphics.Bitmap;

import com.trkpo.ptinder.pojo.Feed;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class FeedTest {
    @Test
    public void feedConstructor() {
        String author = "author";
        String score = "score";
        String title = "title";
        Bitmap content = Mockito.mock(Bitmap.class);

        Feed feed = new Feed(author, score, title, content);

        assertEquals(author, feed.getAuthor());
        assertEquals(score, feed.getScore());
        assertEquals(title, feed.getTitle());
        assertEquals(content, feed.getContent());
    }

    @Test
    public void feedSetMethods() {
        String authorInit = "";
        String scoreInit = "";
        String titleInit = "";
        Bitmap contentInit = Mockito.mock(Bitmap.class);

        String author = "author";
        String score = "score";
        String title = "title";
        Bitmap content = Mockito.mock(Bitmap.class);

        Feed feed = new Feed(authorInit, scoreInit, titleInit, contentInit);

        feed.setAuthor(author);
        feed.setScore(score);
        feed.setTitle(title);
        feed.setContent(content);

        assertEquals(author, feed.getAuthor());
        assertEquals(score, feed.getScore());
        assertEquals(title, feed.getTitle());
        assertEquals(content, feed.getContent());
    }
}