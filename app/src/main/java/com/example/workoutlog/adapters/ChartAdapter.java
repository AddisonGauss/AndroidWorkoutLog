package com.example.workoutlog.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.models.ChartData;
import com.example.workoutlog.models.Exercise;
import com.example.workoutlog.models.WorkoutDetails;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {
    private List<Exercise> exerciseList;
    private List<List<WorkoutDetails>> listOfListsOfWorkoutDetails;
    private List<ChartData> chartDataList;
    private Context context;

    public ChartAdapter(List<ChartData> chartDataList, Context context) {
        this.chartDataList = chartDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (chartDataList != null && chartDataList.size() > 0 && chartDataList.get(position).getyValues().size() > 0) {

            holder.mChart.setDragEnabled(true);
            holder.mChart.setScaleEnabled(true);
            holder.mChart.setTouchEnabled(true);
            holder.mChart.setPinchZoom(true);
            holder.txtExerciseName.setText(chartDataList.get(position).getExercise().getName());
            holder.txtExerciseName.setTextColor(context.getResources().getColor(R.color.green));
            holder.mChart.getAxisLeft().setEnabled(false);

            YAxis axisRight = holder.mChart.getAxisRight();
            axisRight.enableGridDashedLine(10f, 10f, 0f);
            axisRight.setDrawLimitLinesBehindData(false);
            XAxis xAxis = holder.mChart.getXAxis();
            xAxis.enableGridDashedLine(10f, 10f, 0f);
            //xAxis.setDrawLimitLinesBehindData(true);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(1f);

            xAxis.setValueFormatter(new MyXAxisValueFormatter(chartDataList.get(position).getDates()));

            LineDataSet set1 = new LineDataSet(chartDataList.get(position).getyValues(), "Data set 1");

            set1.setValueFormatter(new YAxisFormatter());
            holder.mChart.setBackgroundColor(context.getResources().getColor(R.color.dkgray));
            set1.setColor(context.getResources().getColor(R.color.green));
            set1.setLineWidth(3f);
            set1.setValueTextSize(15f);
            set1.setFillAlpha(255);
            set1.setCircleColor(Color.BLACK);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);

            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            holder.mChart.setData(data);

            holder.mChart.invalidate();
            holder.mChart.setExtraLeftOffset(25f);
            holder.mChart.getDescription().setEnabled(true);

            Description description = new Description();
            description.setText(chartDataList.get(position).getExercise().getName());
            description.setTextSize(12f);
            description.setTextColor(Color.GREEN);
            holder.mChart.setDescription(description);

        }

    }

    @Override
    public int getItemCount() {
        return chartDataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private LineChart mChart;
        private TextView txtExerciseName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mChart = itemView.findViewById(R.id.lineChartItem);
            txtExerciseName = itemView.findViewById(R.id.txtExerciseNameChart);

        }
    }

    public class YAxisFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            return "" + ((float) value);
        }


    }

    public class MyXAxisValueFormatter extends ValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value) {
            return super.getFormattedValue(value);
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if (value < 0) {
                return "";
            }
            if (value < mValues.length) {
                return mValues[(int) value];
            } else {
                return "";
            }

        }
    }

}
