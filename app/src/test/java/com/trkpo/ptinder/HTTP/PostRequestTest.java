package com.trkpo.ptinder.HTTP;

import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PostRequestTest {
    private MockWebServer server = new MockWebServer();
    private String postBody = "Text body to server";
    private String body = "Text response from server";

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void correctPostRequestResponseCode200() throws ExecutionException, InterruptedException {
        server.enqueue(new MockResponse()
                .setBody(body)
                .setResponseCode(200));
        String url = server.url("/").toString();

        String response = new PostRequest().execute(new PostRequestParams(url, postBody)).get();
        assertEquals(body, response);
    }

    @Test
    public void correctPostRequestResponseCode500() throws ExecutionException, InterruptedException {
        server.enqueue(new MockResponse()
                .setBody(body)
                .setResponseCode(500));
        String url = server.url("/").toString();

        String response = new PostRequest().execute(new PostRequestParams(url, postBody)).get();
        assertEquals("", response);
    }

    @Test
    public void connectionError() throws ExecutionException, InterruptedException {
        String url = server.url("/").toString();

        String response = new PostRequest().execute(new PostRequestParams(url, postBody)).get();
        assertEquals("", response);
    }
}