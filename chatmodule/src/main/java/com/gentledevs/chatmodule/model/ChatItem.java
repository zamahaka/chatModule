package com.gentledevs.chatmodule.model;

/**
 * Created by Yura Stetsyc on 4/18/17.
 */

public interface ChatItem {

    Type getType();

    enum Type {
        SELF, OTHER, DATE, TYPING
    }
}
