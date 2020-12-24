package com.example.navigation.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.navigation.models.RoutineDetails;

import java.util.List;

@Dao
public interface IRoutineDetails {

    @Transaction
    @Query("SELECT * FROM user_routine_exercise_table WHERE workoutId = :workoutId")
    LiveData<List<RoutineDetails>> getAllRoutinesForWorkout(Long workoutId);

    @Transaction
    @Query("SELECT * FROM user_routine_exercise_table WHERE workoutId = :workoutId")
    List<RoutineDetails> getListOfRoutinesForWorkout(Long workoutId);

    @Transaction
    @Query("SELECT * FROM user_routine_exercise_table, set_table WHERE set_table.userRoutineExerciseRoutineId = user_routine_exercise_table.id")
    List<RoutineDetails> getListOfAllRoutines();

}
