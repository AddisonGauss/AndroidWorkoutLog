package com.example.navigation;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private List<WorkoutDetails> listOfFinishedWorkouts = new ArrayList<>();
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM-dd-yyyy");


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(listOfFinishedWorkouts != null && listOfFinishedWorkouts.size() > 0){
            WorkoutDetails currentWorkoutDetails = listOfFinishedWorkouts.get(position);
            String date = DATE_FORMAT.format(listOfFinishedWorkouts.get(position).getWorkout().getStartTime());
            holder.txtWorkoutDate.setText(date);

            if(currentWorkoutDetails.getWorkout().getFinishTime() != null){
                long millis = Math.abs(currentWorkoutDetails.getWorkout().getFinishTime().getTime()- currentWorkoutDetails.getWorkout().getStartTime().getTime());
                long minutes = TimeUnit.MINUTES.convert(millis,TimeUnit.MILLISECONDS);
                System.out.println(minutes);
                holder.txtWorkoutDuration.setText(String.valueOf(minutes) + " minutes");
            }


            String [] exercisesStringArray = new String[currentWorkoutDetails.getUserRoutineExercises().size()];
            for(int i =0; i< currentWorkoutDetails.getUserRoutineExercises().size(); i++){
                if(i == currentWorkoutDetails.getUserRoutineExercises().size()-1){
                    exercisesStringArray[i] = currentWorkoutDetails.getUserRoutineExercises().get(i).getExercise().getName();
                } else{
                    exercisesStringArray[i] = currentWorkoutDetails.getUserRoutineExercises().get(i).getExercise().getName() + ",\t";
                }

            }
            SpannableStringBuilder content = new SpannableStringBuilder();
                for (String t1 : exercisesStringArray) {
                    if(t1 !=null) {
                        int contentStart = content.length();
                        content.append(t1);
                        int contentEnd = content.length();
                        content.setSpan(new LeadingMarginSpan.Standard(0, 66), contentStart, contentEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }

                holder.txtExercises.setText(content);

        }


    }

    @Override
    public int getItemCount() {
        return listOfFinishedWorkouts.size();
    }

    public void setWorkouts(List<WorkoutDetails> workouts){
        this.listOfFinishedWorkouts = workouts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtWorkoutDate, txtWorkoutDuration, txtExercises;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWorkoutDate = itemView.findViewById(R.id.txtWorkoutDate);
            txtWorkoutDuration = itemView.findViewById(R.id.txtDuration);
            txtExercises = itemView.findViewById(R.id.txtListOfExercises);
        }
    }
}
