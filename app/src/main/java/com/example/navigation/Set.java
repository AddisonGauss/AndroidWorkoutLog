package com.example.navigation;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "set_table")
public class Set implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private double weight;

    private double reps;

    private boolean complete;

    private int displayNumber = 0;

    @ForeignKey(entity = UserRoutineExercise.class, parentColumns = "id", childColumns = "userRoutineExerciseId", onDelete = CASCADE)
    private Long userRoutineExerciseRoutineId;

    public Set(double weight, double reps, boolean complete) {
        this.weight = weight;
        this.reps = reps;
        this.complete = complete;
    }

    public Set(double weight, double reps) {
        this.weight = 0;
        this.reps = 0;
        this.complete = false;
    }

    public Set() {

    }


    protected Set(Parcel in) {
        id = in.readLong();
        weight = in.readDouble();
        reps = in.readDouble();
        complete = in.readByte() != 0;
        displayNumber = in.readInt();
        if (in.readByte() == 0) {
            userRoutineExerciseRoutineId = null;
        } else {
            userRoutineExerciseRoutineId = in.readLong();
        }
    }

    public static final Creator<Set> CREATOR = new Creator<Set>() {
        @Override
        public Set createFromParcel(Parcel in) {
            return new Set(in);
        }

        @Override
        public Set[] newArray(int size) {
            return new Set[size];
        }
    };

    public int getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(int displayNumber) {
        this.displayNumber = displayNumber;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public double getReps() {
        return reps;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setReps(double reps) {
        this.reps = reps;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Long getUserRoutineExerciseRoutineId() {
        return userRoutineExerciseRoutineId;
    }

    public void setUserRoutineExerciseRoutineId(Long userRoutineExerciseRoutineId) {
        this.userRoutineExerciseRoutineId = userRoutineExerciseRoutineId;
    }

    @Override
    public String toString() {
        return "Set{" +
                "id=" + id +
                ", weight=" + weight +
                ", reps=" + reps +
                ", complete=" + complete +
                ", displayNumber=" + displayNumber +
                ", userRoutineExerciseRoutineId=" + userRoutineExerciseRoutineId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(weight);
        dest.writeDouble(reps);
        dest.writeByte((byte) (complete ? 1 : 0));
        dest.writeInt(displayNumber);
        if (userRoutineExerciseRoutineId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userRoutineExerciseRoutineId);
        }
    }
}
