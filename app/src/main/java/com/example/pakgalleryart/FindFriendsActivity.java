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
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FindFriendsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView SearchResultList;
    private EditText SearchInputText;
    private ImageButton SearchButton;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private String currentUserId;

    //Firebase UI Adapter
    private FirebaseRecyclerOptions<FindFriends> options;
    private FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        //ToolBar
        mToolbar = findViewById(R.id.find_friend_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //
        SearchResultList = findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = findViewById(R.id.search_friends_button);
        SearchInputText = findViewById(R.id.search_box_input);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchInputText = SearchInputText.getText().toString();
                SearchPeopleAndFriends(searchInputText);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Query searchFriendsQuery = UsersRef.orderByChild("fullname") ;
        options = new FirebaseRecyclerOptions.Builder<FindFriends>().setQuery(searchFriendsQuery, FindFriends.class).build();
        adapter = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull FindFriends model) {
                holder.mUserName.setText(model.fullname);
                holder.mStatus.setText(model.status);
                Glide.with(FindFriendsActivity.this).load(model.profileimage).placeholder(R.drawable.profile).into(holder.mProfileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        if(visit_user_id.equals(currentUserId))
                        {
                            Intent personProfileActivity = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                            startActivity(personProfileActivity);
                        }
                        else {
                            Intent personProfileActivity = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
                            personProfileActivity.putExtra("VisitUserId", visit_user_id);
                            startActivity(personProfileActivity);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                return new FindFriendsViewHolder(v);
            }
        };
        adapter.startListening();
        SearchResultList.setAdapter(adapter);

    }

    private void SearchPeopleAndFriends(String searchInputText) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        Query searchFriendsQuery = UsersRef.orderByChild("fullname").startAt(searchInputText).endAt(searchInputText + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<FindFriends>().setQuery(searchFriendsQuery, FindFriends.class).build();
        adapter = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull FindFriends model) {
                holder.mUserName.setText(model.fullname);
                holder.mStatus.setText(model.status);
                Glide.with(FindFriendsActivity.this).load(model.profileimage).placeholder(R.drawable.profile).into(holder.mProfileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        if(visit_user_id.equals(currentUserId))
                        {
                            Intent personProfileActivity = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                            startActivity(personProfileActivity);
                        }
                        else {
                            Intent personProfileActivity = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
                            personProfileActivity.putExtra("VisitUserId", visit_user_id);
                            startActivity(personProfileActivity);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                return new FindFriendsViewHolder(v);
            }
        };
        adapter.startListening();
        SearchResultList.setAdapter(adapter);
    }
}