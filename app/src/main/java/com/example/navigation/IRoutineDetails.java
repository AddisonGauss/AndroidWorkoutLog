package com.example.navigation;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface IRoutineDetails {

    @Transaction
    @Query("SELECT * FROM user_routine_exercise_table WHERE workoutId = :workoutId")
    LiveData<List<RoutineDetails>> getAllRoutinesForWorkout(Long workoutId);

    @Transaction
    @Query("SELECT * FROM user_routine_exercise_table WHERE workoutId = :workoutId")
    List<RoutineDetails> getListOfRoutinesForWorkout(Long workoutId);

}
