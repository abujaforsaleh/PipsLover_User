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
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.banglabs.pipslover.R;
import com.banglabs.pipslover.adapter.AgentAdapter;
import com.banglabs.pipslover.datahandler.AgentBundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ReferdMonitorFragment extends Fragment {

    View view;
    private ListView agentlistview;
    private DatabaseReference agent_reference;
    List<AgentBundle> agent_list;


    public ReferdMonitorFragment() {
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
        view = inflater.inflate(R.layout.fragment_referd_monitor, container, false);

        //adding custom toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_refmon);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        agent_reference =  FirebaseDatabase.getInstance().getReference("Reference");

        agent_list = new ArrayList<>();
        agentlistview = view.findViewById(R.id.agent_list);

        callAgentData();


        return view;
    }

    private void callAgentData() {

        agent_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                agent_list.clear();

                for(DataSnapshot agentSnapshot : snapshot.getChildren()){

                    //Log.d("output", String.valueOf(snapshot.getChildrenCount()));
                    AgentBundle agentBundle = agentSnapshot.getValue(AgentBundle.class);

                    agent_list.add(agentBundle);
                }

                AgentAdapter adapter = new AgentAdapter(getActivity(), agent_list);

                agentlistview.setAdapter(adapter);
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