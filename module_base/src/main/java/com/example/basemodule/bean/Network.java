package com.example.basemodule.bean;

/**
 * Created by mguo on 2018/6/6.
 */

/**
 * Created by ：Luo
 * <p>  Network数据类
 * Created Time ：2017/8/30
 */

public class Network {

    private boolean wifi;
    private boolean mobile;
    private boolean connected;

    public Network() {
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

}
