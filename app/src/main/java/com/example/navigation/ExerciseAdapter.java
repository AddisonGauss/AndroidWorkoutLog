package com.example.navigation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder>{
    private static final String TAG = "ExerciseAdapter";
    private Context context;
    public List<RoutineDetails> exercises;
    private addSetClickHandler addSetClickHandler;
    private Set prevMaxSet = new Set();
    private WorkoutViewModel workoutViewModel;
    private sendFromSetAdapterToExercise sendFromSetAdapterToExercise = new sendFromSetAdapterToExercise() {
        @Override
        public void onItemClickedAt(RoutineDetails routineDetails) {
            Log.d(TAG, "onItemClickedAt: ");
            //insert all sets with valid data entered into database
            addSetClickHandler.onSetsClickedAt(routineDetails.getSets());
        }
    };


    public ExerciseAdapter(Context context, addSetClickHandler addSetClickHandler, List<RoutineDetails> exercises, WorkoutViewModel workoutViewModel) {
        this.context = context;
        this.exercises = exercises;
        this.addSetClickHandler = addSetClickHandler;
        this.workoutViewModel = workoutViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(exercises != null && exercises.size() > 0) {
            Log.d(TAG, "onBindViewHolder: " + exercises.get(position).toString());

            holder.txtSetName.setText(exercises.get(position).getExercise().getName());

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext()));

            SetAdapter setAdapter = new SetAdapter(context, exercises.get(position), addSetClickHandler,sendFromSetAdapterToExercise,holder.btnAddSet,prevMaxSet);
            holder.recyclerView.setAdapter(setAdapter);

            holder.btnAddSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: inside  " + exercises.get(position).toString() + " POSITION = " + position);

                    Set blankSet = new Set(0,0);
                    int setSize =  exercises.get(position).getSets().size();

                    if(setSize ==0 ) {
                        //first set template will be added to the selected exercise
                        blankSet.setUserRoutineExerciseRoutineId(exercises.get(position).getUserRoutineExercise().getId());

                        try {
                            addSetClickHandler.onItemClickedAt(blankSet,"insert");
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }else {
            return;
        }
    }

    public List<RoutineDetails> getCurrentRoutines(){
        return exercises;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + exercises.size());
        return exercises.size();
    }

    public void addExercise(RoutineDetails exercise){
        Log.d(TAG, "addExercise: ");
        exercises.add(exercise);
        notifyDataSetChanged();
    }

    public void setExercises(List<RoutineDetails>exercises){
        Log.d(TAG, "setExercise: ");
        this.exercises = exercises;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "EXERCISE ViewHolder";
        private ConstraintLayout parent;
        private Button btnAddSet;
        private RecyclerView recyclerView;
        private TextView txtSetName,txtPrevMaxSet;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: ");
            txtSetName = itemView.findViewById(R.id.txtSetName);
            recyclerView = itemView.findViewById(R.id.recViewSet);
            parent = itemView.findViewById(R.id.parent);
            btnAddSet = itemView.findViewById(R.id.btnAddSet);
        }
    }

}






