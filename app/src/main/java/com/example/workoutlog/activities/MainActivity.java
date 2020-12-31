package com.example.workoutlog.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.workoutlog.R;
import com.example.workoutlog.database.WorkoutViewModel;
import com.example.workoutlog.fragments.InProgressWorkoutFragment;
import com.example.workoutlog.fragments.TrainingFragment;
import com.example.workoutlog.fragments.WorkoutFragment;
import com.example.workoutlog.helpers.Constants;
import com.example.workoutlog.models.Exercise;
import com.example.workoutlog.models.RoutineDetails;
import com.example.workoutlog.models.UserRoutineExercise;
import com.example.workoutlog.models.WorkoutDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements TrainingFragment.workoutExerciseListener, WorkoutFragment.WorkoutDetailsListener {
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private WorkoutViewModel workoutViewModel;
    private UserRoutineExercise userRoutineExercise;
    private RoutineDetails routineDetails;
    private InProgressWorkoutFragment inProgressWorkoutFragment;
    private WorkoutDetails workoutDetails;
    private Boolean isRunning;
    private Boolean isNightMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        return navController.navigateUp() || super.onSupportNavigateUp();

    }


    @Override
    public void sendExercise(List<Exercise> selectedExercisesToAdd) {
        workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(WorkoutViewModel.class);

        for (int i = 0; i < selectedExercisesToAdd.size(); i++) {
            userRoutineExercise = new UserRoutineExercise();
            userRoutineExercise.setWorkoutId(workoutDetails.getWorkout().getId());
            userRoutineExercise.setExerciseTypeId(selectedExercisesToAdd.get(i).getId());

            routineDetails = new RoutineDetails();
            routineDetails.setSets(new ArrayList<>());
            routineDetails.setExercise(selectedExercisesToAdd.get(i));
            routineDetails.setUserRoutineExercise(userRoutineExercise);

            //add to list of routines to display on recycler view
            workoutDetails.getUserRoutineExercises().add(routineDetails);
            routineDetails.getUserRoutineExercise().setWorkoutId(workoutDetails.getWorkout().getId());

            //insert routine into db and get back it's id to reference
            long userRoutineExerciseId = 0;
            try {
                userRoutineExerciseId = workoutViewModel.insertUserRoutineExercise(routineDetails.getUserRoutineExercise());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            routineDetails.getUserRoutineExercise().setId(userRoutineExerciseId);
        }


        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_WORKOUT_DETAILS, workoutDetails);

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        navController.navigate(R.id.inProgressWorkoutFragment, args);
        //prevent from going back to exercise selection screen once an exercise has been chosen and added to the routine
        navController.popBackStack(R.id.trainingFragment, true);

    }

    @Override
    public void sendWorkoutDetails(WorkoutDetails workoutDetails) {
        this.workoutDetails = workoutDetails;

        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_WORKOUT_DETAILS, workoutDetails);

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        navController.navigate(R.id.inProgressWorkoutFragment, args);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        SharedPreferences prefs = getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();


        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                prefsEditor.putBoolean(Constants.NIGHT_MODE, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                prefsEditor.putBoolean(Constants.NIGHT_MODE, true);
                recreate();
                break;
        }
        prefsEditor.apply();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
        isRunning = prefs.getBoolean(Constants.ARG_IS_RUNNING, false);

        if (isRunning) {
            Gson gson = new Gson();
            String json = prefs.getString(Constants.ARG_WORKOUT_DETAILS, "");
            workoutDetails = gson.fromJson(json, WorkoutDetails.class);
        }

        isNightMode = prefs.getBoolean(Constants.NIGHT_MODE, false);
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


    }


}


