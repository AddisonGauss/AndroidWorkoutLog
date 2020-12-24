package com.example.navigation.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.navigation.models.RoutineDetails;
import com.example.navigation.models.UserRoutineExercise;

import java.util.List;

@Dao
public interface IUserRoutineExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserRoutineExercise userRoutineExercise);

    @Update
    void update(UserRoutineExercise userRoutineExercise);

    @Delete
    void delete(UserRoutineExercise userRoutineExercise);

    //define a database operation with @Query
    @Query("DELETE FROM user_routine_exercise_table")
    void deleteAllUserRoutineExercises();

    @Query("SELECT * FROM user_routine_exercise_table")
    LiveData<List<UserRoutineExercise>> getAllUserRoutineExercises();

    @Transaction
    @Query("SELECT * FROM user_routine_exercise_table where exerciseTypeId=:exerciseId")
    List<RoutineDetails> getRoutineWithExerciseId(long exerciseId);

}
