package me.jessyan.retrofiturlmanager;

import okhttp3.HttpUrl;

/**
 * Created by jess on 17/07/2017 18:23
 * Contact with jess.yan.effort@gmail.com
 */

public class DefaultUrlParser implements UrlParser {
    @Override
    public HttpUrl parseUrl(String domainUrl, HttpUrl httpUrl) {
        return httpUrl;
    }
}
