package com.example.workoutlog.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.helpers.DecimalDigitsInputFilter;
import com.example.workoutlog.interfaces.IAddSetClickHandler;
import com.example.workoutlog.interfaces.ISendFromSetAdapterToExercise;
import com.example.workoutlog.models.RoutineDetails;
import com.example.workoutlog.models.Set;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {

    private Context mContext;
    private RoutineDetails currentExercise;
    public IAddSetClickHandler sendExternalClick;
    public ISendFromSetAdapterToExercise sendExerciseAdapterTextInfo;
    private Button addSetButton;
    private double prevMaxWeightForExercise;
    private Set currentSet;
    private String prevMax;


    public SetAdapter(Context mContext, RoutineDetails currentExercise, IAddSetClickHandler sendExternalClick, ISendFromSetAdapterToExercise sendExerciseAdapterTextInfo, Button addSetButton, double prevMaxWeightForExercise) {
        this.mContext = mContext;
        this.currentExercise = currentExercise;
        if (currentExercise.getSets() == null) {
            currentExercise.setSets(new ArrayList<>());
        }
        this.sendExternalClick = sendExternalClick;
        this.sendExerciseAdapterTextInfo = sendExerciseAdapterTextInfo;
        this.addSetButton = addSetButton;
        this.prevMaxWeightForExercise = prevMaxWeightForExercise;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_set_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (currentExercise.getSets() != null && currentExercise.getSets().size() > 0) {

            currentSet = currentExercise.getSets().get(position);

            currentSet.setDisplayNumber(position + 1);
            holder.txtSetNumber.setText(String.valueOf(currentSet.getDisplayNumber()));

            //if max weight doesn't contain a decimal value don't show .0 else show decimal value
            prevMax = prevMaxWeightForExercise % 1 == 0 ? String.format(mContext.getResources().getString(R.string.lbs_no_decimal), prevMaxWeightForExercise) : String.format(mContext.getResources().getString(R.string.lbs_decimal), prevMaxWeightForExercise);
            holder.txtPrevMaxSet.setText(prevMax);

            //display hint data in edittext's hint if currentset's values are 0 and only display decimal value if there is one
            if (currentSet.getWeight() == 0) {
                holder.editTxtWeight.setHint(String.format("%.00f", currentSet.getHintWeight()));
            } else {
                holder.editTxtWeight.setText(currentSet.getWeight() % 1 == 0 ? String.format("%.00f", currentSet.getWeight()) : String.valueOf(currentSet.getWeight()));
            }

            if (currentSet.getReps() == 0) {
                holder.editTxtReps.setHint(String.format("%.00f", currentSet.getHintReps()));
            } else {
                holder.editTxtReps.setText(currentSet.getReps() % 1 == 0 ? String.format("%.00f", currentSet.getReps()) : String.valueOf(currentSet.getReps()));
            }

            //if there is valid numbers in both weight and reps, show activated checkmark set complete button
            if (!String.valueOf(holder.editTxtReps.getText()).equals("") && !String.valueOf(holder.editTxtWeight.getText()).equals("")) {
                holder.btnSetComplete.setActivated(true);
            }

            //if set complete button has been clicked, change color of recycler view item, change background of weight/rep edittexts, and change the color/icon of the set complete button
            if (currentSet.isComplete()) {
                holder.btnSetComplete.setBackground(mContext.getResources().getDrawable(R.drawable.check_button_pressed_background));
                holder.btnSetComplete.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_white));
                holder.txtSetNumber.setTextColor(Color.BLACK);
                holder.txtPrevMaxSet.setTextColor(Color.BLACK);


                holder.parent.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));

                holder.editTxtWeight.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));
                holder.editTxtReps.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));

                //display decimal only if not zero
                holder.editTxtWeight.setText(currentSet.getWeight() % 1 == 0 ? String.format("%.00f", currentSet.getWeight()) : String.valueOf(currentSet.getWeight()));
                holder.editTxtReps.setText(currentSet.getReps() % 1 == 0 ? String.format("%.00f", currentSet.getReps()) : String.valueOf(currentSet.getReps()));
            }


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
            addSetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSet = currentExercise.getSets().get(position);

                    Set editedSet = new Set();
                    //takes the last edittext's text data or the edittext's hint data and creates a new set with that hint data to display on next recycler view cycle
                    //this makes it easy to repeat the same weight/reps without having to enter the values again, just need to click the set complete button
                    editedSet.setHintWeight(Double.parseDouble(String.valueOf(String.valueOf(holder.editTxtWeight.getText()).equals("") ? currentSet.getHintWeight() : holder.editTxtWeight.getText())));
                    editedSet.setHintReps(Double.parseDouble(String.valueOf(String.valueOf(holder.editTxtReps.getText()).equals("") ? currentSet.getHintReps() : holder.editTxtReps.getText())));

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
                    if (!String.valueOf(s).equals("")) {
                        //if edittext weight isnt blank, set this set's data to whatever is typed in as it changes
                        currentExercise.getSets().get(position).setWeight(Double.parseDouble(String.valueOf(s)));
                        if (!String.valueOf(holder.editTxtReps.getText()).equals("") && !currentExercise.getSets().get(position).isComplete()) {
                            //if the edit text's rep value also has data, then change the complete checkmark logo to appear clickable
                            holder.btnSetComplete.setActivated(true);
                        }
                    } else {
                        //nothing is typed in the edit text's weight so set the checkmark to appear unclickable and set edittext hint to 0
                        holder.btnSetComplete.setActivated(false);
                        holder.editTxtWeight.setHint("0");
                        currentExercise.getSets().get(position).setHintWeight(0);
                        currentExercise.getSets().get(position).setWeight(0);
                    }
                }
            });

            holder.editTxtReps.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!String.valueOf(s).equals("")) {
                        //if edittext reps isnt blank, set this set's data to whatever is typed in, as it changes
                        currentExercise.getSets().get(position).setReps(Double.parseDouble(String.valueOf(s)));
                        if (!String.valueOf(holder.editTxtWeight.getText()).equals("") && !currentExercise.getSets().get(position).isComplete()) {
                            //if the edit text's weight value also has data, then change the complete checkmark logo to appear clickable
                            holder.btnSetComplete.setActivated(true);
                        }
                    } else {
                        //nothing is typed in the edit text's reps so set the checkmark to appear unclickable
                        holder.btnSetComplete.setActivated(false);
                        holder.editTxtReps.setHint("0");
                        currentExercise.getSets().get(position).setHintReps(0);
                        currentExercise.getSets().get(position).setReps(0);
                    }
                }
            });

            //this fixes an issue of the keyboard staying open if you click out of the edittext
            holder.editTxtReps.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });

            //this fixes an issue of the keyboard staying open if you click out of the edittext
            holder.editTxtWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });


            holder.btnSetComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Set currentSet = currentExercise.getSets().get(position);
                    currentSet.setComplete(!currentSet.isComplete());

                    if (currentSet.isComplete()) {

                        //if marked complete and no text has been entered, add the hint data to set and display complete design
                        if (String.valueOf(holder.editTxtReps.getText()).equals((""))) {
                            currentSet.setReps(Double.parseDouble(String.valueOf(holder.editTxtReps.getHint())));
                        }
                        if (String.valueOf(holder.editTxtWeight.getText()).equals((""))) {
                            currentSet.setWeight(Double.parseDouble(String.valueOf(holder.editTxtWeight.getHint())));
                        }
                    }
                    //send the current exercise's sets to insert into database to prevent view from not updating this and any other set's text boxes that might have been edited after adding.
                    sendExternalClick.onSetsClickedAt(currentExercise.getSets());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return currentExercise.getSets().size();
    }

    public List<Set> getSets() {
        return currentExercise.getSets();
    }

    public void addToSets(Set set) {
        set.setUserRoutineExerciseRoutineId(currentExercise.getUserRoutineExercise().getId());
        currentExercise.getSets().add(set);
        notifyItemChanged(currentExercise.getSets().size() - 1);
    }

    public void setSets(List<Set> sets) {
        currentExercise.setSets(sets);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout parent;
        private EditText editTxtWeight, editTxtReps;
        private TextView txtSetNumber, txtPrevMaxSet;
        private Button btnRemoveSet;
        private ImageButton btnSetComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            txtPrevMaxSet = itemView.findViewById(R.id.txtPrevMax);
            editTxtReps = itemView.findViewById(R.id.editTextReps);
            editTxtWeight = itemView.findViewById(R.id.editTxtPounds);
            txtSetNumber = itemView.findViewById(R.id.txtSetNumber);
            btnSetComplete = itemView.findViewById(R.id.btnComplete);
            btnRemoveSet = itemView.findViewById(R.id.btnRemoveSet);
            //only allow a certain amount of digits in edit text
            editTxtWeight.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
            editTxtReps.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
 
