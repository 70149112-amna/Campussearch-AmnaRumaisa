package com.campussearch.app.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item {
    public int itemNo;          // 1 -> CF-01
    public String type;         // Lost / Found
    public String title;
    public String category;
    public String place;
    public String date;         // stored as yyyy-MM-dd (sortable)
    public double lat;
    public double lng;
    public String reportedBy;
    public String reportedByEmail;
    public String contact;
    public String status;       // Open / Returned
    public float distanceMeters = -1f; // used only by Nearby tab

    public String getDisplayId() {
        return String.format(Locale.US, "CF-%02d", itemNo);
    }

    public String getDisplayDate() {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
            return new SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(d);
        } catch (Exception e) {
            return date;
        }
    }

    public boolean isReturned() {
        return "Returned".equals(status);
    }
}
