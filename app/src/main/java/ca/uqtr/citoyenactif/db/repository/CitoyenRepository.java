package ca.uqtr.citoyenactif.db.repository;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import ca.uqtr.citoyenactif.api.dto.CitoyenDTO;
import ca.uqtr.citoyenactif.db.entity.CitoyenEntity;
import ca.uqtr.citoyenactif.db.CitoyenActifDatabase;
import ca.uqtr.citoyenactif.db.dao.CitoyenDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

public class CitoyenRepository {
    private final CitoyenDAO dao;
    private final CitoyenActifDatabase db;

    public CitoyenRepository(Context context) {
        db = CitoyenActifDatabase.instance(context);
        this.dao = db.citoyenDAO();
    }

    public void getCitoyen(long citoyenId, Consumer<CitoyenEntity> consumer) {
        db.read(() -> {
            CitoyenEntity entity = dao.findCitoyenById(citoyenId);
            new Handler(Looper.getMainLooper()).post(() -> consumer.accept(entity));
        });
    }

    public void getAll(Consumer<List<CitoyenEntity>> consumer) {
        db.read(() -> {
            List<CitoyenEntity> entity = dao.getAll();
            new Handler(Looper.getMainLooper()).post(() -> consumer.accept(entity));
        });
    }

    public void insertCitoyen(CitoyenEntity... citoyen) {
        db.write(() -> dao.insert(citoyen));
    }

    public void updatePassword(String userEmail, String nouveau, Consumer<Integer> callback) {
        db.write(() -> {
            int count = dao.updatePassword(userEmail, nouveau);

            dao.setUpdated(LocalDateTime.now(), true, userEmail);

            new Handler(Looper.getMainLooper()).post(() -> callback.accept(count));
        });
    }

    public void findByEmailPassword(String email, String password, Consumer<CitoyenEntity> callback) {
        db.read(() -> {
            CitoyenEntity entity = dao.findCitoyenByEmailAndPassword(email, password);
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.accept(entity);
            });
        });
    }

    public void findByAgentIdPassword(String agentId, String password, Consumer<CitoyenEntity> callback) {
        db.read(() -> {
            CitoyenEntity entity = dao.findCitoyenByAgentIdAndPassword(agentId, password);
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.accept(entity);
            });
        });
    }

    public void confirmSync() {
        db.write(() -> dao.setUpdated(LocalDateTime.now(), true));
    }

    public void updateToken(CitoyenEntity entity, String token) {
        db.write(() -> dao.setToken(entity.getId(), token));
    }

    public void updateCitoyens(List<CitoyenDTO> citoyens) {
        db.write(() -> {
            dao.truncate();

            for (CitoyenDTO citoyen : citoyens) {
                CitoyenEntity entity = new CitoyenEntity();

                entity.setId(citoyen.getId());
                entity.setPrenom(citoyen.getPrenom());
                entity.setNom(citoyen.getNom());
                entity.setAdresse(citoyen.getAdresse());
                entity.setCourriel(citoyen.getCourriel());
                entity.setMotDePasse(citoyen.getMotDePasse());
                entity.setNumeroTel(citoyen.getNumeroTel());
                entity.setAgent(citoyen.isAgent());
                entity.setFcmToken(citoyen.getFcmToken());
                entity.setVille(citoyen.getVille());
                entity.setNumeroAgent(citoyen.getNumeroAgent());

                dao.insert(entity);
            }
        });
    }
}
