package com.example.navigation;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class WorkoutRepository {
    private static final String TAG = "WorkoutRepository";
    private IWorkoutDao workoutDao;
    private IWorkoutDetailsDao workoutDetailsDao;
    private LiveData<List<Workout>> allWorkouts;
    private LiveData<List<WorkoutDetails>> allWorkoutDetails;
    private IExerciseDao exerciseDao;
    private ISetDao setDao;
    private IUserRoutineExerciseDao userRoutineExerciseDao;
    private IRoutineDetails routineDetailsDao;
    private static boolean complete = false;
    private WorkoutDetails currentWorkoutDetails;
    private RoutineDetails routineDetailsWithExerciseId;

    public WorkoutRepository(Application application) {
        Log.d(TAG, "WorkoutRepository: ");
        Database workoutDatabase = Database.getInstance(application);
        workoutDao = workoutDatabase.workoutDao();
        workoutDetailsDao = workoutDatabase.workoutDetailsDao();
        exerciseDao = workoutDatabase.exerciseDao();
        setDao = workoutDatabase.setDao();
        userRoutineExerciseDao = workoutDatabase.userRoutineExerciseDao();
        routineDetailsDao = workoutDatabase.routineDetailsDao();
        allWorkoutDetails = workoutDetailsDao.getAllWorkouts(1);

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

//    public List<RoutineDetails> getRoutineWithExerciseId(int id){
//        return exerciseDao.getRoutineWithExercise(id);
//
//    }
    public LiveData<List<WorkoutDetails>> getAllWorkouts(long id){
        return allWorkoutDetails;

    }
    public LiveData<List<WorkoutDetails>> getAllWorkoutsAll(){
        return workoutDetailsDao.getAllWorkoutsAll();

    }
    public LiveData<List<RoutineDetails>> getAllRoutinesForCurrentWorkout(long id) {
        return routineDetailsDao.getAllRoutinesForWorkout(id);
    }
//    public LiveData<List<RoutineDetails>> getAllRoutines(int id){
//        return workoutDetailsDao.getAllRoutines(id);
//
//    }
    public List<Exercise> getAllExercises(){
        return exerciseDao.getAllExerciseNames();
    }

    public List<RoutineDetails> getListOfRoutinesForWorkout(long workoutId) throws ExecutionException, InterruptedException {
        return new GetRoutinesForWorkoutAsyncTask(routineDetailsDao).execute(workoutId).get();
    }


    //inner classes for asynch tasks
    private static class InsertWorkoutAsyncTask extends AsyncTask<Workout, Void, Long> {
        private static final String TAG = "InsertWorkoutAsyncTask";
        private IWorkoutDao workoutDao;

        private InsertWorkoutAsyncTask(IWorkoutDao workoutDao) {
            this.workoutDao = workoutDao;
        }
        @Override
        protected Long doInBackground(Workout... workouts) {
            return workoutDao.insert(workouts[0]);
        }
    }

    private static class InsertSet extends AsyncTask<Set,Void,Long>{
        private ISetDao setDao;

        private InsertSet(ISetDao setDao) {
            this.setDao = setDao;
        }
        @Override
        protected Long doInBackground(Set... sets) {
            return setDao.insert(sets[0]);

        }
    }
    private static class InsertAllSetsAsyncTask extends AsyncTask<List<Set>,Void,Void>{
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
    private static class InsertUserRoutineExercise extends AsyncTask<UserRoutineExercise,Void,Long>{
        private IUserRoutineExerciseDao userRoutineExerciseDao;

        private InsertUserRoutineExercise(IUserRoutineExerciseDao userRoutineExerciseDao) {
            this.userRoutineExerciseDao = userRoutineExerciseDao;
        }
        @Override
        protected Long doInBackground(UserRoutineExercise... userRoutineExercises) {
            return userRoutineExerciseDao.insert(userRoutineExercises[0]);

        }
    }

    private static class UpdateWorkoutAsyncTask extends AsyncTask<Workout,Void,Void>{
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

    private static class DeleteWorkoutAsyncTask extends AsyncTask<Workout,Void,Void>{
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

    private static class DeleteAllWorkoutsAsyncTask extends AsyncTask<Void,Void,Void>{
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

    private static class DeleteSetAsyncTask extends AsyncTask<Set,Void,Void>
    {
        private ISetDao setDao;

        //class is static so cannot access repository directly
        //so have to pass it over a constructor
        private DeleteSetAsyncTask(ISetDao setDao)
        {
            this.setDao = setDao;
        }
        @Override
        protected Void doInBackground(Set... sets)
        {
            setDao.delete(sets[0]);
            return null;
        }
    }

    private static class GetRoutinesForWorkoutAsyncTask extends AsyncTask<Long,Void,List<RoutineDetails>> {
        private IRoutineDetails routineDetailsDao;

        private GetRoutinesForWorkoutAsyncTask(IRoutineDetails routineDetailsDao)
        {
            this.routineDetailsDao = routineDetailsDao;
        }
        @Override
        protected List<RoutineDetails> doInBackground(Long... longs) {
            return routineDetailsDao.getListOfRoutinesForWorkout(longs[0]);
        }
    }


}
