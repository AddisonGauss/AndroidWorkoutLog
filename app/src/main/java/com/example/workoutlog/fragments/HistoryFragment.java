package com.example.workoutlog.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.adapters.WorkoutAdapter;
import com.example.workoutlog.database.WorkoutViewModel;
import com.example.workoutlog.helpers.Constants;
import com.example.workoutlog.models.WorkoutDetails;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private Calendar [] calendarArray;
    private LinearLayoutManager linearLayoutManager;
    private WorkoutAdapter workoutAdapter;
    private DatePickerDialog datePicker;
    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        final RecyclerView recyclerView = getView().findViewById(R.id.recHistory);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        workoutAdapter = new WorkoutAdapter(getActivity());
        recyclerView.setAdapter(workoutAdapter);
        WorkoutViewModel workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);
        workoutViewModel.getAllWorkoutsAll().observe(getViewLifecycleOwner(), new Observer<List<WorkoutDetails>>() {
            @Override
            public void onChanged(List<WorkoutDetails> workouts) {
                workoutAdapter.setWorkouts(workouts);
                calendarArray = new Calendar[workouts.size()];
                for(int i = 0; i < workouts.size(); i++){
                    Date date = workouts.get(i).getWorkout().getStartTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendarArray[i] = calendar;
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.history_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.workoutHistoryCalendar):
                Calendar now = Calendar.getInstance();
                now.set(2021, 1, 1);
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,now);
                datePickerDialog.setHighlightedDays(calendarArray);
                datePickerDialog.setAccentColor(getResources().getColor(R.color.teal_700));
                datePickerDialog.setSelectableDays(calendarArray);
                //check for night mode
                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
                boolean isNightMode = prefs.getBoolean(Constants.NIGHT_MODE, false);
                if (isNightMode) {
                    datePickerDialog.setThemeDark(true);
                }
                //if no workouts
                if(calendarArray.length == 0){
                    datePickerDialog.setTitle("No Workout Data");
                }
                datePickerDialog.show(getActivity().getSupportFragmentManager(), "Datepickerdialog");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        System.out.println(monthOfYear +1);
        LocalDate givenDate = LocalDate.of(year, monthOfYear + 1 , dayOfMonth);
        System.out.println(givenDate);
        int position = workoutAdapter.getItemPositionWithDate(givenDate);
        linearLayoutManager.scrollToPositionWithOffset(position,0);
    }
}