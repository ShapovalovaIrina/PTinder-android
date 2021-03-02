package com.trkpo.ptinder.ui;

import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class HelpFragmentTest {
    @Test
    public void correctHelpFragmentCreating() {
        FragmentScenario<HelpFragment> uf = FragmentScenario.launch(HelpFragment.class);
        uf.onFragment(fragment -> {
            TextView text = fragment.getRoot().findViewById(R.id.help);
            String helpText = fragment.getActivity().getResources().getString(R.string.help_text);
            assertEquals(helpText, text.getText().toString());
        });
    }
}