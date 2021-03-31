package com.banglabs.pipslover.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.banglabs.pipslover.R;
import com.banglabs.pipslover.datahandler.StatisticsBundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ChartFragment extends Fragment {
    View view;
    Pie pie;
    AnyChartView anyChartView;
    DatabaseReference statistics_reference;

    public ChartFragment() {
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
        view = inflater.inflate(R.layout.fragment_chart, container, false);

        //adding custom toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_chart);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //initializing database reference
        statistics_reference = FirebaseDatabase.getInstance().getReference("Statistics");

        anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        pie = AnyChart.pie();
        loadingData();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {

            @Override
            public void onClick(Event event) {
                //Toast.makeText(ChartActivity.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    void loadingData(){

        statistics_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StatisticsBundle statisticsBundle = snapshot.getValue(StatisticsBundle.class);


                List<DataEntry> data = new ArrayList<>();
                data.add(new ValueDataEntry("TP 1", statisticsBundle.getRecent_tp1()));
                data.add(new ValueDataEntry("TP 2", statisticsBundle.getRecent_tp2()));
                data.add(new ValueDataEntry("SL", statisticsBundle.getRecent_sl()));
                data.add(new ValueDataEntry("Unhitted Signal", statisticsBundle.getRecent_untouched()));

                pie.data(data);

                pie.title("Signal Accurecy Chart of Last 7 days");

                pie.labels().position("outside");

                pie.legend().title().enabled(true);
                pie.legend().title()
                        .text("Retail channels")
                        .padding(0d, 0d, 10d, 0d);

                pie.legend()
                        .position("center-bottom")
                        .itemsLayout(LegendLayout.HORIZONTAL)
                        .align(Align.CENTER);

                anyChartView.setChart(pie);

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


}