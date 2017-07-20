package me.jessyan.retrofiturlmanager.demo;

import android.app.Application;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_DOUBAN_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_GANK_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_GETHUB_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.DOUBAN_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GANK_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GITHUB_DOMAIN_NAME;

/**
 * Created by jess on 19/07/2017 18:45
 * Contact with jess.yan.effort@gmail.com
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //将每个 BaseUrl 进行初始化,运行时可以随时改变 DOMAIN_NAME 对应的值,从而达到改变 BaseUrl 的效果
        RetrofitUrlManager.getInstance().putDomain(GITHUB_DOMAIN_NAME, APP_GETHUB_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(GANK_DOMAIN_NAME, APP_GANK_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(DOUBAN_DOMAIN_NAME, APP_DOUBAN_DOMAIN);
    }

}
