package com.iimb.vokaldemo.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iimb.vokaldemo.R;
import com.iimb.vokaldemo.controller.util.SmsReceiver;

public class MainActivity extends AppCompatActivity {
    private static final int READ_SMS = 100;
    private static final int RECEIVE_SMS = 102;
    private Button btnReadMessages;
    private Button btnReceiveMessages;
    private SmsReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smsReceiver = new SmsReceiver();
        registerReceiver(smsReceiver,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));


        btnReadMessages = findViewById(R.id.btnReadMessages);
        btnReceiveMessages = findViewById(R.id.btnReceiveMessages);
        btnReceiveMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestSmsReceivePermissions()) {
                    registerReceiver(smsReceiver,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
                    Toast.makeText(MainActivity.this,"Listening to Inbox messages",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnReadMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestSmsReadPermissions()){
                    startActivity(new Intent(MainActivity.this,MessagesActivity.class));
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(checkAndRequestSmsReceivePermissions()) {
//        }
    }

    private boolean checkAndRequestSmsReadPermissions()
    {
        int smsRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int smsReceived = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if (smsRead != PackageManager.PERMISSION_GRANTED)
        {
            if(smsReceived != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS
                        ,Manifest.permission.RECEIVE_SMS}, READ_SMS);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, READ_SMS);
            }
            return false;
        }else if(smsReceived != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, READ_SMS);
            return false;
        }
        return true;
    }

    private boolean checkAndRequestSmsReceivePermissions()
    {
        int smsReceived = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(smsReceived != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_SMS) {
            if (grantResults[0] == RESULT_OK) {
                startActivity(new Intent(MainActivity.this,MessagesActivity.class));
            }
        }

        if (requestCode == RECEIVE_SMS) {
            if (grantResults[0] == RESULT_OK) {
                registerReceiver(new SmsReceiver(),new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (smsReceiver != null) {
                unregisterReceiver(smsReceiver);
            }
        }catch (Exception e){
            //smsReceiver is not registered
        }
    }

}
