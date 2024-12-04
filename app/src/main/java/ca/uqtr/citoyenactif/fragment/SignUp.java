package ca.uqtr.citoyenactif.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.uqtr.citoyenactif.Authentication;
import ca.uqtr.citoyenactif.databinding.SignInBinding;
import ca.uqtr.citoyenactif.fragment.model.AuthenticationViewModel;

public class SignUp extends Fragment {
    private SignInBinding binding;
    private AuthenticationViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SignInBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.agentIndicator.setOnClickListener(v -> {
            if (binding.agentIndicator.isChecked()) {
                binding.numeroAgent.setVisibility(View.VISIBLE);
                binding.ville.setVisibility(View.VISIBLE);

                binding.emailEditText.setVisibility(View.GONE);
                binding.homeAddressEditText.setVisibility(View.GONE);
                binding.phoneNumberEditText.setVisibility(View.GONE);
            } else {
                binding.numeroAgent.setVisibility(View.GONE);
                binding.ville.setVisibility(View.GONE);

                binding.emailEditText.setVisibility(View.VISIBLE);
                binding.homeAddressEditText.setVisibility(View.VISIBLE);
                binding.phoneNumberEditText.setVisibility(View.VISIBLE);
            }
        });

        // Enregistrer les coordonnées du citoyen dans la base de données
        binding.btnInscrire.setOnClickListener(v -> {
            viewModel.register(
                    binding.firstName.getText().toString(),
                    binding.lastName.getText().toString(),
                    binding.homeAddressEditText.getText().toString(),
                    binding.emailEditText.getText().toString(),
                    binding.password.getText().toString(),
                    binding.phoneNumberEditText.getText().toString(),
                    binding.agentIndicator.isChecked(),
                    binding.ville.getText().toString(),
                    binding.numeroAgent.getText().toString()
            );

            startActivity(new Intent(getActivity(), Authentication.class));
            getActivity().finish();
        });


    }
}