package com.example.basemodule.utils;

import com.example.basemodule.bean.Event;

import org.greenrobot.eventbus.EventBus;

/**
 * Describe：EventBusUtils
 * @author gm
 */

public class EventBusUtils {

    /**
     * 注册事件
     */
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    /**
     * 解除事件
     */
    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    /**
     * 发送普通事件
     */
    public static void sendEvent(Event event) {
        EventBus.getDefault().post(event);
    }
    //...
    /**
     * 发送普通事件
     */
    public static void sendEventSticky(Event event) {
        EventBus.getDefault().postSticky(event);
    }
}
