package com.example.workoutlog.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.adapters.ExerciseAdapter;
import com.example.workoutlog.database.WorkoutViewModel;
import com.example.workoutlog.helpers.Constants;
import com.example.workoutlog.interfaces.IAddSetClickHandler;
import com.example.workoutlog.models.RoutineDetails;
import com.example.workoutlog.models.Set;
import com.example.workoutlog.models.WorkoutDetails;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InProgressWorkoutFragment extends Fragment {

    private Button btnAddExercise;
    private WorkoutDetails workoutDetails;
    private WorkoutViewModel workoutViewModel;
    private boolean isRunning;
    private ExerciseAdapter exerciseAdapter;


    public InProgressWorkoutFragment() {
        // Required empty public constructor
    }

    public static InProgressWorkoutFragment newInstance(WorkoutDetails workoutDetails) {
        InProgressWorkoutFragment fragment = new InProgressWorkoutFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_WORKOUT_DETAILS, workoutDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        setHasOptionsMenu(true);
        if (workoutDetails == null && getArguments() != null) {
            workoutDetails = getArguments().getParcelable(Constants.ARG_WORKOUT_DETAILS);
        }


        IAddSetClickHandler IAddSetClickHandler = new IAddSetClickHandler() {
            @Override
            public void onItemClickedAt(Set set, String operation) throws ExecutionException, InterruptedException {

                if (operation.equals("insert")) {
                    try {
                        long setResult = workoutViewModel.insertSet(set);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (operation.equals("delete")) {
                    workoutViewModel.deleteSet(set);
                }
            }

            @Override
            public void onSetsClickedAt(List<Set> sets) {

                try {
                    workoutViewModel.insertAllSets(sets);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        };

        workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);

        final RecyclerView recyclerView = getView().findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapter = new ExerciseAdapter(getActivity(), IAddSetClickHandler, workoutDetails.getUserRoutineExercises(), workoutViewModel);
        recyclerView.setAdapter(exerciseAdapter);

        //uses LiveData to handle any layout changes
        workoutViewModel.getAllRoutinesForCurrentWorkout(workoutDetails.getWorkout().getId()).observe(getViewLifecycleOwner(), new Observer<List<RoutineDetails>>() {
            @Override
            public void onChanged(List<RoutineDetails> workouts) {
                exerciseAdapter.setExercises(workouts);
            }
        });
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
        return inflater.inflate(R.layout.fragment_in_progress_workout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAddExercise = view.findViewById(R.id.btnAddExercise);

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start fragment that lets user select an exercise to start a routine on
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.action_insideDashboardFragment_to_trainingFragment);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.workout_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_workout:

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Finish Workout?")
                        .setMessage("All sets with valid data will be marked as completed")
                        .setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    saveWorkout();
                                } catch (ExecutionException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("Cancel", null);

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void saveWorkout() throws ExecutionException, InterruptedException {
        workoutDetails.getWorkout().setFinishTime(new Date());
        isRunning = false;

        workoutViewModel.insert(workoutDetails.getWorkout());

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.ARG_IS_RUNNING, false);
        editor.apply();

        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_WORKOUT_DETAILS, workoutDetails);
        NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
        navController.navigate(R.id.action_inProgressWorkoutFragment_to_finishedWorkoutFragment, args);
    }


    @Override
    public void onDestroy() {
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
        super.onDestroy();
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
        isRunning = prefs.getBoolean(Constants.ARG_IS_RUNNING, false);

        if (isRunning) {
            List<RoutineDetails> routinesToSave = exerciseAdapter.getCurrentRoutines();
            for (int i = 0; i < routinesToSave.size(); i++) {
                workoutViewModel.insertAllSets(routinesToSave.get(i).getSets());
            }

            //if workout is running, save workoutDetails to JSON inside shared preferences
            SharedPreferences.Editor prefsEditor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(workoutDetails);
            prefsEditor.putString(Constants.ARG_WORKOUT_DETAILS, json);
            prefsEditor.apply();
        }
    }


}