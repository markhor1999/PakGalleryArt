package com.example.pakgalleryart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private TextView ForgotPasswordLink;
    private ProgressDialog loadingBar;
    private Button NeedNewAccountLink;

    private FirebaseAuth mAuth;

    //
    private ImageView bookIconImageView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;
    TextInputLayout emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        initViews();
        startLogoAnim();
        //Hooks
        LoginButton = findViewById(R.id.login_btn);
        NeedNewAccountLink = findViewById(R.id.login_signup_btn);
        ForgotPasswordLink = findViewById(R.id.forgot_password_link);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);


        //onClick
        ForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToResetPasswordActivity();
            }
        });
        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFileds())
                    UserLoginButton();
            }
        });
    }

    private void startLogoAnim()
    {
        final Intent i = getIntent();
        if (i.getStringExtra("activityLogin") == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    loadingProgressBar.setVisibility(View.GONE);
                    rootView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.white));
                    bookIconImageView.setImageResource(R.drawable.logo);
                    bookIconImageView.getLayoutParams().height = 300;
                    bookIconImageView.getLayoutParams().width = 300;
                    startAnimation();

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                }
            }, 2000);
        } else {

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            loadingProgressBar.setVisibility(View.GONE);
            bookIconImageView.getLayoutParams().height = 300;
            bookIconImageView.getLayoutParams().width = 300;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)bookIconImageView.getLayoutParams();
            layoutParams.addRule(rootView.ALIGN_PARENT_START);
            layoutParams.addRule(rootView.ALIGN_PARENT_TOP);
            afterAnimationView.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        bookIconImageView = findViewById(R.id.bookIconImageView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.rootView);
        afterAnimationView = findViewById(R.id.after_animation_view);
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailEditText);
        passwordText = findViewById(R.id.passwordEditText);
    }
    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    private boolean validateFileds()  {
        if(!validatePassword() |  !validateEmail() ){
            return false;
        }
        return true;
    }
    private Boolean validateEmail() {
        String val = emailText.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            emailText.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            emailText.setError("Invalid email address");
            return false;
        } else {
            emailText.setError(null);
            emailText.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword() {
        String val = passwordText.getEditText().getText().toString();
        if (val.isEmpty()) {
            passwordText.setError("Field cannot be empty");
            return false;
        } else {
            passwordText.setError(null);
            passwordText.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            SendUserToMainActivity();
        }
    }

    private void UserLoginButton() {
        String email = emailText.getEditText().getText().toString();
        String password = passwordText.getEditText().getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Logging in");
            loadingBar.setMessage("Please Wait...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error Occured "+message, Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void SendUserToResetPasswordActivity()
    {
        Intent resetPasswordIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(resetPasswordIntent);
    }
}