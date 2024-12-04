package ca.uqtr.citoyenactif.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.uqtr.citoyenactif.Authentication;
import ca.uqtr.citoyenactif.databinding.ResetBinding;
import ca.uqtr.citoyenactif.fragment.model.AuthenticationViewModel;

public class ResetPassword extends Fragment {
    private ResetBinding binding;
    private AuthenticationViewModel viewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        binding = ResetBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnReset.setOnClickListener(v -> {
            final String userEmail = binding.mail.getText().toString();
            final String userPassword = binding.newpass.getText().toString();
            if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {

                Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                this.viewModel.resetPassword(userEmail, userPassword, row -> {
                    if (row > 0) {
                        Toast.makeText(getActivity(), "Mot de passe mis à jour avec succès", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), Authentication.class));
                    } else {
                        Toast.makeText(getActivity(), "Aucun compte trouvé avec cet email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
