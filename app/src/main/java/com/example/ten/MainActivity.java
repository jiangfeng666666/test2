package com.example.ten;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private MyService.Downloadbinder downloadbinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadbinder = (MyService.Downloadbinder) service;
            downloadbinder.startdownload();;
            downloadbinder.getprogess();
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
        Button stop = findViewById(R.id.stop);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                Intent intent1 = new Intent(MainActivity.this,MyService.class);
                bindService(intent1,connection,BIND_AUTO_CREATE);
                break;
            case R.id.stop:
              unbindService(connection);
                break;
            case 9:
                break;
                default:
                    break;
        }
    }
}
