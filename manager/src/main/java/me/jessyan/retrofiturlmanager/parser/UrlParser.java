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
package me.jessyan.retrofiturlmanager.parser;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * ================================================
 * Url解析器
 * <p>
 * Created by JessYan on 17/07/2017 17:44
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */

public interface UrlParser {

    /**
     * 这里可以做一些初始化操作
     *
     * @param retrofitUrlManager {@link RetrofitUrlManager}
     */
    void init(RetrofitUrlManager retrofitUrlManager);

    /**
     * 将 {@link RetrofitUrlManager#mDomainNameHub} 中映射的 URL 解析成完整的{@link HttpUrl}
     * 用来替换 @{@link Request#url} 达到动态切换 URL
     *
     * @param domainUrl 用于替换的 URL 地址
     * @param url       旧 URL 地址
     * @return
     */
    HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl url);
}
