package com.trkpo.ptinder.adapter;

import android.os.Build;
import android.widget.ImageView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.pojo.PetInfo;
import com.trkpo.ptinder.ui.OtherUserProfileFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class PetCardAdapterTest {
    private MockWebServer server = new MockWebServer();

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void petAdapterDeletePetFromFavourite() {
        server.enqueue(new MockResponse().setBody("Not empty body"));
        String url = server.url("/").toString();

        PetInfo petInfo = new PetInfo();
        ImageView favourite = Mockito.mock(ImageView.class);
        ImageView petImage = Mockito.mock(ImageView.class);

        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            Mockito.when(petImage.getContext()).thenReturn(fragment.getContext());

            PetCardAdapter adapter = fragment.getPetCardAdapter();
            adapter.setOptUrlAndConnectionPermission(url, true);
            adapter.deleteFromFavourite(fragment.getView(),petInfo, favourite, petImage);

            assertFalse(petInfo.isFavourite());
        });
    }

    @Test
    public void petAdapterAddPetToFavourite() {
        server.enqueue(new MockResponse().setBody("Not empty body"));
        String url = server.url("/").toString();

        PetInfo petInfo = new PetInfo();
        ImageView favourite = Mockito.mock(ImageView.class);
        ImageView petImage = Mockito.mock(ImageView.class);

        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            Mockito.when(petImage.getContext()).thenReturn(fragment.getContext());

            PetCardAdapter adapter = fragment.getPetCardAdapter();
            adapter.setOptUrlAndConnectionPermission(url, true);
            adapter.addToFavourite(fragment.getView(),petInfo, favourite, petImage);

            assertTrue(petInfo.isFavourite());
        });
    }

    @Test
    public void noConnection() {
        PetInfo petInfo = new PetInfo();
        ImageView favourite = Mockito.mock(ImageView.class);
        ImageView petImage = Mockito.mock(ImageView.class);

        FragmentScenario<OtherUserProfileFragment> uf = FragmentScenario.launch(OtherUserProfileFragment.class);
        uf.onFragment(fragment -> {
            Mockito.when(petImage.getContext()).thenReturn(fragment.getContext());

            PetCardAdapter adapter = fragment.getPetCardAdapter();

            petInfo.setFavourite(false);
            adapter.setOptUrlAndConnectionPermission("", false);
            adapter.addToFavourite(fragment.getView(),petInfo, favourite, petImage);
            assertFalse(petInfo.isFavourite());

            petInfo.setFavourite(true);
            adapter.setOptUrlAndConnectionPermission("", false);
            adapter.deleteFromFavourite(fragment.getView(),petInfo, favourite, petImage);
            assertTrue(petInfo.isFavourite());
        });
    }
}