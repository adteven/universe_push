package com.comsince.github;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import com.comsince.github.service.StackService;
import com.meizu.cloud.pushinternal.DebugLogger;


public class PushService extends Service {

    public static String START_PUSH_SERVICE = "start_push_service";
    public static int FOREGROUND_SERVICE = 102;

    private StackService stackService;

    @Override
    public void onCreate() {
        super.onCreate();
        stackService = new StackService(this);
        stackService.start();
        startForegroudService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DebugLogger.i("LocalService", "Received start id " + startId + ": " + intent);
        stackService.switchIntent(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DebugLogger.i(START_PUSH_SERVICE,"close push channel");
        stackService.stop();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void startForegroudService(){
        CharSequence text = getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PushMainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.flyme_status_ic_notification ))
                .setSmallIcon(R.drawable.mz_push_notification_small_icon)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("PushSevice foreground")  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        startForeground(FOREGROUND_SERVICE,notification);
    }

}