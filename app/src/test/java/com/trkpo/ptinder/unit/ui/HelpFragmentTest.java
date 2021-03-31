package com.trkpo.ptinder.unit.ui;

import android.os.Build;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.R;
import com.trkpo.ptinder.ui.HelpFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
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