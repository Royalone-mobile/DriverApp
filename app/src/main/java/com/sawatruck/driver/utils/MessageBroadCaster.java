package com.sawatruck.driver.utils;

/**
 * Created by royal on 8/19/2017.
 */

import java.util.ArrayList;
import java.util.List;


public class MessageBroadCaster {

    public interface MessageListener {
        void onMessage(int messageID, Object message);
    }

    private static MessageBroadCaster _instance = new MessageBroadCaster();

    public static MessageBroadCaster getInstance() {
        return _instance;
    }

    private final List<MessageListener> _listenerList = new ArrayList<MessageListener>();

    public boolean registerListener(final MessageListener listener) {
        if (!_listenerList.contains(listener)) {
            return _listenerList.add(listener);
        }

        return false;
    }

    public boolean unregisterListener(final MessageListener listener) {
        return _listenerList.remove(listener);
    }

    public void postMessage(final int messageID, final Object message) {
        for (MessageListener ml : _listenerList) {
            ml.onMessage(messageID, message);
        }
    }
}
