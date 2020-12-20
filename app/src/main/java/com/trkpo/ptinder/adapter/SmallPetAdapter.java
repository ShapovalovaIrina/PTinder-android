package com.trkpo.ptinder.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.PetInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SmallPetAdapter extends RecyclerView.Adapter<SmallPetAdapter.ViewHolder> {
    private List<PetInfo> petsList = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PetInfo petInfo;
        private TextView petName;
        private TextView petBreed;

        public ViewHolder(View itemView) {
            super(itemView);
            this.petName = itemView.findViewById(R.id.card_pet_name);
            this.petBreed = itemView.findViewById(R.id.card_pet_breed);

        }

        public void bind(PetInfo petInfo) {
            this.petInfo = petInfo;
            petName.setText(petInfo.getName() != null ? petInfo.getName() : "");
            petBreed.setText(petInfo.getAnimalType() != null ? petInfo.getAnimalType() : "");
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("petInfo", petInfo);
            NavController navController = Navigation.findNavController(v);
        }

    }

    @NonNull
    @Override
    public SmallPetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.small_pet_card, parent, false);
        return new SmallPetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SmallPetAdapter.ViewHolder holder, int position) {
        holder.bind(petsList.get(position));
    }

    @Override
    public int getItemCount() {
        return petsList.size();
    }

    public void setItems(Collection<PetInfo> pets) {
        petsList.addAll(pets);
        notifyDataSetChanged();
    }

    public void clearItems() {
        petsList.clear();
        notifyDataSetChanged();
    }
}

