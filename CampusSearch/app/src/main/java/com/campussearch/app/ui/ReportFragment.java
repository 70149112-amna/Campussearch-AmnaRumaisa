package com.campussearch.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.campussearch.app.R;
import com.campussearch.app.data.DatabaseHelper;
import com.campussearch.app.data.SessionManager;
import com.campussearch.app.databinding.FragmentReportBinding;
import com.campussearch.app.util.LocationHelper;
import com.campussearch.app.util.Validators;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private FragmentReportBinding b;
    private ActivityResultLauncher<String[]> permLauncher;
    private Double lat = null, lng = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    if (b == null) return;
                    if (LocationHelper.hasPermission(requireContext())) {
                        fetchLocation();
                    } else {
                        showLocationError("Location permission is required to tag this item");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentReportBinding.inflate(inflater, container, false);

        String[] cats = getResources().getStringArray(R.array.categories);
        b.actCategory.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, cats));

        b.btnLocation.setOnClickListener(v -> {
            if (LocationHelper.hasPermission(requireContext())) fetchLocation();
            else permLauncher.launch(LocationHelper.PERMISSIONS);
        });
        b.btnSubmit.setOnClickListener(v -> submit());
        return b.getRoot();
    }

    private void fetchLocation() {
        b.btnLocation.setEnabled(false);
        b.tvCoords.setTextColor(requireContext().getColor(R.color.grey_text));
        b.tvCoords.setText("Getting your location…");
        LocationHelper.getCurrent(requireContext(), loc -> {
            if (b == null) return;
            b.btnLocation.setEnabled(true);
            if (loc == null) {
                showLocationError("Could not get location. Turn on GPS and try again.");
                return;
            }
            lat = loc.getLatitude();
            lng = loc.getLongitude();
            b.tvCoords.setTextColor(requireContext().getColor(R.color.green_dark));
            b.tvCoords.setText(String.format(Locale.US,
                    "Latitude: %.5f\nLongitude: %.5f", lat, lng));
        });
    }

    private void showLocationError(String msg) {
        lat = null;
        lng = null;
        b.btnLocation.setEnabled(true);
        b.tvCoords.setTextColor(requireContext().getColor(R.color.error_red));
        b.tvCoords.setText(msg);
        Snackbar.make(b.getRoot(), msg, Snackbar.LENGTH_LONG).show();
    }

    private void submit() {
        String title = b.etTitle.getText().toString();
        String category = b.actCategory.getText().toString().trim();
        String place = b.etPlace.getText().toString();
        String contact = b.etContact.getText().toString();

        String eTitle = Validators.title(title);
        String eCat = category.isEmpty() ? "Please select a category" : null;
        String ePlace = Validators.place(place);
        String eContact = Validators.contact(contact);
        b.tilTitle.setError(eTitle);
        b.tilCategory.setError(eCat);
        b.tilPlace.setError(ePlace);
        b.tilContact.setError(eContact);
        boolean ok = eTitle == null && eCat == null && ePlace == null && eContact == null;

        if (lat == null || lng == null) {
            b.tvCoords.setTextColor(requireContext().getColor(R.color.error_red));
            b.tvCoords.setText("Tap \"Use my current location\" to tag this item");
            ok = false;
        }
        if (!ok) return;

        String type = b.toggleType.getCheckedButtonId() == R.id.btnFound ? "Found" : "Lost";
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        SessionManager session = new SessionManager(requireContext());

        int no = DatabaseHelper.getInstance(requireContext()).insertItem(
                type, title, category, place, contact.trim(), lat, lng, today,
                session.getName(), session.getEmail());

        // reset form
        b.etTitle.setText("");
        b.actCategory.setText("", false);
        b.etPlace.setText("");
        b.etContact.setText("");
        b.tvCoords.setText("");
        lat = null;
        lng = null;

        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                String.format(Locale.US, "Item CF-%02d saved", no), Snackbar.LENGTH_LONG).show();
        ((MainActivity) requireActivity()).openTab(R.id.nav_home);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
