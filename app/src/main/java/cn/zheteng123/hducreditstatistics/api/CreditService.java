package cn.zheteng123.hducreditstatistics.api;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created on 2017/3/12.
 */


public interface CreditService {

    String BASE_URL = "http://jxgl.hdu.edu.cn/";

    /**
     * 获取指定学年及学期的成绩
     * @param xh 学号
     * @param xn 学年 2016-2017
     * @param xq 学期 1 或 2
     * @param btnCx 固定参数 +%B2%E9++%D1%AF+
     * @return 页面源码
     */
    @FormUrlEncoded
    @POST(BASE_URL + "xscjcx_dq.aspx")
    @Headers("Referer:http://jxgl.hdu.edu.cn/xscjcx_dq.aspx")
    Observable<ResponseBody> getSubjectScore(
            @Query("xh") String xh,
            @Field("ddlxn") String xn,
            @Field("ddlxq") String xq,
            @Field(value = "btnCx", encoded = true) String btnCx,
            @Field("__VIEWSTATE") String __VIEWSTATE,
            @Field("__EVENTVALIDATION") String __EVENTVALIDATION
    );


    /**
     * 获取 我的成绩 页面表单隐藏项
     * @param xh 学号
     * @return 页面源码
     */
    @GET("xscjcx_dq.aspx")
    @Headers("Referer:http://jxgl.hdu.edu.cn/xscjcx_dq.aspx")
    Observable<ResponseBody> getSubjectHiddenInput(@Query("xh") String xh);

}
