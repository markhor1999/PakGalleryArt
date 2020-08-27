package com.example.pakgalleryart;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    CircleImageView mProfileImage;
    TextView mUserName, mPostDate, mPostTime, mDescription, DisplayNoOfLikes;
    ImageView mPostImage;
    ImageButton LikePostButton, CommentPostButton;
    int countLikes;
    Button mPurchasePicBtn;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        mProfileImage = itemView.findViewById(R.id.post_profile_image);
        mUserName = itemView.findViewById(R.id.post_user_name);
        mPostDate = itemView.findViewById(R.id.post_date);
        mPostTime = itemView.findViewById(R.id.post_time);
        mDescription = itemView.findViewById(R.id.post_description);
        mPostImage = itemView.findViewById(R.id.post_image);
        LikePostButton = itemView.findViewById(R.id.like_button);
        CommentPostButton = itemView.findViewById(R.id.comment_button);
        DisplayNoOfLikes = itemView.findViewById(R.id.display_no_of_likes);
        mPurchasePicBtn = itemView.findViewById(R.id.purchase_post);
    }

    public void setLikesButtonStatus(DatabaseReference likesRef, final String currentUserId, final String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(currentUserId)) {
                    countLikes = (int) snapshot.child(postKey).getChildrenCount();
                    LikePostButton.setImageResource(R.drawable.like);
                    DisplayNoOfLikes.setText(Integer.toString(countLikes) + " Likes");
                } else {
                    countLikes = (int) snapshot.child(postKey).getChildrenCount();
                    LikePostButton.setImageResource(R.drawable.dislike);
                    DisplayNoOfLikes.setText(Integer.toString(countLikes) + " Likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
