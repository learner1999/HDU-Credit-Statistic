package cn.zheteng123.hducreditstatistics.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created on 2017/3/11.
 */


public class AuthJumpInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (request.url().toString().contains("Captcha.jpg")) {
            return chain.proceed(request);
        }

        Response response = chain.proceed(request);
        String body = response.body().string();
        while (body.contains("认证转向")) {
            String urlJump = body.split("请点击<a href=\"")[1].split("\">这里")[0];
            request = new Request.Builder().url(urlJump).build();
            response = chain.proceed(request);
            body = response.body().string();
        }
        MediaType mediaType = response.body().contentType();
        return response.newBuilder().body(ResponseBody.create(mediaType, body)).build();

    }
}
