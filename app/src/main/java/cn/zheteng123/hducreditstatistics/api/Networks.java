package cn.zheteng123.hducreditstatistics.api;

import java.util.concurrent.TimeUnit;

import cn.zheteng123.hducreditstatistics.util.MyCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 2017/3/11.
 */


public class Networks {

    private static final int DEFAULT_TIMEOUT = 5;

    private static Networks sNetworks;

    private static OkHttpClient sOkHttpClient;

    private static LoginService sLoginService;

    public LoginService getLoginService() {
        if (sLoginService == null) {
            sLoginService = configRetrofit(LoginService.class);
        }
        return sLoginService;
    }

    public static Networks getInstance() {
        if (sNetworks == null) {
            sNetworks = new Networks();
        }
        return sNetworks;
    }

    private <T extends BaseService> T configRetrofit(Class<T> service) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(T.BASE_URL)
                .client(configClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(service);
    }

    private OkHttpClient configClient() {
        if (sOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .cookieJar(new MyCookieJar())
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(new HttpLoggingInterceptor());
            sOkHttpClient = builder.build();
        }

        return sOkHttpClient;
    }
}
