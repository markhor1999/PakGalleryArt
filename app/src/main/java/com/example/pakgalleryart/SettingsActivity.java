package com.example.pakgalleryart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextInputLayout userName, userProfName, userStatus, userCountry, userContactNumber, userRelation, userDOB;
    private Button UpdateAccountSettingsButton;
    private CircleImageView userProfImage;

    private FirebaseAuth mAuth;
    private DatabaseReference SettingsUserRef;
    private DatabaseReference mPostsProfileRef;
    private String currentUserId;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //ToolBar
        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profileImage");
        mPostsProfileRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        loadingBar = new ProgressDialog(this);


        //Hooks
        userName = findViewById(R.id.settings_username);
        userProfName = findViewById(R.id.settings_profile_full_name);
        userStatus = findViewById(R.id.settings_status);
        userCountry = findViewById(R.id.settings_country);
        userDOB = findViewById(R.id.settings_dob);
        userRelation = findViewById(R.id.settings_relationship_status);
        userProfImage = findViewById(R.id.settings_profile_image);
        userContactNumber = findViewById(R.id.settings_contact_number);
        UpdateAccountSettingsButton = findViewById(R.id.update_account_settings);
        //

        //

        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String myProfileImage = snapshot.child("profileimage").getValue().toString();
                    String myUserName = snapshot.child("username").getValue().toString();
                    String myCountry = snapshot.child("country").getValue().toString();
                    String myDOB = snapshot.child("dob").getValue().toString();
                    String myProfileStatus = snapshot.child("status").getValue().toString();
                    String myRelationStatus = snapshot.child("relationshipstatus").getValue().toString();
                    String myProfileName = snapshot.child("fullname").getValue().toString();
                    String myContactNumber = snapshot.child("contact").getValue().toString();

                    Glide.with(SettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.getEditText().setText(myUserName);
                    userCountry.getEditText().setText(myCountry);
                    userStatus.getEditText().setText(myProfileStatus);
                    userRelation.getEditText().setText(myRelationStatus);
                    userDOB.getEditText().setText(myDOB);
                    userProfName.getEditText().setText(myProfileName);
                    userContactNumber.getEditText().setText(myContactNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

        UpdateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                loadingBar.setTitle("Updating Profile Image");
                loadingBar.setMessage("Please Wait...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                final Uri resultUri = result.getUri();
                userProfImage.setImageURI(resultUri);
                final StorageReference filepath = UserProfileImageRef.child(currentUserId + ".jpg");
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                SettingsUserRef.child("profileimage").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    userProfImage.setImageURI(resultUri);
                                                    Toast.makeText(SettingsActivity.this, "Profile Updated ", Toast.LENGTH_LONG).show();
                                                    loadingBar.dismiss();
                                                }
                                                else {
                                                    //if URL is not saved in database
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(SettingsActivity.this, "Error " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });
            }
            else {
                Toast.makeText(this, "Erorr Occourd: Please Choose Image Again ", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void ValidateAccountInfo() {
        String vUserName = userName.getEditText().getText().toString();
        String vProfileName = userProfName.getEditText().getText().toString();
        String vCountry = userCountry.getEditText().getText().toString();
        String vStatus = userStatus.getEditText().getText().toString();
        String vContact = userContactNumber.getEditText().getText().toString();
        String vRelation = userRelation.getEditText().getText().toString();
        String vDOB = userDOB.getEditText().getText().toString();

        if(TextUtils.isEmpty(vUserName))
        {
            Toast.makeText(this, "Please Enter The UserName", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(vProfileName))
        {
            Toast.makeText(this, "Please Enter The Full Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(vContact))
        {
            Toast.makeText(this, "Please Enter The Contact Number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(vStatus))
        {
            Toast.makeText(this, "Please Enter The Bio", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(vDOB))
        {
            Toast.makeText(this, "Please Enter The Date of Birth", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(vCountry))
        {
            Toast.makeText(this, "Please Enter The Country", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(vRelation))
        {
            Toast.makeText(this, "Please Enter The Relationship Status", Toast.LENGTH_SHORT).show();
        }
        else
        {
            UpdateAccountInfo(vUserName, vProfileName, vCountry, vDOB, vContact, vStatus, vRelation);
        }
    }

    private void UpdateAccountInfo(String vUserName, String vProfileName, String vCountry, String vDOB, String vContact, String vStatus, String vRelation)
    {
        HashMap userMap = new HashMap();
        userMap.put("username", vUserName);
        userMap.put("fullname", vProfileName);
        userMap.put("country", vCountry);
        userMap.put("dob", vDOB);
        userMap.put("contact", vContact);
        userMap.put("status", vStatus);
        userMap.put("relationshipstatus", vRelation);
        SettingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Settings Updated Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this, "Error "+message, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}