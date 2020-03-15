package com.example.ten_106;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private downloadserver.DownloadBinder downloadBinder;
private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        downloadBinder = (downloadserver.DownloadBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = findViewById(R.id.start);
        Button pause = findViewById(R.id.pause);
        Button cancel = findViewById(R.id.cancel);
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        cancel.setOnClickListener(this);
        Intent intent = new Intent(this,downloadserver.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onClick(View v) {
        if (downloadBinder == null){
            return;
        }
        switch (v.getId()){ //http://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe
            case R.id.start:
                String url = "http://www.jiangfeng1803.icu/data.json ";
                downloadBinder.startDownload(url);
                break;
            case R.id.pause:
                downloadBinder.pauseDownload();
                break;
            case R.id.cancel:
                downloadBinder.cancelDoadload();
                break;
                default:
                    break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "请打开权限", Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
