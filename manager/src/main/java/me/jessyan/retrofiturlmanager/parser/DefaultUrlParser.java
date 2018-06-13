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
 * 默认解析器, 可根据自定义策略选择不同的解析器
 *
 * @see UrlParser
 * Created by JessYan on 17/07/2017 18:23
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class DefaultUrlParser implements UrlParser {

    private UrlParser mDomainUrlParser;
    private volatile UrlParser mAdvancedUrlParser;
    private RetrofitUrlManager mRetrofitUrlManager;

    @Override
    public void init(RetrofitUrlManager retrofitUrlManager) {
        this.mRetrofitUrlManager = retrofitUrlManager;
        this.mDomainUrlParser = new DomainUrlParser();
    }

    @Override
    public HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl url) {
        if (null == domainUrl) return url;

        //如果是高级模式则使用高级解析器
        if (mRetrofitUrlManager.isAdvancedModel()) {
            if (mAdvancedUrlParser == null) {
                synchronized (this) {
                    if (mAdvancedUrlParser == null) {
                        mAdvancedUrlParser = new AdvancedUrlParser();
                    }
                }
            }
            return mAdvancedUrlParser.parseUrl(domainUrl, url);
        }
        return mDomainUrlParser.parseUrl(domainUrl, url);
    }
}
