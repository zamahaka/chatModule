package com.gentledevs.chatmodule;

/**
 * Created by Yura Stetsyc on 4/24/17.
 */

public interface ImageErrorListener {

    void onSend(int messageId, String tempId, String uri);

    void onCancel(int messageId, String tempId);

}
