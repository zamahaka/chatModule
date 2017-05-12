package com.gentledevs.chatmodule.model;

/**
 * Created by Yura Stetsyc on 4/18/17.
 */

public class ChatTypingItem implements ChatItem {
    @Override
    public Type getType() {
        return Type.TYPING;
    }
}
