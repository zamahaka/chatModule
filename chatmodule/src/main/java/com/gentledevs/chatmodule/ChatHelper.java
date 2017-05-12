package com.gentledevs.chatmodule;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.gentledevs.chatmodule.model.ChatDate;
import com.gentledevs.chatmodule.model.ChatItem;
import com.gentledevs.chatmodule.model.ChatMessage;
import com.gentledevs.chatmodule.model.ChatTypingItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Yura Stetsyc on 4/19/17.
 */

public class ChatHelper {

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    @NonNull
    private ChatAdapter mAdapter;

    private Calendar mCalendarCompare = Calendar.getInstance();
    private Calendar mCalendarTo = Calendar.getInstance();

    private ChatTypingItem mTypingItem = new ChatTypingItem();

    public ChatHelper(@NonNull ChatAdapter adapter) {
        mAdapter = adapter;
    }

    public void addTypingItem() {
        mAdapter.addItem(mTypingItem, 0);
    }

    public void removeTyingItem() {
        mAdapter.removeItem(mTypingItem);
    }

    public void addMessageEnd(ChatMessage message) {
        List<ChatItem> items = mAdapter.getItems();
        if (items.isEmpty()) {
            addChatDateEnd(message.getDate());
        }

        ChatItem chatDate = items.get(items.size() - 1);
        if (chatDate instanceof ChatDate) {
            Date headerDate = ((ChatDate) chatDate).getDate();
            Date currentDate = message.getDate();

            if (currentDate.before(headerDate)) {
                addChatDateEnd(message.getDate());
            }

            mAdapter.addItem(message, items.size() - 1);
        }
    }

    public void addMessageFront(ChatMessage message) {
        List<ChatItem> items = mAdapter.getItems();
        int index = findMessageIndex(message, items);
        if (index != -1) {
            ChatItem item = items.get(index);
            if (item instanceof ChatMessage)
                if (((ChatMessage) item).getContentType().equals(ChatMessage.ContentType.IMAGE)) {
                    message.setText(((ChatMessage) item).getText());
                }

            mAdapter.update(message, index);
        } else {

            boolean hasTypingItem = hasTypingItem(items);
            if (hasTypingItem) {
                addMessageFrontNoTyping(message, items);
            } else {
                addMessageFrontWithTyping(message, items);
            }

            mAdapter.addItem(message, hasTypingItem ? 1 : 0);
        }
    }

    public void removeMessage(int messageId, String tempId) {
        int index = findMessageIndex(messageId, tempId, mAdapter.getItems());

        mAdapter.removeItem(index);
    }

    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    public int getFirstKnownMessageId() {
        for (int i = mAdapter.getItems().size() - 1; i >= 0; i--) {
            ChatItem item = mAdapter.getItems().get(i);
            if (item instanceof ChatMessage) {
                return ((ChatMessage) item).getId();
            }
        }
        return 0;
    }

    public int getMinId() {
        int minId = 0;
        for (ChatItem item : mAdapter.getItems()) {
            if (item instanceof ChatMessage) {
                if (minId > ((ChatMessage) item).getId()) {
                    minId = ((ChatMessage) item).getId();
                }
            }
        }
        return minId;
    }

    public ChatItem get(int index) {
        return mAdapter.getItems().get(index);
    }

    public void setMessageError(String tempId) {
        int index = findMessageIndex(0, tempId, mAdapter.getItems());
        if (index != -1) {
            ChatItem item = mAdapter.getItems().get(index);
            if (item instanceof ChatMessage) {
                ((ChatMessage) item).setPending(false);
                ((ChatMessage) item).setHasError(true);
            }
            mAdapter.notifyItemChanged(index);
        }
    }

    private int findMessageIndex(ChatMessage message, List<ChatItem> items) {
        return findMessageIndex(message.getId(), message.getTempId(), items);
    }

    private int findMessageIndex(int id, String tempId, List<ChatItem> items) {
        for (ChatItem item : items) {
            if (item instanceof ChatMessage) {
                if (((ChatMessage) item).getId() == id) {
                    return items.indexOf(item);
                }
            }
        }
        if (!ChatMessage.DEFAULT_TEMP_ID.equals(tempId)) {
            for (ChatItem item : items) {
                if (item instanceof ChatMessage) {
                    if (((ChatMessage) item).getTempId().equals(tempId)) {
                        return items.indexOf(item);
                    }
                }
            }
        }
        return -1;
    }

    private void addMessageFrontWithTyping(ChatMessage message, List<ChatItem> items) {
        if (items.size() < 2) {
            addChatDateFront(message.getDate(), true);
        } else {
            Date lastDate = getLastDate(items, 1);

            if (hasDayDifference(message.getDate(), lastDate)) {
                addChatDateFront(message.getDate(), false);
            }
        }
    }

    private void addMessageFrontNoTyping(ChatMessage message, List<ChatItem> items) {
        if (items.isEmpty()) {
            addChatDateFront(message.getDate(), false);
        } else {
            Date lastDate = getLastDate(items, 0);

            if (hasDayDifference(message.getDate(), lastDate)) {
                addChatDateFront(message.getDate(), false);
            }
        }
    }

    private Date getLastDate(List<ChatItem> items, int index) {
        ChatItem item = items.get(index);
        Date lastDate;
        if (item instanceof ChatDate) {
            lastDate = ((ChatDate) item).getDate();
        } else if (item instanceof ChatMessage) {
            lastDate = ((ChatMessage) item).getDate();
        } else {
            lastDate = new Date(System.currentTimeMillis());
        }
        return lastDate;
    }

    private boolean hasTypingItem(List<ChatItem> items) {
        return !items.isEmpty() && items.get(0) instanceof ChatTypingItem;
    }

    private void addChatDateFront(Date date, boolean hasTypingItem) {
        addChatDate(date, hasTypingItem ? 1 : 0);
    }

    private void addChatDateEnd(Date date) {
        addChatDate(date, mAdapter.getItems().size());
    }

    private void addChatDate(Date date, int position) {
        try {
            mAdapter.addItem(new ChatDate(mFormat.parse(mFormat.format(date))), position);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private boolean hasDayDifference(Date compare, Date to) {
        mCalendarCompare.setTimeInMillis(compare.getTime());
        mCalendarTo.setTimeInMillis(to.getTime());

        return (mCalendarCompare.get(Calendar.DAY_OF_YEAR) != mCalendarTo.get(Calendar.DAY_OF_YEAR)) ||
                (mCalendarCompare.get(Calendar.YEAR) != mCalendarCompare.get(Calendar.YEAR));
    }
}
