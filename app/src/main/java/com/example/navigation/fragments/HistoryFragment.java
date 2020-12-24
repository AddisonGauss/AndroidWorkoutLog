package com.example.navigation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigation.R;
import com.example.navigation.adapters.WorkoutAdapter;
import com.example.navigation.models.WorkoutDetails;
import com.example.navigation.database.WorkoutViewModel;

import java.util.List;


public class HistoryFragment extends Fragment {


    public HistoryFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = getView().findViewById(R.id.recHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        WorkoutAdapter workoutAdapter = new WorkoutAdapter(getActivity());
        recyclerView.setAdapter(workoutAdapter);
        WorkoutViewModel workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);
        workoutViewModel.getAllWorkoutsAll().observe(getViewLifecycleOwner(), new Observer<List<WorkoutDetails>>() {
            @Override
            public void onChanged(List<WorkoutDetails> workouts) {
                workoutAdapter.setWorkouts(workouts);
            }
        });
    }
}