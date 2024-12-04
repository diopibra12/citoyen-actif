package ca.uqtr.citoyenactif;


import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import ca.uqtr.citoyenactif.databinding.ActivityReportBinding;
import ca.uqtr.citoyenactif.db.entity.ReportEntity;
import ca.uqtr.citoyenactif.db.repository.CitoyenRepository;
import ca.uqtr.citoyenactif.db.repository.ReportRepository;
import ca.uqtr.citoyenactif.fragment.model.ReportViewModel;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportEditor extends AppCompatActivity {


    private ReportEntity report;
    private ActivityReportBinding binding;
    private ReportViewModel viewModel;
    private Uri photoURI;
    private BottomSheetBehavior<CardView> photoPickerSheet;
    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                photoPickerSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);

        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        RecyApp app = (RecyApp) getApplicationContext();
        report = app.getCurrent();
        
        this.requestUserPosition();

        if (report == null || !app.getCurrentCitoyen().isAgent()) {
            binding.duplicateButton.setVisibility(View.INVISIBLE);
            binding.repairedButton.setVisibility(View.INVISIBLE);
        }

        if (report == null) {
            report = new ReportEntity();
            report.setNew(true);
        }

        binding.editTextReportTitle.setText(report.getTitre());
        binding.editTextReportDescription.setText(report.getDescription());

        binding.buttonSubmitReport.setOnClickListener(v -> {
            report.setTitre(binding.editTextReportTitle.getText().toString());
            report.setDescription(binding.editTextReportDescription.getText().toString());
            report.setCitoyenId(app.getCurrentCitoyen().getId());

            this.viewModel.createOrUpdateReport(report);
            finish();
        });

        binding.duplicateButton.setOnClickListener(v -> {
            report.setDuplicate(true);

            this.viewModel.createOrUpdateReport(report);
            finish();
        });

        binding.repairedButton.setOnClickListener(v -> {
            report.setRepaired(true);

            this.viewModel.createOrUpdateReport(report);
            finish();
        });

        photoPickerSheet = BottomSheetBehavior.from(binding.imagePicker.pickerSheet);
        photoPickerSheet.setDraggable(true);
        photoPickerSheet.setPeekHeight(150);
        photoPickerSheet.addBottomSheetCallback(bottomSheetCallback);
        binding.imageView.setOnClickListener(v -> {
            photoPickerSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        setupBottomSheetBehavior();
        setupActivityResultLaunchers();

        binding.imagePicker.camera.setOnClickListener(v -> requestCameraPermission());
        binding.imagePicker.photo.setOnClickListener(v -> pickImage());
    }

    private void setupBottomSheetBehavior() {
        photoPickerSheet = BottomSheetBehavior.from(binding.imagePicker.pickerSheet);
        photoPickerSheet.setDraggable(true);
        photoPickerSheet.setPeekHeight(150);
        photoPickerSheet.addBottomSheetCallback(bottomSheetCallback);
        binding.imageView.setOnClickListener(v -> {
            photoPickerSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    private void setupActivityResultLaunchers() {
        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        binding.imageView.setImageURI(photoURI);
                    }
                });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
                    }
                });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        binding.imageView.setImageURI(uri);
                    }
                });
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {

            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            "ca.uqtr.citoyenactif.fileprovider", photoFile);
                    report.setImagePath(photoFile.getAbsolutePath());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    cameraActivityResultLauncher.launch(intent);
                }
            } catch (IOException ex) {
                Toast.makeText(this, "Erreur lors de la création du fichier image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    private void pickImage() {
        pickImageLauncher.launch("image/*");
    }

    @SuppressLint("MissingPermission")
    public void requestUserPosition() {

        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            return;
        }

        viewModel.requestLocation();
    }

    private final ActivityResultLauncher<String[]> requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
        if (isGranted.values().stream().allMatch(g -> g)) {
            requestUserPosition();
        } else {
            Toast.makeText(this, "Permission refusée, veuillez redémarrer pour continuer!", Toast.LENGTH_SHORT).show();

        }
    });
}

