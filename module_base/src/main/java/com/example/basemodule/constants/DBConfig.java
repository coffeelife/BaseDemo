package com.example.basemodule.constants;

import com.example.basemodule.R;
import com.example.basemodule.base.BaseApplication;

/**
 * Describe：数据库配置文件
 * @author gm
 */

public interface DBConfig {
    String DB_NAME = BaseApplication.getApplication().getString(R.string.app_name) + ".db";
}
