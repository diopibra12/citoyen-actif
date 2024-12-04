package ca.uqtr.citoyenactif.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ca.uqtr.citoyenactif.R;
import ca.uqtr.citoyenactif.RecyApp;
import ca.uqtr.citoyenactif.ReportEditor;
import ca.uqtr.citoyenactif.databinding.ReportsMapBinding;
import ca.uqtr.citoyenactif.db.entity.ReportEntity;
import ca.uqtr.citoyenactif.fragment.model.ReportViewModel;


public class ReportsMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private ReportViewModel viewModel;
    private RecyApp app;
    private ReportsMapBinding binding;
    static final float COORDINATE_OFFSET = 0.00002F;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = ReportsMapBinding.inflate(inflater, container, false);

        this.app = (RecyApp) requireActivity().getApplicationContext();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFrag != null) {
            mapFrag.getMapAsync(this);
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.viewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.map = map;

        this.requestUserPosition();

        this.viewModel.currentLocation().observe(getViewLifecycleOwner(), this::onLocationChanged);

        this.viewModel.getReports().observe(getViewLifecycleOwner(), reports -> {
            int i = 0;
            for (ReportEntity report : reports) {
                MarkerOptions marker = new MarkerOptions()
                        .title(report.getTitre())
                        .snippet(report.getDescription())
                        .position(new LatLng(report.getLat() + COORDINATE_OFFSET * i++, report.getLng()));
                Marker m = map.addMarker(marker);
                m.setTag(report);
            }

        });

        this.map.setOnMarkerClickListener(marker -> {
            ReportEntity report = (ReportEntity) marker.getTag();

            this.app.setCurrent(report);
            startActivity(new Intent(getActivity(), ReportEditor.class));
            this.map.clear();

            return true;
        });

        this.binding.addReportButton.setOnClickListener(v -> {
            this.app.setCurrent(null);
            startActivity(new Intent(requireContext(), ReportEditor.class));
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        this.viewModel.updateReports();
    }

    private void onLocationChanged(Location location) {
        if (location != null && map != null) {
            CameraUpdate cu = CameraUpdateFactory.zoomTo(10);
            CameraUpdate place = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            map.moveCamera(place);
            map.moveCamera(cu);
        }
    }

    @SuppressLint("MissingPermission")
    public void requestUserPosition() {

        if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});

            return;
        }

        this.map.setMyLocationEnabled(true);

        viewModel.requestLocation();
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
        if (isGranted.values().stream().allMatch(g -> g)) {
            requestUserPosition();
        } else {
            Toast.makeText(requireContext(), "Permission refusée, veuillez redémarrer pour continuer!", Toast.LENGTH_SHORT).show();

        }
    });


}