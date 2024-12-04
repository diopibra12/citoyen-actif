package ca.uqtr.citoyenactif.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.time.LocalDateTime;
import java.util.List;

import ca.uqtr.citoyenactif.db.entity.CitoyenEntity;

@Dao
public interface CitoyenDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CitoyenEntity... citoyens);

    @Query("SELECT * FROM CitoyenEntity WHERE courriel = :email AND motDePasse = :password AND agent = 0")
    CitoyenEntity findCitoyenByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM CitoyenEntity WHERE id = :id")
    CitoyenEntity findCitoyenById(long id);

    @Query("SELECT * FROM CitoyenEntity")
    List<CitoyenEntity> getAll();

    @Query("UPDATE CitoyenEntity SET motDePasse = :newPassword WHERE courriel = :email")
    int updatePassword(String email, String newPassword);

    @Query("UPDATE CitoyenEntity SET updated = :updated, needUpdate = :needsUpdate WHERE courriel = :email")
    void setUpdated(LocalDateTime updated, boolean needsUpdate, String email);

    @Query("UPDATE CitoyenEntity SET updated = :updated, needUpdate = :needsUpdate")
    void setUpdated(LocalDateTime updated, boolean needsUpdate);

    @Query("UPDATE CitoyenEntity SET fcm_token = :token WHERE id = :id")
    void setToken(long id, String token);

    @Query("DELETE FROM CitoyenEntity")
    void truncate();

    @Query("SELECT * FROM CitoyenEntity WHERE numeroAgent = :agentId AND motDePasse = :password AND agent = 1")
    CitoyenEntity findCitoyenByAgentIdAndPassword(String agentId, String password);
}