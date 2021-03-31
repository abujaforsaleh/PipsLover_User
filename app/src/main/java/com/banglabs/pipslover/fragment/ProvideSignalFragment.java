package com.banglabs.pipslover.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.banglabs.pipslover.R;
import com.banglabs.pipslover.datahandler.PairBundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.round;

public class ProvideSignalFragment extends Fragment implements View.OnClickListener {

    //database and api variables
    DatabaseReference pair_reference, admin_reference;
    private RequestQueue apiRequest;

    //views variable
    RadioGroup signal;
    RadioGroup signal_type;
    Button submit;
    TextView pair_nametv;
    TextView tp1, tp2, sl, initial_value, tp1_pip, tp2_pip, sl_pip, current_value, update_time, statas_view, result_view, signal_view;
    ImageButton copy_pest;
    View view;

    //data variables
    String exchangeRate, takep1, takep2, stopl, signaloption, signalType, initial_price, pair_name, currentDateandTime, pair_selected;
    DecimalFormat df = new DecimalFormat("0.00");

    public ProvideSignalFragment() {
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
        view = inflater.inflate(R.layout.fragment_provide_signal, container, false);

        //adding custom toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_provide_signal);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getting current date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currentDateandTime = sdf.format(new Date());

        //initializing firebase reference
        pair_reference = FirebaseDatabase.getInstance().getReference("Pairs").child(currentDateandTime);
        admin_reference = FirebaseDatabase.getInstance().getReference("PairList");
        apiRequest = Volley.newRequestQueue(getActivity());

        pair_selected = getArguments().getString("pair_name");

        //finding views from xml file
        pair_nametv = view.findViewById(R.id.pair_name_id);
        copy_pest = view.findViewById(R.id.copy_pest_id);
        tp1 = view.findViewById(R.id.tp1);
        tp2 = view.findViewById(R.id.tp2);
        sl = view.findViewById(R.id.sl);
        update_time = view.findViewById(R.id.update_time);
        //name from the adapter may be swaped
        statas_view = view.findViewById(R.id.result_view_id);
        result_view = view.findViewById(R.id.statas_view_id);
        signal_view = view.findViewById(R.id.signal_id);


        tp1_pip = view.findViewById(R.id.tp1_pips);
        tp2_pip = view.findViewById(R.id.tp2_pips);
        sl_pip = view.findViewById(R.id.sl_pips);

        current_value = view.findViewById(R.id.current_value);
        initial_value = view.findViewById(R.id.initial_value);
        submit = view.findViewById(R.id.submit_btn_id);
        signal = view.findViewById(R.id.action_group_id);
        signal_type = view.findViewById(R.id.action_group_id1);

        pair_nametv.setText(pair_selected);
        deriveCurrentRecord();
        pair_name = pair_selected;

        copy_pest.setOnClickListener(ProvideSignalFragment.this);
        submit.setOnClickListener(ProvideSignalFragment.this);

        tp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!tp1_pip.hasFocus()){
                    RadioButton selected = view.findViewById(radio_checked(signal));

                    if(radio_checked(signal)!=-1){
                        try{
                            if(selected.getText().toString().equals("BUY")){
                                float val = Float.parseFloat(String.valueOf(s));
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                float result = round(Float.parseFloat(df.format(((val-val2)*10000f))));
                                tp1_pip.setText(String.valueOf(result).equals("0.0")?    "0" :  String.valueOf(result));

                            }else{

                                float val = Float.parseFloat(String.valueOf(s));
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                float result = round(Float.parseFloat(df.format(((val2-val)*10000f))));
                                tp1_pip.setText(String.valueOf(result).equals("0.0")?    "0" :  String.valueOf(result));
                            }
                        }catch (Exception e){

                        }

                    }else Toast.makeText(requireActivity(), "Please select an action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!tp2_pip.hasFocus()){
                    RadioButton selected = view.findViewById(radio_checked(signal));

                    if(radio_checked(signal)!=-1){
                        try{
                            if(selected.getText().toString().equals("BUY")){
                                float val = Float.parseFloat(String.valueOf(s));
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                float result = round(Float.parseFloat(df.format(((val-val2)*10000f))));
                                tp2_pip.setText(String.valueOf(result).equals("0.0")?    "0" :  String.valueOf(result));

                            }else{

                                float val = Float.parseFloat(String.valueOf(s));
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                float result = round(Float.parseFloat(df.format(((val2-val)*10000f))));
                                tp2_pip.setText(String.valueOf(result).equals("0.0")?    "0" :  String.valueOf(result));
                            }
                        }catch (Exception e){

                        }

                    }else Toast.makeText(requireActivity(), "Please select an action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!sl_pip.hasFocus()){
                    RadioButton selected = view.findViewById(radio_checked(signal));

                    if(radio_checked(signal)!=-1){
                        try{
                            if(selected.getText().toString().equals("BUY")){
                                float val = Float.parseFloat(String.valueOf(s));
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                float result = round(Float.parseFloat(df.format(((val2-val)*10000f))));
                                sl_pip.setText(String.valueOf(result).equals("0.0")?    "0" :  String.valueOf(result));

                            }else{

                                float val = Float.parseFloat(String.valueOf(s));
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                float result = round(Float.parseFloat(df.format(((val-val2)*10000f))));
                                sl_pip.setText(String.valueOf(result).equals("0.0")?    "0" :  String.valueOf(result));
                            }
                        }catch (Exception e){

                        }

                    }else Toast.makeText(requireActivity(), "Please select an action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tp1_pip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(tp1_pip.hasFocus()){
                    RadioButton selected = view.findViewById(radio_checked(signal));

                    if(radio_checked(signal)!=-1){
                        try{
                            if(selected.getText().toString().equals("BUY")){


                                float val = Float.parseFloat(String.valueOf(s));
                                val/=10000f;
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                tp1.setText(String.valueOf(val+val2));

                            }else{

                                float val = Float.parseFloat(String.valueOf(s));
                                val/=10000f;
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                tp1.setText(String.valueOf(val2-val));
                                Log.d("valout", "sell");
                            }
                        }catch (Exception e){

                        }

                    }else Toast.makeText(requireActivity(), "Please select an action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tp2_pip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(tp2_pip.hasFocus()){
                    RadioButton selected = view.findViewById(radio_checked(signal));

                    if(radio_checked(signal)!=-1){
                        try{
                            if(selected.getText().toString().equals("BUY")){


                                float val = Float.parseFloat(String.valueOf(s));
                                val/=10000f;
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                tp2.setText(String.valueOf(val+val2));

                            }else{
                                float val = Float.parseFloat(String.valueOf(s));
                                val/=10000f;
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                tp2.setText(String.valueOf(val2-val));
                                Log.d("valout", "sell");
                            }
                        }catch (Exception e){

                        }


                    }else Toast.makeText(requireActivity(), "Please select an action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sl_pip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if(sl_pip.hasFocus()){
                    RadioButton selected = view.findViewById(radio_checked(signal));

                    if(radio_checked(signal)!=-1){
                        try{
                            if(selected.getText().toString().equals("BUY")){


                                float val = Float.parseFloat(String.valueOf(s));
                                val/=10000f;
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                sl.setText(String.valueOf(val2-val));

                            }else{
                                float val = Float.parseFloat(String.valueOf(s));
                                val/=10000f;
                                float val2 = Float.parseFloat(initial_value.getText().toString());
                                sl.setText(String.valueOf(val2+val));
                                Log.d("valout", "sell");
                            }
                        }catch (Exception e){

                        }


                    }else Toast.makeText(requireActivity(), "Please select an action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void get_PairValue_From_API(String name) {
        String url = "https://free.currconv.com/api/v7/convert?apiKey=2ec64af2c4a5303dd43f&q=" + name;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {

                JSONObject obj = response.getJSONObject("results");
                JSONObject obj2 = obj.getJSONObject(name);

                exchangeRate = String.valueOf(obj2.getDouble("val"));
                initial_value.setText(exchangeRate);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("outmsg2", String.valueOf(e));
            }

        }, Throwable::printStackTrace);
        apiRequest.add(request);
    }
    //uploading signal
    private void addSignal() {
        String slpip, tp1pip, tp2pip;
        slpip = sl_pip.getText().toString();
        tp1pip = tp1_pip.getText().toString();
        tp2pip = tp2_pip.getText().toString();
        PairBundle pairBundle = new PairBundle(pair_name, initial_price, "Online", signaloption, signalType, initial_price, stopl, takep1, takep2, "Waiting", currentDateandTime, slpip, tp1pip, tp2pip);
        pair_reference.child(pair_name).setValue(pairBundle);
        admin_reference.child(pair_name).setValue(pairBundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new ShowSignalFragment()).commit();

    }
    //getting current record from database
    public void deriveCurrentRecord(){

        pair_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PairBundle pairBundle = snapshot.child(pair_name).getValue(PairBundle.class);
                if(pairBundle!=null){

                    if(pairBundle.getPair_action().equals("BUY")){

                        signal.check(R.id.buy_option_id);
                    }else{
                        signal.check(R.id.sell_option_id);
                    }
                    if(pairBundle.getPair_type().equals("FREE")){

                        signal_type.check(R.id.buy_option_id1);
                    }else{
                        signal_type.check(R.id.sell_option_id1);
                    }


                    tp1.setText(pairBundle.getTake_profit_1());
                    tp2.setText(pairBundle.getTake_profit_2());
                    sl.setText(pairBundle.getStop_loss());
                    sl_pip.setText(pairBundle.getSl_pip());
                    tp1_pip.setText(pairBundle.getTp1_pip());
                    tp2_pip.setText(pairBundle.getTp2_pip());
                    initial_value.setText(pairBundle.getInitial_price());
                    if(!pairBundle.getPair_statas().equals("Expired")){
                        current_value.setText(pairBundle.getCurrent_price());
                    }

                    update_time.setText(pairBundle.getUpdate_time());
                    statas_view.setText(pairBundle.getPair_statas());
                    result_view.setText(pairBundle.getTrade_result());
                    signal_view.setText(pairBundle.getPair_action());
                    if(pairBundle.getPair_action().equals("BUY")){
                        signal_view.setTextColor(Color.GREEN);
                    }else{
                        signal_view.setTextColor(Color.RED);
                    }



                }
                else{
                    get_PairValue_From_API(pair_selected.replace(" ", "_"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                get_PairValue_From_API(pair_selected.replace(" ", "_"));
            }
        });

    }

    @Override
    public void onClick(View v) {
        
        if(v.getId()==R.id.copy_pest_id){

            String current_p = initial_value.getText().toString();
            sl.setText(current_p);
            tp1.setText(current_p);
            tp2.setText(current_p);
        }else if(v.getId()==R.id.submit_btn_id){


            if(radio_checked(signal)!=-1 && radio_checked(signal_type)!=-1){
                RadioButton selected = view.findViewById(radio_checked(signal));
                RadioButton selected_type = view.findViewById(radio_checked(signal_type));
                signaloption = selected.getText().toString();
                signalType = selected_type.getText().toString();

            }else{
                Toast.makeText(requireActivity(), "Please Select an Action", Toast.LENGTH_SHORT).show();
            }

            takep1 = tp1.getText().toString();
            takep2 = tp2.getText().toString();
            stopl = sl.getText().toString();
            initial_price = initial_value.getText().toString();

            if(!(TextUtils.isEmpty(takep1) || TextUtils.isEmpty(takep2) || TextUtils.isEmpty(stopl) || TextUtils.isEmpty(signaloption) || TextUtils.isEmpty(signalType))){

                addSignal();
                Toast.makeText(getContext(), "Signal updated!!!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(), "Please fill all the info!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int radio_checked(RadioGroup sig) {
        int id;
        id = sig.getCheckedRadioButtonId();
        return id;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new ShowSignalFragment()).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}