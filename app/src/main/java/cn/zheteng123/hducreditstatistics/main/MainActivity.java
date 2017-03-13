package cn.zheteng123.hducreditstatistics.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import cn.zheteng123.hducreditstatistics.R;
import cn.zheteng123.hducreditstatistics.base.BaseActivity;
import cn.zheteng123.hducreditstatistics.entity.SubjectScore;
import cn.zheteng123.hducreditstatistics.entity.TrainingPlanSubject;

public class MainActivity extends BaseActivity implements MainView {

    private static final String TAG = "MainActivity";

    private MainPresenter mMainPresenter;
    private String mStrXh;

    @BindView(R.id.tv_result)
    TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainPresenter = new MainPresenter(this);
        mMainPresenter.bindView(this);

        Intent intent = getIntent();
        mStrXh = intent.getStringExtra("xh");

        initData();
    }

    private void initData() {
        Log.d(TAG, "initData: " + mStrXh);
        mMainPresenter.getSubjectScore(mStrXh);
    }

    /**
     * 启动主界面
     * @param context 上下文
     * @param xh 学号
     */
    public static void actionStart(Context context, String xh) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("xh", xh);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    public void showCompareResult(List<SubjectScore> subjectScoreList,
                                  List<TrainingPlanSubject> trainingPlanSubjectList,
                                  double requiredCredit,
                                  double practiceCredit,
                                  double optionalCredit,
                                  double limitedOptionalCredit,
                                  double arbitrarilyOptionalCredit) {

        double gainRequiredCredit = 0;
        double gainPracticeCredit = 0;
        double gainOptionalCredit = 0;
        double gainLimitedOptionalCredit = 0;
        double gainArbitrarilyOptionalCredit = 0;

        String strNotGainRequired = "\n未获得的必修及实践学分如下：\n";
        for (TrainingPlanSubject subject : trainingPlanSubjectList) {
            String strCategory = subject.category;
            if (subject.isPass) {
                if (strCategory.contains("必修")) {
                    gainRequiredCredit += subject.credit;
                } else if (strCategory.contains("实践")) {
                    gainPracticeCredit += subject.credit;
                } else if (strCategory.contains("选修")) {
                    gainOptionalCredit += subject.credit;
                } else if (strCategory.contains("限选")) {
                    gainLimitedOptionalCredit += subject.credit;
                } else if (strCategory.contains("任选")) {
                    gainArbitrarilyOptionalCredit += subject.credit;
                }
            } else if(strCategory.contains("实践") || strCategory.contains("必修")) {
                strNotGainRequired += subject.code + " " + subject.name + " " + subject.credit + " " + subject.category + "\n";
            }
        }

        String strNotMatchSubject = "\n未匹配的课程如下：\n";
        for (SubjectScore subjectScore : subjectScoreList) {
            if (subjectScore.code.startsWith("C")) {
                gainArbitrarilyOptionalCredit += subjectScore.credit;
            } else {
                strNotMatchSubject += subjectScore.code + " " + subjectScore.name + " " + subjectScore.credit + "\n";
            }
        }

        String strResult = "必修已修学分 " + String.format(Locale.CHINA, "%.1f", gainRequiredCredit) +
                "，要求总分 " + String.format(Locale.CHINA, "%.1f", requiredCredit) + "\n" +
                "实践已修学分 " + String.format(Locale.CHINA, "%.1f", gainPracticeCredit) +
                "，要求总分 " + String.format(Locale.CHINA, "%.1f", practiceCredit) + "\n" +
                "选修已修学分 " + String.format(Locale.CHINA, "%.1f", gainOptionalCredit) +
                "，要求总分 " + String.format(Locale.CHINA, "%.1f", optionalCredit) + "\n" +
                "限选已修学分 " + String.format(Locale.CHINA, "%.1f", gainLimitedOptionalCredit) +
                "，要求总分 " + String.format(Locale.CHINA, "%.1f", limitedOptionalCredit) + "\n" +
                "任选已修学分 " + String.format(Locale.CHINA, "%.1f", gainArbitrarilyOptionalCredit) +
                "，要求总分 " + String.format(Locale.CHINA, "%.1f", arbitrarilyOptionalCredit) + "\n";

        mTvResult.setText(strResult + strNotMatchSubject + strNotGainRequired);
    }
}
