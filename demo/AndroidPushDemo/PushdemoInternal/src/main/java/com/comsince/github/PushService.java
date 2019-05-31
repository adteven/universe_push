package com.comsince.github;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.comsince.github.service.StackService;
import com.meizu.cloud.pushinternal.DebugLogger;


public class PushService extends Service {

    public static String START_PUSH_SERVICE = "start_foreground_service";

    private StackService stackService;

    @Override
    public void onCreate() {
        super.onCreate();
        stackService = new StackService(this);
        stackService.start();
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

}