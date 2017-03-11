package cn.zheteng123.hducreditstatistics.login;

import android.content.Context;

import cn.zheteng123.hducreditstatistics.base.interfaces.BasePresenter;
import cn.zheteng123.hducreditstatistics.base.interfaces.BaseView;

/**
 * Created on 2017/3/10.
 */


public class LoginPresenter implements BasePresenter {

    private LoginView mView;
    private Context mContext;

    public LoginPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void bindView(BaseView view) {
        mView = (LoginView) view;
    }
}
