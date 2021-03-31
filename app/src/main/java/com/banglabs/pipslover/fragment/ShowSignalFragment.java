package com.banglabs.pipslover.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.banglabs.pipslover.datahandler.StatisticsBundle;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.banglabs.pipslover.R;
import com.banglabs.pipslover.activity.Login;
import com.banglabs.pipslover.adapter.PairAdapter;
import com.banglabs.pipslover.datahandler.PairBundle;
import com.banglabs.pipslover.datahandler.UserBundle;
import com.banglabs.pipslover.dialog.AddPairDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowSignalFragment extends Fragment {

    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    TextView navUsername, navEmail;
    private RequestQueue mQueue;
    private final int REQUEST_CODE = 11;
    String currentDateandTime;
    Boolean is_premioum = false;
    Button add_item;
    ListView pairs;
    DatabaseReference pair_reference, admin_reference, schedule_reference, statistics_reference, user_info_reference;
    DataSnapshot admin_snapshot, pair_snapshot, statistics_snapshot;
    List<PairBundle> pair_list;
    TextView timer_view;
    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public ShowSignalFragment() {



    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_signal, container, false);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currentDateandTime = sdf.format(new Date());

        //finding views
        toolbar = view.findViewById(R.id.toolbar);
        navigationView = view.findViewById(R.id.navmenu);
        drawerLayout = view.findViewById(R.id.drawerlayout_id);
        timer_view = view.findViewById(R.id.timer);
        navigationView = (NavigationView) view.findViewById(R.id.navmenu);
        add_item = view.findViewById(R.id.new_paer_btn);
        pairs = view.findViewById(R.id.paer_list);

        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.user_name_id);
        navEmail = (TextView) headerView.findViewById(R.id.user_mail_id);

        //setting firebase database references
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        admin_reference = FirebaseDatabase.getInstance().getReference("PairList");
        statistics_reference = FirebaseDatabase.getInstance().getReference("Statistics");
        assert firebaseUser != null;
        user_info_reference = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid());
        pair_reference = FirebaseDatabase.getInstance().getReference("Pairs").child(currentDateandTime);
        schedule_reference = FirebaseDatabase.getInstance().getReference("Scheduler");
        mQueue = Volley.newRequestQueue(requireActivity());
        //checking and forsing for app update
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(requireActivity());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(result -> {

            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, requireActivity(), REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }

        });


        //creating custom toolbar and navigation drawyer
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle( ((AppCompatActivity) requireActivity()), drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        
        timer_view.setText(currentDateandTime);

        pair_list = new ArrayList<>();


        add_item.setOnClickListener(v -> {
            AddPairDialog cdd = new AddPairDialog( ((AppCompatActivity) requireActivity()));
            cdd.show();
            /*
            AddPairs addPairs = new AddPairs();
            addPairs.show(getSupportFragmentManager(), "example dialog");*/
        });

        //handeling navigation click events
        navigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.profile:
                    ((AppCompatActivity) requireActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new ProfileFragment()).addToBackStack("stack").commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.privecy:
                     ((AppCompatActivity) requireActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new PrivecyFragment()).addToBackStack("stack").commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.risk_ratio:
                     ((AppCompatActivity) requireActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new RiskRatioFragment()).addToBackStack("stack").commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.contact:
                     ((AppCompatActivity) requireActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new ContactFragment()).addToBackStack("stack").commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.chart:
                     ((AppCompatActivity) requireActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new ChartFragment()).addToBackStack("stack").commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.referd_monitor:
                     ((AppCompatActivity) requireActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, new ReferdMonitorFragment()).addToBackStack("stack").commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;


                case R.id.share:
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                        String shareMessage = "";
                        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "choose one"));
                    } catch (Exception e) {
                        //e.toString();
                    }
                    break;

                case R.id.rate_id:
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                        String shareMessage = "if you like,share this app";
                        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=co.banglabs.pips_lover ";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "choose one"));

                    } catch (Exception e) {
                        //e.toString();
                    }
                    break;


                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent( ((AppCompatActivity) requireActivity()), Login.class);
                    startActivity(intent);
                    //drawerLayout.closeDrawer(GravityCompat.START);
                    break;
            }

            return true;
        });

        //getting user generel informations
        user_info_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserBundle userBundle = snapshot.getValue(UserBundle.class);
                assert userBundle != null;
                navUsername.setText(userBundle.getName().toUpperCase());
                navEmail.setText(userBundle.getEmail());
                if(Integer.parseInt(userBundle.getSubscription())>0){
                    is_premioum = true;
                }
                call_pair_reference();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //getting pairs for admin
        admin_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                admin_snapshot = snapshot;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        schedule_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String check = snapshot.getValue(String.class);
                if (check.equals("false")) {
                    call_statistics();
                    call_pair_reference();
                    schedule_reference.setValue("true");
                    derive_PairList(pair_snapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void call_pair_reference() {
        pair_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pair_snapshot = snapshot;

                pair_list.clear();

                for (DataSnapshot pairsnapshot : snapshot.getChildren()) {
                    //Log.d("output", String.valueOf(snapshot.getChildrenCount()));
                    PairBundle pairBundle = pairsnapshot.getValue(PairBundle.class);

                    if(is_premioum){
                        pair_list.add(pairBundle);
                    }else{
                        if(pairBundle.getPair_type().equals("FREE")){
                            pair_list.add(pairBundle);
                        }
                    }

                }

                try{
                    PairAdapter adapter = new PairAdapter(requireActivity(), pair_list);

                    pairs.setAdapter(adapter);
                    /*Log.d("inival", pair_list.get(0).getInitial_price() + " " +
                            pair_list.get(0).getInitial_price() + " " + pair_list.get(0).getPair_action() + " " +
                            pair_list.get(0).getPair_name() + " " + pair_list.get(0).getPair_statas() + " " +
                            pair_list.get(0).getStop_loss() + " " + pair_list.get(0).getTake_profit_1() + " " +
                            pair_list.get(0).getTake_profit_2() + " " + pair_list.get(0).getTrade_result() + " " +
                            pair_list.get(0).getUpdate_time());*/
                }catch (Exception e){
                    Log.d("value212", String.valueOf(e));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void call_statistics() {

        statistics_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statistics_snapshot = snapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void derive_PairList(DataSnapshot snapshot) {


        Log.d("finalout", "derive pair list");
        try {

            for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                String name = postSnapshot.getKey();

                if (snapshot.child(name).child("pair_statas").getValue(String.class).equals("Online")) {

                    getandset_PairValue_From_API(name.replace(" ", "_"));
                } else {
                    Log.d("newurl", "else");
                }

                Log.d("newurl", name);
            }
            schedule_reference.setValue("true");

        } catch (Exception e) {
            schedule_reference.setValue("true");
            Log.d("newurl", String.valueOf(e));
        }


    }

    private void getandset_PairValue_From_API(String name) {
        Log.d("finalout", "getandset pair value from api");
        String url = "https://free.currconv.com/api/v7/convert?apiKey=2ec64af2c4a5303dd43f&q=" + name;
        Log.d("newurl", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {

                JSONObject obj = response.getJSONObject("results");
                JSONObject obj2 = obj.getJSONObject(name);

                Double exchangeRate = obj2.getDouble("val");
                String pair_name = name.replace("_", " ");

                admin_reference.child(pair_name).child("current_price").setValue(String.valueOf(exchangeRate));
                pair_reference.child(pair_name).child("current_price").setValue(String.valueOf(exchangeRate));

                updatingStatas(pair_name, exchangeRate);

                Log.d("newurl", name + "= " + String.valueOf(exchangeRate));


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("outmsg2", String.valueOf(e));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);


    }

    private void updatingStatas(String pair_name, Double exchangeRate) {

        Log.d("finalout", String.valueOf(exchangeRate));
        String ssll = pair_snapshot.child(pair_name).child("stop_loss").getValue(String.class);
        String stp1 = pair_snapshot.child(pair_name).child("take_profit_1").getValue(String.class);
        String stp2 = pair_snapshot.child(pair_name).child("take_profit_2").getValue(String.class);
        String signal = pair_snapshot.child(pair_name).child("pair_action").getValue(String.class);
        String signal_type = pair_snapshot.child(pair_name).child("pair_type").getValue(String.class);
        if (TextUtils.isEmpty(ssll) || TextUtils.isEmpty(stp1) || TextUtils.isEmpty(stp2) || TextUtils.isEmpty(signal)) {

            Log.d("newurl", "blank found");
            //Toast.makeText(this, "blank found", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("newurl", ssll + " " + stp1 + " " + stp2 + " " + signal);
            Double dsl = null;
            if (ssll != null) {
                dsl = Double.parseDouble(ssll);
            }
            Double dtp1 = null;
            if (stp1 != null) {
                dtp1 = Double.parseDouble(stp1);
            }
            Double dtp2 = null;
            if (stp2 != null) {
                dtp2 = Double.parseDouble(stp2);
            }

            String result = "Waiting";
            assert signal != null;
            if (signal.equals("BUY")) {

                if (exchangeRate <= dsl) result = "Stop Loss";
                else if (exchangeRate >= dtp2) result = "Take Profit 2";
                else if (exchangeRate >= dtp1 && exchangeRate < dtp2) result = "Take Profit 1";

            } else if (signal.equals("SELL")) {

                if (exchangeRate >= dsl) result = "Stop Loss";
                else if (exchangeRate <= dtp2) result = "Take Profit 2";
                else if (exchangeRate <= dtp1 && exchangeRate > dtp2) result = "Take Profit 1";

            }
            if (result.equals("Stop Loss") || result.equals("Take Profit 2")) {
                admin_reference.child(pair_name).child("trade_result").setValue("Expired");
                pair_reference.child(pair_name).child("trade_result").setValue("Expired");
            }
            int current_value, history_value;
            StatisticsBundle statisticsBundle = statistics_snapshot.getValue(StatisticsBundle.class);

            if (result.equals("Stop Loss")) {

                current_value = statisticsBundle.getRecent_sl();
                history_value = statisticsBundle.getHistory_sl();
                current_value++;
                history_value++;
                statistics_reference.child("recent_sl").setValue(current_value);
                statistics_reference.child("history_sl").setValue(history_value);
                Log.d("logout", "al");

            } else if (result.equals("Take Profit 1") && !admin_snapshot.child(pair_name).child("pair_statas").getValue(String.class).equals("Take Profit 1")) {

                current_value = statisticsBundle.getRecent_tp1();
                history_value = statisticsBundle.getHistory_tp1();
                Log.d("logout", current_value + " " + history_value);
                current_value++;
                history_value++;
                statistics_reference.child("recent_tp1").setValue(current_value);
                statistics_reference.child("history_tp1").setValue(history_value);
                Log.d("logout", "tp1");

            } else if (result.equals("Take Profit 2")) {

                current_value = statisticsBundle.getRecent_tp2();
                history_value = statisticsBundle.getHistory_tp2();
                current_value++;
                history_value++;
                statistics_reference.child("recent_tp2").setValue(current_value);
                statistics_reference.child("history_tp2").setValue(history_value);
                //reducing value of tp1
                current_value = statisticsBundle.getRecent_tp1();
                history_value = statisticsBundle.getHistory_tp1();
                current_value--;
                history_value--;
                statistics_reference.child("recent_tp1").setValue(current_value);
                statistics_reference.child("history_tp1").setValue(history_value);
                Log.d("logout", "tp2");
            }

            admin_reference.child(pair_name).child("pair_statas").setValue(result);
            pair_reference.child(pair_name).child("pair_statas").setValue(result);
            Log.d("newurl", result);

        }

    }



}