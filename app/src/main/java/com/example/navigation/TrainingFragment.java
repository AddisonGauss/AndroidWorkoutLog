package com.example.navigation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class TrainingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btnAddExercise;
    private TrainingAdapter trainingAdapter;
    private ExerciseNameListener exerciseNameListener;
    private workoutExerciseListener workoutExerciseListener;

    public interface workoutExerciseListener {
        void sendExercise(List<Exercise> selectedExercisesToAdd);
    }

    public TrainingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TrainingFragment newInstance(String param1, String param2) {
        TrainingFragment fragment = new TrainingFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAddExercise = view.findViewById(R.id.btnAddExercises);

        //if an exercise is selected show add exercise button
        exerciseNameListener = new ExerciseNameListener() {
            @Override
            public void showExerciseNames(boolean isSelected) {
                if (isSelected) {
                    btnAddExercise.setVisibility(View.VISIBLE);
                } else {
                    btnAddExercise.setVisibility(View.GONE);
                }
            }
        };


        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Exercise> exerciseList = trainingAdapter.getSelectedExercises();
                workoutExerciseListener.sendExercise(exerciseList);
            }
        });

        RecyclerView recyclerView = getView().findViewById(R.id.recViewTraining);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        trainingAdapter = new TrainingAdapter(exerciseNameListener, this.getActivity());
        recyclerView.setAdapter(trainingAdapter);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof workoutExerciseListener) {
            workoutExerciseListener = (workoutExerciseListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement workoutExerciseListener");

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        workoutExerciseListener = null;
    }

}