package com.example.pakgalleryart;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    CircleImageView mProfileImage;
    TextView mUserName, mDate;
    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        mProfileImage = itemView.findViewById(R.id.all_users_profile_image);
        mUserName = itemView.findViewById(R.id.all_users_profile_name);
        mDate = itemView.findViewById(R.id.all_users_profile_status);
    }
}
