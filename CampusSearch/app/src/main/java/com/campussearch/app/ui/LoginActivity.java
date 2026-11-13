package com.campussearch.app.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.campussearch.app.data.DatabaseHelper;
import com.campussearch.app.data.SessionManager;
import com.campussearch.app.data.User;
import com.campussearch.app.databinding.ActivityLoginBinding;
import com.campussearch.app.util.Validators;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding b;
    private SessionManager session;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        if (session.isLoggedIn()) {          // session survives app restart
            goHome();
            return;
        }
        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        db = DatabaseHelper.getInstance(this);

        b.btnLogin.setOnClickListener(v -> attemptLogin());
        b.tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        b.tilEmail.setError(null);
        b.tilPassword.setError(null);

        String email = b.etEmail.getText().toString().trim();
        String pass = b.etPassword.getText().toString();

        boolean ok = true;
        if (email.isEmpty()) { b.tilEmail.setError("Enter your email"); ok = false; }
        else if (Validators.email(email) != null) { b.tilEmail.setError(Validators.email(email)); ok = false; }
        if (pass.isEmpty()) { b.tilPassword.setError("Enter your password"); ok = false; }
        if (!ok) return;

        User user = db.login(email, pass);
        if (user == null) {
            Snackbar.make(b.getRoot(), "Invalid email or password", Snackbar.LENGTH_LONG).show();
            return;
        }
        session.login(user.name, user.email);
        goHome();
    }

    private void goHome() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
