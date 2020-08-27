package com.example.pakgalleryart;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsHolder extends RecyclerView.ViewHolder {
    TextView mUerName, mComment, mDate, mTime;
    CircleImageView mProfileImage;
    public CommentsHolder(@NonNull View itemView) {
        super(itemView);
        mUerName = itemView.findViewById(R.id.comment_username);
        mComment = itemView.findViewById(R.id.comment_text);
        mProfileImage = itemView.findViewById(R.id.comment_user_profile_image);
        mDate = itemView.findViewById(R.id.comment_date);
        mTime = itemView.findViewById(R.id.comment_time);
    }
}
