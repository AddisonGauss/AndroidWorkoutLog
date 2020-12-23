package com.example.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class ShowWorkoutFragment extends Fragment {
    private static final String TAG = "ShowWorkoutFragment";
    private TextView txtWorkoutDate, txtWorkoutDuration, txtExercises;
    private Button btnPerformWorkoutAgain;
    private WorkoutDetails workoutDetails;
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM-dd-yyyy");
    private Boolean isWorkoutRunning;
    private WorkoutViewModel workoutViewModel;
    private UserRoutineExercise userRoutineExercise;
    private WorkoutFragment.WorkoutDetailsListener workoutDetailsListener;

    public ShowWorkoutFragment() {
        // Required empty public constructor
    }


    public static ShowWorkoutFragment newInstance() {
        ShowWorkoutFragment fragment = new ShowWorkoutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            workoutDetails = getArguments().getParcelable("workoutDetails");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtWorkoutDuration = view.findViewById(R.id.txtDuration);
        txtWorkoutDate = view.findViewById(R.id.txtWorkoutDate);
        txtExercises = view.findViewById(R.id.txtListOfExercises);
        btnPerformWorkoutAgain = view.findViewById(R.id.btnPerformWorkoutAgain);

        btnPerformWorkoutAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                isWorkoutRunning = prefs.getBoolean("isRunning", false);

                if(!isWorkoutRunning){
                    workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);

                    //initialize new workout and insert workout into database to retrieve id and set workoutDetail's workoutId to that id
                    Workout workout = new Workout();

                    workout.setStartTime(new Date());
                    long id = 0;
                    try {
                        id = workoutViewModel.insert(workout);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    workout.setId(id);

                    WorkoutDetails copyOfWorkoutDetails = new WorkoutDetails();
                    copyOfWorkoutDetails.setUserRoutineExercises(new ArrayList<RoutineDetails>());
                    copyOfWorkoutDetails.setWorkout(workout);



                    for (int i = 0; i < workoutDetails.getUserRoutineExercises().size(); i++) {
                        userRoutineExercise = new UserRoutineExercise();
                        userRoutineExercise.setWorkoutId(copyOfWorkoutDetails.getWorkout().getId());
                        userRoutineExercise.setExerciseTypeId(workoutDetails.getUserRoutineExercises().get(i).getExercise().getId());

                        RoutineDetails routineDetails = new RoutineDetails();
                        routineDetails.setSets(new ArrayList<>());
                        routineDetails.setExercise(workoutDetails.getUserRoutineExercises().get(i).getExercise());
                        routineDetails.setUserRoutineExercise(userRoutineExercise);

                        //add to list of routines to display on recycler view
                        copyOfWorkoutDetails.getUserRoutineExercises().add(routineDetails);

                        routineDetails.getUserRoutineExercise().setWorkoutId(copyOfWorkoutDetails.getWorkout().getId());
                        //insert routine into db and get back it's id to reference
                        long userRoutineExerciseId = 0;
                        try {
                            userRoutineExerciseId = workoutViewModel.insertUserRoutineExercise(routineDetails.getUserRoutineExercise());
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        routineDetails.getUserRoutineExercise().setId(userRoutineExerciseId);


                        for(int j=0; j< workoutDetails.getUserRoutineExercises().get(i).getSets().size(); j++){
                           Set newSet = new Set();
                           newSet.setUserRoutineExerciseRoutineId(userRoutineExerciseId);
                           newSet.setWeight(workoutDetails.getUserRoutineExercises().get(i).getSets().get(j).getWeight());
                           newSet.setReps(workoutDetails.getUserRoutineExercises().get(i).getSets().get(j).getReps());
                           routineDetails.getSets().add(newSet);
                            try {
                                long setId = workoutViewModel.insertSet(newSet);
                                newSet.setId(setId);
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    isWorkoutRunning = true;

                    workoutDetailsListener.sendWorkoutDetails(copyOfWorkoutDetails);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isRunning", isWorkoutRunning);
                    editor.apply();
                } else {
                    Toast.makeText(getContext(), "A workout is already running, please finish", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(workoutDetails != null) {
            String date = DATE_FORMAT.format(workoutDetails.getWorkout().getStartTime());
            txtWorkoutDate.setText(date);

            if(workoutDetails.getWorkout().getFinishTime() != null){
                long millis = Math.abs(workoutDetails.getWorkout().getFinishTime().getTime()- workoutDetails.getWorkout().getStartTime().getTime());
                long minutes = TimeUnit.MINUTES.convert(millis,TimeUnit.MILLISECONDS);
                txtWorkoutDuration.setText(String.valueOf(minutes));
                txtWorkoutDuration.append(" minutes");
            }


            List<List<Set>> listOfListsOfSets = new ArrayList<>();

            //get all the completed routines for the finished workout and add to list
            for (RoutineDetails routineDetails : workoutDetails.getUserRoutineExercises()) {
                listOfListsOfSets.add(routineDetails.getSets());
            }

            //Each element will hold an array of Strings that contain the exercise name in index 0, each index after will contain a weight and rep value
            List<String[]> listOfStringArrays = new ArrayList<>();


            for (RoutineDetails routineDetails : workoutDetails.getUserRoutineExercises()) {
                //add 1 to size to account for the first element holding the exercise name
                String[] setStringArray = new String[routineDetails.getSets().size() + 1];
                for (int i = 0; i < routineDetails.getSets().size(); i++) {
                    if (i == 0) {
                        setStringArray[i] = routineDetails.getExercise().getName() + "\n";
                    }
                    setStringArray[i + 1] = "\t" + (i + 1) + ". " + routineDetails.getSets().get(i).getWeight() + " x " + routineDetails.getSets().get(i).getReps() + "\n";
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

            txtExercises.setText(content);

        }

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof WorkoutFragment.WorkoutDetailsListener) {
            workoutDetailsListener = (WorkoutFragment.WorkoutDetailsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement WorkoutDetailsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        workoutDetailsListener = null;
    }
}