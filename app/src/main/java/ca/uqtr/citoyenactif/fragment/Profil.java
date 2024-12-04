package ca.uqtr.citoyenactif.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ca.uqtr.citoyenactif.Authentication;
import ca.uqtr.citoyenactif.RecyApp;
import ca.uqtr.citoyenactif.databinding.FragmentProfilBinding;
import ca.uqtr.citoyenactif.db.repository.CitoyenRepository;


public class Profil extends Fragment {
    private FragmentProfilBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyApp app = (RecyApp) requireActivity().getApplicationContext();

        binding.firstName.setText(app.getCurrentCitoyen().getPrenom());
        binding.lastName.setText(app.getCurrentCitoyen().getNom());

        if (app.getCurrentCitoyen().isAgent()) {
            binding.numeroAgent.setText(app.getCurrentCitoyen().getNumeroAgent());

            binding.phoneNumber.setVisibility(View.GONE);
            binding.numeroAgent.setVisibility(View.VISIBLE);
        } else {
            binding.phoneNumber.setText(app.getCurrentCitoyen().getNumeroTel());

            binding.phoneNumber.setVisibility(View.VISIBLE);
            binding.numeroAgent.setVisibility(View.GONE);
        }

        binding.disconnectButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), Authentication.class)));
    }

}