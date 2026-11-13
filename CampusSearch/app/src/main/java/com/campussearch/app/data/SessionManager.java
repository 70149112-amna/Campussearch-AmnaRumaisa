package com.campussearch.app.data;

import android.content.Context;
import android.content.SharedPreferences;

/** Keeps the user logged in (even after app restart) until Logout. */
public class SessionManager {
    private static final String PREFS = "campus_search_session";
    private final SharedPreferences prefs;

    public SessionManager(Context ctx) {
        prefs = ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void login(String name, String email) {
        prefs.edit().putBoolean("logged_in", true)
                .putString("name", name).putString("email", email).apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() { return prefs.getBoolean("logged_in", false); }
    public String getName() { return prefs.getString("name", ""); }
    public String getEmail() { return prefs.getString("email", ""); }
}
