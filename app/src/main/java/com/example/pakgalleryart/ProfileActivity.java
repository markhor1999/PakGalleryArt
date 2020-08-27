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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    //XML
    private Toolbar mToolbar;
    private TextView userName, userProfName, userCountry;
    private CircleImageView userProfImage;
    private RecyclerView mPostsRecyclerView;
    private LinearLayout mFollowingBtn, mFollowersBtn;
    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference ProfileUserRef, UsersPostsRef, LikesRef;
    private String currentUserId;

    //Firebase Adapter
    private FirebaseRecyclerOptions<Posts> options;
    private FirebaseRecyclerAdapter<Posts, MyViewHolder> adapter;

    //var
    private Boolean LikeChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //ToolBar
        mToolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        ProfileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UsersPostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        //Hooks
        userName = findViewById(R.id.my_profile_username);
        userProfName = findViewById(R.id.my_profile_full_name);
        userProfImage = findViewById(R.id.my_profile_pic);
        userCountry = findViewById(R.id.my_profile_country);
        mFollowingBtn = findViewById(R.id.my_profile_following_btn);
        mFollowersBtn = findViewById(R.id.my_profile_followers_btn);
        //Posts RecyclerView
        mPostsRecyclerView = findViewById(R.id.profile_posts_recycler_view);
        mPostsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPostsRecyclerView.setLayoutManager(linearLayoutManager);

        ProfileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String myProfileImage = snapshot.child("profileimage").getValue().toString();
                    String myUserName = snapshot.child("username").getValue().toString();
                    String myCountry = snapshot.child("country").getValue().toString();
                    String myProfileName = snapshot.child("fullname").getValue().toString();

                    Glide.with(ProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText("@" + myUserName);
                    userCountry.setText(myCountry);
                    userProfName.setText(myProfileName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DisplayAllPostsOfCurrentUser();
        mFollowingBtn.setOnClickListener(this);
        mFollowersBtn.setOnClickListener(this);
    }

    private void DisplayAllPostsOfCurrentUser() {
        Query query = UsersPostsRef.orderByChild("uid").equalTo(currentUserId);
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull Posts model) {
                final String PostKey = getRef(position).getKey();
                holder.mUserName.setText(model.fullname);
                holder.mPostDate.setText(model.date);
                holder.mPostTime.setText(model.time);
                holder.mDescription.setText(model.description);
                ProfileUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("profileimage")) {
                            String image = snapshot.child("profileimage").getValue().toString();
                            Glide.with(ProfileActivity.this).load(image).placeholder(R.drawable.profile).into(holder.mProfileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Glide.with(ProfileActivity.this).load(model.postimage).into(holder.mPostImage);

                holder.setLikesButtonStatus(LikesRef, currentUserId, PostKey);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(ProfileActivity.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });

                holder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(ProfileActivity.this, CommentsActivity.class);
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
                                    if (snapshot.child(PostKey).hasChild(currentUserId)) {
                                        LikesRef.child(PostKey).child(currentUserId).removeValue();
                                        LikeChecker = false;
                                    } else {
                                        LikesRef.child(PostKey).child(currentUserId).setValue(true);
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

    @Override
    public void onClick(View v) {
        if (v == mFollowingBtn) {
            SendUserToFollowingActivity();
        }
        else if (v == mFollowersBtn) {
            SendUserToFollowersActivity();
        }

    }

    private void SendUserToFollowersActivity() {
        Intent intent = new Intent(this, FollowersActivity.class);
        startActivity(intent);
    }

    private void SendUserToFollowingActivity() {
        Intent intent = new Intent(this, FollowingActivity.class);
        startActivity(intent);
    }
}