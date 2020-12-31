package com.example.workoutlog.database;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.workoutlog.interfaces.IExerciseDao;
import com.example.workoutlog.interfaces.IRoutineDetails;
import com.example.workoutlog.interfaces.ISetDao;
import com.example.workoutlog.interfaces.IUserRoutineExerciseDao;
import com.example.workoutlog.interfaces.IWorkoutDao;
import com.example.workoutlog.interfaces.IWorkoutDetailsDao;
import com.example.workoutlog.models.BestSet;
import com.example.workoutlog.models.Exercise;
import com.example.workoutlog.models.RoutineDetails;
import com.example.workoutlog.models.Set;
import com.example.workoutlog.models.UserRoutineExercise;
import com.example.workoutlog.models.Workout;
import com.example.workoutlog.models.WorkoutDetails;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class WorkoutRepository {
    private static final String TAG = "WorkoutRepository";
    private IWorkoutDao workoutDao;
    private IWorkoutDetailsDao workoutDetailsDao;
    private LiveData<List<WorkoutDetails>> allWorkoutDetails;
    private IExerciseDao exerciseDao;
    private ISetDao setDao;
    private IUserRoutineExerciseDao userRoutineExerciseDao;
    private IRoutineDetails routineDetailsDao;
    private List<RoutineDetails> allRoutineDetails;

    public WorkoutRepository(Application application) {
        Log.d(TAG, "WorkoutRepository: ");
        Database workoutDatabase = Database.getInstance(application);
        workoutDao = workoutDatabase.workoutDao();
        workoutDetailsDao = workoutDatabase.workoutDetailsDao();
        exerciseDao = workoutDatabase.exerciseDao();
        setDao = workoutDatabase.setDao();
        userRoutineExerciseDao = workoutDatabase.userRoutineExerciseDao();
        routineDetailsDao = workoutDatabase.routineDetailsDao();
    }


    public long insert(Workout workout) throws ExecutionException, InterruptedException {
        return new InsertWorkoutAsyncTask(workoutDao).execute(workout).get();
    }


    public long insertSet(Set set) throws ExecutionException, InterruptedException {
        return new InsertSet(setDao).execute(set).get();

    }

    public void deleteSet(Set set) {
        new DeleteSetAsyncTask(setDao).execute(set);
    }

    public void insertAllSets(List<Set> setList) {
        new InsertAllSetsAsyncTask(setDao).execute(setList);
    }

    public void deleteUserRoutineExercise(UserRoutineExercise userRoutineExercise) {
        new DeleteUserRoutineExerciseAsyncTask(userRoutineExerciseDao).execute(userRoutineExercise);
    }
    public long insertUserRoutineExercise(UserRoutineExercise userRoutineExercise) throws ExecutionException, InterruptedException {
        return new InsertUserRoutineExercise(userRoutineExerciseDao).execute(userRoutineExercise).get();
    }

    public void update(Workout workout) {
        new UpdateWorkoutAsyncTask(workoutDao).execute(workout);
    }

    public void delete(Workout workout) {
        new DeleteWorkoutAsyncTask(workoutDao).execute(workout);
    }

    public void deleteAllWorkouts() {
        new DeleteAllWorkoutsAsyncTask(workoutDao).execute();
    }

    public LiveData<List<WorkoutDetails>> getAllWorkoutsWithWorkoutId(long id) {
        return workoutDetailsDao.getAllWorkoutsWithWorkoutId(id);

    }

    public LiveData<List<WorkoutDetails>> getAllWorkoutsAll() {
        return workoutDetailsDao.getAllWorkoutsAll();

    }

    public LiveData<List<RoutineDetails>> getAllRoutinesForCurrentWorkout(long id) {
        return routineDetailsDao.getAllRoutinesForWorkout(id);
    }

    public BestSet getBestSetFromWorkoutWithExercise(long workoutId, long exerciseId) throws ExecutionException, InterruptedException {
        return new GetBestSetFromWorkoutWithExerciseAsyncTask(setDao).execute(workoutId, exerciseId).get();
    }

    public List<Exercise> getAllExercises() throws ExecutionException, InterruptedException {
        return new GetAllExercisesAsyncTask(exerciseDao).execute().get();
    }

    public List<WorkoutDetails> getAllWorkoutsWithExercise(long exerciseId) throws ExecutionException, InterruptedException {
        return new GetAllWorkoutDetailsWithExerciseAsyncTask(workoutDetailsDao).execute(exerciseId).get();
    }

    public List<RoutineDetails> getListOfRoutinesForWorkout(long workoutId) throws ExecutionException, InterruptedException {
        return new GetRoutinesForWorkoutAsyncTask(routineDetailsDao).execute(workoutId).get();
    }

    public List<WorkoutDetails> getListOfAllWorkoutDetails() throws ExecutionException, InterruptedException {
        return new GetAllWorkoutDetailsAsyncTask(workoutDetailsDao).execute().get();
    }


    //inner classes for asynch tasks
    private static class InsertWorkoutAsyncTask extends AsyncTask<Workout, Void, Long> {
        private IWorkoutDao workoutDao;

        private InsertWorkoutAsyncTask(IWorkoutDao workoutDao) {
            this.workoutDao = workoutDao;
        }

        @Override
        protected Long doInBackground(Workout... workouts) {
            return workoutDao.insert(workouts[0]);
        }
    }

    private static class InsertSet extends AsyncTask<Set, Void, Long> {
        private ISetDao setDao;

        private InsertSet(ISetDao setDao) {
            this.setDao = setDao;
        }

        @Override
        protected Long doInBackground(Set... sets) {
            return setDao.insert(sets[0]);

        }
    }

    private static class InsertAllSetsAsyncTask extends AsyncTask<List<Set>, Void, Void> {
        private ISetDao setDao;

        private InsertAllSetsAsyncTask(ISetDao setDao) {
            this.setDao = setDao;
        }

        @Override
        protected Void doInBackground(List<Set>... lists) {
            setDao.insertAll(lists[0]);
            return null;
        }
    }

    private static class InsertUserRoutineExercise extends AsyncTask<UserRoutineExercise, Void, Long> {
        private IUserRoutineExerciseDao userRoutineExerciseDao;

        private InsertUserRoutineExercise(IUserRoutineExerciseDao userRoutineExerciseDao) {
            this.userRoutineExerciseDao = userRoutineExerciseDao;
        }

        @Override
        protected Long doInBackground(UserRoutineExercise... userRoutineExercises) {
            return userRoutineExerciseDao.insert(userRoutineExercises[0]);

        }
    }

    private static class UpdateWorkoutAsyncTask extends AsyncTask<Workout, Void, Void> {
        private IWorkoutDao workoutDao;

        private UpdateWorkoutAsyncTask(IWorkoutDao workoutDao) {
            this.workoutDao = workoutDao;
        }

        @Override
        protected Void doInBackground(Workout... workouts) {
            workoutDao.update(workouts[0]);
            return null;
        }
    }

    private static class DeleteWorkoutAsyncTask extends AsyncTask<Workout, Void, Void> {
        private IWorkoutDao workoutDao;

        private DeleteWorkoutAsyncTask(IWorkoutDao workoutDao) {
            this.workoutDao = workoutDao;
        }

        @Override
        protected Void doInBackground(Workout... workouts) {
            workoutDao.delete(workouts[0]);
            return null;
        }
    }

    private static class DeleteAllWorkoutsAsyncTask extends AsyncTask<Void, Void, Void> {
        private IWorkoutDao workoutDao;

        private DeleteAllWorkoutsAsyncTask(IWorkoutDao workoutDao) {
            this.workoutDao = workoutDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            workoutDao.deleteAllWorkouts();
            return null;
        }
    }

    private static class DeleteUserRoutineExerciseAsyncTask extends AsyncTask<UserRoutineExercise, Void, Void> {
        private IUserRoutineExerciseDao userRoutineExerciseDao;

        //class is static so cannot access repository directly
        //so have to pass it over a constructor
        private DeleteUserRoutineExerciseAsyncTask(IUserRoutineExerciseDao userRoutineExerciseDao) {
            this.userRoutineExerciseDao = userRoutineExerciseDao;
        }

        @Override
        protected Void doInBackground(UserRoutineExercise... userRoutineExercises) {
            userRoutineExerciseDao.delete(userRoutineExercises[0]);
            return null;
        }
    }


    private static class DeleteSetAsyncTask extends AsyncTask<Set, Void, Void> {
        private ISetDao setDao;

        //class is static so cannot access repository directly
        //so have to pass it over a constructor
        private DeleteSetAsyncTask(ISetDao setDao) {
            this.setDao = setDao;
        }

        @Override
        protected Void doInBackground(Set... sets) {
            setDao.delete(sets[0]);
            return null;
        }
    }

    private static class GetRoutinesForWorkoutAsyncTask extends AsyncTask<Long, Void, List<RoutineDetails>> {
        private IRoutineDetails routineDetailsDao;

        private GetRoutinesForWorkoutAsyncTask(IRoutineDetails routineDetailsDao) {
            this.routineDetailsDao = routineDetailsDao;
        }

        @Override
        protected List<RoutineDetails> doInBackground(Long... longs) {
            return routineDetailsDao.getListOfRoutinesForWorkout(longs[0]);
        }
    }

    private static class GetAllWorkoutDetailsAsyncTask extends AsyncTask<Void, Void, List<WorkoutDetails>> {
        private IWorkoutDetailsDao workoutDetailsDao;

        private GetAllWorkoutDetailsAsyncTask(IWorkoutDetailsDao workoutDetailsDao) {
            this.workoutDetailsDao = workoutDetailsDao;
        }

        @Override
        protected List<WorkoutDetails> doInBackground(Void... voids) {
            return workoutDetailsDao.getAllWorkoutDetails();
        }
    }

    private static class GetAllWorkoutDetailsWithExerciseAsyncTask extends AsyncTask<Long, Void, List<WorkoutDetails>> {
        private IWorkoutDetailsDao workoutDetailsDao;

        private GetAllWorkoutDetailsWithExerciseAsyncTask(IWorkoutDetailsDao workoutDetailsDao) {
            this.workoutDetailsDao = workoutDetailsDao;
        }

        @Override
        protected List<WorkoutDetails> doInBackground(Long... longs) {
            return workoutDetailsDao.getAllWorkoutDetailsWithExercise(longs[0]);
        }
    }

    private static class GetBestSetFromWorkoutWithExerciseAsyncTask extends AsyncTask<Long, Void, BestSet> {
        private ISetDao setDao;

        private GetBestSetFromWorkoutWithExerciseAsyncTask(ISetDao setDao) {
            this.setDao = setDao;
        }

        @Override
        protected BestSet doInBackground(Long... longs) {
            return setDao.getBestSetFromWorkoutWithExercise(longs[0], longs[1]);
        }
    }

    private static class GetAllExercisesAsyncTask extends AsyncTask<Void, Void, List<Exercise>> {
        private IExerciseDao exerciseDao;

        private GetAllExercisesAsyncTask(IExerciseDao exerciseDao) {
            this.exerciseDao = exerciseDao;
        }

        @Override
        protected List<Exercise> doInBackground(Void... voids) {
            return exerciseDao.getAllExerciseNames();
        }
    }


}
