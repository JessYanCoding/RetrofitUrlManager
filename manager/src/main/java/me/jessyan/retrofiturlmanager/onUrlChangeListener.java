package me.jessyan.retrofiturlmanager;

import okhttp3.HttpUrl;

/**
 * Created by jess on 20/07/2017 14:18
 * Contact with jess.yan.effort@gmail.com
 */

public interface onUrlChangeListener {
    /**
     * 当 Url 的 BaseUrl 被改变时回调
     * 调用时间是在接口请求服务器之前
     *
     * @param newUrl
     * @param oldUrl
     */
    void onUrlChange(HttpUrl newUrl, HttpUrl oldUrl);
}
