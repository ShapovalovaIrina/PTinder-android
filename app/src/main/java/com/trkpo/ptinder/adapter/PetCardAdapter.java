package com.trkpo.ptinder.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class PetCardAdapter extends RecyclerView.Adapter<PetCardAdapter.ViewHolder> {
    private List<PetInfo> petsList = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PetInfo petInfo;
        private TextView petName;
        private TextView petBreed;
        private TextView petAge;
        private ImageView petImage;

        public ViewHolder(View itemView) {
            super(itemView);
            this.petName = itemView.findViewById(R.id.card_pet_name);
            this.petBreed = itemView.findViewById(R.id.card_pet_breed);
            this.petAge = itemView.findViewById(R.id.card_pet_age);
            this.petImage = itemView.findViewById(R.id.card_pet_image);
            itemView.setOnClickListener(this);
        }

        public void bind(PetInfo petInfo) {
            this.petInfo = petInfo;
            petName.setText(petInfo.getName() != null ? petInfo.getName() : "");
            petBreed.setText(petInfo.getBreed() != null ? petInfo.getBreed() : "");
            petAge.setText(petInfo.getAge() != null ? petInfo.getAge() : "");
            if (petInfo.getIconsAmount() != 0) {
                /* Set first image as main */
                petImage.setImageBitmap(petInfo.getIcons().get(0));
            }
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("petInfo", petInfo);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_nav_user_profile_to_nav_pet_profile, bundle);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
