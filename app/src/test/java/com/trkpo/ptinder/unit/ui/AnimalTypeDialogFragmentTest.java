package com.trkpo.ptinder.unit.ui;

import android.os.Build;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.R;
import com.trkpo.ptinder.ui.PetRegistrationFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static com.trkpo.ptinder.ui.AnimalTypeDialogFragment.getTypesFromJSON;
import static com.trkpo.ptinder.ui.AnimalTypeDialogFragment.saveType;
import static com.trkpo.ptinder.ui.AnimalTypeDialogFragment.showOpenDialog;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class AnimalTypeDialogFragmentTest {
    private final MockWebServer server = new MockWebServer();

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void showOpenDialogIsCorrect() {
        String expectedType = "anotherType";
        server.enqueue(new MockResponse().setBody(""));
        server.enqueue(new MockResponse().setBody("[{\"id\":1, \"type\":\"" + expectedType + "\"}]"));
        String url = server.url("/").toString();

        FragmentScenario<PetRegistrationFragment> uf = FragmentScenario.launch(PetRegistrationFragment.class);
        uf.onFragment(fragment -> {
            try {
                saveType(fragment.getActivity(), fragment.getPetTypeAdapter(), expectedType, url);

                RecordedRequest requestToServer = server.takeRequest();
                Assert.assertEquals("POST", requestToServer.getMethod());
                String strRequest = requestToServer.getBody().readString(StandardCharsets.UTF_8);
                JSONObject jsonObject = new JSONObject(strRequest);
                Assert.assertEquals(expectedType, jsonObject.getString("type"));

                requestToServer = server.takeRequest();
                Assert.assertEquals("GET", requestToServer.getMethod());

                Assert.assertEquals(expectedType, fragment.getPetTypeAdapter().getItem(0));

            } catch (InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void noConnectionTest() {
        server.enqueue(new MockResponse().setBody(""));
        String url = server.url("/").toString();

        FragmentScenario<PetRegistrationFragment> uf = FragmentScenario.launch(PetRegistrationFragment.class,
                null,
                R.style.AppTheme,
                null);
        uf.onFragment(fragment -> {
            showOpenDialog(fragment.getActivity(), fragment.getPetTypeAdapter());
            saveType(fragment.getActivity(),
                    fragment.getPetTypeAdapter(),
                    "expectedType",
                    url,
                    "false");
            assertEquals(0, server.getRequestCount());
        });
    }

    @Test
    public void getTypesFromJSONIsCorrect() {
        final String typesJson =
                "[" +
                        "{\"id\":1, \"type\":type1}," +
                        "{\"id\":2, \"type\":anotherType}," +
                        "{\"id\":6, \"type\":nextType}" +
                "]";
        try {
            ArrayList<String> types = (ArrayList<String>) getTypesFromJSON(typesJson);
            Assert.assertEquals("type1", types.get(0));
            Assert.assertEquals("anotherType", types.get(1));
            Assert.assertEquals("nextType", types.get(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
