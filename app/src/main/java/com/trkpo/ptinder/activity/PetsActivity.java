package com.trkpo.ptinder.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.pojo.PetInfo;

import java.util.Arrays;
import java.util.Collection;

public class PetsActivity extends AppCompatActivity {
    private RecyclerView petCardRecycleView;
    PetCardAdapter petCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pets_activity);

        initRecycleView();
    }

    private void initRecycleView() {
        petCardRecycleView = findViewById(R.id.pet_cards_recycle_view);
        petCardRecycleView.setLayoutManager(new LinearLayoutManager(this));

        petCardAdapter = new PetCardAdapter();
        petCardRecycleView.setAdapter(petCardAdapter);

        loadPets();

        MaterialToolbar appTopBar = findViewById(R.id.top_app_bar_user_pets);
        appTopBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadPets() {
        Collection<PetInfo> pets = getPets();
        petCardAdapter.setItems(pets);
    }

    private Collection<PetInfo> getPets(){
        return Arrays.asList(
                new PetInfo("Симба", "Котик :3", "1 год"),
                new PetInfo("Мотя", "Котик :3", "2 года"),
                new PetInfo("Рэя", "Котик :3", "3 года")
        );
    }
}