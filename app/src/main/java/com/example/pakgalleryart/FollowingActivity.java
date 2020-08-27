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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FollowingActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView myFriendsList;

    //
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, FollowingRef;
    private String currentUserId;

    //Firebase UI Adapter
    private FirebaseRecyclerOptions<FriendsModel> options;
    private FirebaseRecyclerAdapter<FriendsModel, FriendsViewHolder> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        //ToolBar
        mToolbar = findViewById(R.id.friends_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Following");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FollowingRef = FirebaseDatabase.getInstance().getReference().child("Following").child(currentUserId);

        //
        myFriendsList = findViewById(R.id.friends_list);
        myFriendsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendsList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();
    }

    private void DisplayAllFriends()
    {
        options = new FirebaseRecyclerOptions.Builder<FriendsModel>().setQuery(FollowingRef, FriendsModel.class).build();
        adapter = new FirebaseRecyclerAdapter<FriendsModel, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, final int position, @NonNull FriendsModel model) {
                holder.mDate.setText(model.date);
                final String FriendsIds = getRef(position).getKey();

                UsersRef.child(FriendsIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String userName = snapshot.child("fullname").getValue().toString();
                            holder.mUserName.setText(userName);
                            String profileImage = snapshot.child("profileimage").getValue().toString();
                            Glide.with(FollowingActivity.this).load(profileImage).placeholder(R.drawable.profile).into(holder.mProfileImage);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent ChatActivity = new Intent(FollowingActivity.this, PersonProfileActivity.class);
                        ChatActivity.putExtra("VisitUserId", visit_user_id);
                        ChatActivity.putExtra("userName", holder.mUserName.getText());
                        startActivity(ChatActivity);
                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                return new FriendsViewHolder(v);
            }
        };
        adapter.startListening();
        myFriendsList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}