package me.jessyan.retrofiturlmanager;

import okhttp3.HttpUrl;

/**
 * Created by jess on 20/07/2017 14:18
 * Contact with jess.yan.effort@gmail.com
 */

public interface onUrlChangeListener {
    void onUrlChange(HttpUrl newUrl, HttpUrl oldUrl);
}
