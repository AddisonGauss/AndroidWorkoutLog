package com.example.workoutlog.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.adapters.ChartAdapter;
import com.example.workoutlog.database.WorkoutViewModel;
import com.example.workoutlog.models.BestSet;
import com.example.workoutlog.models.ChartData;
import com.example.workoutlog.models.Exercise;
import com.example.workoutlog.models.WorkoutDetails;
import com.github.mikephil.charting.data.Entry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ProfileFragment extends Fragment {

    private List<WorkoutDetails> listOfWorkoutDetails;
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM-dd");
    private ChartAdapter chartAdapter;
    private List<ChartData> chartDataList;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkoutViewModel workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);
        chartDataList = new ArrayList<>();
        try {
            List<Exercise> exerciseList = workoutViewModel.getAllExercises();

            //loop through all exercises and search for all workouts with that exercise
            for (int i = 0; i < exerciseList.size(); i++) {
                listOfWorkoutDetails = workoutViewModel.getAllWorkoutDetailsWithExercise(exerciseList.get(i).getId());
                //sort by workout start time
                listOfWorkoutDetails.sort(new Comparator<WorkoutDetails>() {
                    @Override
                    public int compare(WorkoutDetails o1, WorkoutDetails o2) {
                        return (o1.getWorkout().getStartTime().compareTo(o2.getWorkout().getStartTime()));
                    }
                });

                if (listOfWorkoutDetails.size() > 0) {
                    //x value for chart data will be the date of the workout, y value will be the max weight lifted for that workout and certain exercise
                    ChartData chartData = new ChartData(new String[listOfWorkoutDetails.size()], new ArrayList<>());
                    chartData.setExercise(exerciseList.get(i));

                    //loop through all workouts dealing with a specific exercise and get the max weight lifted
                    for (int j = 0; j < listOfWorkoutDetails.size(); j++) {
                        chartData.getDates()[j] = DATE_FORMAT.format(listOfWorkoutDetails.get(j).getWorkout().getStartTime());
                        try {
                            float weight;
                            BestSet set = workoutViewModel.getBestSetFromWorkoutWithExercise(listOfWorkoutDetails.get(j).getWorkout().getId(), exerciseList.get(i).getId());
                            weight = (float) set.getWeight();
                            chartData.getyValues().add(new Entry(j, weight));
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //add x and y values to list of items to display on chart
                    chartDataList.add(chartData);
                }
            }

            //display charts in recycler view
            RecyclerView recyclerView = getView().findViewById(R.id.recViewChart);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            chartAdapter = new ChartAdapter(chartDataList, getContext());
            recyclerView.setAdapter(chartAdapter);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}