package com.example.navigation;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;



public class ProfileFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener{

    private static final String TAG = "ProfileFragment";

    private LineChart mChart;
    private List<WorkoutDetails> listOfWorkoutDetails;
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM-dd");

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WorkoutViewModel workoutViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(WorkoutViewModel.class);
        try {
            listOfWorkoutDetails = workoutViewModel.getAllWorkoutDetailsWithExercise(2);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }




       String [] dates = new String[listOfWorkoutDetails.size()];
        ArrayList<Entry> yValues = new ArrayList<>();
       for (int i=0; i<listOfWorkoutDetails.size(); i++){
           dates[i] = DATE_FORMAT.format(listOfWorkoutDetails.get(i).getWorkout().getStartTime());
           try {
               float weight;
               BestSet set = workoutViewModel.getBestSetFromWorkoutWithExercise(listOfWorkoutDetails.get(i).getWorkout().getId(), 2);
               weight = (float) set.getWeight();
               yValues.add(new Entry(i, weight));
               System.out.println("WEIGHT IS " + weight);

           } catch (ExecutionException | InterruptedException e) {
               e.printStackTrace();
           }
       }


        mChart = view.findViewById(R.id.lineChart);

        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getAxisLeft().setEnabled(false);


        //axisRight.removeAllLimitLines();


        YAxis axisRight = mChart.getAxisRight();
        axisRight.enableGridDashedLine(10f, 10f, 0f);
        axisRight.setDrawLimitLinesBehindData(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setDrawLimitLinesBehindData(true);
        //xAxis.removeAllLimitLines();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);


        xAxis.setValueFormatter(new MyXAxisValueFormatter(dates));

            LineDataSet set1 = new LineDataSet(yValues, "Data set 1");
            set1.setFillAlpha(110);

            set1.setColor(getResources().getColor(R.color.purple_200));
            set1.setLineWidth(5f);
            set1.setValueTextSize(15f);
            set1.setCircleColor(getResources().getColor(R.color.purple_700));
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);

        //set1.setDrawFilled(true);
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            mChart.setData(data);
            mChart.invalidate();
            mChart.getDescription().setEnabled(true);

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.d(TAG, "onChartGestureStart: X: " + me.getX() + " Y: " + me.getY());
        Log.i(TAG, "onChartGestureStart: ");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.d(TAG, "onChartGestureEnd: " + lastPerformedGesture);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.d(TAG, "onChartLongPressed: ");
        Log.i(TAG, "onChartLongPressed: ");
        
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i(TAG, "onChartDoubleTapped: ");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i(TAG, "onChartSingleTapped: ");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i(TAG, "onChartFling: velX: " + velocityX + " veloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i(TAG, "onChartScale: ScaleX: " + scaleX + " ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i(TAG, "onValueSelected: " + e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i(TAG, "onNothingSelected: ");
    }

    public class MyXAxisValueFormatter extends ValueFormatter{
        private String[] mValues;
        public MyXAxisValueFormatter(String [] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value) {
            return super.getFormattedValue(value);
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if(value<0){
                return "";
            }
           if (value < mValues.length){
               return mValues[(int)value];
           } else {
               return "";
           }

        }
    }
}