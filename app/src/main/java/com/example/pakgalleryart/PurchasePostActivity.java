package com.example.pakgalleryart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PurchasePostActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private String mPostKey, mPostUserId;
    private CircleImageView mUserProfImage;
    private TextView mUserProfName, mUserContact;
    private ImageView mPostImage;
    private Button mPostPriceBtn;

    //Firebase
    private DatabaseReference UsersRef, UsersPostsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_post);

        //ToolBar
        mToolbar = findViewById(R.id.purchase_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Purchase");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        mPostKey = getIntent().getExtras().get("PostKey").toString();
        mPostUserId = getIntent().getExtras().get("uid").toString();
        mUserProfImage = findViewById(R.id.purchase_person_profile_pic);
        mUserProfName = findViewById(R.id.purchase_profile_full_name);
        mUserContact = findViewById(R.id.person_profile_contact);
        mPostPriceBtn = findViewById(R.id.post_price);
        mPostImage = findViewById(R.id.post_image);
        //
        UsersPostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(mPostKey);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mPostUserId);

        getAndFillData();
    }

    private void getAndFillData() {
        UsersPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("postimage"))
                {
                    Glide.with(PurchasePostActivity.this).load(snapshot.child("postimage").getValue().toString()).placeholder(R.drawable.add_post).into(mPostImage);
                    mPostPriceBtn.setText(snapshot.child("price").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    mUserProfName.setText(snapshot.child("fullname").getValue().toString());
                    mUserContact.setText(snapshot.child("contact").getValue().toString());
                    Glide.with(PurchasePostActivity.this).load(snapshot.child("profileimage").getValue().toString()).placeholder(R.drawable.add_post).into(mUserProfImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}