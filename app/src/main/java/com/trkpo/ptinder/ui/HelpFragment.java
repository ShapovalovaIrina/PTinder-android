package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.trkpo.ptinder.R;

public class HelpFragment extends Fragment {

    private Activity activity;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_help, container, false);
        activity = getActivity();

        return root;
    }

    public View getRoot() {
        return root;
    }
}