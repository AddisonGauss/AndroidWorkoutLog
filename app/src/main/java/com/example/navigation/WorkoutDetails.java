package com.example.navigation;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetails implements Parcelable {

    public WorkoutDetails() {

    }

    @Embedded
    private Workout workout;

    @Relation(parentColumn = "id", entityColumn = "workoutId", entity = UserRoutineExercise.class)
    private List<RoutineDetails> userRoutineExercises = new ArrayList<>();


    protected WorkoutDetails(Parcel in) {
        this.workout = in.readParcelable(Workout.class.getClassLoader());
        in.readList(this.userRoutineExercises, RoutineDetails.class.getClassLoader());

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.workout, flags);
        dest.writeList(userRoutineExercises);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WorkoutDetails> CREATOR = new Creator<WorkoutDetails>() {
        @Override
        public WorkoutDetails createFromParcel(Parcel in) {
            return new WorkoutDetails(in);
        }

        @Override
        public WorkoutDetails[] newArray(int size) {
            return new WorkoutDetails[size];
        }
    };

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public List<RoutineDetails> getUserRoutineExercises() {
        return userRoutineExercises;
    }

    public void setUserRoutineExercises(List<RoutineDetails> userRoutineExercises) {
        this.userRoutineExercises = userRoutineExercises;
    }

    @Override
    public String toString() {
        return "WorkoutDetails{" +
                "workout=" + workout +
                ", userRoutineExercises=" + userRoutineExercises +
                '}';
    }

}
