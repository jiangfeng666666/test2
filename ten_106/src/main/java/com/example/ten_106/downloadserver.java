package com.example.ten_106;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;

public class downloadserver extends Service {
    private DownloadTask downloadTask;
    private String downloadURL;
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
        getNotificationManager().notify(1,getNotification("Doadloading...",progress));
        }

        @Override
        public void onSuccess() {
        downloadTask = null;
        stopForeground(true);
        getNotificationManager().notify(1,getNotification("Download Succsee",-1));
            Toast.makeText(downloadserver.this,"Download Succsee", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
        downloadTask  = null;
        stopForeground(true);
        getNotificationManager().notify(1,getNotification("Downlaod Fail",-1));
            Toast.makeText(downloadserver.this, "Downlaod Fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
        downloadTask = null;
            Toast.makeText(downloadserver.this, "paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
        downloadTask = null;
        stopForeground(true);
            Toast.makeText(downloadserver.this, "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    public downloadserver() {
    }
private DownloadBinder mbind = new DownloadBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mbind;
    }

    class DownloadBinder extends Binder{
        public void startDownload(String url){
            if (downloadTask == null){
                downloadURL = url;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadURL);
                startForeground(1,getNotification("Downloading...",0));
                Toast.makeText(downloadserver.this, "Downloading", Toast.LENGTH_SHORT).show();
            }
        }
        public void pauseDownload(){
            if (downloadTask!= null){
                downloadTask.pauseDownload();
            }
        }
        public void cancelDoadload(){
            if (downloadTask!=null){
                downloadTask.cancelDownload();
            }
            if (downloadURL!=null){
                String fileName = downloadURL.substring(downloadURL.lastIndexOf("/"));
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(directory+fileName);
                if (file.exists()){
                    file.delete();
                }
                getNotificationManager().cancel(1);
                stopForeground(true);
                Toast.makeText(downloadserver.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    private Notification getNotification(String titil,int progress){
        NotificationChannel channel = new NotificationChannel("1","消息",NotificationManager.IMPORTANCE_DEFAULT);
        getNotificationManager().createNotificationChannel(channel);
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(titil);
        if (progress>=0){
            builder.setContentTitle(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();

    }
}
