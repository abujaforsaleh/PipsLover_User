package com.banglabs.pipslover.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.banglabs.pipslover.R;
import com.banglabs.pipslover.datahandler.FeedbackBundle;

import java.util.Objects;


public class ContactFragment extends Fragment implements View.OnClickListener{

    DatabaseReference feedbackreference;
    FirebaseUser user;

    EditText revew;
    Button clear, send;
    View view;

    public ContactFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact, container, false);

        //adding custom toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_contact);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        //database reference
        feedbackreference = FirebaseDatabase.getInstance().getReference("Feedback").child(user.getUid());


        //finding views
        revew = view.findViewById(R.id.user_revew_box_id);
        clear = view.findViewById(R.id.clear_btn_id);
        send = view.findViewById(R.id.send_btn_id);

        clear.setOnClickListener(ContactFragment.this);
        send.setOnClickListener(ContactFragment.this);

        return view;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.clear_btn_id) {


            revew.setText("");

        } else if (view.getId() == R.id.send_btn_id) {


            try {

                if (!revew.getText().toString().isEmpty()) {
                    FeedbackBundle feedbackBundle = new FeedbackBundle(user.getEmail(), revew.getText().toString());
                    String key = feedbackreference.push().getKey();
                    feedbackreference.child(key).setValue(feedbackBundle);
                    Toast.makeText(getContext(), "Thank you for your cooperation", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new ShowSignalFragment()).commit();

                } else {
                    Toast.makeText(getContext(), "Please fill all the information correctly", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception ignored) {

            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new ShowSignalFragment()).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}