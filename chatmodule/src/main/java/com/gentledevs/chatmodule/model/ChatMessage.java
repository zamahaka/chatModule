package com.gentledevs.chatmodule.model;

import java.util.Date;

/**
 * Created by Yura Stetsyc on 4/18/17.
 */

public class ChatMessage implements ChatItem {

    public static final String DEFAULT_TEMP_ID = "default";

    private Type mType;

    private ContentType mContentType;
    private Status mStatus;

    private int mId;
    private String mTempId = DEFAULT_TEMP_ID;

    private String mText;
    private Date mDate;
    private String mAvatarURI;

    private boolean mIsPending;
    private boolean mHasError;

    public ChatMessage(ContentType contentType, Status status, int id, String tempId, String text,
                       Date date, boolean isPending, String avatarURI, boolean hasError) {
        this(Type.SELF, contentType, status, id, tempId, text, date, isPending, avatarURI, false);
    }

    public ChatMessage(Type type, ContentType contentType, Status status, int id, String tempId,
                       String text, Date date, boolean isPending, String avatarURI, boolean hasError) {
        mType = type;
        mContentType = contentType;
        mStatus = status;
        mId = id;
        mTempId = tempId;
        mText = text;
        mDate = date;
        mIsPending = isPending;
        mAvatarURI = avatarURI;
        mHasError = hasError;
    }

    @Override
    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTempId() {
        return mTempId;
    }

    public void setTempId(String tempId) {
        mTempId = tempId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isPending() {
        return mIsPending;
    }

    public void setPending(boolean pending) {
        mIsPending = pending;
    }

    public String getAvatarURI() {
        return mAvatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        mAvatarURI = avatarURI;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public ContentType getContentType() {
        return mContentType;
    }

    public void setContentType(ContentType contentType) {
        mContentType = contentType;
    }

    public boolean hasError() {
        return mHasError;
    }

    public void setHasError(boolean hasError) {
        mHasError = hasError;
    }

    public enum ContentType {
        TEXT, IMAGE
    }

    public enum Status {
        ACTIVE, FORWARDED, UNPUBLISHED, WAITING
    }
}
