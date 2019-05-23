package com.example.basemodule.base.bean;

import java.io.Serializable;

/**
 * Created by mguo on 2018/7/6.
 */

public class UserBean implements Serializable {
    public String loginName;
    public String name;
    public String loginPw;
    public String bizId;
    public String userId;
    public String headurl;
    public String userName;
    public String qcodeUrl;
    public String phone;
    public boolean isFingerSetting;//是否弹出指纹开启框
    public boolean isFingerSupported;//是否支持指纹
    public boolean isLogin;//是否登录
    public String address;
    public String erpCode;
    public String businessId;
}
