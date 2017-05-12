package com.gentledevs.chatmodule;

import com.gentledevs.chatmodule.model.ChatMessage;

/**
 * Created by Yura Stetsyc on 4/18/17.
 */

public interface ForwardListener {
    void onForward(ChatMessage message);

    void onPublish(ChatMessage message);

    void onEdit(ChatMessage message);
}
