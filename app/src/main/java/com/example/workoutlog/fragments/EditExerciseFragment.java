package com.example.workoutlog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutlog.R;
import com.example.workoutlog.database.WorkoutViewModel;
import com.example.workoutlog.models.Exercise;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ExecutionException;


public class EditExerciseFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText editTxtExerciseName, editTxtExerciseTargetBodyPart;
    private Button btnAddExercise;
    private WorkoutViewModel workoutViewModel;
    private ConstraintLayout constraintLayout;

    public interface AddExerciseListener {
        void sendNewExerciseToAddToDb(Exercise exercise);
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditExerciseFragment() {
        // Required empty public constructor
    }


    public static EditExerciseFragment newInstance(String param1, String param2) {
        EditExerciseFragment fragment = new EditExerciseFragment();
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
        return inflater.inflate(R.layout.fragment_edit_exercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTxtExerciseName = view.findViewById(R.id.editTxtExerciseName);
        editTxtExerciseTargetBodyPart = view.findViewById(R.id.editTxtExerciseTarget);
        btnAddExercise = view.findViewById(R.id.edit_exercise_btnAddExercise);
        constraintLayout = view.findViewById(R.id.edit_exercise_layout);


        workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!String.valueOf(editTxtExerciseName.getText()).trim().isEmpty() && !String.valueOf(editTxtExerciseTargetBodyPart.getText()).trim().isEmpty()) {
                    String name = String.valueOf(editTxtExerciseName.getText());
                    String targetedBodyPart = String.valueOf(editTxtExerciseTargetBodyPart.getText());

                    Exercise exerciseToAdd = new Exercise(name, targetedBodyPart);

                    try {

                        workoutViewModel.insertExercise(exerciseToAdd);
                        showSnackbar("Exercise: " + exerciseToAdd.getName() + " Added!");
                        editTxtExerciseName.getText().clear();
                        editTxtExerciseTargetBodyPart.getText().clear();
                        closeKeyboard();

                    } catch (ExecutionException e) {
                        showSnackbar("Error :" + e.getMessage());
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        showSnackbar("Error :" + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    showSnackbar("Please enter name and targeted body part");
                }

            }
        });
    }

    public void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(constraintLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setAnchorView(btnAddExercise);

        snackbar.show();
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }

    }
}