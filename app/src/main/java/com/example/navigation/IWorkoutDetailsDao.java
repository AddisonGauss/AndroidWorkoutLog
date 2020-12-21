package com.example.navigation;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface IWorkoutDetailsDao {

    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    LiveData<List<WorkoutDetails>> getAllWorkouts(long workoutId);

    @Transaction
    @Query("SELECT * FROM workout_table")
    LiveData<List<WorkoutDetails>> getAllWorkoutsAll();

    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    WorkoutDetails getWorkoutDetailsFromWorkoutId(long workoutId);
}
