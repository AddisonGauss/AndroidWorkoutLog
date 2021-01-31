package com.example.workoutlog.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.interfaces.IExerciseNameListener;
import com.example.workoutlog.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {

    private List<Exercise> listOfExercises;
    private IExerciseNameListener listener;

    public TrainingAdapter(IExerciseNameListener listener, List<Exercise> listOfExercises) {
        this.listOfExercises = listOfExercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.training_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (listOfExercises != null) {
            holder.txtExerciseName.setText(listOfExercises.get(position).getName());
            holder.txtTargetedBodyPart.setText(listOfExercises.get(position).getTargetedBodyPart());

            if (listOfExercises.get(position).isSelected()) {
                holder.parent.setBackgroundColor(holder.parent.getResources().getColor(R.color.light_green2));
            } else {
                holder.parent.setBackgroundColor(holder.parent.getSolidColor());
            }

            //if exercise name is clicked, set selected or deselected
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listOfExercises.get(position).setSelected(!listOfExercises.get(position).isSelected());

                    if (listOfExercises.get(position).isSelected()) {
                        holder.parent.setBackgroundColor(holder.parent.getResources().getColor(R.color.light_green2));
                    } else {
                        holder.parent.setBackgroundColor(holder.parent.getSolidColor());
                    }

                    if (getSelectedExercises().size() > 0) {
                        listener.showExerciseNames(true);
                    } else {
                        listener.showExerciseNames(false);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listOfExercises.size();
    }

    public List<Exercise> getSelectedExercises() {
        List<Exercise> selectedExercises = new ArrayList<>();
        for (Exercise e : listOfExercises) {
            if (e.isSelected()) {
                selectedExercises.add(e);
                notifyDataSetChanged();
            }
        }
        return selectedExercises;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtExerciseName, txtTargetedBodyPart;
        private ImageView exerciseImage;
        private ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            txtExerciseName = itemView.findViewById(R.id.txtExerciseName);
            txtTargetedBodyPart = itemView.findViewById(R.id.txtTargetBodyPart);
        }
    }
}
