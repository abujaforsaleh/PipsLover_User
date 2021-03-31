package com.banglabs.pipslover.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.banglabs.pipslover.R;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText logName, logPassword;
    TextView goto_reg, forgot_pass, didnt_get_mail;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        

        mAuth = FirebaseAuth.getInstance();
        logName = findViewById(R.id.reg_name_et);
        logPassword = findViewById(R.id.reg_password_et);
        goto_reg = findViewById(R.id.goto_reg_page_id);
        forgot_pass = findViewById(R.id.forgot_id);
        Button login_btn = findViewById(R.id.login_btn_id);
        progressBar = findViewById(R.id.log_progress_id);
        frameLayout = findViewById(R.id.login_fade_id);
        didnt_get_mail = findViewById(R.id.didnt_get_mail_id);

        login_btn.setOnClickListener(Login.this);
        goto_reg.setOnClickListener(Login.this);
        forgot_pass.setOnClickListener(Login.this);
        didnt_get_mail.setOnClickListener(Login.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "starting again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.goto_reg_page_id){
            //Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        }
        else if(v.getId()==R.id.forgot_id){
            //Toast.makeText(this, "forgot", Toast.LENGTH_SHORT).show();
            final EditText resetMail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password ?");
            passwordResetDialog.setMessage("Enter Your Login Email To receive Password Reset mail");
            passwordResetDialog.setView(resetMail);
            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
                String mail;
                if(!TextUtils.isEmpty(resetMail.getText())){
                    mail = resetMail.getText().toString();
                    mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(Login.this, "Reset URL has been send to your email", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(Login.this, "Email can't be send", Toast.LENGTH_SHORT).show());

                }
                else{
                    resetMail.setError("Enter an Email Address");
                }

            });
            passwordResetDialog.setNeutralButton("Cancel", (dialog, which) -> {

            });
            passwordResetDialog.setCancelable(true);
            passwordResetDialog.create().show();

        }else if(v.getId()==R.id.didnt_get_mail_id){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.sendEmailVerification();
                Toast.makeText(this, "Check your mail", Toast.LENGTH_SHORT).show();
            }
        }


        else{
            userLogin();
        }
    }

    private void userLogin() {

        String email, password;

        email = logName.getText().toString().trim();
        password = logPassword.getText().toString().trim();

        if(email.isEmpty()){

            logName.setError("Enter an Email address");
            logName.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){


            logName.setError("Enter a valid Email address");
            logName.requestFocus();
            return;
        }
        if(password.isEmpty()){

            logPassword.setError("Enter an password");
            logPassword.requestFocus();
            return;
        }
        if(password.length()<6){

            logPassword.setError("Enter an strong");
            logPassword.requestFocus();
            return;
        }

        frameLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user.isEmailVerified()){
                    // Sign in success, update UI with the signed-in user's information

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    frameLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    finish();
                }else{
                    frameLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    didnt_get_mail.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Please Verify your Email", Toast.LENGTH_SHORT).show();
                }

            } else {
                // If sign in fails, display a message to the user.
                logName.requestFocus();
                frameLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Email or password is not match.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}