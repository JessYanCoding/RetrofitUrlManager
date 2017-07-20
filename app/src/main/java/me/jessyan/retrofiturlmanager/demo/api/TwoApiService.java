package me.jessyan.retrofiturlmanager.demo.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GANK_DOMAIN_NAME;

/**
 * Created by jess on 19/07/2017 11:50
 * Contact with jess.yan.effort@gmail.com
 */

public interface TwoApiService {
    @Headers({DOMAIN_NAME_HEADER + GANK_DOMAIN_NAME}) //如果不需要多个 BaseUrl ,继续使用初始化时传入 Retrofit 中的默认 BaseUrl ,就不要加上 DOMAIN_NAME_HEADER 这个 Header
    @GET("/api/data/Android/{size}/{page}") // 可以通过在注解里给全路径达到使用不同的 BaseUrl ,但是这样无法在 App 运行时动态切换 BaseUrl
    Observable<ResponseBody> getData(@Path("size") int size, @Path("page") int page);
}
