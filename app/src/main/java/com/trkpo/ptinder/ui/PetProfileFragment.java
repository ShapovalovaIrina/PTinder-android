package com.trkpo.ptinder.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.PetInfo;

public class PetProfileFragment extends Fragment {
    private PetInfo petInfo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pet_profile, container, false);

        petInfo = (PetInfo) getArguments().getSerializable("petInfo");

        CarouselView petIcons = root.findViewById(R.id.carousel_view);
        TextView petName = root.findViewById(R.id.full_pet_info_name);
        TextView petType = root.findViewById(R.id.full_pet_info_type);
        TextView petBreed = root.findViewById(R.id.full_pet_info_breed);
        TextView petAge = root.findViewById(R.id.full_pet_info_age);
        TextView petComment = root.findViewById(R.id.full_pet_info_comment);

        petIcons.setPageCount(petInfo.getIconsAmount());
        petIcons.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(petInfo.getIcon(position));
            }
        });
        petName.setText(petInfo.getName());
        petType.setText(petInfo.getAnimalType());
        petBreed.setText(petInfo.getBreed());
        petAge.setText(petInfo.getAge());
        petComment.setText(petInfo.getComment());

        return root;
    }
}
