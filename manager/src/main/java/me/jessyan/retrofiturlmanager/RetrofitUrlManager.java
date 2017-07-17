package me.jessyan.retrofiturlmanager;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jess on 17/07/2017 14:29
 * Contact with jess.yan.effort@gmail.com
 */

public class RetrofitUrlManager {
    public static final boolean DEPENDENCY_OKHTTP;
    private static final String DOMAIN_NAME = "Domain-Name";
    public static final String DOMAIN_NAME_HEADER = DOMAIN_NAME + ": ";

    private boolean isRun = true; //默认开始运行,可以随时停止运行,比如你在 App 启动后已经不需要在动态切换 baseurl 了
    private final Map<String, String> mDomainNameHub = new HashMap<>();
    private final Interceptor mInterceptor;
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
                if (!isRun()) // 可以在 App 运行时,随时通过 setRun(false) 来结束本管理器的运行,
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
     * 将 {@link okhttp3.OkHttpClient.Builder} 传入,配置一些本管理器需要的参数
     *
     * @param builder
     * @return
     */
    public OkHttpClient.Builder with(OkHttpClient.Builder builder) {
        return builder
                .addInterceptor(mInterceptor);
    }

    /**
     * @param request
     * @return
     */
    public Request processRequest(Request request) {
        String domainName = obtainDomainNameFromHeaders(request);
        if (TextUtils.isEmpty(domainName))// headers中没有 Domain-Name 就表示不需要处理
            return request;

        String domainUrl = fetchDomain(domainName);
        if (TextUtils.isEmpty(domainUrl))// DomainNameHub 中没有给 DomainName 映射对应的 DomainUrl 则不处理
            return request.newBuilder()
                    .removeHeader(DOMAIN_NAME)
                    .build();

        return request.newBuilder()
                .removeHeader(DOMAIN_NAME)
                .url(mUrlParser.parseUrl(domainUrl, request.url()))
                .build();

    }

    public boolean isRun() {
        return this.isRun;
    }

    public void setRun(boolean run) {
        this.isRun = run;
    }

    public void putDomain(String domainName, String domainUrl) {
        mDomainNameHub.put(domainName, domainUrl);
    }

    public String fetchDomain(String domainName) {
        return mDomainNameHub.get(domainName);
    }

    public void removeDomain(String domainName) {
        mDomainNameHub.remove(domainName);
    }

    public void clearAllDomain() {
        mDomainNameHub.clear();
    }

    public boolean haveDomain(String domainName) {
        return mDomainNameHub.containsKey(domainName);
    }

    public int domainSize() {
        return mDomainNameHub.size();
    }

    /**
     * 可自行实现 {@link UrlParser} 动态切换 Url 解析策略
     *
     * @param parser
     */
    public void setUrlParser(UrlParser parser) {
        this.mUrlParser = parser;
    }


    private String obtainDomainNameFromHeaders(Request request) {
        List<String> headers = request.headers(DOMAIN_NAME);
        if (headers == null || headers.size() == 0)
            return null;
        if (headers.size() > 1)
            throw new IllegalArgumentException("Only one Domain-Name in the headers");
        return request.header(DOMAIN_NAME);
    }

}
