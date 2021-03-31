package com.banglabs.pipslover.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.banglabs.pipslover.R;
import com.banglabs.pipslover.datahandler.UserBundle;

import java.util.Objects;


public class ProfileFragment extends Fragment implements BillingProcessor.IBillingHandler {
    View view;
    TextView username, email,subscribe;
    DatabaseReference userReference;
    Button subscribeButton;
    BillingProcessor billingProcessor;
    private TransactionDetails subscriptionTransaction = null;


    public ProfileFragment() {
        // Required empty public constructor
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
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        //adding custom toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_profile);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //database references
        userReference = FirebaseDatabase.getInstance().getReference("UserInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //billing setup
        billingProcessor = new BillingProcessor(getContext(), "licence key", this);

        //finding viewes
        username = view.findViewById(R.id.user_name_id2);
        email = view.findViewById(R.id.user_mail_id);
        subscribe = view.findViewById(R.id.day_id);
        subscribeButton = view.findViewById(R.id.subscribe_btn_id);

        getUserData();

        return view;
    }

    private void getUserData() {

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserBundle userBundle = snapshot.getValue(UserBundle.class);
                username.setText(userBundle.getName());
                email.setText(userBundle.getEmail());
                subscribe.setText(userBundle.getSubscription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new ShowSignalFragment()).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d("TAG", "onProductPurchased: ");

    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d("TAG", "onPurchaseHistoryRestored: ");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d("TAG", "onBillingError: ");
    }

    @Override
    public void onBillingInitialized() {
        Log.d("TAG", "onBillingInitialized: ");
        subscriptionTransaction =  billingProcessor.getSubscriptionTransactionDetails("product id");
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(billingProcessor.isSubscriptionUpdateSupported()){
                    billingProcessor.subscribe(requireActivity(), "product id");
                }else{
                    Toast.makeText(requireContext(), "Your Device is not capable of Google Pay", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if(hasSubscribe()){
            //subscribed user
            //set days in user account


        }else{

            //free user
        }

    }

    private boolean hasSubscribe() {

        if(subscriptionTransaction != null){
            return  subscriptionTransaction.purchaseInfo != null;
        }
        else return false;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }
}