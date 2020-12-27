package com.example.navigation.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercise_table")
public class Exercise implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private String targetedBodyPart;

    private boolean isSelected =false;

    public Exercise() {

    }

    public Exercise(String name, String targetedBodyPart) {
        this.name = name;
        this.targetedBodyPart = targetedBodyPart;
    }

    protected Exercise(Parcel in) {
        id = in.readLong();
        name = in.readString();
        targetedBodyPart = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setTargetedBodyPart(String targetedBodyPart) {
        this.targetedBodyPart = targetedBodyPart;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTargetedBodyPart() {
        return targetedBodyPart;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(targetedBodyPart);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", targetedBodyPart='" + targetedBodyPart + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
