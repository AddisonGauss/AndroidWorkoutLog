package com.example.workoutlog.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
    private TextView txtCancelWorkout;
    private EditText editTextWorkoutName;
    private ImageView btnExerciseNameMenu;
    private WorkoutDetails workoutDetails;
    private WorkoutViewModel workoutViewModel;
    private boolean isRunning;
    private ExerciseAdapter exerciseAdapter;
    private IAddSetClickHandler IAddSetClickHandler;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (workoutDetails == null && getArguments() != null) {
            workoutDetails = getArguments().getParcelable(Constants.ARG_WORKOUT_DETAILS);
        }

        IAddSetClickHandler = new IAddSetClickHandler() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_in_progress_workout_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
        btnAddExercise = view.findViewById(R.id.btnAddExercise);
        txtCancelWorkout = view.findViewById(R.id.txtCancelWorkout);
        btnExerciseNameMenu = view.findViewById(R.id.btnExerciseNameMenu);
        editTextWorkoutName = view.findViewById(R.id.editTxtWorkoutName);

        editTextWorkoutName.setText(workoutDetails.getWorkout().getName());

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

        btnExerciseNameMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(getContext(), btnExerciseNameMenu);
                menu.inflate(R.menu.workout_name_menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editWorkoutName:
                                editTextWorkoutName.requestFocus();
                                editTextWorkoutName.setSelection(editTextWorkoutName.getText().length());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                menu.show();
            }
        });

        editTextWorkoutName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                workoutDetails.getWorkout().setName(String.valueOf(editTextWorkoutName.getText()));
            }
        });

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start fragment that lets user select an exercise to start a routine on
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.action_insideDashboardFragment_to_trainingFragment);
            }
        });

        txtCancelWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setTitle("Cancel Workout?")
                        .setMessage("Are you sure you want to cancel this workout?")
                        .setPositiveButton("Delete Workout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                workoutViewModel.delete(workoutDetails.getWorkout());
                                isRunning = false;
                                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean(Constants.ARG_IS_RUNNING, false);
                                editor.apply();
                                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                                navController.popBackStack(R.id.workoutFragment, true);
                                navController.navigate(R.id.workoutFragment);
                            }
                        })
                        .setNegativeButton("No", null);

                final AlertDialog alert = alertBuilder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(R.attr.colorOnBackground);
                        alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(R.attr.colorOnBackground);
                    }
                });
                alert.show();
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
                //clear focus from workout name's edit text to make sure setOnChangeFocusListener gets called, in case user was changing name and then clicked finish workout
                editTextWorkoutName.clearFocus();

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

                final AlertDialog alert = builder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(R.attr.colorOnBackground);
                        alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(R.attr.colorOnBackground);
                    }
                });
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

        //insert all sets again to prevent any edits to weight/rep amount user might have done and not clicked completed set
        for(RoutineDetails routineDetails : exerciseAdapter.getCurrentRoutines()){
            workoutViewModel.insertAllSets(routineDetails.getSets());
        }

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