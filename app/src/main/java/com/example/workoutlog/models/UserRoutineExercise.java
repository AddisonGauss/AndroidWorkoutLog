package com.example.workoutlog.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "user_routine_exercise_table")
public class UserRoutineExercise implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String user;

    @ForeignKey(entity = Exercise.class, parentColumns = "id", childColumns = "exerciseTypeId", onDelete = CASCADE)
    private Long exerciseTypeId;

    @ForeignKey(entity = Workout.class, parentColumns = "id", childColumns = "workoutId", onDelete = CASCADE)
    private Long workoutId;

    public UserRoutineExercise(String user, Long workoutId, Long exerciseTypeId) {
        this.user = user;
        this.exerciseTypeId = exerciseTypeId;
        this.workoutId = workoutId;
    }

    public UserRoutineExercise() {

    }

    protected UserRoutineExercise(Parcel in) {
        id = in.readLong();
        user = in.readString();
        if (in.readByte() == 0) {
            exerciseTypeId = null;
        } else {
            exerciseTypeId = in.readLong();
        }
        if (in.readByte() == 0) {
            workoutId = null;
        } else {
            workoutId = in.readLong();
        }
    }

    public static final Creator<UserRoutineExercise> CREATOR = new Creator<UserRoutineExercise>() {
        @Override
        public UserRoutineExercise createFromParcel(Parcel in) {
            return new UserRoutineExercise(in);
        }

        @Override
        public UserRoutineExercise[] newArray(int size) {
            return new UserRoutineExercise[size];
        }
    };

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }


    public Long getExerciseTypeId() {
        return exerciseTypeId;
    }

    public Long getWorkoutId() {
        return workoutId;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }


    public void setExerciseTypeId(Long exerciseTypeId) {
        this.exerciseTypeId = exerciseTypeId;
    }

    @Override
    public String toString() {
        return "UserRoutineExercise{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", exerciseTypeId=" + exerciseTypeId +
                ", workoutId=" + workoutId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(user);
        if (exerciseTypeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(exerciseTypeId);
        }
        if (workoutId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(workoutId);
        }
    }
}
