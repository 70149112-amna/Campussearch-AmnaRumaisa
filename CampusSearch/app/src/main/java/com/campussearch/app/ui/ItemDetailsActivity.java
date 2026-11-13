package com.campussearch.app.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.campussearch.app.R;
import com.campussearch.app.data.DatabaseHelper;
import com.campussearch.app.data.Item;
import com.campussearch.app.data.SessionManager;
import com.campussearch.app.databinding.ActivityItemDetailsBinding;
import com.campussearch.app.databinding.ItemDetailRowBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class ItemDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_NO = "item_no";

    private ActivityItemDetailsBinding b;
    private DatabaseHelper db;
    private SessionManager session;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityItemDetailsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        item = db.getItem(getIntent().getIntExtra(EXTRA_ITEM_NO, -1));
        if (item == null) { finish(); return; }

        b.toolbar.setNavigationOnClickListener(v -> finish());
        b.btnMaps.setOnClickListener(v -> openInMaps());
        b.btnReturned.setOnClickListener(v -> {
            db.markReturned(item.itemNo);
            item = db.getItem(item.itemNo);
            bind();
            Snackbar.make(b.getRoot(), "Marked as returned", Snackbar.LENGTH_LONG).show();
        });
        bind();
    }

    private void bind() {
        b.toolbar.setTitle(item.getDisplayId());
        b.tvTitle.setText(item.title);
        b.tvType.setText(item.type.toUpperCase());
        b.tvType.setBackgroundResource("Lost".equals(item.type) ? R.drawable.bg_tag_lost : R.drawable.bg_tag_found);
        b.tvReturned.setVisibility(item.isReturned() ? View.VISIBLE : View.GONE);

        b.detailsContainer.removeAllViews();
        addRow("ID", item.getDisplayId());
        addRow("Type", item.type);
        addRow("Title", item.title);
        addRow("Category", item.category);
        addRow("Place", item.place);
        addRow("Date", item.getDisplayDate());
        addRow("Coordinates", String.format(Locale.US, "%.5f, %.5f", item.lat, item.lng));
        addRow("Reported by", item.reportedBy);
        if (item.contact != null && !item.contact.isEmpty()) addRow("Contact", item.contact);
        addRow("Status", item.status);

        // R8: only the reporter sees "Mark as Returned"
        boolean mine = !item.reportedByEmail.isEmpty()
                && item.reportedByEmail.equalsIgnoreCase(session.getEmail());
        b.btnReturned.setVisibility(mine && !item.isReturned() ? View.VISIBLE : View.GONE);
    }

    private void addRow(String label, String value) {
        ItemDetailRowBinding row = ItemDetailRowBinding.inflate(
                LayoutInflater.from(this), b.detailsContainer, false);
        row.tvLabel.setText(label);
        row.tvValue.setText(value);
        b.detailsContainer.addView(row.getRoot());
    }

    private void openInMaps() {
        String coords = String.format(Locale.US, "%.5f,%.5f", item.lat, item.lng);
        Uri geo = Uri.parse("geo:0,0?q=" + coords + "(" + Uri.encode(item.title) + ")");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, geo));
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=" + coords)));
            } catch (ActivityNotFoundException e2) {
                Snackbar.make(b.getRoot(), "No maps app found", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
