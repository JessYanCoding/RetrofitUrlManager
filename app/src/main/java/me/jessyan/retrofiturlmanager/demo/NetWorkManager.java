package me.jessyan.retrofiturlmanager.demo;

import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import me.jessyan.retrofiturlmanager.demo.api.OneApiService;
import me.jessyan.retrofiturlmanager.demo.api.ThreeApiService;
import me.jessyan.retrofiturlmanager.demo.api.TwoApiService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_DEFAULT_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_DOUBAN_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_GANK_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_GETHUB_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.DOUBAN_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GANK_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GITHUB_DOMAIN_NAME;

/**
 * Created by jess on 18/07/2017 17:03
 * Contact with jess.yan.effort@gmail.com
 */
public class NetWorkManager {
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;
    private OneApiService mOneApiService;
    private TwoApiService mTwoApiService;
    private ThreeApiService mThreeApiService;

    private static class NetWorkManagerHolder {
        private static final NetWorkManager INSTANCE = new NetWorkManager();
    }

    public static final NetWorkManager getInstance() {
        return NetWorkManagerHolder.INSTANCE;
    }

    private NetWorkManager() {
        this.mOkHttpClient = RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder()) //RetrofitUrlManager 初始化
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

        this.mRetrofit = new Retrofit.Builder()
                .baseUrl(APP_DEFAULT_DOMAIN)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用rxjava
                .addConverterFactory(GsonConverterFactory.create())//使用Gson
                .client(mOkHttpClient)
                .build();

        this.mOneApiService = mRetrofit.create(OneApiService.class);
        this.mTwoApiService = mRetrofit.create(TwoApiService.class);
        this.mThreeApiService = mRetrofit.create(ThreeApiService.class);

        //将每个 BaseUrl 进行初始化,运行时可以随时改变 DOMAIN_NAME 对应的值,从而达到改变 BaseUrl 的效果
        RetrofitUrlManager.getInstance().putDomain(GITHUB_DOMAIN_NAME, APP_GETHUB_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(GANK_DOMAIN_NAME, APP_GANK_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(DOUBAN_DOMAIN_NAME, APP_DOUBAN_DOMAIN);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public OneApiService getOneApiService() {
        return mOneApiService;
    }

    public TwoApiService getTwoApiService() {
        return mTwoApiService;
    }

    public ThreeApiService getThreeApiService() {
        return mThreeApiService;
    }

}
