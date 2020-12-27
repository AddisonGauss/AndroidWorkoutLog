package com.example.navigation.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigation.R;
import com.example.navigation.models.WorkoutDetails;
import com.example.navigation.database.WorkoutViewModel;
import com.example.navigation.adapters.ChartAdapter;
import com.example.navigation.models.BestSet;
import com.example.navigation.models.ChartData;
import com.example.navigation.models.Exercise;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ProfileFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    private static final String TAG = "ProfileFragment";

    private LineChart mChart;
    private List<WorkoutDetails> listOfWorkoutDetails;
    private List<List<WorkoutDetails>> listOflistsOfWorkoutDetails;
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

                if(listOfWorkoutDetails.size() > 0) {
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
                    //add
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

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.d(TAG, "onChartGestureStart: X: " + me.getX() + " Y: " + me.getY());
        Log.i(TAG, "onChartGestureStart: ");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.d(TAG, "onChartGestureEnd: " + lastPerformedGesture);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.d(TAG, "onChartLongPressed: ");
        Log.i(TAG, "onChartLongPressed: ");

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i(TAG, "onChartDoubleTapped: ");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i(TAG, "onChartSingleTapped: ");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i(TAG, "onChartFling: velX: " + velocityX + " veloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i(TAG, "onChartScale: ScaleX: " + scaleX + " ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i(TAG, "onValueSelected: " + e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i(TAG, "onNothingSelected: ");
    }

}