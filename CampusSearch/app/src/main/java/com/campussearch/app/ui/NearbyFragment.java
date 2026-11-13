package com.campussearch.app.ui;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.campussearch.app.data.DatabaseHelper;
import com.campussearch.app.data.Item;
import com.campussearch.app.databinding.FragmentNearbyBinding;
import com.campussearch.app.util.LocationHelper;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Intent;

public class NearbyFragment extends Fragment {

    private FragmentNearbyBinding b;
    private ItemAdapter adapter;
    private ActivityResultLauncher<String[]> permLauncher;
    private boolean askedOnce = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    if (b == null) return;
                    if (LocationHelper.hasPermission(requireContext())) loadNearby();
                    else showMessage("Location permission is required to see nearby items");
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentNearbyBinding.inflate(inflater, container, false);
        adapter = new ItemAdapter(true, item -> {
            Intent i = new Intent(requireContext(), ItemDetailsActivity.class);
            i.putExtra(ItemDetailsActivity.EXTRA_ITEM_NO, item.itemNo);
            startActivity(i);
        });
        b.rvNearby.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvNearby.setAdapter(adapter);
        b.btnRefresh.setOnClickListener(v -> refresh(true));
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(false);
    }

    private void refresh(boolean force) {
        if (LocationHelper.hasPermission(requireContext())) {
            loadNearby();
        } else if (force || !askedOnce) {
            askedOnce = true;
            permLauncher.launch(LocationHelper.PERMISSIONS);
        } else {
            showMessage("Location permission is required to see nearby items");
        }
    }

    private void showMessage(String msg) {
        b.tvMessage.setText(msg);
        b.tvMessage.setVisibility(View.VISIBLE);
        adapter.setData(Collections.emptyList());
        b.tvLocation.setText("");
    }

    private void loadNearby() {
        b.tvMessage.setText("Getting your location…");
        b.tvMessage.setVisibility(View.VISIBLE);
        LocationHelper.getCurrent(requireContext(), loc -> {
            if (b == null) return;
            if (loc == null) {
                showMessage("Could not get your location. Turn on GPS and try again.");
                return;
            }
            b.tvLocation.setText(String.format(Locale.US, "Your location: %.5f, %.5f",
                    loc.getLatitude(), loc.getLongitude()));
            List<Item> items = DatabaseHelper.getInstance(requireContext()).getAllItems();
            float[] r = new float[1];
            for (Item i : items) {
                Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), i.lat, i.lng, r);
                i.distanceMeters = r[0];
            }
            Collections.sort(items, (x, y) -> Float.compare(x.distanceMeters, y.distanceMeters));
            adapter.setData(items);
            b.tvMessage.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
