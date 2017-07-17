package me.jessyan.retrofiturlmanager;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Url解析器
 * <p>
 * Created by jess on 17/07/2017 17:44
 * Contact with jess.yan.effort@gmail.com
 */

public interface UrlParser {
    /**
     * 将 {@link RetrofitUrlManager#mDomainNameHub} 中映射的 Url 解析成完整的{@link HttpUrl}
     * 用来替换 @{@link Request#url} 达到动态切换 Url
     *
     * @param domainUrl
     * @param httpUrl
     * @return
     */
    HttpUrl parseUrl(String domainUrl, HttpUrl httpUrl);
}
