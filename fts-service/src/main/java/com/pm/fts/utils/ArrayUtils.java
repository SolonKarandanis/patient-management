package com.pm.fts.utils;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class ArrayUtils {
    private ArrayUtils() {

    }

    public static String arrayGet(final String[] array, final int i) {
        return array != null && i >= 0 && i < array.length ? array[i] : "";
    }

    public static String[] toStringArray(@NonNull List<String> list) {
        String[] array = new String[list.size()];
        return list.toArray(array);
    }

    public static Integer[] toIntegerArray(@NonNull List<Integer> list) {
        Integer[] array = new Integer[list.size()];
        return list.toArray(array);
    }
}
