package com.github.khangnt.transactionviewer.utils;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public final class Preconditions {

    private Preconditions() {
    }

    @NonNull
    public static <T> T checkNotNull(T value) {
        if (value != null)
            return value;
        throw new NullPointerException();
    }

    public static void checkArgument(boolean cond) {
        if (!cond)
            throw new IllegalArgumentException();
    }

    public static void checkArgument(boolean cond, @Nullable Object errorMessage) {
        if (!cond)
            throw new IllegalArgumentException(String.valueOf(errorMessage));
    }

    @NonNull
    public static String checkNotEmpty(String value) {
        checkArgument(!TextUtils.isEmpty(value));
        return value;
    }

    @NonNull
    public static String checkNotEmpty(String value, @Nullable Object errorMessage) {
        checkArgument(!TextUtils.isEmpty(value), errorMessage);
        return value;
    }


}