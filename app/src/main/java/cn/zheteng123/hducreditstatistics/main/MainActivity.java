package cn.zheteng123.hducreditstatistics.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import cn.zheteng123.hducreditstatistics.R;
import cn.zheteng123.hducreditstatistics.base.BaseActivity;
import cn.zheteng123.hducreditstatistics.entity.SubjectScore;
import cn.zheteng123.hducreditstatistics.entity.TrainingPlanSubject;

public class MainActivity extends BaseActivity implements MainView {

    private static final String TAG = "MainActivity";

    private MainPresenter mMainPresenter;
    private String mStrXh;

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
    public void showCompareResult(List<SubjectScore> subjectScoreList, List<TrainingPlanSubject> trainingPlanSubjectList) {
        Log.d(TAG, "showCompareResult: " + "完成啦！未匹配的课程有" + subjectScoreList.size() + "门");
        Toast.makeText(this, "完成啦！未匹配的课程有" + subjectScoreList.size() + "门", Toast.LENGTH_SHORT).show();
    }
}
