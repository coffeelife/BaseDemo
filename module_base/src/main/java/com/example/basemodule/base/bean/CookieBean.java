package com.example.basemodule.base.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by mguo on 2018/7/6.
 */

public class CookieBean implements Serializable {
    public String xsfrToken;
    public String accessToken;
    public String sessionToken;

    public String getXsfrToken() {
        return xsfrToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getCookie() {
        StringBuffer sb = new StringBuffer("");

        if (!TextUtils.isEmpty(xsfrToken)) {
            sb.append("XSRF-TOKEN=").append(xsfrToken).append(";");
        }
        if (!TextUtils.isEmpty(accessToken)) {
            sb.append("access_token=").append(accessToken).append(";");
        }
        if (!TextUtils.isEmpty(sessionToken)) {
            sb.append("refresh_token=").append(sessionToken).append(";");
        }
        return sb.toString();
    }

}
