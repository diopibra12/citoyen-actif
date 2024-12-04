package ca.uqtr.citoyenactif.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ca.uqtr.citoyenactif.Citoyen;
import ca.uqtr.citoyenactif.R;
import ca.uqtr.citoyenactif.RecyApp;
import ca.uqtr.citoyenactif.ReportEditor;
import ca.uqtr.citoyenactif.db.entity.CitoyenEntity;
import ca.uqtr.citoyenactif.db.repository.ReportRepository;
import ca.uqtr.citoyenactif.fragment.ReportsList;

public class FCMService extends FirebaseMessagingService {

    private NotificationManager notificationManager;
    private RecyApp app;
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

    @Override
    public void onCreate() {
        super.onCreate();

        this.app = (RecyApp) this.getApplication();

        this.notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        this.getApplication().bindService(new Intent(this.getApplication(), SyncService.class), starter, Context.BIND_AUTO_CREATE);

        this.createNotificationChannel();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.i("FCM", "Received message FROM=" + message.getFrom());

        if (message.getFrom() == null) {
            return;
        }

        if (message.getFrom().equals("/topics/users.new")) {
            this.syncService.fetchUsers();
        } else if (message.getFrom().equals("/topics/reports.new")) {
            this.syncService.fetchReports(() -> {
                CitoyenEntity current = this.app.getCurrentCitoyen();
                if (current != null && current.isAgent()) {

                    long reportId = Long.parseLong(message.getData().get("report_id"));
                    ReportRepository reportRepository = new ReportRepository(this);

                    reportRepository.getById(reportId, report -> {
                        if (report == null) {
                            Log.i("FCM", "No report found");
                            return;
                        }

                        app.setCurrent(report);

                        Intent intent = new Intent(this, ReportEditor.class);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(Citoyen.class);
                        stackBuilder.addNextIntent(intent);

                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplication(), "REPORT-CHANNEL")
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("Nouveau report")
                                .setContentText("Un nouveau report de bris a été ajouté")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true);

                        this.notificationManager.notify(1, builder.build());
                    });

                } else {
                    Log.i("FCM", "User is not an agent");
                }
            });
        }

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("REPORT-CHANNEL", "Reports", importance);
            channel.setDescription("En lien avec les reports");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
