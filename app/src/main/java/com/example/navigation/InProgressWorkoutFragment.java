package com.example.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InProgressWorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InProgressWorkoutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_WORKOUT_DETAILS = "workoutDetails";
    private static final String ARG_PARAM2 = "param2";
    private Button btnAddExercise;
    private Workout currentWorkout;
    private WorkoutDetails workoutDetails;
    private WorkoutViewModel workoutViewModel;
    private UserRoutineExercise userRoutineExercise;
    private RoutineDetails routineDetails;
    private List<RoutineDetails> listOfRoutines;
    private long workoutId;
    private boolean isRunning;
    private ExerciseAdapter exerciseAdapter;
    private sendFinishedWorkoutInfo sendFinishedWorkoutInfoListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public interface sendFinishedWorkoutInfo{
        void sendWorkoutInfo(List<RoutineDetails> routineDetails);
    }
//    public interface workoutExerciseListener{
//        void onInputSent(Exercise exercise);
//    }

    public InProgressWorkoutFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static InProgressWorkoutFragment newInstance(WorkoutDetails workoutDetails) {
        InProgressWorkoutFragment fragment = new InProgressWorkoutFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_WORKOUT_DETAILS, workoutDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        if(workoutDetails== null && getArguments() != null){
            workoutDetails = getArguments().getParcelable(ARG_WORKOUT_DETAILS);
            System.out.println("WORKOUT DETAILS = " + workoutDetails.toString());

        }

        workoutId = workoutDetails.getWorkout().getId();
        addSetClickHandler addSetClickHandler = new addSetClickHandler() {
            @Override
            public void onItemClickedAt(Set set, String operation) throws ExecutionException, InterruptedException {

                if (operation.equals("insert")) {
                    System.out.println(" =====SETITEMS CHANGED OR CLICKED ===== " + set.toString());
                    try {
                        long setResult = workoutViewModel.insertSet(set);
                        System.out.println("===SET ID : =============== " + setResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("inserted set");
                } else if (operation.equals("delete")) {
                    workoutViewModel.deleteSet(set);
                    System.out.println("DELETED SET");
                }
            }
            @Override
            public void onSetsClickedAt(List<Set> sets) {
                System.out.println(" =====SETSSSSS ITEMS CHANGED OR CLICKED ===== " +  sets.toString());
                sets.toString();
                try {
                    workoutViewModel.insertAllSets(sets);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("inserted set");
            }
        };
        workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);
        final RecyclerView recyclerView = getView().findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapter = new ExerciseAdapter(getActivity(),addSetClickHandler, workoutDetails.getUserRoutineExercises(), workoutViewModel);
        recyclerView.setAdapter(exerciseAdapter);
        workoutViewModel.getAllRoutinesForCurrentWorkout(workoutId).observe(getViewLifecycleOwner(), new Observer<List<RoutineDetails>>() {
            @Override
            public void onChanged(List<RoutineDetails> workouts) {

                //System.out.println("=========== workouts.get(0) ===\n" + workouts.get(0));
                System.out.println(workoutDetails.toString());
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
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            workoutDetails = getArguments().getParcelable(ARG_WORKOUT_DETAILS);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_in_progress_workout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAddExercise = view.findViewById(R.id.btnAddExercise);

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity that lets user select an exercise to start a routine on

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
                try {
                    saveWorkout();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void saveWorkout() throws ExecutionException, InterruptedException {
        workoutDetails.getWorkout().setFinishTime(new Date());

        //insert all sets into database
//        for (int i = 0; i < workoutDetails.getUserRoutineExercises().size(); i++) {
//            workoutViewModel.insertAllSets(workoutDetails.getUserRoutineExercises().get(i).getSets());
//        }

        //insert workout
        //workoutDetails.setWorkout(currentWorkout);

        //TODO INSERT SETS

        isRunning = false;
        workoutViewModel.insert(workoutDetails.getWorkout());

        System.out.println("+++++++++++++++++++++++++++++++ \n +++++++++++++++++++ \n +++++++++" + workoutDetails.toString() + "++++++++++++++++++++++++++ \n +++++++++++++++++ \n ++++++++");

        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isRunning", false);
        editor.apply();



        Bundle args = new Bundle();
        args.putParcelable("workoutDetails", workoutDetails);
        NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
        navController.navigate(R.id.action_inProgressWorkoutFragment_to_finishedWorkoutFragment,args);



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
        List<RoutineDetails> routinesToSave = exerciseAdapter.getCurrentRoutines();
        for(int i =0; i<routinesToSave.size(); i++) {
            workoutViewModel.insertAllSets(routinesToSave.get(i).getSets());
        }
        SharedPreferences  mPrefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(workoutDetails);
        prefsEditor.putString("workoutDetails", json);
        //prefsEditor.putBoolean("isRunning", true);
        prefsEditor.commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof InProgressWorkoutFragment.sendFinishedWorkoutInfo)
        {
            sendFinishedWorkoutInfoListener =(InProgressWorkoutFragment.sendFinishedWorkoutInfo) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement sendFinishedWorkoutListener");

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sendFinishedWorkoutInfoListener = null;
    }

}