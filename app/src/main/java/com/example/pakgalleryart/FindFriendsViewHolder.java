package com.example.pakgalleryart;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsViewHolder extends RecyclerView.ViewHolder {
    CircleImageView mProfileImage;
    TextView mUserName, mStatus;
    public FindFriendsViewHolder(@NonNull View itemView) {
        super(itemView);
        mProfileImage = itemView.findViewById(R.id.all_users_profile_image);
        mUserName = itemView.findViewById(R.id.all_users_profile_name);
        mStatus = itemView.findViewById(R.id.all_users_profile_status);
    }
}
