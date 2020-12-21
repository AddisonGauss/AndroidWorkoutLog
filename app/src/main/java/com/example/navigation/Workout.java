package com.example.navigation;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "workout_table")
public class Workout implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;


    private Date startTime;

    private Date finishTime;

    public Workout( Date startTime, Date finishTime) {
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public Workout(){

    }

//    /**
//     * constructor that maps the {@link PublisherDetails} to {@link Publisher}
//     */
//    public Workout(WorkoutDetails workoutDetails) {
//        this.id = workoutDetails.getWorkout().getId();
//        this.startTime = workoutDetails.getWorkout().getStartTime();
//        this.finishTime = workoutDetails.getWorkout().getFinishTime();
//        this.routines = this.getRoutines(workoutDetails.getUserRoutineExercises());
//    }
//
//
//    private List<Author> getAuthors(List<AuthorBookDetails> authorBookDetails) {
//        for (AuthorBookDetails details : authorBookDetails) {
//            Author author = details.getAuthor();
//            author.setBooks(details.getBooks());
//            this.setAuthor(details.getAuthor());
//        }
//        return this.authors;
//    }
//    @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE)
//    private Integer userId;

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
