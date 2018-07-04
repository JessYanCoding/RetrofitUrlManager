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
package me.jessyan.retrofiturlmanager.demo.api;

import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GITHUB_DOMAIN_NAME;

/**
 * ================================================
 * Created by JessYan on 19/07/2017 11:49
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public interface OneApiService {
    /**
     * 如果不需要多个 BaseUrl, 继续使用初始化时传入 Retrofit 中的默认 BaseUrl, 就不要加上 DOMAIN_NAME_HEADER 这个 Header
     */
    @Headers({DOMAIN_NAME_HEADER + GITHUB_DOMAIN_NAME})
    /**
     * 可以通过在注解里给全路径达到使用不同的 BaseUrl, 但是这样无法在 App 运行时动态切换 BaseUrl
     */
    @GET("/users")
    Observable<ResponseBody> getUsers(@Query("since") int lastIdQueried, @Query("per_page") int perPage);

    /**
     * 切换 Url 的优先级: DomainHeader 中对应 BaseUrl 的将覆盖全局的 BaseUrl
     * 这里不配置 DomainHeader, 将只受到设置的全局 BaseUrl 的影响, 没有全局 BaseUrl 将请求原始的 BaseUrl
     * 当你项目中只有一个 BaseUrl, 但需要动态切换 BaseUrl 时, 全局 BaseUrl 显得非常方便
     * 当你设置了全局 BaseUrl, 整个项目中没有加入 DomainHeader 的网络请求地址都将被替换成这个全局 BaseUrl
     * 如果在设置了全局 BaseUrl 的情况下你有某些网络请求地址不需要被替换成这个全局 BaseUrl, 请在 Url 地址尾部加入 {@link RetrofitUrlManager#IDENTIFICATION_IGNORE} 这个标识符
     * 当某些下载接口需要请求固定的全路径地址或者 Glide 使用 Okhttp 请求某一个固定图片地址, {@link RetrofitUrlManager#IDENTIFICATION_IGNORE} 就可以派上用场
     */
    @GET("/BaseUrl-Solution")
    Observable<ResponseBody> requestDefault();
}
