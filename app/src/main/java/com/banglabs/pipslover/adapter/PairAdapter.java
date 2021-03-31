package com.banglabs.pipslover.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.banglabs.pipslover.R;
import com.banglabs.pipslover.datahandler.PairBundle;
import com.banglabs.pipslover.dialog.CustomDialogClass;
import com.banglabs.pipslover.fragment.ProvideSignalFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PairAdapter extends ArrayAdapter {

    private final Activity activity;

    private final List<PairBundle> pairlist;



    public PairAdapter(Activity activity, List<PairBundle> pairlist) {
        super(activity, R.layout.pair_base_layout, pairlist);
        this.activity = activity;
        this.pairlist = pairlist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = activity.getLayoutInflater();

        @SuppressLint({"ViewHolder", "InflateParams"}) View listviewitem = inflater.inflate(R.layout.pair_base_layout, null, true);

        TextView name = listviewitem.findViewById(R.id.pair_name);
        TextView statas = listviewitem.findViewById(R.id.statas);
        TextView current_position = listviewitem.findViewById(R.id.current_position);
        TextView pswitch = listviewitem.findViewById(R.id.switch_id);
        LinearLayout parentv = listviewitem.findViewById(R.id.parent_view);


        PairBundle pairBundle = pairlist.get(position);
        name.setText(pairBundle.getPair_name());
        statas.setText(pairBundle.getPair_statas());
        if(pairBundle.getTrade_result().equals("Expired")){
            current_position.setVisibility(View.VISIBLE);
            current_position.setTextColor(Color.WHITE );
            current_position.setText(pairBundle.getTrade_result());
        }else{
            current_position.setVisibility(View.GONE);
        }


        pswitch.setText(pairBundle.getPair_action());

        /*@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        if(pairBundle.getUpdate_time().equals(currentDateandTime)){

        }*/
        if(pairBundle.getPair_action().equals("BUY")){
            parentv.setBackgroundResource(R.drawable.buy_background);
        }
        else if(pairBundle.getPair_action().equals("SELL")){
            parentv.setBackgroundResource(R.drawable.sell_background);
        }
        listviewitem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                CustomDialogClass cdd=new CustomDialogClass(activity, pairBundle.getPair_name());
                cdd.show();

                return true;
            }
        });

        listviewitem.setOnClickListener(v -> {

            //Toast.makeText(activity, name.getText().toString(), Toast.LENGTH_SHORT).show();

            ProvideSignalFragment provideSignalFragment = new ProvideSignalFragment();
            Bundle args = new Bundle();
            args.putString("pair_name", name.getText().toString());
            provideSignalFragment.setArguments(args);
            ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, provideSignalFragment).addToBackStack("stack").commit();

            /*Intent intent = new Intent(getContext(), ProvideSignal.class);
            intent.putExtra("pair_name", name.getText().toString());
            //Toast.makeText(context, name.getText().toString(), Toast.LENGTH_SHORT).show();
            getContext().startActivity(intent);*/
        });

        return listviewitem;

    }

}
