package me.jessyan.retrofiturlmanager.demo.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GITHUB_DOMAIN_NAME;

/**
 * Created by jess on 19/07/2017 11:49
 * Contact with jess.yan.effort@gmail.com
 */

public interface OneApiService {
    @Headers({DOMAIN_NAME_HEADER + GITHUB_DOMAIN_NAME}) //如果不需要多个 BaseUrl ,继续使用初始化时传入 Retrofit 中的默认 BaseUrl ,就不要加上 DOMAIN_NAME_HEADER 这个 Header
    @GET("/users") // 可以通过在注解里给全路径达到使用不同的 BaseUrl ,但是这样无法在 App 运行时动态切换 BaseUrl
    Observable<ResponseBody> getUsers(@Query("since") int lastIdQueried, @Query("per_page") int perPage);
}
