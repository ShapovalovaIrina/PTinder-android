package com.trkpo.ptinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.PetInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PetCardAdapter extends RecyclerView.Adapter<PetCardAdapter.ViewHolder> {
    private List<PetInfo> petsList = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            petName.setText(petInfo.getName() != null ? petInfo.getName() : "");
            petBreed.setText(petInfo.getBreed() != null ? petInfo.getBreed() : "");
            petAge.setText(petInfo.getAge() != null ? petInfo.getAge() : "");
            if (petInfo.getIcon() != null) {
                petImage.setImageBitmap(petInfo.getIcon());
            }
        }

        @Override
        public void onClick(View v) {
            // open pet
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
