package com.example.workoutlog.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.workoutlog.models.Exercise;

import java.util.List;

@Dao
public interface IExerciseDao {

    @Insert
    void insert(Exercise exercise);

    @Update
    void update(Exercise exercise);

    @Delete
    void delete(Exercise exercise);

    //define a database operation with @Query
    @Query("DELETE FROM exercise_table")
    void deleteAllExercises();

    @Query("SELECT * FROM exercise_table")
    LiveData<List<Exercise>> getAllExercises();

    @Query("SELECT * FROM exercise_table")
    List<Exercise> getAllExerciseNames();



}
