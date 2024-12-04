package ca.uqtr.citoyenactif.fragment.model;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.function.Consumer;

import ca.uqtr.citoyenactif.db.entity.CitoyenEntity;
import ca.uqtr.citoyenactif.db.repository.CitoyenRepository;
import ca.uqtr.citoyenactif.service.SyncService;

public class AuthenticationViewModel extends AndroidViewModel {

    private CitoyenRepository citoyenRepository;
    private SyncService syncService;
    protected ServiceConnection starter = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (service instanceof SyncService.LocalBinder) {
                SyncService.LocalBinder binder = (SyncService.LocalBinder) service;
                syncService = binder.getService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public AuthenticationViewModel(@NonNull Application application) {
        super(application);

        application.bindService(new Intent(application, SyncService.class), starter, Context.BIND_AUTO_CREATE);
        this.citoyenRepository = new CitoyenRepository(application);
    }

    public void register(String firstname, String lastname, String address, String email, String password, String phone, boolean agent, String ville, String numeroAgent) {
        CitoyenEntity citoyen = new CitoyenEntity();

        citoyen.setPrenom(firstname);
        citoyen.setNom(lastname);
        citoyen.setAdresse(address);
        citoyen.setCourriel(email);
        citoyen.setMotDePasse(password);
        citoyen.setNumeroTel(phone);
        citoyen.setAgent(agent);
        citoyen.setVille(ville);
        citoyen.setNumeroAgent(numeroAgent);

        this.citoyenRepository.insertCitoyen(citoyen);
        this.syncService.syncUsers();
        this.syncService.registerUser();
    }

    public void login(String email, String password, Consumer<CitoyenEntity> callback) {
        this.citoyenRepository.findByEmailPassword(email, password, callback);
    }

    public void loginAgent(String agentId, String password, Consumer<CitoyenEntity> callback) {
        this.citoyenRepository.findByAgentIdPassword(agentId, password, callback);
    }

    public void resetPassword(String email, String password, Consumer<Integer> callback) {
        this.citoyenRepository.updatePassword(email, password, callback);
        this.syncService.syncUsers();
    }

    public void updateFCMToken(CitoyenEntity entity, String token) {
        this.citoyenRepository.updateToken(entity, token);
        this.syncService.syncUsers();
    }

}
