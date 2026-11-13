package com.campussearch.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.campussearch.app.R;
import com.campussearch.app.data.DatabaseHelper;
import com.campussearch.app.data.Item;
import com.campussearch.app.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding b;
    private ItemAdapter adapter;
    private List<Item> all = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(inflater, container, false);

        adapter = new ItemAdapter(false, item -> {
            Intent i = new Intent(requireContext(), ItemDetailsActivity.class);
            i.putExtra(ItemDetailsActivity.EXTRA_ITEM_NO, item.itemNo);
            startActivity(i);
        });
        b.rvItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.rvItems.setAdapter(adapter);

        b.chipGroup.setOnCheckedStateChangeListener((group, ids) -> applyFilter());
        b.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) { }
            @Override public void onTextChanged(CharSequence s, int a, int c, int d) { }
            @Override public void afterTextChanged(Editable s) { applyFilter(); }
        });
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        all = DatabaseHelper.getInstance(requireContext()).getAllItems();
        applyFilter();
    }

    private void applyFilter() {
        if (b == null) return;
        int checked = b.chipGroup.getCheckedChipId();
        String typeFilter = checked == R.id.chipLost ? "Lost" : checked == R.id.chipFound ? "Found" : null;
        String q = b.etSearch.getText() == null ? "" :
                b.etSearch.getText().toString().trim().toLowerCase(Locale.ROOT);

        List<Item> out = new ArrayList<>();
        for (Item i : all) {
            if (typeFilter != null && !typeFilter.equals(i.type)) continue;
            if (!q.isEmpty() && !(i.title.toLowerCase(Locale.ROOT).contains(q)
                    || i.place.toLowerCase(Locale.ROOT).contains(q)
                    || i.category.toLowerCase(Locale.ROOT).contains(q))) continue;
            out.add(i);
        }
        adapter.setData(out);
        b.tvEmpty.setVisibility(out.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
