package cn.zheteng123.hducreditstatistics.main;

import android.content.Context;
import android.util.Log;

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
import cn.zheteng123.hducreditstatistics.api.TrainingPlanService;
import cn.zheteng123.hducreditstatistics.base.interfaces.BasePresenter;
import cn.zheteng123.hducreditstatistics.base.interfaces.BaseView;
import cn.zheteng123.hducreditstatistics.entity.SubjectScore;
import cn.zheteng123.hducreditstatistics.entity.TrainingPlanSubject;
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
    List<TrainingPlanSubject> mTrainingPlanSubjectList = new ArrayList<>();

    int mFlag = 0; // 用于判断是否爬取完成绩及培养计划

    private double mRequiredCredit = 0; // 必修课程学分
    private double mPracticeCredit = 0; // 实践部分学分
    private double mOptionalCredit = 0; // 选修课程学分
    private double mLimitedOptionalCredit = 0; // 限选课程学分
    private double mArbitrarilyOptionalCredit = 0; // 任选课程学分

    // 一些请求参数
    private String xymc;
    private String zymc;
    private String nj;
    private String __VIEWSTATE;
    private String __EVENTVALIDATION;

    public MainPresenter(Context context) {
        mContext = context;
    }

    /**
     * 获取用户所有课程成绩
     * @param xh 学号
     */
    public void getSubjectScore(final String xh) {
        mView.showProgressDialog();

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
                        if (mFlag == 1) {
                            compareSubject();
                        } else {
                            mFlag++;
                        }
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
                        mView.changeDialogMessage("已爬取 " + mSubjectScoreList.size() + " 门课的成绩……");
                    }
                });

        Networks.getInstance()
                .getTrainingPlanService()
                .getTrainingPlanHome(xh)
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        String html = "";
                        try {
                            html =  responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Document doc = Jsoup.parse(html);
                        xymc = doc.select("select[name=xymc]>option[selected=selected]").val();
                        zymc = doc.select("select[name=zymc]>option[selected=selected]").val();
                        nj = doc.select("select[name=nj]>option[selected=selected]").val();
                        updateValidation(html);

                        Element table = doc.select("#DataGrid4").first();
                        Elements trs = table.select("tr");
                        if (trs != null && trs.size() > 0) {
                            trs.remove(0);
                            for (Element tr : trs) {
                                Elements tds = tr.select("td");
                                double credit = 0;
                                try {
                                    credit = Double.parseDouble(tds.eq(1).text());
                                } catch (NumberFormatException e) {
                                    continue;
                                }

                                String strCategory = tds.eq(0).text();
                                if (strCategory.contains("必修")) {
                                    mRequiredCredit += credit;
                                } else if (strCategory.contains("实践")) {
                                    mPracticeCredit += credit;
                                } else if (strCategory.contains("选修")) {
                                    mOptionalCredit += credit;
                                } else if (strCategory.contains("限选")) {
                                    mLimitedOptionalCredit += credit;
                                } else if (strCategory.contains("任选")) {
                                    mArbitrarilyOptionalCredit += credit;
                                }
                            }
                        }

                        Log.d(TAG, "onNext: " + "必修=" + mRequiredCredit + "，实践=" + mPracticeCredit +
                                "，选修=" + mOptionalCredit + "，限选" + mLimitedOptionalCredit +
                                "，任选" + mArbitrarilyOptionalCredit);

                        return Networks
                                .getInstance()
                                .getTrainingPlanService()
                                .getByPage(xh, "DBGrid$ctl24$ctl00", __VIEWSTATE, __EVENTVALIDATION, xymc, zymc, nj, "%C8%AB%B2%BF", "%C8%AB%B2%BF");
                    }
                })
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
                        updateValidation(html);
                        int pageNum = doc.select("#DBGrid tr").last().select("td a").size() + 1;
                        Log.d(TAG, "call: pageNum=" + pageNum);

                        TrainingPlanService trainingPlanService = Networks.getInstance().getTrainingPlanService();
                        List<Observable<ResponseBody>> observableList = new ArrayList<>();
                        for(int i = 0; i < pageNum; i++) {
                            // TODO: 2017/3/13 这个地方可能埋了一个坑，没有考虑页数超过 10 页的情况
                            Observable<ResponseBody> observable =  trainingPlanService.getByPage(xh, "DBGrid$ctl24$ctl0" + i, __VIEWSTATE, __EVENTVALIDATION, xymc, zymc, nj, "%C8%AB%B2%BF", "%C8%AB%B2%BF");
                            observableList.add(observable);
                        }
                        return Observable.concat(observableList);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: " + mTrainingPlanSubjectList.size());
                        if (mFlag == 1) {
                            compareSubject();
                        } else {
                            mFlag++;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String html = "";
                        try {
                            html = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateValidation(html);

                        Document doc = Jsoup.parse(html);
                        Elements trs = doc.select("#DBGrid tr");
                        for(int i = 1, len = trs.size(); i < len - 1; i++) {
                            TrainingPlanSubject subject = new TrainingPlanSubject();
                            Element tr = trs.get(i);
                            Elements tds = tr.select("td");
                            subject.code = tds.get(0).text();
                            subject.name = tds.get(1).text();
                            subject.credit = Double.parseDouble(tds.get(2).text());
                            subject.category = tds.get(4).text();
                            mTrainingPlanSubjectList.add(subject);
                            mView.changeDialogMessage("已爬取 " + mTrainingPlanSubjectList.size() + " 门培养计划课程……");
                        }

                    }
                });
    }

    /**
     * 比较已经通过的课程和培养计划要求
     */
    private void compareSubject() {
        for (int i = mSubjectScoreList.size() - 1; i >= 0; i--) {
            for (TrainingPlanSubject trainingPlanSubject : mTrainingPlanSubjectList) {
                if (mSubjectScoreList.get(i).code.equals(trainingPlanSubject.code)) {
                    trainingPlanSubject.isPass = true;
                    mSubjectScoreList.remove(i);
                    break;
                }
            }
        }
        mView.showCompareResult(mSubjectScoreList, mTrainingPlanSubjectList, mRequiredCredit, mPracticeCredit, mOptionalCredit, mLimitedOptionalCredit, mArbitrarilyOptionalCredit);
    }

    /**
     * 更新 aspx 隐藏表单项
     * @param html 页面源码
     */
    private void updateValidation(String html) {
        Document doc = Jsoup.parse(html);
        __VIEWSTATE = doc.select("#__VIEWSTATE").val();
        __EVENTVALIDATION = doc.select("#__EVENTVALIDATION").val();
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
