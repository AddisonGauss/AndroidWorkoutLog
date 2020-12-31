package com.example.workoutlog.interfaces;

import com.example.workoutlog.models.Set;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IAddSetClickHandler {
    void onItemClickedAt(Set set, String operation) throws ExecutionException, InterruptedException;
    void onSetsClickedAt(List<Set> sets);
}
