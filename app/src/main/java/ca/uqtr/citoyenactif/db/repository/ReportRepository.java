package ca.uqtr.citoyenactif.db.repository;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import ca.uqtr.citoyenactif.api.dto.ReportDTO;
import ca.uqtr.citoyenactif.db.CitoyenActifDatabase;
import ca.uqtr.citoyenactif.db.dao.ReportDAO;
import ca.uqtr.citoyenactif.db.entity.ReportEntity;

public class ReportRepository {
    private final ReportDAO dao;
    private final CitoyenActifDatabase db;

    public ReportRepository(Context context) {
        db = CitoyenActifDatabase.instance(context);
        this.dao = db.reportDAO();
    }

    public void insert(ReportEntity report, Consumer<Long> callback) {
        report.setUpdated(LocalDateTime.now());
        report.setNeedUpdate(true);

        db.write(() -> {
            long id = dao.insert(report);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(id));
        });
    }

    public void delete(ReportEntity report) {
        db.read(() -> dao.delete(report));
    }

    public void getCitoyenReport(long citoyenId, Consumer<List<ReportEntity>> callback) {
        db.read(() -> {
            List<ReportEntity> reports = dao.getReports(citoyenId);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(reports));
        });
    }

    public void getAll(Consumer<List<ReportEntity>> callback) {
        db.read(() -> {
            List<ReportEntity> reports = dao.getAll();
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(reports));
        });
    }


    public void confirmSync() {
        db.write(() -> dao.setUpdated(LocalDateTime.now(), true));
    }

    public void updateReports(List<ReportDTO> reports, Runnable callback) {
        db.write(() -> {
            dao.truncate();
            for (ReportDTO report : reports) {
                ReportEntity entity = new ReportEntity();
                entity.setId(report.getId());
                entity.setTitre(report.getTitre());
                entity.setDescription(report.getDescription());
                entity.setImagePath(report.getImagePath());
                entity.setCitoyenId(report.getCitoyenId());
                entity.setDuplicate(report.isDuplicate());
                entity.setRepaired(report.isRepaired());

                dao.insert(entity);
            }

            new Handler(Looper.getMainLooper()).post(callback);
        });
    }

    public void getById(long reportId, Consumer<ReportEntity> callback) {
        db.read(() -> {
            ReportEntity entity = dao.byId(reportId);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(entity));
        });
    }

    public void truncate() {
        db.write(dao::truncate);
    }
}
