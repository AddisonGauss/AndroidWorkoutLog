package com.example.navigation.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "workout_table")
public class Workout implements Parcelable {

    public Workout() {

    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    private Date startTime;

    private Date finishTime;

    public Workout(Date startTime, Date finishTime) {
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    protected Workout(Parcel in) {
        id = in.readLong();
        startTime = (Date) in.readSerializable();
        finishTime = (Date) in.readSerializable();
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeSerializable(startTime);
        dest.writeSerializable(finishTime);
    }
}
