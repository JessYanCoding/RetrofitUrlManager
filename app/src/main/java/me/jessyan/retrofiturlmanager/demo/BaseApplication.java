/**
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
package me.jessyan.retrofiturlmanager.demo;

import android.app.Application;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_DOUBAN_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_GANK_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_GITHUB_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.DOUBAN_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GANK_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GITHUB_DOMAIN_NAME;

/**
 * ================================================
 * Created by JessYan on 19/07/2017 18:45
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //将每个 BaseUrl 进行初始化,运行时可以随时改变 DOMAIN_NAME 对应的值,从而达到改变 BaseUrl 的效果
        RetrofitUrlManager.getInstance().putDomain(GITHUB_DOMAIN_NAME, APP_GITHUB_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(GANK_DOMAIN_NAME, APP_GANK_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(DOUBAN_DOMAIN_NAME, APP_DOUBAN_DOMAIN);
    }

}
