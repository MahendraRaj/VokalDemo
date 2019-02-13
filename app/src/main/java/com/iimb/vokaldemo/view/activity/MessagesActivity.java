package com.iimb.vokaldemo.view.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.iimb.vokaldemo.R;
import com.iimb.vokaldemo.view.adaptor.MessagesAdaptor;
import com.iimb.vokaldemo.controller.util.RecyclerSectionItemDecoration;
import com.iimb.vokaldemo.controller.util.SmsListener;
import com.iimb.vokaldemo.controller.util.SmsReceiver;
import com.iimb.vokaldemo.model.SMSObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessagesActivity extends AppCompatActivity implements SmsListener {
    RecyclerView recyclerView;
    MessagesAdaptor messagesAdaptor;
    LinearLayoutManager linearLayoutManager;
    List<SMSObject> smsObjectList;
    ProgressBar progressBar;
    private int NUM_ITEMS_IN_A_PAGE = 15;
    private int NUM_ITEMS = NUM_ITEMS_IN_A_PAGE;
    String messageId;
    SmsReceiver smsReceiver;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                if(bundle.getString("UNIQUE_ID") != null){
                    messageId = bundle.getString("UNIQUE_ID");
                    smsObjectList = getAllSms();
                    if(messagesAdaptor != null){
                        messagesAdaptor.updateList(getPagedSms(NUM_ITEMS_IN_A_PAGE));
                    }
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Messages");
        smsReceiver = new SmsReceiver();
        smsReceiver.bindListener(this);
        setContentView(R.layout.activity_messages);
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                if(bundle.getString("UNIQUE_ID") != null){
                    messageId = bundle.getString("UNIQUE_ID");
                }
            }

        }
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        linearLayoutManager = new LinearLayoutManager(MessagesActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        smsObjectList = getAllSms();

        messagesAdaptor = new MessagesAdaptor(getPagedSms(NUM_ITEMS_IN_A_PAGE),MessagesActivity.this);
        recyclerView.setAdapter(messagesAdaptor);
        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
                        true, // true for sticky, false for not
                        new RecyclerSectionItemDecoration.SectionCallback() {
                            int HOUR_MILLIS = 3600000;
                            int THREE_HOURS_MILLIS = HOUR_MILLIS * 3;

                            @Override
                            public View inflateHeaderView(RecyclerView parent) {
                                return LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.recycler_section_header,
                                                parent,
                                                false);
                            }

                            @Override
                            public CharSequence getSectionHeader(int position) {
                                long time = smsObjectList == null ? 0 : Long.parseLong(smsObjectList.get(position)
                                        .getTime());
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(time);
                                Calendar current = Calendar.getInstance();
                                String sectionHeader = "";
                                if((current.getTimeInMillis() - calendar.getTimeInMillis()) < HOUR_MILLIS){
                                    sectionHeader = "0 hours ago";
                                }else if((current.getTimeInMillis() - calendar.getTimeInMillis()) < (2 * HOUR_MILLIS)){
                                    sectionHeader = "1 hour ago";
                                }else if((current.getTimeInMillis() -  calendar.getTimeInMillis()) < (THREE_HOURS_MILLIS)){
                                    sectionHeader = "2 hours ago";
                                }else if((current.getTimeInMillis() -  calendar.getTimeInMillis()) < (THREE_HOURS_MILLIS * 2)){
                                    sectionHeader = "3 hours ago";
                                }else if((current.getTimeInMillis() -  calendar.getTimeInMillis()) < (THREE_HOURS_MILLIS * 4 )){
                                    sectionHeader = "6 hours ago";
                                }else if((current.getTimeInMillis() -  calendar.getTimeInMillis()) < (THREE_HOURS_MILLIS * 8)){
                                    sectionHeader = "12 hours ago";
                                }else
                                    sectionHeader = "1 day ago";
                                smsObjectList.get(position).setSectionHeader(sectionHeader);
                                return sectionHeader;
                            }
                        });
        recyclerView.addItemDecoration(sectionItemDecoration);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == 0){
                    if(recyclerView.getLayoutManager() != null){
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                        if(last >= (messagesAdaptor.getItemCount() - 1)  && last >= 10){
                            progressBar.setVisibility(View.VISIBLE);
                            new Handler(getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            },1000);
                            messagesAdaptor.updateList(getPagedSms(NUM_ITEMS_IN_A_PAGE));


                        }
                    }
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(messageId != null) {
            messagesAdaptor.setReceivedMessageSentTime(messageId);
            messagesAdaptor.notifyDataSetChanged();
            messageId = null;
        }

        if(smsReceiver != null) {
            registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }

    }

    public List<SMSObject> getAllSms() {
        List<SMSObject> lstSms = new ArrayList<SMSObject>();
        SMSObject objSms = new SMSObject();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(message, null, null, null, "date DESC");
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new SMSObject();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c
                        .getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                objSms.setSentTime(c.getString(c.getColumnIndexOrThrow("date_sent")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                lstSms.add(objSms);
                c.moveToNext();
            }
        }
        c.close();
        return lstSms;
    }

    public List<SMSObject> getPagedSms(int count) {
        List<SMSObject> lstSms = new ArrayList<SMSObject>();
        if(smsObjectList.size() >= count) {
            lstSms = smsObjectList.subList(0, count);
            NUM_ITEMS_IN_A_PAGE = count + NUM_ITEMS;

        }else{
            lstSms = smsObjectList;
            NUM_ITEMS_IN_A_PAGE = smsObjectList.size();

        }
        return lstSms;
    }

    //This is to handle the UI Updates when the user is on
    // MessagesActivity and receives a notification without clicking on notification to see the list updated.
    //disabling it will allow to see the updates only clicking on notification
    @Override
    public void messageReceived(String newMessageId) {
        messageId  = newMessageId;
        if(messagesAdaptor !=  null){
            smsObjectList = getAllSms();
            messagesAdaptor.setReceivedMessageSentTime(messageId);
            messagesAdaptor.updateList(getPagedSms(NUM_ITEMS_IN_A_PAGE));
            if(messagesAdaptor.getItemCount() > 0) {
                if (recyclerView != null) {
                    recyclerView.scrollToPosition(0);
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(smsReceiver != null){
            unregisterReceiver(smsReceiver);
        }
    }
}
