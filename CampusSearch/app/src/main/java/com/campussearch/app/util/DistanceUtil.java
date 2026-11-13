package com.campussearch.app.util;

import java.util.Locale;

public final class DistanceUtil {
    private DistanceUtil() { }

    /** R7: whole metres below 1,000 m, otherwise km with one decimal. */
    public static String format(float meters) {
        if (meters < 1000f) return String.format(Locale.US, "%d m", Math.round(meters));
        return String.format(Locale.US, "%.1f km", meters / 1000f);
    }
}
