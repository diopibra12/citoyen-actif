package ca.uqtr.citoyenactif.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ca.uqtr.citoyenactif.ApiUtils;
import ca.uqtr.citoyenactif.api.CitoyenAPI;
import ca.uqtr.citoyenactif.api.dto.CitoyenDTO;
import ca.uqtr.citoyenactif.api.dto.ReportCreateDTO;
import ca.uqtr.citoyenactif.api.dto.ReportDTO;
import ca.uqtr.citoyenactif.db.repository.CitoyenRepository;
import ca.uqtr.citoyenactif.db.repository.ReportRepository;
import retrofit2.Call;
import retrofit2.Response;

public class SyncService extends Service {

    private final ExecutorService pool = Executors.newFixedThreadPool(4);
    private final IBinder binder = new LocalBinder();
    private final CitoyenRepository citoyenRepository;
    private final ReportRepository reportRepository;

    public SyncService() {
        this.citoyenRepository = new CitoyenRepository(this);
        this.reportRepository = new ReportRepository(this);
    }

    public void createReport(long reportId) {
        pool.submit(() -> {
            Call<Void> call = ApiUtils.getAPIService().create(CitoyenAPI.class).createReport(new ReportCreateDTO(String.valueOf(reportId)));
            try {
                call.execute();
            } catch (IOException ignored) {
            }
        });
    }

    public void registerUser() {
        pool.submit(() -> {
            Call<Void> call = ApiUtils.getAPIService().create(CitoyenAPI.class).register();
            try {
                call.execute();
            } catch (IOException ignored) {
            }
        });
    }

    public void fetchUsers() {
        Log.i("FCM", "Fetching citoyens");

        pool.submit(() -> {
            Call<List<CitoyenDTO>> call = ApiUtils.getAPIService().create(CitoyenAPI.class).fetchUsers();
            try {
                Response<List<CitoyenDTO>> resp = call.execute();

                if (resp.isSuccessful()) {
                    Log.i("FCM", "Updating citoyens");
                    this.reportRepository.truncate();
                    this.citoyenRepository.updateCitoyens(resp.body());
                } else {
                    Log.i("FCM", "Updating citoyens not succesful");
                }
            } catch (IOException ex) {
                Log.i("FCM", "Exception while fetching users", ex);
            }
        });
    }

    public void syncUsers() {
        this.citoyenRepository.getAll(users -> {
            pool.submit(() -> {
                List<CitoyenDTO> dto = users.stream().map(x -> new CitoyenDTO(x.getId(), x.getPrenom(), x.getNom(), x.getAdresse(), x.getCourriel(), x.getMotDePasse(), x.getNumeroTel(), x.isAgent(), x.getFcmToken(), x.getVille(), x.getNumeroAgent())).collect(Collectors.toList());

                Call<Void> call = ApiUtils.getAPIService().create(CitoyenAPI.class).syncUsers(dto);
                try {
                    Response<Void> resp = call.execute();

                    if (resp.isSuccessful()) {
                        this.citoyenRepository.confirmSync();
                    }
                } catch (IOException ignored) {
                }
            });
        });
    }

    public void fetchReports(Runnable callback) {
        pool.submit(() -> {
            Call<List<ReportDTO>> call = ApiUtils.getAPIService().create(CitoyenAPI.class).fetchReports();
            try {
                Response<List<ReportDTO>> resp = call.execute();

                if (resp.isSuccessful()) {
                    this.reportRepository.updateReports(resp.body(), callback);
                    return;
                }

            } catch (IOException ignored) {}
            new Handler(Looper.getMainLooper()).post(callback);
        });
    }

    public void syncReports() {
        this.reportRepository.getAll(reports -> {
            pool.submit(() -> {
                List<ReportDTO> dto = reports.stream().map(x -> new ReportDTO(x.getId(), x.getTitre(), x.getDescription(), x.getImagePath(), x.isDuplicate(), x.isRepaired(), x.getLng(), x.getLat(), x.getCitoyenId(), x.isNew())).collect(Collectors.toList());

                Call<Void> call = ApiUtils.getAPIService().create(CitoyenAPI.class).syncReports(dto);
                try {
                    Response<Void> resp = call.execute();

                    if (resp.isSuccessful()) {
                        this.reportRepository.confirmSync();
                    }
                } catch (IOException ignored) {
                }
            });
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public class LocalBinder extends Binder {
        public SyncService getService() {
            return SyncService.this;
        }
    }

}