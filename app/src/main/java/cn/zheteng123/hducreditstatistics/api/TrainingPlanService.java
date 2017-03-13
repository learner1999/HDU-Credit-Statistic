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
 * 与培养计划相关的一些接口
 * Created on 2017/3/12.
 */


public interface TrainingPlanService {

    String BASE_URL = "http://jxgl.hdu.edu.cn/";

    @GET(BASE_URL + "pyjh.aspx")
    @Headers("Referer:http://jxgl.hdu.edu.cn/pyjh.aspx")
    Observable<ResponseBody> getTrainingPlanHome(@Query("xh") String xh);


    /**
     * 根据页码查询
     * @param xh 学号
     * @param __EVENTTARGET 包含页码信息 ex: DBGrid$ctl24$ctl00 表示第一页
     * @param __VIEWSTATE // 必要参数，从上一次 GET 请求的页面中获取
     * @param __EVENTVALIDATION // 必要参数，从上一次 GET 请求的页面中获取
     * @param xymc 学院名称，此处是 select 的 value
     * @param zymc 专业名称，此处是 select 的 value
     * @param nj 年级
     * @param xq 学期，固定为 %C8%AB%B2%BF，表示全部学期
     * @param kcxz 课程性质，，固定为 %C8%AB%B2%BF，表示全部
     * @return 页面源码
     */
    @FormUrlEncoded
    @POST(BASE_URL + "pyjh.aspx")
    @Headers("Referer:http://jxgl.hdu.edu.cn/pyjh.aspx")
    Observable<ResponseBody> getByPage(
            @Query("xh") String xh,
            @Field("__EVENTTARGET") String __EVENTTARGET,
            @Field("__VIEWSTATE") String __VIEWSTATE,
            @Field("__EVENTVALIDATION") String __EVENTVALIDATION,
            @Field("xymc") String xymc,
            @Field("zymc") String zymc,
            @Field("nj") String nj,
            @Field(value = "xq", encoded = true) String xq,
            @Field(value = "kcxz", encoded = true) String kcxz
    );
}
