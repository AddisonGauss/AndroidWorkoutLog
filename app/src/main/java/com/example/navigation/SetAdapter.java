package com.example.navigation;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {
    private static final String TAG = "SetAdapter";

    private Context mContext;
    private RoutineDetails currentExercise;
    public addSetClickHandler sendExternalClick;
    public sendFromSetAdapterToExercise sendExerciseAdapterTextInfo;
    private Button addSetButton;
    private Set prevMaxSet;
    private Set currentSet;


    public SetAdapter(Context mContext, RoutineDetails currentExercise, addSetClickHandler sendExternalClick, sendFromSetAdapterToExercise sendExerciseAdapterTextInfo, Button addSetButton, Set prevMaxSet) {
        this.mContext = mContext;
        this.currentExercise = currentExercise;
        if (currentExercise.getSets() == null) {
            Log.d(TAG, "SetAdapter: currentExercises.sets is null");
            currentExercise.setSets(new ArrayList<>());
        }
        this.sendExternalClick = sendExternalClick;
        this.sendExerciseAdapterTextInfo = sendExerciseAdapterTextInfo;
        this.addSetButton = addSetButton;
        this.prevMaxSet = prevMaxSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "SET ADAPTER onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_set_item, parent, false);
        return new ViewHolder(itemView);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "SET ADAPTER onBindViewHolder: ");
        if (currentExercise.getSets() != null && currentExercise.getSets().size() > 0) {

            currentSet = currentExercise.getSets().get(position);
            currentSet.setDisplayNumber(position + 1);

            holder.txtSetNumber.setText(String.valueOf(currentSet.getDisplayNumber()));
            holder.txtPrevMaxSet.setText(prevMaxSet.getWeight() + " x " + prevMaxSet.getReps());
            holder.editTxtWeight.setText(String.valueOf(currentSet.getWeight()));
            holder.editTxtReps.setText(String.valueOf(currentSet.getReps()));
            if (currentSet.isComplete()) {
                holder.btnSetComplete.setText("complete");
                holder.btnSetComplete.setBackgroundColor(Color.GREEN);
            } else {
                holder.btnSetComplete.setText("not complete");
                holder.btnSetComplete.setBackgroundColor(Color.GRAY);
            }
//            if (Double.parseDouble(String.valueOf(holder.editTxtReps.getText())) == 0.0){
//                holder.editTxtReps.setText(String.valueOf(0));
//            }
//            if (Double.parseDouble(String.valueOf(holder.editTxtWeight.getText())) == 0.0){
//                holder.editTxtWeight.setText(String.valueOf(0));
//            }


            holder.btnRemoveSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sendExternalClick.onItemClickedAt(currentExercise.getSets().get(position), "delete");
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });

            //reference to addSetButton is received from exercise adapter
            //When add set is clicked, copies values from last successful set and adds it into the currentExercise's set list
            addSetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: ");
                    if (String.valueOf(holder.editTxtWeight.getText()).trim().equals("")) {
                        holder.editTxtWeight.setText(String.valueOf(0));
                    }
                    if (String.valueOf(holder.editTxtReps.getText()).trim().equals("")) {
                        holder.editTxtReps.setText(String.valueOf(0));
                    }
                    currentSet = currentExercise.getSets().get(position);

                    //takes last entered set data in editText boxes and creates a new set with that data
                    Set editedSet = new Set(Double.parseDouble(String.valueOf(holder.editTxtWeight.getText())), Double.parseDouble(String.valueOf(holder.editTxtReps.getText())), false);

                    //set currentSet's info to info written in edittextbox to retrieve correct values incase focusChange doesn't hit
                    currentSet.setWeight(editedSet.getWeight());
                    currentSet.setReps(editedSet.getReps());

                    editedSet.setUserRoutineExerciseRoutineId(currentExercise.getUserRoutineExercise().getId());

                    currentExercise.getSets().add(editedSet);

                    //send exercise adapter reference to current exercise that add set button was clicked on
                    if (sendExerciseAdapterTextInfo != null) {
                        sendExerciseAdapterTextInfo.onItemClickedAt(currentExercise);
                    }
                }
            });


            //sets values after editText is changed to prevent losing entered data whenever livedata is changed
            holder.editTxtWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG, "afterTextChanged: WEIGHT");
                    if (!String.valueOf(s).equals(""))
                        currentExercise.getSets().get(position).setWeight(Double.parseDouble(String.valueOf(s)));
                }
            });
            holder.editTxtReps.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(TAG, "onTextChanged: ");
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG, "afterTextChanged: REPS");
                    if (!String.valueOf(s).equals(""))
                        currentExercise.getSets().get(position).setReps(Double.parseDouble(String.valueOf(s)));
                }
            });

            holder.btnSetComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set currentSet = currentExercise.getSets().get(position);
                    currentSet.setComplete(!currentSet.isComplete());

                    if (currentSet.isComplete()) {
                        //if marked complete add data to set and display complete design
                        holder.btnSetComplete.setBackgroundColor(Color.GREEN);
                        holder.btnSetComplete.setText("complete");
                        if (!String.valueOf(holder.editTxtReps.getText()).equals("") && !String.valueOf(holder.editTxtWeight.getText()).equals("")) {
                            currentSet.setReps(Double.parseDouble(String.valueOf(holder.editTxtReps.getText())));
                            currentSet.setWeight(Double.parseDouble(String.valueOf(holder.editTxtWeight.getText())));
                        }
                    } else {
                        //display incomplete design
                        holder.btnSetComplete.setText("not complete");
                        holder.btnSetComplete.setBackgroundColor(Color.GRAY);
                    }
                    //send the current exercise's sets to insert into database to prevent view from not updating set's text boxes that were edited after adding.
                    sendExternalClick.onSetsClickedAt(currentExercise.getSets());
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + currentExercise.getSets().size());
        return currentExercise.getSets().size();
    }


    public void addToSets(Set set) {
        Log.d(TAG, "addToSets: " + currentExercise.toString());
        set.setUserRoutineExerciseRoutineId(currentExercise.getUserRoutineExercise().getId());
        currentExercise.getSets().add(set);
        notifyDataSetChanged();

    }


    public void setSets(List<Set> sets) {
        Log.d(TAG, "setSets: ");
        currentExercise.setSets(sets);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout parent;
        private EditText editTxtWeight, editTxtReps;
        private TextView txtSetNumber, txtPrevMaxSet;
        private Button btnSetComplete, btnRemoveSet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "SET ADAPTER ViewHolder: ");
            parent = itemView.findViewById(R.id.parent);
            txtPrevMaxSet = itemView.findViewById(R.id.txtPrevMax);
            editTxtReps = itemView.findViewById(R.id.editTextReps);
            editTxtWeight = itemView.findViewById(R.id.editTxtPounds);
            txtSetNumber = itemView.findViewById(R.id.txtSetNumber);
            btnSetComplete = itemView.findViewById(R.id.btnComplete);
            btnRemoveSet = itemView.findViewById(R.id.btnRemoveSet);
            editTxtWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
            editTxtReps.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            Log.d(TAG, "finalize: ");
        }
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d(TAG, "onAttachedToRecyclerView: ");
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d(TAG, "onDetachedFromRecyclerView: ");
        super.onDetachedFromRecyclerView(recyclerView);
    }
}

