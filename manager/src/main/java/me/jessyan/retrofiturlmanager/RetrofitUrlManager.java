/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.jessyan.retrofiturlmanager;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jessyan.retrofiturlmanager.parser.DefaultUrlParser;
import me.jessyan.retrofiturlmanager.parser.UrlParser;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * RetrofitUrlManager 以简洁的 Api, 让 Retrofit 不仅支持多 BaseUrl
 * 还可以在 App 运行时动态切换任意 BaseUrl, 在多 BaseUrl 场景下也不会影响到其他不需要切换的 BaseUrl
 * 注意: 本管理只能替换域名, 比如使用 "https:www.google.com" 作为 BaseUrl 可以被替换, 但是 "https:www.google.com/api" 因为后面加入了 "/api" 则不能被替换
 * <p>
 * Created by JessYan on 17/07/2017 14:29
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class RetrofitUrlManager {
    private static final String TAG = "RetrofitUrlManager";
    private static final boolean DEPENDENCY_OKHTTP;
    private static final String DOMAIN_NAME = "Domain-Name";
    private static final String GLOBAL_DOMAIN_NAME = "me.jessyan.retrofiturlmanager.globalDomainName";
    public static final String DOMAIN_NAME_HEADER = DOMAIN_NAME + ": ";
    public static final String IDENTIFICATION_IGNORE = "#url_ignore";//如果在 Url 地址中加入此标识符, 管理器将不会对此 Url 进行任何切换 BaseUrl 的操作

    private boolean isRun = true; //默认开始运行, 可以随时停止运行, 比如你在 App 启动后已经不需要再动态切换 BaseUrl 了
    private boolean debug = false;//在 Debug  模式下可以打印日志
    private final Map<String, HttpUrl> mDomainNameHub = new HashMap<>();
    private final Interceptor mInterceptor;
    private final List<onUrlChangeListener> mListeners = new ArrayList<>();
    private UrlParser mUrlParser;

    static {
        boolean hasDependency;
        try {
            Class.forName("okhttp3.OkHttpClient");
            hasDependency = true;
        } catch (ClassNotFoundException e) {
            hasDependency = false;
        }
        DEPENDENCY_OKHTTP = hasDependency;
    }


    private RetrofitUrlManager() {
        if (!DEPENDENCY_OKHTTP) { //使用本管理器必须依赖 Okhttp
            throw new IllegalStateException("Must be dependency Okhttp");
        }
        setUrlParser(new DefaultUrlParser());
        this.mInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (!isRun()) // 可以在 App 运行时, 随时通过 setRun(false) 来结束本管理器的运行
                    return chain.proceed(chain.request());
                return chain.proceed(processRequest(chain.request()));
            }
        };
    }

    private static class RetrofitUrlManagerHolder {
        private static final RetrofitUrlManager INSTANCE = new RetrofitUrlManager();
    }

    public static final RetrofitUrlManager getInstance() {
        return RetrofitUrlManagerHolder.INSTANCE;
    }

    /**
     * 将 {@link OkHttpClient.Builder} 传入, 配置一些本管理器需要的参数
     *
     * @param builder {@link OkHttpClient.Builder}
     * @return {@link OkHttpClient.Builder}
     */
    public OkHttpClient.Builder with(OkHttpClient.Builder builder) {
        return builder
                .addInterceptor(mInterceptor);
    }

    /**
     * 对 {@link Request} 进行一些必要的加工, 执行切换 BaseUrl 的相关逻辑
     *
     * @param request {@link Request}
     * @return {@link Request}
     */
    public Request processRequest(Request request) {

        Request.Builder newBuilder = request.newBuilder();

        String url = request.url().toString();
        //如果 Url 地址中包含 IDENTIFICATION_IGNORE 标识符, 管理器将不会对此 Url 进行任何切换 BaseUrl 的操作
        if (url.contains(IDENTIFICATION_IGNORE)) {
            return pruneIdentification(newBuilder, url);
        }

        String domainName = obtainDomainNameFromHeaders(request);

        HttpUrl httpUrl;

        Object[] listeners = listenersToArray();

        // 如果有 header,获取 header 中 domainName 所映射的 url,若没有,则检查全局的 BaseUrl,未找到则为null
        if (!TextUtils.isEmpty(domainName)) {
            notifyListener(request, domainName, listeners);
            httpUrl = fetchDomain(domainName);
            newBuilder.removeHeader(DOMAIN_NAME);
        } else {
            notifyListener(request, GLOBAL_DOMAIN_NAME, listeners);
            httpUrl = getGlobalDomain();
        }

        if (null != httpUrl) {
            HttpUrl newUrl = mUrlParser.parseUrl(httpUrl, request.url());
            if (debug)
                Log.d(RetrofitUrlManager.TAG, "The new url is { " + newUrl.toString() + " }, old url is { " + request.url().toString() + " }");

            if (listeners != null) {
                for (int i = 0; i < listeners.length; i++) {
                    ((onUrlChangeListener) listeners[i]).onUrlChanged(newUrl, request.url()); // 通知监听器此 Url 的 BaseUrl 已被切换
                }
            }

            return newBuilder
                    .url(newUrl)
                    .build();
        }

        return newBuilder.build();

    }

    /**
     * 将 {@code IDENTIFICATION_IGNORE} 从 Url 地址中修剪掉
     *
     * @param newBuilder {@link Request.Builder}
     * @param url        原始 Url 地址
     * @return 被修剪过 Url 地址的 {@link Request}
     */
    private Request pruneIdentification(Request.Builder newBuilder, String url) {
        String[] split = url.split(IDENTIFICATION_IGNORE);
        StringBuffer buffer = new StringBuffer();
        for (String s : split) {
            buffer.append(s);
        }
        return newBuilder
                .url(buffer.toString())
                .build();
    }

    /**
     * 通知所有监听器的 {@link onUrlChangeListener#onUrlChangeBefore(HttpUrl, String)} 方法
     *
     * @param request    {@link Request}
     * @param domainName 域名的别名
     * @param listeners  监听器列表
     */
    private void notifyListener(Request request, String domainName, Object[] listeners) {
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ((onUrlChangeListener) listeners[i]).onUrlChangeBefore(request.url(), domainName);
            }
        }
    }

    /**
     * 管理器是否在运行
     *
     * @return {@code true} 为正在运行, 反之亦然
     */
    public boolean isRun() {
        return this.isRun;
    }

    /**
     * 控制管理器是否运行, 在每个域名地址都已经确定, 不需要再动态更改时可设置为 {@code false}
     *
     * @param run {@code true} 为正在运行, 反之亦然
     */
    public void setRun(boolean run) {
        this.isRun = run;
    }

    /**
     * 开启 Debug 模式下可以打印日志
     *
     * @param debug true 开启 Debug 模式
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * 将 url 地址作为参数传入此方法, 并使用此方法返回的 url 地址进行网络请求, 则会使此 url 地址忽略掉本框架的所有更改效果
     * <p>
     * 使用场景:
     * 比如当你使用了 {@link #setGlobalDomain(String url)} 配置了全局 BaseUrl 后, 想请求一个与全局 BaseUrl
     * 不同的第三方服务商地址获取图片
     *
     * @param url url 路径
     * @return 处理后的 url 路径
     */
    public String setUrlNotChange(String url) {
        return url + IDENTIFICATION_IGNORE;
    }

    /**
     * 全局动态替换 BaseUrl, 优先级: Header中配置的 BaseUrl > 全局配置的 BaseUrl
     * 除了作为备用的 BaseUrl, 当你项目中只有一个 BaseUrl, 但需要动态切换
     * 这种方式不用在每个接口方法上加入 Header, 就能实现动态切换 BaseUrl
     *
     * @param url 全局 BaseUrl
     */
    public void setGlobalDomain(String url) {
        synchronized (mDomainNameHub) {
            mDomainNameHub.put(GLOBAL_DOMAIN_NAME, Utils.checkUrl(url));
        }
    }

    /**
     * 获取全局 BaseUrl
     */
    public HttpUrl getGlobalDomain() {
        return mDomainNameHub.get(GLOBAL_DOMAIN_NAME);
    }

    /**
     * 移除全局 BaseUrl
     */
    public void removeGlobalDomain() {
        synchronized (mDomainNameHub) {
            mDomainNameHub.remove(GLOBAL_DOMAIN_NAME);
        }
    }

    /**
     * 存放 Domain(BaseUrl) 的映射关系
     *
     * @param domainName
     * @param domainUrl
     */
    public void putDomain(String domainName, String domainUrl) {
        synchronized (mDomainNameHub) {
            mDomainNameHub.put(domainName, Utils.checkUrl(domainUrl));
        }
    }

    /**
     * 取出对应 {@code domainName} 的 Url(BaseUrl)
     *
     * @param domainName
     * @return
     */
    public HttpUrl fetchDomain(String domainName) {
        return mDomainNameHub.get(domainName);
    }

    /**
     * 移除某个 {@code domainName}
     *
     * @param domainName {@code domainName}
     */
    public void removeDomain(String domainName) {
        synchronized (mDomainNameHub) {
            mDomainNameHub.remove(domainName);
        }
    }

    /**
     * 清理所有 Domain(BaseUrl)
     */
    public void clearAllDomain() {
        mDomainNameHub.clear();
    }

    /**
     * 存放 Domain(BaseUrl) 的容器中是否存在这个 {@code domainName}
     *
     * @param domainName {@code domainName}
     * @return {@code true} 为存在, 反之亦然
     */
    public boolean haveDomain(String domainName) {
        return mDomainNameHub.containsKey(domainName);
    }

    /**
     * 存放 Domain(BaseUrl) 的容器, 当前的大小
     *
     * @return 容量大小
     */
    public int domainSize() {
        return mDomainNameHub.size();
    }

    /**
     * 可自行实现 {@link UrlParser} 动态切换 Url 解析策略
     *
     * @param parser {@link UrlParser}
     */
    public void setUrlParser(UrlParser parser) {
        this.mUrlParser = parser;
    }

    /**
     * 注册监听器(当 Url 的 BaseUrl 被切换时会被回调的监听器)
     *
     * @param listener 监听器列表
     */
    public void registerUrlChangeListener(onUrlChangeListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    /**
     * 注销监听器(当 Url 的 BaseUrl 被切换时会被回调的监听器)
     *
     * @param listener 监听器列表
     */
    public void unregisterUrlChangeListener(onUrlChangeListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    private Object[] listenersToArray() {
        Object[] listeners = null;
        synchronized (mListeners) {
            if (mListeners.size() > 0) {
                listeners = mListeners.toArray();
            }
        }
        return listeners;
    }


    /**
     * 从 {@link Request#header(String)} 中取出 DomainName
     *
     * @param request {@link Request}
     * @return DomainName
     */
    private String obtainDomainNameFromHeaders(Request request) {
        List<String> headers = request.headers(DOMAIN_NAME);
        if (headers == null || headers.size() == 0)
            return null;
        if (headers.size() > 1)
            throw new IllegalArgumentException("Only one Domain-Name in the headers");
        return request.header(DOMAIN_NAME);
    }

}
