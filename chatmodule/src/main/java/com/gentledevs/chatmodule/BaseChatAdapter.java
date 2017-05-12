package com.gentledevs.chatmodule;

import com.gentledevs.chatmodule.model.ChatItem;
import com.gentledevs.chatmodule.model.ChatMessage;

import java.util.List;

/**
 * Created by Yura Stetsyc on 4/19/17.
 */

public interface BaseChatAdapter {

    void addItem(ChatItem item);

    void addItem(ChatItem item, int position);

    void update(ChatMessage message, int index);

    void addAll(List<ChatItem> items);

    void removeItem(ChatItem item);

    void removeItem(int index);

    void clear();
}
