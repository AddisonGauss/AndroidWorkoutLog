package com.example.navigation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigation.R;
import com.example.navigation.database.WorkoutViewModel;
import com.example.navigation.adapters.TrainingAdapter;
import com.example.navigation.interfaces.ExerciseNameListener;
import com.example.navigation.models.Exercise;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class TrainingFragment extends Fragment {

    private Button btnAddExercise;
    private TrainingAdapter trainingAdapter;
    private ExerciseNameListener exerciseNameListener;
    private workoutExerciseListener workoutExerciseListener;
    private List<Exercise> listOfExercises;

    public interface workoutExerciseListener {
        void sendExercise(List<Exercise> selectedExercisesToAdd);
    }

    public TrainingFragment() {
        // Required empty public constructor
    }


    public static TrainingFragment newInstance() {
        TrainingFragment fragment = new TrainingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        WorkoutViewModel workoutViewModel = workoutViewModel = new ViewModelProvider.AndroidViewModelFactory((getActivity().getApplication())).create(WorkoutViewModel.class);
        try {
            listOfExercises = workoutViewModel.getAllExercises();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
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

        if (listOfExercises != null) {
            RecyclerView recyclerView = getView().findViewById(R.id.recViewTraining);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            trainingAdapter = new TrainingAdapter(exerciseNameListener, this.getContext(), listOfExercises);
            recyclerView.setAdapter(trainingAdapter);
        }

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