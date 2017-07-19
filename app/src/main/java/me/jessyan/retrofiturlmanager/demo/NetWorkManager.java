package me.jessyan.retrofiturlmanager.demo;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import me.jessyan.retrofiturlmanager.demo.api.OneApiService;
import me.jessyan.retrofiturlmanager.demo.api.ThreeApiService;
import me.jessyan.retrofiturlmanager.demo.api.TwoApiService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_DEFAULT_DOMAIN;

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
    private Handler mHandler;

    private static class NetWorkManagerHolder {
        private static final NetWorkManager INSTANCE = new NetWorkManager();
    }

    public static final NetWorkManager getInstance() {
        return NetWorkManagerHolder.INSTANCE;
    }

    private NetWorkManager() {
        mHandler = new Handler(Looper.getMainLooper());
        this.mOkHttpClient = RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder()) //RetrofitUrlManager 初始化
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(final Chain chain) throws IOException {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BaseApplication.getApplication(), chain.request().url().toString(), Toast.LENGTH_SHORT);
                            }
                        });
                        return chain.proceed(chain.request());
                    }
                })
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
