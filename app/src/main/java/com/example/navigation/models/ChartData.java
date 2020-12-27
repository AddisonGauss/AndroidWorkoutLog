package com.example.navigation.models;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class ChartData {

    //used as x value for charting
    private String[] dates;

    //used as y value for charting
    private ArrayList<Entry> yValues;

    //reference to exercise to display exercise name as chart title
    private Exercise exercise;

    public ChartData(String[] dates, ArrayList<Entry> yValues) {
        this.dates = dates;
        this.yValues = yValues;
    }

    public String[] getDates() {
        return dates;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }

    public ArrayList<Entry> getyValues() {
        return yValues;
    }

    public void setyValues(ArrayList<Entry> yValues) {
        this.yValues = yValues;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }
}
