package com.campussearch.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.campussearch.app.R;
import com.campussearch.app.data.Item;
import com.campussearch.app.databinding.ItemRowBinding;
import com.campussearch.app.util.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {

    public interface OnItemClick { void onClick(Item item); }

    private final List<Item> items = new ArrayList<>();
    private final boolean showDistance;
    private final OnItemClick listener;

    public ItemAdapter(boolean showDistance, OnItemClick listener) {
        this.showDistance = showDistance;
        this.listener = listener;
    }

    public void setData(List<Item> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Item item = items.get(position);
        ItemRowBinding r = h.b;
        r.tvTitle.setText(item.title);
        r.tvType.setText(item.type.toUpperCase());
        r.tvType.setBackgroundResource("Lost".equals(item.type) ? R.drawable.bg_tag_lost : R.drawable.bg_tag_found);
        r.tvCategory.setText(item.category);
        r.tvPlace.setText("\uD83D\uDCCD " + item.place);
        r.tvDate.setText(item.getDisplayDate());
        r.tvReturned.setVisibility(item.isReturned() ? View.VISIBLE : View.GONE);

        if (showDistance && item.distanceMeters >= 0) {
            r.tvDistance.setVisibility(View.VISIBLE);
            r.tvDistance.setText(DistanceUtil.format(item.distanceMeters) + " away");
        } else {
            r.tvDistance.setVisibility(View.GONE);
        }
        r.getRoot().setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ItemRowBinding b;
        VH(ItemRowBinding b) { super(b.getRoot()); this.b = b; }
    }
}
