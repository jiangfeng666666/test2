package com.example.ten;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    private static final String TAG = "database";
     Downloadbinder mBinder = new Downloadbinder();
    class Downloadbinder extends Binder {
        public void startdownload(){
            Log.d(TAG, "startdownload: ");
        }
        public int getprogess(){
            Log.d(TAG, "stopdownload: ");
            return 0;
        }
    }
    public MyService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationChannel channel = new NotificationChannel("1","消息",NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this,"1").setContentTitle("This is contetn title").setContentText("text").setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)).setContentIntent(pi).build();
        startForeground(2,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
        
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}
