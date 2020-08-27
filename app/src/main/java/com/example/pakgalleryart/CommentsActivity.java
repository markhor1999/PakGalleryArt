package com.example.pakgalleryart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView CommentsList;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;

    private DatabaseReference UserRef, PostsRef;
    private FirebaseAuth mAuth;

    private String Post_Key, currentUserId;
    //
    private FirebaseRecyclerOptions<CommentsModel> options;
    private FirebaseRecyclerAdapter<CommentsModel, CommentsHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();
        //
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");
        //

        //ToolBar
        mToolbar = findViewById(R.id.comments_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        CommentInputText = findViewById(R.id.comments_input_text);
        PostCommentButton = findViewById(R.id.comments_post_button);
        CommentsList = findViewById(R.id.comments_recycler_view);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);


        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String userName = snapshot.child("username").getValue().toString();
                            String userProfileImage = snapshot.child("profileimage").getValue().toString();
                            ValidateComment(userName, userProfileImage);
                            CommentInputText.setText("");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    //


    @Override
    protected void onStart() {
        super.onStart();
        options = new FirebaseRecyclerOptions.Builder<CommentsModel>().setQuery(PostsRef, CommentsModel.class).build();
        adapter = new FirebaseRecyclerAdapter<CommentsModel, CommentsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsHolder holder, int position, @NonNull CommentsModel model) {
                holder.mUerName.setText(" @"+model.username);
                holder.mTime.setText(model.time);
                holder.mComment.setText(model.comment);
                holder.mDate.setText(" Date: " + model.date);

                Glide.with(CommentsActivity.this).load(model.profileimage).placeholder(R.drawable.profile).into(holder.mProfileImage);
            }

            @NonNull
            @Override
            public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout,parent,false);
                return new CommentsHolder(v);
            }
        };
        adapter.startListening();
        CommentsList.setAdapter(adapter);

    }

    private void ValidateComment(String userName, String userprofileimage)
    {
        String comment = CommentInputText.getText().toString();
        if(TextUtils.isEmpty(comment))
        {
            Toast.makeText(this, "Comment is Empty", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-YYYY");
            final String saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calForTime.getTime());
            final String postRandomName = currentUserId + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", currentUserId);
            commentsMap.put("comment", comment);
            commentsMap.put("date", saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("username", userName);
            commentsMap.put("profileimage", userprofileimage);

            PostsRef.child(postRandomName).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {

                    }
                    else
                    {
                        Toast.makeText(CommentsActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}