package com.example.navigation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;


@androidx.room.Database(entities = {Workout.class,Exercise.class,UserRoutineExercise.class,Set.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase {
    private static final String TAG = "Database";
    private static Database instance;

    public abstract IWorkoutDao workoutDao();

    public abstract IExerciseDao exerciseDao();

    public abstract IUserRoutineExerciseDao userRoutineExerciseDao();

    public abstract IRoutineDetails routineDetailsDao();

    public abstract  IWorkoutDetailsDao workoutDetailsDao();

    public abstract ISetDao setDao();

    public static synchronized Database getInstance(Context context){
        Log.d(TAG, "getInstance: ");
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "database").fallbackToDestructiveMigration().addCallback(roomCallback).build();

        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        private static final String TAG = "Database";
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            Log.d(TAG, "onCreate: ");
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };



    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {
        private static final String TAG = "PopulateDbAsyncTask";
        private IWorkoutDao workoutDao;
        private IExerciseDao exerciseDao;
        private IUserRoutineExerciseDao userRoutineExerciseDao;
        private ISetDao setDao;
        private IWorkoutDetailsDao workoutDetailsDao;

        private PopulateDbAsyncTask(Database db){
            workoutDao = db.workoutDao();
            exerciseDao = db.exerciseDao();
            userRoutineExerciseDao = db.userRoutineExerciseDao();
            setDao = db.setDao();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: ");

            exerciseDao.insert(new Exercise( "Bench Press", "Chest"));
            exerciseDao.insert(new Exercise( "Squat", "Legs"));
            exerciseDao.insert(new Exercise( "Deadlift", "Back"));
            exerciseDao.insert(new Exercise( "Overhead Press", "Shoulder"));
            exerciseDao.insert(new Exercise( "Dumbell Curl", "Bicep"));
            exerciseDao.insert(new Exercise( "Shrug", "Traps"));
            exerciseDao.insert(new Exercise( "EZ bar curl", "Bicep"));
            exerciseDao.insert(new Exercise( "Skullcrusher", "Tricep"));
            exerciseDao.insert(new Exercise( "Hammer Curl", "Bicep"));
            exerciseDao.insert(new Exercise( "Neck Curl", "Neck"));
            exerciseDao.insert(new Exercise( "Stiff Leg Deadlift", "Hamstring"));
            exerciseDao.insert(new Exercise( "Quad Extension", "Quadricep"));
//            List<Set> sets = new ArrayList<>();
//            System.out.println("adding set");
//            sets.add(new Set(225, 10, true));
//            List<UserRoutineExercise> routines = new ArrayList<>();

//            Set set = new Set(150,10,true);
//            Set set2 = new Set(225,10,true);
//            set.setUserRoutineExerciseRoutineId(1);
//            set2.setUserRoutineExerciseRoutineId(2);
//            Exercise exercise = new Exercise("Squat", "Legs");
//            Exercise exercise2 = new Exercise("Deadlift", "Back");
//            User user = new User("add", "add", "add");
//            UserRoutineExercise userRoutineExercise = new UserRoutineExercise("add", 1,1);
//            UserRoutineExercise userRoutineExercise2 = new UserRoutineExercise("add", 1,2);


//            routines.add(userRoutineExercise);
//
//            Workout workout = new Workout(new Date(), new Date());
//
//
//            exerciseDao.insert(exercise);
//            exerciseDao.insert(exercise2);
//            userRoutineExerciseDao.insert(userRoutineExercise);
//            userRoutineExerciseDao.insert(userRoutineExercise2);
//            setDao.insert(set);
//            setDao.insert(set2);
//            workoutDao.insert(workout);
            return null;
        }
    }
}

