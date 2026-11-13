package com.campussearch.app.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.campussearch.app.data.DatabaseHelper;
import com.campussearch.app.data.SessionManager;
import com.campussearch.app.databinding.ActivityRegisterBinding;
import com.campussearch.app.util.Validators;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding b;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        db = DatabaseHelper.getInstance(this);

        b.toolbar.setNavigationOnClickListener(v -> finish());
        b.tvGoLogin.setOnClickListener(v -> finish());
        b.btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String name = b.etName.getText().toString();
        String email = b.etEmail.getText().toString();
        String pass = b.etPassword.getText().toString();
        String confirm = b.etConfirm.getText().toString();

        String eName = Validators.name(name);
        String eEmail = Validators.email(email);
        String ePass = Validators.password(pass);
        String eConfirm = Validators.confirm(pass, confirm);

        b.tilName.setError(eName);
        b.tilEmail.setError(eEmail);
        b.tilPassword.setError(ePass);
        b.tilConfirm.setError(eConfirm);
        if (eName != null || eEmail != null || ePass != null || eConfirm != null) return;

        if (!db.registerUser(name, email, pass)) {
            b.tilEmail.setError("An account with this email already exists");
            Snackbar.make(b.getRoot(), "An account with this email already exists",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Auto-login after successful sign up
        new SessionManager(this).login(name.trim(), email.trim().toLowerCase());
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
