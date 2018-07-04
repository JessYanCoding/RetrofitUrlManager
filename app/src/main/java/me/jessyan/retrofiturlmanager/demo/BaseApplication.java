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
package me.jessyan.retrofiturlmanager.demo;

import android.app.Application;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import me.jessyan.retrofiturlmanager.parser.AdvancedUrlParser;
import me.jessyan.retrofiturlmanager.parser.DomainUrlParser;
import me.jessyan.retrofiturlmanager.parser.SuperUrlParser;

import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_DOUBAN_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_GANK_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.APP_GITHUB_DOMAIN;
import static me.jessyan.retrofiturlmanager.demo.api.Api.DOUBAN_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GANK_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GITHUB_DOMAIN_NAME;

/**
 * ================================================
 * RetrofitUrlManager 以简洁的 Api, 让 Retrofit 不仅支持多 BaseUrl
 * 还可以在 App 运行时动态切换任意 BaseUrl, 在多 BaseUrl 场景下也不会影响到其他不需要切换的 BaseUrl
 * <p>
 * 想要更深入的使用本框架必须要了解2个术语 pathSegments 和 PathSize
 * "https://www.github.com/wiki/part?name=jess" 其中的 "/wiki" 和 "/part" 就是 pathSegment, PathSize 就是 pathSegment 的 Size
 * 这个 Url 的 PathSize 就是 2, 可以粗略理解为域名后面跟了几个 "/" PathSize 就是几
 * <p>
 * 本框架分为三种模式, 普通模式 (默认)、高级模式 (需要手动开启)、超级模式 (需要手动开启)
 * <p>
 * 普通模式:
 * 普通模式只能替换域名, 比如使用 "https:www.google.com" 作为 Retrofit 的 BaseUrl 可以被替换
 * 但是以 "https:www.google.com/api" 作为 BaseUrl 还是只能替换其中的域名 "https:www.google.com"
 * 详细替换规则可以查看 {@link DomainUrlParser}
 * <p>
 * 高级模式:
 * 高级模式只能替换 {@link RetrofitUrlManager#startAdvancedModel(String)} 中传入的 BaseUrl, 但可以替换拥有多个 pathSegments 的 BaseUrl
 * 如 "https:www.google.com/api", 需要手动开启高级模式 {@link RetrofitUrlManager#startAdvancedModel(String)}
 * 详细替换规则可以查看 {@link AdvancedUrlParser}
 * <p>
 * 超级模式:
 * 详细替换规则可以查看 {@link SuperUrlParser}
 * 超级模式属于高级模式的加强版, 优先级高于高级模式, 在高级模式中, 需要传入一个 BaseUrl (您传入 Retrofit 的 BaseUrl) 作为被替换的基准
 * 如这个传入的 BaseUrl 为 "https://www.github.com/wiki/part" (PathSize = 2), 那框架会将所有需要被替换的 Url 中的 域名 以及 域名 后面的前两个 pathSegments
 * 使用您传入 {@link RetrofitUrlManager#putDomain(String, String)} 方法的 Url 替换掉
 * 但如果突然有一小部分的 Url 只想将 "https://www.github.com/wiki" (PathSize = 1) 替换掉, 后面的 pathSegment '/part' 想被保留下来
 * 这时项目中就出现了多个 PathSize 不同的需要被替换的 BaseUrl
 * <p>
 * 使用高级模式实现这种需求略显麻烦, 所以我创建了超级模式, 让每一个 Url 都可以随意指定不同的 BaseUrl (PathSize 自己定) 作为被替换的基准
 * 使 RetrofitUrlManager 可以从容应对各种复杂的需求
 * <p>
 * 超级模式也需要手动开启, 但与高级模式不同的是, 开启超级模式并不需要调用 API, 只需要在 Url 中加入 {@link RetrofitUrlManager#IDENTIFICATION_PATH_SIZE} + PathSize
 * <p>
 * 至此三种模式替换 BaseUrl 的自由程度 (可扩展性) 排名, 从小到大依次是:
 * 普通模式 (只能替换域名) < 高级模式 (只能替换 {@link RetrofitUrlManager#startAdvancedModel(String)} 中传入的 BaseUrl) < 超级模式 (每个 Url 都可以随意指定可被替换的 BaseUrl, pathSize 随意变换)
 * <p>
 * 三种模式在使用上的复杂程度排名, 从小到大依次是:
 * 普通模式 (无需做过多配置) < 高级模式 (App 初始化时调用一次 {@link RetrofitUrlManager#startAdvancedModel(String)} 即可) < 超级模式 (每个需要被替换 BaseUrl 的 Url 中都需要加入 {@link RetrofitUrlManager#IDENTIFICATION_PATH_SIZE} + PathSize)
 * <p>
 * 由此可见，自由度越强, 操作也越复杂, 所以可以根据自己的需求选择不同的模式, 并且也可以在需求变化时随意升级或降级这三种模式
 * <p>
 * Created by JessYan on 19/07/2017 18:45
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitUrlManager.getInstance().setDebug(true);
        //将每个 BaseUrl 进行初始化,运行时可以随时改变 DOMAIN_NAME 对应的值,从而达到切换 BaseUrl 的效果
        RetrofitUrlManager.getInstance().putDomain(GITHUB_DOMAIN_NAME, APP_GITHUB_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(GANK_DOMAIN_NAME, APP_GANK_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(DOUBAN_DOMAIN_NAME, APP_DOUBAN_DOMAIN);
    }
}
