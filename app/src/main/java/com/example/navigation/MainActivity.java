package com.example.navigation;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements TrainingFragment.workoutExerciseListener, WorkoutFragment.WorkoutDetailsListener, InProgressWorkoutFragment.sendFinishedWorkoutInfo {
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private WorkoutViewModel workoutViewModel;
    private UserRoutineExercise userRoutineExercise;
    private RoutineDetails routineDetails;
    private InProgressWorkoutFragment inProgressWorkoutFragment;
    private WorkoutDetails workoutDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController);
        navController.popBackStack(R.id.workoutFragment, false);

        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            List<BestSet> listOfBestSets = (Database.getInstance(getApplication()).setDao().getAllBestSets());
            for (BestSet bestSet : listOfBestSets) {
                System.out.println(bestSet);
                System.out.println("FOR EXERCISE : " + bestSet.getExercise().getName() + " THE HIGHEST WEIGHT IS : " + bestSet.getWeight());
                System.out.println("ON DATE : " + bestSet.getWorkout().getFinishTime());
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        return navController.navigateUp() || super.onSupportNavigateUp();

    }


    @Override
    public void sendExercise(List<Exercise> selectedExercisesToAdd) {
        Log.d(TAG, "sendExercise: ");
        workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(WorkoutViewModel.class);

        for (int i = 0; i < selectedExercisesToAdd.size(); i++) {
            userRoutineExercise = new UserRoutineExercise();
            System.out.println("WORKOUT DETAILS IN MAIN IS ----" + workoutDetails);
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
        args.putParcelable("workoutDetails", workoutDetails);

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        navController.navigate(R.id.inProgressWorkoutFragment, args);
        navController.popBackStack(R.id.trainingFragment, true);

    }

    @Override
    public void sendWorkoutDetails(WorkoutDetails workoutDetails) {
        Log.d(TAG, "sendWorkoutDetails:");
        this.workoutDetails = workoutDetails;

//        insideDashboardFragment = InsideDashboardFragment.newInstance(workoutDetails);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, insideDashboardFragment);

        Bundle args = new Bundle();
        args.putParcelable("workoutDetails", workoutDetails);

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        navController.popBackStack();
        navController.navigate(R.id.inProgressWorkoutFragment, args);
    }

    @Override
    public void sendWorkoutInfo(List<RoutineDetails> routineDetails) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("listOfRoutines", (ArrayList<? extends Parcelable>) routineDetails);

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        navController.popBackStack();
        navController.navigate(R.id.finishedWorkoutFragment, args);
    }


}

