package cn.zheteng123.hducreditstatistics.main;

import java.util.List;

import cn.zheteng123.hducreditstatistics.base.interfaces.BaseView;
import cn.zheteng123.hducreditstatistics.entity.SubjectScore;
import cn.zheteng123.hducreditstatistics.entity.TrainingPlanSubject;

/**
 * Created on 2017/3/11.
 */


public interface MainView extends BaseView {

    void showCompareResult(List<SubjectScore> subjectScoreList, List<TrainingPlanSubject> trainingPlanSubjectList);
}
