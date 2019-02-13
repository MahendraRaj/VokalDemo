package com.iimb.vokaldemo.view.callback;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.iimb.vokaldemo.model.SMSObject;

import java.util.List;

public class MyDiffCallback extends DiffUtil.Callback {
    private List<SMSObject> oldSmsObjects;
    private List<SMSObject> newSmsObjects;

    public MyDiffCallback(List<SMSObject> oldSmsObjects, List<SMSObject> newSmsObjects) {
        this.oldSmsObjects = oldSmsObjects;
        this.newSmsObjects = newSmsObjects;
    }

    @Override
    public int getOldListSize() {
        return oldSmsObjects.size();
    }

    @Override
    public int getNewListSize() {
        return newSmsObjects.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return oldSmsObjects.get(i).getId().equals(newSmsObjects.get(i1).getId());
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        return oldSmsObjects.get(i).equals(newSmsObjects.get(i1));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}
