package com.campussearch.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.campussearch.app.R;
import com.campussearch.app.data.SessionManager;
import com.campussearch.app.databinding.FragmentProfileBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentProfileBinding.inflate(inflater, container, false);
        SessionManager session = new SessionManager(requireContext());

        b.tvName.setText(session.getName());
        b.tvEmail.setText(session.getEmail());
        b.tvTeam.setText(getString(R.string.team_members));
        b.tvGithub.setText(getString(R.string.github_link));
        b.tvGithub.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_link))));
            } catch (Exception ignored) { }
        });

        b.btnLogout.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Logout")
                        .setMessage("Do you want to log out?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Logout", (d, w) -> {
                            session.logout();
                            Intent i = new Intent(requireContext(), LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            requireActivity().finish();
                        }).show());
        return b.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
