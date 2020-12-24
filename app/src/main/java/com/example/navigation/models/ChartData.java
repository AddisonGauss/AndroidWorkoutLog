package com.example.navigation.models;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class ChartData {

    private String[] dates;

    private ArrayList<Entry> yValues;

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
