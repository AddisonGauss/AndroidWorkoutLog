package com.example.navigation;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {

    private List<Exercise> listOfExercises;
    private List<Exercise> selectedExercises;
    private ExerciseNameListener listener;
    //recycler view needs our own listener
    private OnItemClickListener listener2;
    private Context context;

    public TrainingAdapter( ExerciseNameListener listener, Context context ) {
        List<Exercise> exerciseList;
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            listOfExercises = (Database.getInstance(context).exerciseDao().getAllExerciseNames());
        });

        //this.listOfExercises = listOfExercises;
        this.selectedExercises = new ArrayList<>();
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.training_item, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtExerciseName.setText(listOfExercises.get(position).getName());
        holder.txtTargetedBodyPart.setText(listOfExercises.get(position).getTargetedBodyPart());

        if(listOfExercises.get(position).isSelected()) {
            holder.parent.setBackgroundColor(Color.GREEN);
            //listener.showExerciseNames(true);
        } else {
            holder.parent.setBackgroundColor(holder.parent.getResources().getColor(R.color.white));
            //listener.showExerciseNames(false);
        }

        //if exercise name is clicked, set selected or deselected
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOfExercises.get(position).setSelected(!listOfExercises.get(position).isSelected());
                if (listOfExercises.get(position).isSelected()){
                    holder.parent.setBackgroundColor(Color.GREEN);

                } else{
                    holder.parent.setBackgroundColor(holder.parent.getResources().getColor(R.color.white));
                }
                if(getSelectedExercises().size() > 0){
                    listener.showExerciseNames(true);
                } else {
                    listener.showExerciseNames(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfExercises.size();
    }

    public List<Exercise> getSelectedExercises () {
        List<Exercise> selectedExercises = new ArrayList<>();
        for(Exercise e: listOfExercises){
            if(e.isSelected()) {
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
        private Button btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.parent);
            txtExerciseName = itemView.findViewById(R.id.txtExerciseName);
            txtTargetedBodyPart = itemView.findViewById(R.id.txtTargetBodyPart);
            btnAdd = itemView.findViewById(R.id.btnAddExercises);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get position that has been clicked
//                    int position = getAdapterPosition();
                    //no guarantee that setOnItemCLickListener has been clicked so check if null
                    //also to make sure that clicked item has valid position
//                    if (listener2 != null && position != RecyclerView.NO_POSITION) {
//                        listener2.onItemCLick(listOfExercises.get(position));
//                    }

                }
            });
        }

    }

    public interface OnItemClickListener {
        void onItemCLick(Exercise exerciseNameModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener2 = listener;
    }

}
