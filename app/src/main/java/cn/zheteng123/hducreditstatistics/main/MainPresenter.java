package cn.zheteng123.hducreditstatistics.main;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.zheteng123.hducreditstatistics.api.CreditService;
import cn.zheteng123.hducreditstatistics.api.Networks;
import cn.zheteng123.hducreditstatistics.base.interfaces.BasePresenter;
import cn.zheteng123.hducreditstatistics.base.interfaces.BaseView;
import cn.zheteng123.hducreditstatistics.entity.SubjectScore;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 2017/3/11.
 */


public class MainPresenter implements BasePresenter {

    private static final String TAG = "MainPresenter";

    private Context mContext;

    private MainView mView;

    List<SubjectScore> mSubjectScoreList = new ArrayList<>();

    public MainPresenter(Context context) {
        mContext = context;
    }

    /**
     * 获取用户所有课程成绩
     * @param xh 学号
     */
    public void getSubjectScore(final String xh) {
        final CreditService creditService = Networks.getInstance()
                .getCreditService();

        creditService
                .getSubjectHiddenInput(xh)
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        String html = "";
                        try {
                            html = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Document doc = Jsoup.parse(html);
                        String __VIEWSTATE = doc.select("input[name=__VIEWSTATE]").get(0).val();
                        String __EVENTVALIDATION = doc.select("input[name=__EVENTVALIDATION]").get(0).val();

                        // 根据学号判断入学年份
                        String strXn = xh.substring(0, 2);
                        int xn = Integer.parseInt(strXn);

                        return Observable.merge(
                                creditService.getSubjectScore(xh, "20" + xn + "-20" + (xn + 1), "1", "+%B2%E9++%D1%AF+", __VIEWSTATE, __EVENTVALIDATION),
                                creditService.getSubjectScore(xh, "20" + xn + "-20" + (xn + 1), "2", "+%B2%E9++%D1%AF+", __VIEWSTATE, __EVENTVALIDATION),
                                creditService.getSubjectScore(xh, "20" + (xn + 1) + "-20" + (xn + 2), "1", "+%B2%E9++%D1%AF+", __VIEWSTATE, __EVENTVALIDATION),
                                creditService.getSubjectScore(xh, "20" + (xn + 1) + "-20" + (xn + 2), "2", "+%B2%E9++%D1%AF+", __VIEWSTATE, __EVENTVALIDATION),
                                creditService.getSubjectScore(xh, "20" + (xn + 2) + "-20" + (xn + 3), "1", "+%B2%E9++%D1%AF+", __VIEWSTATE, __EVENTVALIDATION),
                                creditService.getSubjectScore(xh, "20" + (xn + 2) + "-20" + (xn + 3), "2", "+%B2%E9++%D1%AF+", __VIEWSTATE, __EVENTVALIDATION),
                                creditService.getSubjectScore(xh, "20" + (xn + 3) + "-20" + (xn + 4), "1", "+%B2%E9++%D1%AF+", __VIEWSTATE, __EVENTVALIDATION),
                                creditService.getSubjectScore(xh, "20" + (xn + 3) + "-20" + (xn + 4), "2", "+%B2%E9++%D1%AF+", __VIEWSTATE, __EVENTVALIDATION)
                        );
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        // 此处已经取到所有成绩的集合
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String html = "";
                        try {
                            html = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        List<SubjectScore> subjectScoreList = parseSubjectScore(html);
                        mSubjectScoreList.addAll(subjectScoreList);
                    }
                });
    }



    /**
     * 解析 HTML 中的课程成绩
     * @param html 页面源码
     * @return 通过的课程信息列表
     */
    private List<SubjectScore> parseSubjectScore(String html) {
        Document doc = Jsoup.parse(html);
        Elements trs = doc.select("#DataGrid1 tr");
        List<SubjectScore> subjectScoreList = new ArrayList<>();
        if (trs != null && trs.size() > 1) {
            trs.remove(0);
            SubjectScore subjectScore;
            for(Element tr : trs) {
                Elements tds = tr.select("td");
                if (!isPass(tds.eq(7).text(), tds.eq(8).text())) {
                    continue;
                }

                subjectScore = new SubjectScore();
                subjectScore.code = tds.eq(2).text();
                subjectScore.name = tds.eq(3).text();
                subjectScore.credit = Double.parseDouble(tds.eq(6).text());
                subjectScoreList.add(subjectScore);
            }
        }
        return subjectScoreList;
    }

    /**
     * 判断课程是否及格
     * @param score 考试成绩
     * @param makeUpScore 补考成绩
     * @return 是否及格
     */
    private boolean isPass(String score, String makeUpScore) {
        String[] strPass = {"优秀", "中等", "良好", "及格", "合格"};
        List<String> stringList = Arrays.asList(strPass);
        if (stringList.contains(score) || stringList.contains(makeUpScore)) {
            return true;
        }

        double doubleScore, doubleMakeUpScore;
        try {
            doubleScore = Double.parseDouble(score);
        } catch (NumberFormatException e) {
            return false;
        }
        if (doubleScore >= 60) {
            return true;
        }

        try {
            doubleMakeUpScore = Double.parseDouble(makeUpScore);
        } catch (NumberFormatException e) {
            return false;
        }
        if (doubleMakeUpScore >= 60) {
            return true;
        }

        return false;
    }

    @Override
    public void bindView(BaseView view) {
        mView = (MainView) view;
    }
}
