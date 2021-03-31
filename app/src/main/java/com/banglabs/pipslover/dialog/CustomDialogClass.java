package com.banglabs.pipslover.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.banglabs.pipslover.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDialogClass extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    String chield;
    TextView message;
    DatabaseReference pair_reference, signal_reference;

    //getting current date
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String currentDateandTime = sdf.format(new Date());

    public CustomDialogClass(Activity a, String chield) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.chield = chield;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        message = findViewById(R.id.message_id);
        message.setText(chield+" ?");
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:

                try{

                    pair_reference = FirebaseDatabase.getInstance().getReference("PairList").child(chield);
                    signal_reference = FirebaseDatabase.getInstance().getReference("Pairs").child(currentDateandTime).child(chield);
                    pair_reference.removeValue();
                    signal_reference.removeValue();
                }catch (Exception e){

                }
                break;
            case R.id.btn_no:
                dismiss();
                break;
        }
        dismiss();
    }
}
