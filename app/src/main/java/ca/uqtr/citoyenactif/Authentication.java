package ca.uqtr.citoyenactif;


import android.Manifest.permission;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.messaging.FirebaseMessaging;

import ca.uqtr.citoyenactif.databinding.ActivityMainBinding;
import ca.uqtr.citoyenactif.fragment.ResetPassword;
import ca.uqtr.citoyenactif.fragment.SignIn;
import ca.uqtr.citoyenactif.fragment.SignUp;
import ca.uqtr.citoyenactif.service.SyncService;

public class Authentication extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SyncService syncService;
    protected ServiceConnection starter = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (service instanceof SyncService.LocalBinder) {
                SyncService.LocalBinder binder = (SyncService.LocalBinder) service;
                syncService = binder.getService();

                syncService.fetchUsers();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.bindService(new Intent(this.getApplication(), SyncService.class), starter, Context.BIND_AUTO_CREATE);

        this.askNotificationPermission();
        FirebaseMessaging.getInstance().subscribeToTopic("users.new");
        FirebaseMessaging.getInstance().subscribeToTopic("reports.new");

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignIn()).commit();

        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.connexion) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignIn()).commit();
                return true;
            } else if (item.getItemId() == R.id.inscription) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignUp()).commit();
                return true;
            } else if (item.getItemId() == R.id.resetPassword) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ResetPassword()).commit();
                return true;
            }
            return false;
        });
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (this.checkSelfPermission(permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(permission.POST_NOTIFICATIONS)) {
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(permission.POST_NOTIFICATIONS);
            }
        }
    }

}