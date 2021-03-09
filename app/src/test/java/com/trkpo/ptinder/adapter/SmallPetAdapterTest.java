package com.trkpo.ptinder.adapter;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.pojo.PetInfo;
import com.trkpo.ptinder.ui.SettingsFragment;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class SmallPetAdapterTest {
    List<PetInfo> pets = Arrays.asList(
            new PetInfo(111L, "", "", "", "", "", "", "", new ArrayList<Bitmap>(), 1, false),
            new PetInfo(112L, "", "", "", "", "", "", "", new ArrayList<Bitmap>(), 1, false)
    );

    @Test
    public void getAndSetItemsIsCorrect() {
        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> {
            SmallPetAdapter adapter = (SmallPetAdapter) fragment.getSmallPetRecycleView().getAdapter();
            adapter.setItems(pets);
            Assert.assertEquals(2, adapter.getItemCount());
            Assert.assertTrue(adapter.getItems().equals(pets));
        });
    }

    @Test
    public void clearItemsIsCorrect() {
        FragmentScenario<SettingsFragment> uf = FragmentScenario.launch(SettingsFragment.class);
        uf.onFragment(fragment -> {
            SmallPetAdapter adapter = (SmallPetAdapter) fragment.getSmallPetRecycleView().getAdapter();
            adapter.setItems(pets);

            adapter.clearItems();
            Assert.assertEquals(0, adapter.getItemCount());
            Assert.assertTrue(adapter.getItems().isEmpty());
        });
    }
}
