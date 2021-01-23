package com.example.workoutlog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.adapters.TrainingAdapter;
import com.example.workoutlog.database.WorkoutViewModel;
import com.example.workoutlog.interfaces.IExerciseNameListener;
import com.example.workoutlog.models.Exercise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class TrainingFragment extends Fragment {
    private FloatingActionButton btnAddExercise;
    private TrainingAdapter trainingAdapter;
    private IExerciseNameListener IExerciseNameListener;
    private workoutExerciseListener workoutExerciseListener;
    private List<Exercise> listOfExercises;

    //used to send selected exercises to main activity
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
        Collections.sort(listOfExercises, new Comparator<Exercise>() {
            @Override
            public int compare(Exercise o1, Exercise o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
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
        btnAddExercise = view.findViewById(R.id.btnAddExercise);
        btnAddExercise.setBackgroundColor(getResources().getColor(R.color.purple_500));

        //if an exercise is selected show add exercise button
        IExerciseNameListener = new IExerciseNameListener() {
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
            trainingAdapter = new TrainingAdapter(IExerciseNameListener, listOfExercises);
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