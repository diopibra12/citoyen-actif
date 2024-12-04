package ca.uqtr.citoyenactif.fragment.model;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ca.uqtr.citoyenactif.RecyApp;
import ca.uqtr.citoyenactif.db.entity.CitoyenEntity;
import ca.uqtr.citoyenactif.db.entity.ReportEntity;
import ca.uqtr.citoyenactif.db.repository.ReportRepository;
import ca.uqtr.citoyenactif.service.SyncService;

public class ReportViewModel extends AndroidViewModel {

    private final RecyApp app;
    private MutableLiveData<List<ReportEntity>> reports;
    private final FusedLocationProviderClient flp;
    private final MutableLiveData<Location> currentLocation;
    private final LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location loc = locationResult.getLastLocation();
            if (loc != null) {
                currentLocation.postValue(loc);
            }

        }

        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            if (!locationAvailability.isLocationAvailable()) {
                currentLocation.postValue(null);
            }
        }
    };
    private final ReportRepository reportRepository;
    private SyncService syncService;
    protected ServiceConnection starter = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (service instanceof SyncService.LocalBinder) {
                SyncService.LocalBinder binder = (SyncService.LocalBinder) service;
                syncService = binder.getService();

                syncService.fetchReports(() -> {
                    if (app.getCurrentCitoyen().isAgent()) {
                        reportRepository.getAll(list -> reports.postValue(list.stream().filter(x -> !x.isRepaired() && !x.isDuplicate()).collect(Collectors.toList())));
                    } else {
                        reportRepository.getCitoyenReport(app.getCurrentCitoyen().getId(), list -> reports.postValue(list.stream().filter(x -> !x.isRepaired() && !x.isDuplicate()).collect(Collectors.toList())));
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public ReportViewModel(@NonNull Application application) {
        super(application);

        this.app = (RecyApp) application;

        application.bindService(new Intent(application, SyncService.class), starter, Context.BIND_AUTO_CREATE);
        this.flp = LocationServices.getFusedLocationProviderClient(application);
        this.currentLocation = new MutableLiveData<>();
        this.reportRepository = new ReportRepository(application);

        this.reports = new MutableLiveData<>();
    }

    public MutableLiveData<List<ReportEntity>> getReports() {
        return this.reports;
    }

    public void createOrUpdateReport(ReportEntity report) {
        if (currentLocation.getValue() != null) {
            report.setLat(currentLocation.getValue().getLatitude());
            report.setLng(currentLocation.getValue().getLongitude());

        }

        this.reportRepository.insert(report, id -> {
            this.syncService.syncReports();

            if (report.isNew()) {
                this.syncService.createReport(id);
            }
        });
    }

    public LiveData<Location> currentLocation() {
        return currentLocation;
    }

    @SuppressLint("MissingPermission")
    public void requestLocation() {
        flp.getLastLocation().addOnSuccessListener(currentLocation::postValue);

        LocationRequest request = new LocationRequest.Builder(1000L).setIntervalMillis(1000).setMinUpdateDistanceMeters(10000).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();

        flp.requestLocationUpdates(request, callback, Looper.getMainLooper());
    }

    public void delete(ReportEntity report) {
        this.reportRepository.delete(report);
    }

    public void updateReports() {
        if (app.getCurrentCitoyen().isAgent()) {
            reportRepository.getAll(list -> reports.postValue(list.stream().filter(x -> !x.isRepaired() && !x.isDuplicate()).collect(Collectors.toList())));
        } else {
            reportRepository.getCitoyenReport(app.getCurrentCitoyen().getId(), list -> reports.postValue(list.stream().filter(x -> !x.isRepaired() && !x.isDuplicate()).collect(Collectors.toList())));
        }
    }
}
