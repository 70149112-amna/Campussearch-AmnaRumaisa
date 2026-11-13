package com.campussearch.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Local SQLite database: users (hashed passwords) + items (with 6 starter items). */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "campus_search.db";
    private static final int DB_VERSION = 1;
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) instance = new DatabaseHelper(ctx.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "salt TEXT NOT NULL," +
                "password_hash TEXT NOT NULL)");

        db.execSQL("CREATE TABLE items (" +
                "item_no INTEGER PRIMARY KEY," +
                "type TEXT NOT NULL," +
                "title TEXT NOT NULL," +
                "category TEXT NOT NULL," +
                "place TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "lat REAL NOT NULL," +
                "lng REAL NOT NULL," +
                "reported_by TEXT NOT NULL," +
                "reported_by_email TEXT NOT NULL," +
                "contact TEXT," +
                "status TEXT NOT NULL)");

        seed(db, 1, "Found", "Black leather wallet", "Wallets & Cards", "CS Block", "2026-11-12", 31.38955, 74.24070);
        seed(db, 2, "Lost", "Casio fx-991ES calculator", "Electronics", "Main Library", "2026-11-11", 31.38800, 74.24200);
        seed(db, 3, "Found", "Student ID card", "Wallets & Cards", "Cafeteria", "2026-11-10", 31.38990, 74.24120);
        seed(db, 4, "Lost", "Blue water bottle", "Other", "Sports Ground", "2026-11-09", 31.39050, 74.23950);
        seed(db, 5, "Found", "Car keys, red keychain", "Keys", "Main Parking", "2026-11-08", 31.38880, 74.24010);
        seed(db, 6, "Lost", "32 GB USB flash drive", "Electronics", "Mosque", "2026-11-07", 31.38920, 74.24180);
    }

    private void seed(SQLiteDatabase db, int no, String type, String title, String cat,
                      String place, String date, double lat, double lng) {
        ContentValues v = new ContentValues();
        v.put("item_no", no);
        v.put("type", type);
        v.put("title", title);
        v.put("category", cat);
        v.put("place", place);
        v.put("date", date);
        v.put("lat", lat);
        v.put("lng", lng);
        v.put("reported_by", "Student Affairs");
        v.put("reported_by_email", "");
        v.put("contact", "");
        v.put("status", "Open");
        db.insert("items", null, v);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS items");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // ---------------- USERS ----------------

    public boolean emailExists(String email) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT 1 FROM users WHERE email = ?", new String[]{email.trim().toLowerCase(Locale.ROOT)});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    /** @return false if the email is already registered. */
    public boolean registerUser(String name, String email, String password) {
        if (emailExists(email)) return false;
        String salt = PasswordUtil.newSalt();
        ContentValues v = new ContentValues();
        v.put("name", name.trim());
        v.put("email", email.trim().toLowerCase(Locale.ROOT));
        v.put("salt", salt);
        v.put("password_hash", PasswordUtil.hash(password, salt));
        return getWritableDatabase().insert("users", null, v) != -1;
    }

    /** @return the user, or null when email/password is wrong. */
    public User login(String email, String password) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT name, email, salt, password_hash FROM users WHERE email = ?",
                new String[]{email.trim().toLowerCase(Locale.ROOT)});
        User user = null;
        if (c.moveToFirst()) {
            if (PasswordUtil.verify(password, c.getString(2), c.getString(3))) {
                user = new User(c.getString(0), c.getString(1));
            }
        }
        c.close();
        return user;
    }

    // ---------------- ITEMS ----------------

    private Item fromCursor(Cursor c) {
        Item i = new Item();
        i.itemNo = c.getInt(c.getColumnIndexOrThrow("item_no"));
        i.type = c.getString(c.getColumnIndexOrThrow("type"));
        i.title = c.getString(c.getColumnIndexOrThrow("title"));
        i.category = c.getString(c.getColumnIndexOrThrow("category"));
        i.place = c.getString(c.getColumnIndexOrThrow("place"));
        i.date = c.getString(c.getColumnIndexOrThrow("date"));
        i.lat = c.getDouble(c.getColumnIndexOrThrow("lat"));
        i.lng = c.getDouble(c.getColumnIndexOrThrow("lng"));
        i.reportedBy = c.getString(c.getColumnIndexOrThrow("reported_by"));
        i.reportedByEmail = c.getString(c.getColumnIndexOrThrow("reported_by_email"));
        i.contact = c.getString(c.getColumnIndexOrThrow("contact"));
        i.status = c.getString(c.getColumnIndexOrThrow("status"));
        return i;
    }

    /** R6: newest date first, same date -> higher ID first. */
    public List<Item> getAllItems() {
        List<Item> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM items ORDER BY date DESC, item_no DESC", null);
        while (c.moveToNext()) list.add(fromCursor(c));
        c.close();
        return list;
    }

    public Item getItem(int itemNo) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM items WHERE item_no = ?", new String[]{String.valueOf(itemNo)});
        Item item = c.moveToFirst() ? fromCursor(c) : null;
        c.close();
        return item;
    }

    /** R5: next ID, today's date, status Open, reported by logged-in user. */
    public int insertItem(String type, String title, String category, String place,
                          String contact, double lat, double lng, String date,
                          String reportedBy, String reportedByEmail) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT IFNULL(MAX(item_no), 0) + 1 FROM items", null);
        c.moveToFirst();
        int next = c.getInt(0);
        c.close();

        ContentValues v = new ContentValues();
        v.put("item_no", next);
        v.put("type", type);
        v.put("title", title.trim());
        v.put("category", category);
        v.put("place", place.trim());
        v.put("date", date);
        v.put("lat", lat);
        v.put("lng", lng);
        v.put("reported_by", reportedBy);
        v.put("reported_by_email", reportedByEmail);
        v.put("contact", contact);
        v.put("status", "Open");
        db.insert("items", null, v);
        return next;
    }

    public void markReturned(int itemNo) {
        ContentValues v = new ContentValues();
        v.put("status", "Returned");
        getWritableDatabase().update("items", v, "item_no = ?", new String[]{String.valueOf(itemNo)});
    }
}
