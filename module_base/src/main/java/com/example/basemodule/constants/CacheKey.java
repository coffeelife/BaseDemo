package com.example.basemodule.constants;

import com.example.basemodule.base.BaseApplication;

/**
 * Describe：缓存key
 * @author gm
 */

public interface CacheKey {

    /**
     * 保存用户信息
     */
    String USER_INFO = BaseApplication.getApplication().getPackageName() + ".UserInfo";

    /**
     * 保存登录状态
     */
    String USER_LOGGED = BaseApplication.getApplication().getPackageName() + ".UserLogged";
}
