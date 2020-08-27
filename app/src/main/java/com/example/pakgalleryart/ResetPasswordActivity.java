package com.example.pakgalleryart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private EditText resetPasswordEmail;
    private Button resetPasswordButton, loginBtn;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mToolBar = findViewById(R.id.reset_password_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        resetPasswordEmail = findViewById(R.id.reset_password_email);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        loginBtn = findViewById(R.id.reset_login_button);
        loadingBar = new ProgressDialog(this);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValikdateEmail();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity("MainActivity");
            }
        });
    }

    private void ValikdateEmail() {
        String email = resetPasswordEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter an Email First", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Sending Rest Email Link");
            loadingBar.setMessage("Please Wait...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            SendResetEmailLink(email);
        }
    }

    private void SendResetEmailLink(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    resetPasswordEmail.setVisibility(View.INVISIBLE);
                    resetPasswordButton.setText("Done");
                    resetPasswordButton.setEnabled(false);
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(ResetPasswordActivity.this, "Error " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void SendUserToLoginActivity(String extra) {
        Intent loginIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loginIntent.putExtra("activityLogin", extra);
        startActivity(loginIntent);
        finish();
    }
}