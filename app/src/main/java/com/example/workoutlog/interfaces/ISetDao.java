package com.example.workoutlog.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.workoutlog.models.Set;
import com.example.workoutlog.models.BestSet;

import java.util.List;


@Dao
public interface ISetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Set set);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Set> sets);

    @Update
    void update(Set set);

    @Delete
    void delete(Set set);

    @Query("DELETE FROM set_table")
    void deleteAllSets();

    @Query("SELECT * FROM set_table")
    LiveData<List<Set>> getAllSets();


    @Transaction
    @Query("SELECT set_table.id,complete,displayNumber,MAX(weight) as weight, MAX(reps) as reps,hintReps, hintWeight FROM set_table,user_routine_exercise_table WHERE set_table.userRoutineExerciseRoutineId = user_routine_exercise_table.id AND user_routine_exercise_table.exerciseTypeId=:exerciseId")
    Set getMaxSetForExercise(long exerciseId);


    @Transaction
    @Query("SELECT *, MAX(set_table.weight) as weight  FROM set_table,user_routine_exercise_table,exercise_table WHERE set_table.userRoutineExerciseRoutineId = user_routine_exercise_table.id AND user_routine_exercise_table.exerciseTypeId = exercise_table.id GROUP BY user_routine_exercise_table.exerciseTypeId ")
    List<BestSet> getAllBestSets();

    @Transaction
    @Query("SELECT * FROM set_table,user_routine_exercise_table,exercise_table,workout_table WHERE set_table.userRoutineExerciseRoutineId = user_routine_exercise_table.id AND user_routine_exercise_table.exerciseTypeId = exercise_table.id AND user_routine_exercise_table.workoutId = workout_table.id GROUP BY user_routine_exercise_table.exerciseTypeId ")
    List<Set> getBestSets();

    @Transaction
    @Query("SELECT *, MAX(set_table.weight) as weight FROM set_table,user_routine_exercise_table,exercise_table,workout_table WHERE set_table.userRoutineExerciseRoutineId = user_routine_exercise_table.id AND user_routine_exercise_table.exerciseTypeId = :exerciseId AND user_routine_exercise_table.workoutId= :workoutId")
    BestSet getBestSetFromWorkoutWithExercise(long workoutId, long exerciseId);



}
