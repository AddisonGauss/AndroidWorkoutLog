package com.example.workoutlog.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.workoutlog.models.BestSet;
import com.example.workoutlog.models.Exercise;
import com.example.workoutlog.models.RoutineDetails;
import com.example.workoutlog.models.Set;
import com.example.workoutlog.models.UserRoutineExercise;
import com.example.workoutlog.models.Workout;
import com.example.workoutlog.models.WorkoutDetails;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class WorkoutViewModel extends AndroidViewModel {
    private WorkoutRepository repository;
    private LiveData<List<WorkoutDetails>> allWorkouts;
    private LiveData<List<RoutineDetails>> allRoutinesForCurrentWorkout;

    public WorkoutViewModel(@NonNull Application application) {
        super(application);
        repository = new WorkoutRepository(application);
    }

    public long insert(Workout workout) throws ExecutionException, InterruptedException {
        return repository.insert(workout);
    }

    public long insertSet(Set set) throws ExecutionException, InterruptedException {
        return repository.insertSet(set);
    }

    public void insertAllSets(List<Set> setList) {
        repository.insertAllSets(setList);
    }

    public long insertExercise(Exercise exercise) throws ExecutionException, InterruptedException {
        return repository.insertExercise(exercise);
    }

    public long insertUserRoutineExercise(UserRoutineExercise userRoutineExercise) throws ExecutionException, InterruptedException {
        return repository.insertUserRoutineExercise(userRoutineExercise);
    }

    public void update(Workout workout) {
        repository.update(workout);
    }

    public void delete(Workout workout) {
        repository.delete(workout);
    }

    public void deleteAllWorkouts(Workout workout) {
        repository.deleteAllWorkouts();
    }

    public void deleteSet(Set set) {
        repository.deleteSet(set);
    }

    public void deleteUserRoutineExercise(UserRoutineExercise userRoutineExercise) {repository.deleteUserRoutineExercise(userRoutineExercise);}

    public WorkoutDetails getWorkoutWithId(long id) throws ExecutionException, InterruptedException {
        return repository.getWorkoutWithId(id);
    }

    public float getMaxWeightFromWorkoutWithExercise(long workoutId, long exerciseId) throws ExecutionException, InterruptedException {
        return repository.getMaxWeightFromWorkoutWithExercise(workoutId, exerciseId);
    }

    public BestSet getBestSetFromWorkoutWithExercise(long workoutId, long exerciseId) throws ExecutionException, InterruptedException {
        return repository.getBestSetFromWorkoutWithExercise(workoutId, exerciseId);
    }

    public List<BestSet> getAllBestSets() throws ExecutionException, InterruptedException {
        return repository.getListOfBestSets();
    }

    public List<Exercise> getAllExercises() throws ExecutionException, InterruptedException {
        return repository.getAllExercises();
    }

    public List<RoutineDetails> getListOfRoutinesForWorkout(long workoutId) throws ExecutionException, InterruptedException {
        return repository.getListOfRoutinesForWorkout(workoutId);
    }

    public List<WorkoutDetails> getAllWorkoutDetails() throws ExecutionException, InterruptedException {
        return repository.getListOfAllWorkoutDetails();
    }

    public List<WorkoutDetails> getAllWorkoutDetailsWithExercise(long exerciseId) throws ExecutionException, InterruptedException {
        return repository.getAllWorkoutsWithExercise(exerciseId);
    }

    public LiveData<List<WorkoutDetails>> getWorkoutDetailsFromWorkoutId(long id) {
        return repository.getAllWorkoutsWithWorkoutId(id);
    }

    public LiveData<List<WorkoutDetails>> getAllWorkoutsAll() {
        return repository.getAllWorkoutsAll();
    }

    public LiveData<List<RoutineDetails>> getAllRoutinesForCurrentWorkout(long id) {
        return repository.getAllRoutinesForCurrentWorkout(id);
    }
}
