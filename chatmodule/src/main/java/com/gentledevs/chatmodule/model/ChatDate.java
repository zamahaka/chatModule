package com.gentledevs.chatmodule.model;

import java.util.Date;

/**
 * Created by Yura Stetsyc on 4/18/17.
 */

public class ChatDate implements ChatItem {
    private Date mDate;

    public ChatDate(Date date) {
        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    @Override
    public Type getType() {
        return Type.DATE;
    }
}
