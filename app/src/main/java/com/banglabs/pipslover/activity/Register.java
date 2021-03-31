package com.banglabs.pipslover.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.banglabs.pipslover.R;
import com.banglabs.pipslover.datahandler.UserBundle;

import java.util.Objects;

public class Register extends AppCompatActivity implements View.OnClickListener {

    DataSnapshot token_snapshot, user_snapshot;
    DatabaseReference token_reference, user_reference;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    FrameLayout frameLayout;

    private EditText regUserName, regUserPassword, regEmail, token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user_reference = FirebaseDatabase.getInstance().getReference("UserInfo");
        token_reference = FirebaseDatabase.getInstance().getReference("Reference");
        mAuth = FirebaseAuth.getInstance();


        get_Tokens();

        get_UserInfo();


        token = findViewById(R.id.reg_token);
        regUserName = findViewById(R.id.reg_name_et);
        regUserPassword = findViewById(R.id.reg_password_et);
        regEmail = findViewById(R.id.reg_email_et);
        Button reg_button = findViewById(R.id.reg_button_join_us);
        Button goto_login = findViewById(R.id.back_btn_id);
        progressBar = findViewById(R.id.reg_progress_id);
        frameLayout = findViewById(R.id.register_fade_id);


        reg_button.setOnClickListener(this);
        goto_login.setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.reg_button_join_us){

            register();


        }else if(v.getId()==R.id.back_btn_id){
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        }

    }

    private void register() {

        String name, email, password, user_token;
        name = regUserName.getText().toString().trim();
        email = regEmail.getText().toString().trim();
        password = regUserPassword.getText().toString().trim();
        user_token = token.getText().toString();


        //checking validity of user inputs
        if(name.isEmpty()){

            regUserName.setError("Enter a Valid name");
            regUserName.requestFocus();
            return;

        }

        if(email.isEmpty()){

            regEmail.setError("Enter an Email address");
            regEmail.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            regEmail.setError("Enter a valid Email address");
            regEmail.requestFocus();
            return;

        }
        if(password.isEmpty()){

            regUserPassword.setError("Enter an password");
            regUserPassword.requestFocus();
            return;


        }
        if(password.length()<6){

            regUserPassword.setError("Enter an strong password");
            regUserPassword.requestFocus();
            return;

        }

        //checking and creating unique user profile
        if(token_snapshot.hasChild(user_token)){
            if(check_name_validity(name)){

                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        UserBundle userBundle = new UserBundle(name, email, user_token);
                        try{


                            user_reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(userBundle);

                            String histroy_val = String.valueOf(token_snapshot.child(user_token).child("total_users").getValue(String.class));
                            String recent_val =  String.valueOf(token_snapshot.child(user_token).child("new_users").getValue(String.class));

                            histroy_val = String.valueOf(Integer.parseInt(histroy_val)+1);
                            recent_val = String.valueOf(Integer.parseInt(recent_val)+1);

                            token_reference.child(user_token).child("total_users").setValue(histroy_val);
                            token_reference.child(user_token).child("new_users").setValue(recent_val);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification();
                            }
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);

                        }
                        catch (Exception e){
                            Toast.makeText(Register.this, "error found", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.GONE);



                        //int history_val = token_reference.child(user_token).child("history").
                        // Sign in success, update UI with the signed-in user's information
                        //Toast.makeText(Registration.this, "Registration Sucessfull.", Toast.LENGTH_SHORT).show();

                    } else {
                        frameLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        regEmail.setError("This email is already registered");
                        regEmail.requestFocus();
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Register.this, "Register is not Sucessfull.", Toast.LENGTH_SHORT).show();
                    }


                });
            }
            else{
                regUserName.setError("This name is already taken");
                regUserName.requestFocus();
            }
        }

        else{
            token.setError("Enter a valid token");
            token.requestFocus();
            //Toast.makeText(Registration.this, "enter a valid token.", Toast.LENGTH_SHORT).show();
        }



    }


    private void get_Tokens() {

        token_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                token_snapshot = snapshot;

                //Log.d("TAGv", String.valueOf(snapshot.child("reference1").child("history").getValue(String.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void get_UserInfo() {

        user_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_snapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    boolean check_name_validity(String user_name){

        return !user_snapshot.hasChild(user_name);

    }


}