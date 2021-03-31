package com.banglabs.pipslover.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.banglabs.pipslover.R;
import com.banglabs.pipslover.fragment.ShowSignalFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        //initial fragment
        ShowSignalFragment showSignalFragment = new ShowSignalFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.main_activity_id, showSignalFragment).addToBackStack("stack").commit();

    }

    void backDialog(){

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();

                    }
                })
                .setNegativeButton("No", null)
                .show();
        /*super.onBackPressed();*/

    }
    // on back press
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 1) {
            Log.d("mms", String.valueOf(count));
            backDialog();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

}