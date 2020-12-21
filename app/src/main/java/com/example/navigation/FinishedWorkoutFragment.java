package com.example.navigation;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class FinishedWorkoutFragment extends Fragment {
    private static final String TAG = "FinishedWorkoutFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private WorkoutDetails workoutDetails;
    private TextView txtFinishedWorkoutDetails;
    private List<RoutineDetails> listOfFinishedRoutines;

    public FinishedWorkoutFragment() {
        // Required empty public constructor
    }


    public static FinishedWorkoutFragment newInstance(String param1, String param2) {
        FinishedWorkoutFragment fragment = new FinishedWorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            workoutDetails = getArguments().getParcelable("workoutDetails");
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
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        txtFinishedWorkoutDetails = view.findViewById(R.id.txtFinishedWorkoutDetails);

        WorkoutViewModel workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);
        List<RoutineDetails> listOfRoutinesForWorkout = null;
        try {
            listOfRoutinesForWorkout = workoutViewModel.getListOfRoutinesForWorkout(workoutDetails.getWorkout().getId());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        List<List<Set>> listOfListsOfSets = new ArrayList<>();
        for (RoutineDetails routineDetails : listOfRoutinesForWorkout) {
            listOfListsOfSets.add(routineDetails.getSets());
        }


        List<String[]> listOfStringArrays = new ArrayList<>();

        System.out.println(listOfListsOfSets.size());


        for (RoutineDetails routineDetails : listOfRoutinesForWorkout) {
            String[] setStringArray = new String[routineDetails.getSets().size() + 1];
            System.out.println("setStringarray size is " + setStringArray.length);
            for (int i = 0; i < routineDetails.getSets().size(); i++) {
                System.out.println("Inside for loop i = " + i);
                if (i == 0) {
                    setStringArray[i] = routineDetails.getExercise().getName() + "\n";
                }
                setStringArray[i + 1] = "\t" + (i + 1) + ". " + routineDetails.getSets().get(i).getWeight() + " x " + routineDetails.getSets().get(i).getReps() + "\n";
                System.out.println(setStringArray[i]);
                System.out.println(setStringArray[i + 1]);
            }
            listOfStringArrays.add(setStringArray);
        }

        SpannableStringBuilder content = new SpannableStringBuilder();
        for (String[] stringArray : listOfStringArrays) {
            for (String t1 : stringArray) {
                if (t1 != null) {
                    System.out.println(t1);
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