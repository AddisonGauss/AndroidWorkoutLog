package com.example.workoutlog.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "workout_table")
public class Workout implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private Date startTime;

    private Date finishTime;

    public Workout() {

    }

    public Workout(Date startTime, Date finishTime) {
        this.startTime = startTime;
        this.finishTime = finishTime;
    }


    protected Workout(Parcel in) {
        id = in.readLong();
        startTime = (Date) in.readSerializable();
        finishTime = (Date) in.readSerializable();
        name = in.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", name='" + name + '\'' +
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
        dest.writeString(name);
    }


}
