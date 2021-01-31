package com.example.workoutlog.fragments;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutlog.R;
import com.example.workoutlog.database.WorkoutViewModel;
import com.example.workoutlog.helpers.Constants;
import com.example.workoutlog.models.RoutineDetails;
import com.example.workoutlog.models.Set;
import com.example.workoutlog.models.WorkoutDetails;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class FinishedWorkoutFragment extends Fragment {

    private WorkoutDetails workoutDetails;
    private TextView txtFinishedWorkoutDetails, txtWorkoutName, txtWorkoutDuration, txtWorkoutDate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d");
    private String weight, reps;
    NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public FinishedWorkoutFragment() {
        // Required empty public constructor
    }

    public static FinishedWorkoutFragment newInstance() {
        FinishedWorkoutFragment fragment = new FinishedWorkoutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_finished_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtFinishedWorkoutDetails = view.findViewById(R.id.txtFinishedWorkoutDetails);
        txtWorkoutName = view.findViewById(R.id.txtFinishedWorkoutName);
        txtWorkoutDuration = view.findViewById(R.id.txtWorkoutDuration);
        txtWorkoutDate = view.findViewById(R.id.txtFinishedWorkoutDate);


        txtWorkoutName.setText(workoutDetails.getWorkout().getName());
        txtWorkoutDate.setText(dateFormat.format(workoutDetails.getWorkout().getStartTime()));

        long millis = Math.abs(workoutDetails.getWorkout().getFinishTime().getTime() - workoutDetails.getWorkout().getStartTime().getTime());
        long minutes = TimeUnit.MINUTES.convert(millis, TimeUnit.MILLISECONDS);
        txtWorkoutDuration.setText(String.valueOf(minutes));
        txtWorkoutDuration.append(" minutes");

        WorkoutViewModel workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);
        List<RoutineDetails> listOfRoutinesForWorkout = null;
        try {
            listOfRoutinesForWorkout = workoutViewModel.getListOfRoutinesForWorkout(workoutDetails.getWorkout().getId());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //Need to convert all exercise names and their sets to strings to display a summary of the finished workout
        List<List<Set>> listOfListsOfSets = new ArrayList<>();

        //get all the completed routines for the finished workout and add to list
        for (RoutineDetails routineDetails : listOfRoutinesForWorkout) {
            listOfListsOfSets.add(routineDetails.getSets());
        }

        //Each element will hold an array of Strings that contain the exercise name in index 0, each index after will contain a weight and rep value
        List<String[]> listOfStringArrays = new ArrayList<>();


        for (RoutineDetails routineDetails : listOfRoutinesForWorkout) {
            //add 1 to size to account for the first element holding the exercise name
            String[] setStringArray = new String[routineDetails.getSets().size() + 1];
            for (int i = 0; i < routineDetails.getSets().size(); i++) {
                if (i == 0) {
                    setStringArray[i] = routineDetails.getExercise().getName() + "\n";
                }

                //don't display decimals if weight/reps don't have any - else do display the decimal
                if (routineDetails.getSets().get(i).getWeight() % 1 == 0) {
                    numberFormat.setMaximumFractionDigits(0);
                    weight = numberFormat.format(routineDetails.getSets().get(i).getWeight());
                } else {
                    weight = String.valueOf(routineDetails.getSets().get(i).getWeight());
                }
                if (routineDetails.getSets().get(i).getReps() % 1 == 0) {
                    numberFormat.setMaximumFractionDigits(0);
                    reps = numberFormat.format(routineDetails.getSets().get(i).getReps());
                } else {
                    reps = String.valueOf(routineDetails.getSets().get(i).getReps());
                }
                setStringArray[i + 1] = "\t\t\t" + (i + 1) + ". " + weight + " lbs" + " x " + reps + "\n";
            }
            listOfStringArrays.add(setStringArray);
        }

        SpannableStringBuilder content = new SpannableStringBuilder();
        for (String[] stringArray : listOfStringArrays) {
            for (String t1 : stringArray) {
                if (t1 != null) {
                    int contentStart = content.length();
                    content.append(t1);
                    int contentEnd = content.length();
                    content.setSpan(new LeadingMarginSpan.Standard(0, 66), contentStart, contentEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }

        txtFinishedWorkoutDetails.setText(content);
    }
}