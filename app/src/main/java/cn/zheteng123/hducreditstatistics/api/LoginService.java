package cn.zheteng123.hducreditstatistics.api;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created on 2017/3/11.
 */


public interface LoginService extends BaseService {

    String BASE_URL = "http://cas.hdu.edu.cn/";

    // 密码错误：错误的用户名或密码
    // 验证码错误：输入的验证码不正确
    @FormUrlEncoded
    @POST("cas/login")
    Observable<ResponseBody> login(
            @Field("username") String username,
            @Field("password") String password,
            @Field("captcha") String captcha
    );

    @GET("cas/login")
    Observable<ResponseBody> getToken();

    @GET("cas/Captcha.jpg")
    Observable<ResponseBody> downloadCaptcha();
}