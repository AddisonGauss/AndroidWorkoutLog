package com.example.navigation;

import java.util.List;
import java.util.concurrent.ExecutionException;

interface IAddSetClickHandler {
    void onItemClickedAt(Set set, String operation) throws ExecutionException, InterruptedException;
    void onSetsClickedAt(List<Set> sets);
}
