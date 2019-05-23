package com.example.basemodule.bean;

import com.example.basemodule.base.bean.BaseBean;

import lombok.Getter;
import lombok.Setter;

/**
 * Describe：水平选项卡Title
 * @author gm
 */
@Getter
@Setter
public class HorizontalTabTitle<T> extends BaseBean {
    private String title;
    private T data;

    public HorizontalTabTitle(String title) {
        this.title = title;
    }

    public HorizontalTabTitle(String title, T data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
