package com.campussearch.app.util;

import android.util.Patterns;

/** Each method returns an error message, or null when the value is valid. */
public final class Validators {
    private Validators() { }

    public static String name(String v) {
        v = v.trim();
        if (v.isEmpty()) return "Full name is required";
        if (!v.matches("[A-Za-z ]+")) return "Use letters and spaces only";
        if (v.length() < 3) return "Name must be at least 3 characters";
        return null;
    }

    public static String email(String v) {
        v = v.trim();
        if (v.isEmpty()) return "Email is required";
        if (!Patterns.EMAIL_ADDRESS.matcher(v).matches()) return "Enter a valid email address";
        return null;
    }

    public static String password(String v) {
        if (v.isEmpty()) return "Password is required";
        if (v.length() < 6) return "Password must be at least 6 characters";
        if (!v.matches(".*\\d.*")) return "Password must include at least one digit";
        return null;
    }

    public static String confirm(String pass, String confirm) {
        if (confirm.isEmpty()) return "Please confirm your password";
        if (!pass.equals(confirm)) return "Passwords do not match";
        return null;
    }

    public static String title(String v) {
        return v.trim().length() < 3 ? "Title must be at least 3 characters" : null;
    }

    public static String place(String v) {
        return v.trim().isEmpty() ? "Place is required" : null;
    }

    public static String contact(String v) {
        v = v.trim();
        if (v.isEmpty()) return "Contact number is required";
        if (!v.matches("03\\d{9}")) return "Enter 11 digits starting with 03 (e.g. 03001234567)";
        return null;
    }
}
