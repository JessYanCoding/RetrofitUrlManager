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

/**
 * ================================================
 * 高级解析器, 当 BaseUrl 中有除了域名以外的其他 Path 时, 可使用此解析器
 * <p>
 * 比如:
 * 1.
 * 旧 URL 地址为 https://www.github.com/wiki/part, 其中 BaseUrl 为 https://www.github.com/wiki
 * 您调用 {@link RetrofitUrlManager#putDomain(String, String)}方法传入的 URL 地址是 https://www.google.com/api
 * 经过本解析器解析后生成的新 URL 地址为 http://www.google.com/api/part
 * <p>
 * 2.
 * 旧 URL 地址为 https://www.github.com/wiki/part, 其中 BaseUrl 为 https://www.github.com/wiki
 * 您调用 {@link RetrofitUrlManager#putDomain(String, String)}方法传入的 URL 地址是 https://www.google.com
 * 经过本解析器解析后生成的新 URL 地址为 http://www.google.com/part
 * <p>
 * 3.
 * 旧 URL 地址为 https://www.github.com/wiki/part, 其中 BaseUrl 为 https://www.github.com
 * 您调用 {@link RetrofitUrlManager#putDomain(String, String)}方法传入的 URL 地址是 https://www.google.com/api
 * 经过本解析器解析后生成的新 URL 地址为 http://www.google.com/api/wiki/part
 * <p>
 * 解析器会将 BaseUrl 全部替换成您传入的 Url 地址
 *
 * @see UrlParser
 * Created by JessYan on 09/06/2018 16:00
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class AdvancedUrlParser implements UrlParser {
    @Override
    public HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl url) {

        if (null == domainUrl) return url;

        return url.newBuilder()
                .scheme(domainUrl.scheme())
                .host(domainUrl.host())
                .port(domainUrl.port())
                .build();
    }
}
