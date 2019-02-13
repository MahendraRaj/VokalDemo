package com.iimb.vokaldemo.view.adaptor;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iimb.vokaldemo.R;
import com.iimb.vokaldemo.model.SMSObject;
import com.iimb.vokaldemo.view.callback.MyDiffCallback;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MessagesAdaptor extends RecyclerView.Adapter<MessagesAdaptor.ViewHolder>{
    private List<SMSObject> smsObjectList;
    private WeakReference<Activity> activityWeakReference;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dayFormat;
    private String newMessageSentTime;
    public MessagesAdaptor(List<SMSObject> smsObjectList, Activity activity) {
        this.smsObjectList = smsObjectList;
        this.activityWeakReference = new WeakReference<>(activity);
        dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        dayFormat = new SimpleDateFormat("EEE d/M", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(activityWeakReference.get());
        return getViewHolder(viewGroup,inflater);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SMSObject smsObject = smsObjectList.get(i);
        viewHolder.tvSenderName.setText(smsObject.getAddress());
        viewHolder.tvMessageBody.setText(smsObject.getMsg());
        if(smsObject.getSentTime().equalsIgnoreCase(newMessageSentTime)){
            viewHolder.tvSenderName.setTextColor(Color.BLACK);
            viewHolder.tvSentAt.setTextColor(Color.BLACK);
            viewHolder.tvSenderName.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
            viewHolder.tvMessageBody.setTextColor(Color.BLACK);
        }else{
            viewHolder.tvSenderName.setTextColor(Color.GRAY);
            viewHolder.tvSentAt.setTextColor(Color.GRAY);
            viewHolder.tvSenderName.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            viewHolder.tvMessageBody.setTextColor(Color.GRAY);
        }
        viewHolder.itemView.setPadding(40,40,40,40);

        long timeInMillis = Long.parseLong(smsObject.getTime());
        Calendar date = Calendar.getInstance();
        Calendar current = (Calendar) date.clone();
        date.setTimeInMillis(timeInMillis);
        Log.d("Date",date.getTime().toString());
        if(current.get(Calendar.DAY_OF_YEAR) != date.get(Calendar.DAY_OF_YEAR)){
            viewHolder.tvSentAt.setText(dayFormat.format(date.getTime()));
        }else {
            viewHolder.tvSentAt.setText(dateFormat.format(date.getTime()));
        }

    }

    @NonNull
    private ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.row_item_message, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return smsObjectList == null ? 0 : smsObjectList.size();    }

    public void setReceivedMessageSentTime(String sentTimeInMillis) {
        this.newMessageSentTime =  sentTimeInMillis;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSender;
        TextView tvSenderName;
        TextView tvSentAt;
        TextView tvMessageBody;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSender = itemView.findViewById(R.id.ivSender);
            tvSentAt = itemView.findViewById(R.id.tvSentAt);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessageBody = itemView.findViewById(R.id.tvMessageBody);
        }
    }


    public void addAll(List<SMSObject> objects) {
        smsObjectList = objects;
    }

    public void updateList(List<SMSObject> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallback(this.smsObjectList, newList));
        diffResult.dispatchUpdatesTo(this);
        smsObjectList = newList;

    }

}

