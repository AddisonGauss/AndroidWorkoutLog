package com.example.workoutlog.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class RoutineDetails implements Parcelable {

    @Embedded
    private UserRoutineExercise userRoutineExercise;

    @Relation(parentColumn = "id", entityColumn = "userRoutineExerciseRoutineId", entity = Set.class)
    private List<Set> sets = new ArrayList<>();

    @Relation(parentColumn = "exerciseTypeId", entityColumn = "id", entity = Exercise.class)
    Exercise exercise;

    public RoutineDetails() {
    }


    protected RoutineDetails(Parcel in) {
        userRoutineExercise = in.readParcelable(UserRoutineExercise.class.getClassLoader());
        exercise = in.readParcelable(Exercise.class.getClassLoader());
        in.readList(this.sets, Set.class.getClassLoader());
    }

    public static final Creator<RoutineDetails> CREATOR = new Creator<RoutineDetails>() {
        @Override
        public RoutineDetails createFromParcel(Parcel in) {
            return new RoutineDetails(in);
        }

        @Override
        public RoutineDetails[] newArray(int size) {
            return new RoutineDetails[size];
        }
    };

    public UserRoutineExercise getUserRoutineExercise() {
        return userRoutineExercise;
    }

    public void setUserRoutineExercise(UserRoutineExercise userRoutineExercise) {
        this.userRoutineExercise = userRoutineExercise;
    }

    public List<Set> getSets() {
        return sets;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    @Override
    public String toString() {
        return "RoutineDetails{" +
                "userRoutineExercise=" + userRoutineExercise +
                ", sets=" + sets +
                ", exercise=" + exercise +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(userRoutineExercise, flags);
        dest.writeParcelable(exercise, flags);
        dest.writeList(sets);
    }
}
