package me.jessyan.retrofiturlmanager;

import okhttp3.HttpUrl;

/**
 *
 * Created by yu on 2017/7/24.
 */
public class DomainHolder {
    private String urlStr;
    private HttpUrl domain;

    DomainHolder(String urlStr) {
        this.urlStr = urlStr;
        this.domain = Utils.checkUrl(urlStr);
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public HttpUrl getDomain() {
        return domain;
    }

    public void setDomain(HttpUrl domain) {
        this.domain = domain;
    }
}
