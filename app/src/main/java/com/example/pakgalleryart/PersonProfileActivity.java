package com.example.pakgalleryart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView userName, userProfName, userCountry;
    private CircleImageView userProfImage;
    private Button mFollowBtn, mUnFollowBtn;
    private RecyclerView mPostsRecyclerView;
    private LinearLayout mPersonProfileMessageButton;

    //Firebase
    private DatabaseReference UsersRef, FollowingRef, FollowersRef, UsersPostsRef, LikesRef;
    private FirebaseAuth mAuth;
    private String mCurrentUserId, mReceiverUserId;

    //Firebase Adapter
    private FirebaseRecyclerOptions<Posts> options;
    private FirebaseRecyclerAdapter<Posts, MyViewHolder> adapter;

    //var
    private Boolean LikeChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        //ToolBar
        mToolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mReceiverUserId = getIntent().getExtras().get("VisitUserId").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FollowingRef = FirebaseDatabase.getInstance().getReference().child("Following");
        FollowersRef = FirebaseDatabase.getInstance().getReference().child("Followers");
        UsersPostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        //
        //Posts RecyclerView
        mPostsRecyclerView = findViewById(R.id.profile_posts_recycler_view);
        mPostsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPostsRecyclerView.setLayoutManager(linearLayoutManager);
        //
        userName = findViewById(R.id.person_profile_username);
        userProfName = findViewById(R.id.person_profile_full_name);
        userCountry = findViewById(R.id.person_profile_country);
        userProfImage = findViewById(R.id.person_profile_pic);
        mFollowBtn = findViewById(R.id.person_follow_button);
        mUnFollowBtn = findViewById(R.id.person_unfollow_button);
        mPersonProfileMessageButton = findViewById(R.id.person_profile_message_button);

        //Fetch Person Data and Place Them in the Views
        fetchPersonData();
        //Display or Hide Follow or UnFollow Buttons
        ManageFollowOrUnFollowButton();
        //
        mUnFollowBtn.setOnClickListener(this);
        mFollowBtn.setOnClickListener(this);
        mPersonProfileMessageButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mReceiverUserId = getIntent().getExtras().get("VisitUserId").toString();

        if (mCurrentUserId.equals(mReceiverUserId)) {
            sendUserToPersonActivity();
        }
    }

    private void sendUserToPersonActivity() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
        finish();
    }

    private void ManageFollowOrUnFollowButton() {
        FollowingRef.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mReceiverUserId)) {
                    mFollowBtn.setVisibility(View.INVISIBLE);
                    mFollowBtn.setEnabled(false);

                    mUnFollowBtn.setVisibility(View.VISIBLE);
                    mUnFollowBtn.setEnabled(true);
                } else {
                    mUnFollowBtn.setVisibility(View.INVISIBLE);
                    mUnFollowBtn.setEnabled(false);

                    mFollowBtn.setVisibility(View.VISIBLE);
                    mFollowBtn.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchPersonData() {
        UsersRef.child(mReceiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String myProfileImage = snapshot.child("profileimage").getValue().toString();
                    String myUserName = snapshot.child("username").getValue().toString();
                    String myCountry = snapshot.child("country").getValue().toString();
                    String myProfileName = snapshot.child("fullname").getValue().toString();

                    Glide.with(PersonProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText("@" + myUserName);
                    userCountry.setText("Country: " + myCountry);
                    userProfName.setText(myProfileName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DisplayAllPostsOfCurrentUser();
    }

    private void DisplayAllPostsOfCurrentUser() {
        Query query = UsersPostsRef.orderByChild("uid").equalTo(mReceiverUserId);
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull Posts model) {
                final String PostKey = getRef(position).getKey();
                holder.mUserName.setText(model.fullname);
                holder.mPostDate.setText(model.date);
                holder.mPostTime.setText(model.time);
                holder.mDescription.setText(model.description);
                UsersRef.child(mReceiverUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("profileimage")) {
                            String image = snapshot.child("profileimage").getValue().toString();
                            Glide.with(PersonProfileActivity.this).load(image).placeholder(R.drawable.profile).into(holder.mProfileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Glide.with(PersonProfileActivity.this).load(model.postimage).placeholder(R.drawable.post_background).into(holder.mPostImage);

                holder.setLikesButtonStatus(LikesRef, mCurrentUserId, PostKey);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(PersonProfileActivity.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });

                holder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(PersonProfileActivity.this, CommentsActivity.class);
                        commentIntent.putExtra("PostKey", PostKey);
                        startActivity(commentIntent);
                    }
                });
                holder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LikeChecker = true;
                        LikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (LikeChecker.equals(true)) {
                                    if (snapshot.child(PostKey).hasChild(mCurrentUserId)) {
                                        LikesRef.child(PostKey).child(mCurrentUserId).removeValue();
                                        LikeChecker = false;
                                    } else {
                                        LikesRef.child(PostKey).child(mCurrentUserId).setValue(true);
                                        LikeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                return new MyViewHolder(v);
            }
        };
        adapter.startListening();
        mPostsRecyclerView.setAdapter(adapter);
    }


    private void followThisUser() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-YYYY");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());
        FollowingRef.child(mCurrentUserId).child(mReceiverUserId).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FollowersRef.child(mReceiverUserId).child(mCurrentUserId).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFollowBtn.setVisibility(View.INVISIBLE);
                                        mFollowBtn.setEnabled(false);

                                        mUnFollowBtn.setVisibility(View.VISIBLE);
                                        mUnFollowBtn.setEnabled(true);

                                        Toast.makeText(PersonProfileActivity.this, "Following", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    private void unFollowThisUser() {
        FollowingRef.child(mCurrentUserId).child(mReceiverUserId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mUnFollowBtn.setVisibility(View.INVISIBLE);
                        mUnFollowBtn.setEnabled(false);

                        mFollowBtn.setVisibility(View.VISIBLE);
                        mFollowBtn.setEnabled(true);

                        Toast.makeText(PersonProfileActivity.this, "UnFollowing", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == mFollowBtn)
            followThisUser();
        else if (v == mUnFollowBtn)
            unFollowThisUser();
        else if (v == mPersonProfileMessageButton)
            SendUserToMessageActivity();
    }

    private void SendUserToMessageActivity() {
        Intent messageIntent = new Intent(this, ChatActivity.class);
        messageIntent.putExtra("userName", userProfName.getText().toString());
        messageIntent.putExtra("VisitUserId", mReceiverUserId);
        startActivity(messageIntent);
    }

}