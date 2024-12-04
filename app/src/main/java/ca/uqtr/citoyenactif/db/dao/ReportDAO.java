package ca.uqtr.citoyenactif.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDateTime;
import java.util.List;

import ca.uqtr.citoyenactif.db.entity.ReportEntity;

@Dao
public interface ReportDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ReportEntity report);

    @Update
    void update(ReportEntity... reports);

    @Query("SELECT * FROM ReportEntity WHERE citoyen_id = :citoyenId")
    List<ReportEntity> getReports(long citoyenId);

    @Query("SELECT * FROM ReportEntity")
    List<ReportEntity> getAll();

    @Delete
    void delete(ReportEntity report);

    @Query("UPDATE ReportEntity SET updated = :updated, needUpdate = :needsUpdate")
    void setUpdated(LocalDateTime updated, boolean needsUpdate);

    @Query("DELETE FROM ReportEntity")
    void truncate();

    @Query("SELECT * FROM ReportEntity WHERE id = :reportId")
    ReportEntity byId(long reportId);
}
