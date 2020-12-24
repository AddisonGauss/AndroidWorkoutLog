package com.example.navigation.adapters;

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

import com.example.navigation.R;
import com.example.navigation.interfaces.IAddSetClickHandler;
import com.example.navigation.interfaces.ISendFromSetAdapterToExercise;
import com.example.navigation.models.RoutineDetails;
import com.example.navigation.models.Set;
import com.example.navigation.database.WorkoutViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder>{
    private static final String TAG = "ExerciseAdapter";
    private Context context;
    public List<RoutineDetails> exercises;
    private IAddSetClickHandler IAddSetClickHandler;
    private Set prevMaxSet = new Set();
    private WorkoutViewModel workoutViewModel;
    private ISendFromSetAdapterToExercise ISendFromSetAdapterToExercise = new ISendFromSetAdapterToExercise() {
        @Override
        public void onItemClickedAt(RoutineDetails routineDetails) {
            //insert all sets with valid data entered into database
            IAddSetClickHandler.onSetsClickedAt(routineDetails.getSets());
        }
    };


    public ExerciseAdapter(Context context, IAddSetClickHandler IAddSetClickHandler, List<RoutineDetails> exercises, WorkoutViewModel workoutViewModel) {
        this.context = context;
        this.exercises = exercises;
        this.IAddSetClickHandler = IAddSetClickHandler;
        this.workoutViewModel = workoutViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        if(exercises != null && exercises.size() > 0) {
            holder.txtSetName.setText(exercises.get(position).getExercise().getName());

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext()));

            SetAdapter setAdapter = new SetAdapter(context, exercises.get(position), IAddSetClickHandler, ISendFromSetAdapterToExercise,holder.btnAddSet,prevMaxSet);
            holder.recyclerView.setAdapter(setAdapter);

            holder.btnAddSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Set blankSet = new Set();

                    int setSize =  exercises.get(position).getSets().size();

                    if(setSize == 0 ) {
                        //first set template will be added to the selected exercise
                        blankSet.setUserRoutineExerciseRoutineId(exercises.get(position).getUserRoutineExercise().getId());

                        try {
                            IAddSetClickHandler.onItemClickedAt(blankSet,"insert");
                        } catch (ExecutionException | InterruptedException e) {
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
        return exercises.size();
    }

    public void addExercise(RoutineDetails exercise){
        exercises.add(exercise);
        notifyDataSetChanged();
    }

    public void setExercises(List<RoutineDetails>exercises){
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
            txtSetName = itemView.findViewById(R.id.txtSetName);
            recyclerView = itemView.findViewById(R.id.recViewSet);
            parent = itemView.findViewById(R.id.parent);
            btnAddSet = itemView.findViewById(R.id.btnAddSet);
        }
    }

}






