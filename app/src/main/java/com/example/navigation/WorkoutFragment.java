package com.example.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public interface WorkoutDetailsListener {
        void sendWorkoutDetails(WorkoutDetails workoutDetails);
    }
    public WorkoutFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static WorkoutFragment newInstance(String param1, String param2) {
        Log.d(TAG, "newInstance: ");
        
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);

        btnLaunchFragment = view.findViewById(R.id.btnLaunchOtherFragment);
        btnGoToRunningWorkout = view.findViewById(R.id.btnGoToRunningWorkout);
        btnLaunchFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);

                //initialize new workout and insert workout into database to retrieve id and set workoutDetail's workoutId to that id
                Workout workout = new Workout();
                workout.setStartTime(new Date());
                long id = 0;
                try {
                    id = workoutViewModel.insert(workout);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                workout.setId(id);
                WorkoutDetails workoutDetails = new WorkoutDetails();
                workoutDetails.setWorkout(workout);
                workoutDetails.setUserRoutineExercises(new ArrayList<RoutineDetails>());

                isWorkoutRunning = true;

                workoutDetailsListener.sendWorkoutDetails(workoutDetails);

        }});

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
        if(context instanceof WorkoutDetailsListener)
        {
            workoutDetailsListener =(WorkoutDetailsListener) context;
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

        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isRunning", isWorkoutRunning);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences  mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        isWorkoutRunning = prefs.getBoolean("isRunning", false);
        System.out.println("isworkoutrunning: " + isWorkoutRunning);
        if(isWorkoutRunning){
            btnGoToRunningWorkout.setVisibility(View.VISIBLE);
            btnLaunchFragment.setVisibility(View.GONE);
            String json = mPrefs.getString("workoutDetails", "");
            workoutDetails = gson.fromJson(json, WorkoutDetails.class);
        } else {
            btnGoToRunningWorkout.setVisibility(View.GONE);
            btnLaunchFragment.setVisibility(View.VISIBLE);
        }
    }
}