package cn.zheteng123.hducreditstatistics.main;

import android.content.Context;

import cn.zheteng123.hducreditstatistics.base.interfaces.BasePresenter;
import cn.zheteng123.hducreditstatistics.base.interfaces.BaseView;

/**
 * Created on 2017/3/11.
 */


public class MainPresenter implements BasePresenter {

    private Context mContext;

    private MainView mView;

    public MainPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void bindView(BaseView view) {
        mView = (MainView) view;
    }
}
