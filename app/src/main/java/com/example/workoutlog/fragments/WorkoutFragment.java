package com.example.workoutlog.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutlog.R;
import com.example.workoutlog.database.WorkoutViewModel;
import com.example.workoutlog.helpers.Constants;
import com.example.workoutlog.models.RoutineDetails;
import com.example.workoutlog.models.Workout;
import com.example.workoutlog.models.WorkoutDetails;
import com.google.gson.Gson;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;


public class WorkoutFragment extends Fragment {
    private static final String TAG = "WorkoutFragment";
    private Button btnLaunchFragment, btnGoToRunningWorkout;
    private WorkoutViewModel workoutViewModel;
    private Boolean isWorkoutRunning;
    private WorkoutDetailsListener workoutDetailsListener;
    private WorkoutDetails workoutDetails;

    public interface WorkoutDetailsListener {
        void sendWorkoutDetails(WorkoutDetails workoutDetails);
    }

    public WorkoutFragment() {
        // Required empty public constructor
    }

    public static WorkoutFragment newInstance() {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            workoutDetails = getArguments().getParcelable(Constants.ARG_WORKOUT_DETAILS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLaunchFragment = view.findViewById(R.id.btnLaunchOtherFragment);
        btnGoToRunningWorkout = view.findViewById(R.id.btnGoToRunningWorkout);
        btnLaunchFragment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);

                //initialize new workout and insert workout into database to retrieve id and set workoutDetail's workoutId to that id
                Workout workout = new Workout();
                LocalTime localTime = LocalTime.now();
                int timeOfDayHour = localTime.getHour();
                String name;
                if (timeOfDayHour >= 0 && timeOfDayHour < 12) {
                    name = "Morning Workout";
                } else if (timeOfDayHour >= 12 && timeOfDayHour < 17) {
                    name = "Afternoon Workout";
                } else if (timeOfDayHour >= 17 && timeOfDayHour < 20) {
                    name = "Evening Workout";
                } else if (timeOfDayHour >= 20 && timeOfDayHour < 24) {
                    name = "Night Workout";
                } else {
                    name = "Workout";
                }

                workout.setName(name);
                workout.setStartTime(new Date());
                long id = 0;
                try {
                    id = workoutViewModel.insert(workout);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                workout.setId(id);

                WorkoutDetails workoutDetails = new WorkoutDetails();
                workoutDetails.setWorkout(workout);
                workoutDetails.setUserRoutineExercises(new ArrayList<RoutineDetails>());

                isWorkoutRunning = true;

                Gson gson = new Gson();
                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();
                String json = gson.toJson(workoutDetails);
                prefsEditor.putString(Constants.ARG_WORKOUT_DETAILS, json);
                prefsEditor.apply();

                workoutDetailsListener.sendWorkoutDetails(workoutDetails);
            }
        });

        btnGoToRunningWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workoutDetailsListener.sendWorkoutDetails(workoutDetails);
                isWorkoutRunning = true;
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof WorkoutDetailsListener) {
            workoutDetailsListener = (WorkoutDetailsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement WorkoutDetailsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        workoutDetailsListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        //save value isWorkoutRunning to be able to go back to this workout if app or fragment is closed
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.ARG_IS_RUNNING, isWorkoutRunning);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        Gson gson = new Gson();
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);

        //search for value that would have been saved in the inProgressWorkout fragment to decide if workout is running or not
        isWorkoutRunning = prefs.getBoolean(Constants.ARG_IS_RUNNING, false);

        if (isWorkoutRunning) {
            //if there is a workout running, set view of buttons and retrieve the workout details that would have been saved to JSON inside shared preferences to retrieve if app is closed.
            btnGoToRunningWorkout.setVisibility(View.VISIBLE);
            btnLaunchFragment.setVisibility(View.GONE);

            String json = prefs.getString(Constants.ARG_WORKOUT_DETAILS, "");
            workoutDetails = gson.fromJson(json, WorkoutDetails.class);
            workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);
            try {
                workoutDetails = workoutViewModel.getWorkoutWithId(workoutDetails.getWorkout().getId());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            btnGoToRunningWorkout.setVisibility(View.GONE);
            btnLaunchFragment.setVisibility(View.VISIBLE);
        }
    }
}