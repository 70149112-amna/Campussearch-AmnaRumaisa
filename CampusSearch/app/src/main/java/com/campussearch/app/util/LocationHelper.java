package com.campussearch.app.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

/** All live-location code lives here. */
public final class LocationHelper {

    public interface Callback {
        void onLocation(Location location); // null when location is unavailable
    }

    private LocationHelper() { }

    public static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static boolean hasPermission(Context ctx) {
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    public static void getCurrent(Context ctx, Callback cb) {
        if (!hasPermission(ctx)) {
            cb.onLocation(null);
            return;
        }
        final FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(ctx);
        CancellationTokenSource cts = new CancellationTokenSource();
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                .addOnSuccessListener(loc -> {
                    if (loc != null) {
                        cb.onLocation(loc);
                    } else {
                        client.getLastLocation()
                                .addOnSuccessListener(cb::onLocation)
                                .addOnFailureListener(e -> cb.onLocation(null));
                    }
                })
                .addOnFailureListener(e -> cb.onLocation(null));
    }
}
