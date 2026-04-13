package com.pacchetto.util;

import java.util.ArrayList;
import java.util.List;

public class TickBatcher<T> {
    private final List<T> snapshot = new ArrayList<>();
    private int cursor = 0;

    public synchronized void refresh(List<T> source) {
        snapshot.clear();
        snapshot.addAll(source);
        if (cursor >= snapshot.size()) {
            cursor = 0;
        }
    }

    public synchronized List<T> nextBatch(int size) {
        if (snapshot.isEmpty() || size <= 0) {
            return List.of();
        }
        int end = Math.min(snapshot.size(), cursor + size);
        List<T> result = new ArrayList<>(snapshot.subList(cursor, end));
        cursor = end >= snapshot.size() ? 0 : end;
        return result;
    }
}
