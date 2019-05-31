package com.comsince.github.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;

import com.comsince.github.PushService;
import com.comsince.github.R;
import com.comsince.github.model.GroupRequest;
import com.comsince.github.push.Signal;
import com.comsince.github.utils.Json;
import com.comsince.github.utils.PreferenceUtil;
import com.meizu.cloud.pushinternal.DebugLogger;
import com.meizu.cloud.pushsdk.util.MinSdkChecker;

public class StackService implements CloseableService,MessageCallback{
    Context context;
    NotificationManager notificationManager;

    ConnectService connectService;
    GroupService groupService;
    NetworkService networkService;

    public StackService(Context context){
        this.context = context;
        this.notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        connectService = new ConnectService(context,"push-connector");
        connectService.setMessageCallback(this);
        groupService = new GroupService(connectService);
        networkService  = new NetworkService(context,connectService);
    }

    @Override
    public void start() {
        connectService.start();
        networkService.start();
    }

    @Override
    public void stop() {
        connectService.stop();
        networkService.stop();
    }

    @Override
    public void receiveMessage(Signal signal, String message) {
        if(Signal.SUB == signal){
            PreferenceUtil.putToken(context,connectService.getToken());
            //测试用，默认加入test群组
            groupService.joinGroup("test");
        } else if(Signal.CONTACT == signal){
            try {
                GroupRequest groupRequest = Json.toBean(message,GroupRequest.class);
                DebugLogger.i("PushService","receive contact message "+groupRequest);
                showNotification(groupRequest);
            } catch (Exception e){
                DebugLogger.e("PushService","invalid group message-> "+message);
            }

        }
    }

    public void switchIntent(Intent intent){
        if(PushService.START_PUSH_SERVICE.equals(intent.getAction())){
            connectService.connect();
        } else if(GroupService.ACTION_GROUP_JOIN_GROUP.equals(intent.getAction())){
            groupService.joinGroup(intent.getStringExtra(GroupService.GROUP_NAME));
        } else if(GroupService.ACTION_SEND_PUBLIC_MESSAGE.equals(intent.getAction())){
            groupService.sendPublicMessage(intent.getStringExtra(GroupService.GROUP_NAME),intent.getStringExtra(GroupService.GROUP_MESSAGE));
        } else if(GroupService.ACTION_SEND_PRIVATE_MESSAGE.equals(intent.getAction())){

        }
    }

    private void showNotification(GroupRequest groupRequest){
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getService(context, 0,
                new Intent(context, PushService.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification.Builder builder = new Notification.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.flyme_status_ic_notification ))
                .setSmallIcon(R.drawable.mz_push_notification_small_icon)  // the status icon
                .setAutoCancel(true)
                .setTicker(groupRequest.getMessage())  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("来自群组["+groupRequest.getGroup()+"]的消息")  // the label of the entry
                .setContentText(groupRequest.getMessage())  // the contents of the entry
                .setContentIntent(contentIntent);  // The intent to send when the entry is clicked

        if(MinSdkChecker.isSupportNotificationBuild() && !TextUtils.isEmpty(groupRequest.getMessage()) && groupRequest.getMessage().length() > 20){
            Notification.BigTextStyle notiStyle = new Notification.BigTextStyle();
            notiStyle.setBigContentTitle("来自群组["+groupRequest.getGroup()+"]的消息");
            notiStyle.bigText(groupRequest.getMessage());
            builder.setStyle(notiStyle);
        }

        if(MinSdkChecker.isSupportNotificationChannel()){
            DebugLogger.e("PushService","support notification channel on non meizu device");
            NotificationChannel notificationChannel = new NotificationChannel("push","push-connector", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true); //是否在桌面icon右上角展示小红点
            notificationChannel.setLightColor(Color.GREEN); //小红点颜色
            notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId("push");
        }
        Notification notification = builder.build();
        int notifyId = Math.abs((int)System.currentTimeMillis());
        notificationManager.notify(notifyId,notification);
    }
}
