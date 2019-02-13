package com.iimb.vokaldemo.controller.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

import com.iimb.vokaldemo.R;
import com.iimb.vokaldemo.view.activity.MessagesActivity;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0;i<pdus.length;i++)
        {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getOriginatingAddress();
            String id = String.valueOf(smsMessage.getTimestampMillis());
            String messageBody = smsMessage.getMessageBody();
            try
            {

                if(messageBody!=null){
                    int notifyID = 1;
                    String CHANNEL_ID = "my_channel_01";
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(sender)
                                    .setContentText(messageBody)
                                    .setChannelId(CHANNEL_ID);
                    NotificationManager mNotificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                id,
                                NotificationManager.IMPORTANCE_DEFAULT);
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    Intent notificationIntent = new Intent(context, MessagesActivity.class);
                    notificationIntent.putExtra("UNIQUE_ID",id);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    builder.setContentIntent(pendingIntent);
                    mNotificationManager.notify(notifyID,builder.build());
                    if(mListener != null){
                        mListener.messageReceived(id);
                    }
                    }
            }
            catch(Exception e){

            }
        }
    }


    public void bindListener(SmsListener listener) {
        mListener = listener; }}