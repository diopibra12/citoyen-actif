package ca.uqtr.citoyenactif.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.function.Consumer;

import ca.uqtr.citoyenactif.RecyApp;
import ca.uqtr.citoyenactif.databinding.SignUpBinding;
import ca.uqtr.citoyenactif.db.entity.CitoyenEntity;
import ca.uqtr.citoyenactif.fragment.model.AuthenticationViewModel;


public class SignIn extends Fragment {
    private SignUpBinding binding;
    private AuthenticationViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SignUpBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.agentIndicator.setOnClickListener(v -> {
            if (binding.agentIndicator.isChecked()) {
                binding.numeroAgent.setVisibility(View.VISIBLE);
                binding.emailEditText.setVisibility(View.GONE);
            } else {
                binding.numeroAgent.setVisibility(View.GONE);
                binding.emailEditText.setVisibility(View.VISIBLE);
            }
        });

        binding.btnConexion.setOnClickListener(v -> {

            final String userAgentId = binding.numeroAgent.getText().toString();
            final String userEmail = binding.emailEditText.getText().toString();
            final String userPassword = binding.password.getText().toString();
            final boolean isAgent = binding.agentIndicator.isChecked();

            if (TextUtils.isEmpty(userPassword) || (!isAgent && TextUtils.isEmpty(userEmail)) || (isAgent && TextUtils.isEmpty(userAgentId))) {
                Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            Consumer<CitoyenEntity> callback = (citoyen) -> {


                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", task.getResult());
                        this.viewModel.updateFCMToken(citoyen, task.getResult());
                    }
                });

                // Sauvegarder le userId connecté pour persister entre les activités
                if(citoyen != null){
                    RecyApp app = (RecyApp) requireActivity().getApplicationContext();
                    app.setCurrentCitoyen(citoyen);
                    Toast.makeText(getActivity(), "Connexion réussie", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), ca.uqtr.citoyenactif.Citoyen.class));
                } else {
                    Toast.makeText(getActivity(), "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }

            };

            if (binding.agentIndicator.isChecked()) {
                this.viewModel.loginAgent(userAgentId, userPassword, callback);
            } else {
                this.viewModel.login(userEmail, userPassword, callback);
            }

        });

    }


}