package com.example.basemodule.bean;

import lombok.Getter;
import lombok.Setter;


/**
 * Describe：EventBus事件类
 * @author gm
 */

@Getter
@Setter
public class Event<T> {

    private String action;
    private T data;

    public Event(String action) {
        this.action = action;
    }

    public Event(String action, T data) {
        this.action = action;
        this.data = data;
    }


}
