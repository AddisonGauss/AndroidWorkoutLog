package com.example.navigation.interfaces;

import com.example.navigation.models.Set;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IAddSetClickHandler {
    void onItemClickedAt(Set set, String operation) throws ExecutionException, InterruptedException;
    void onSetsClickedAt(List<Set> sets);
}
