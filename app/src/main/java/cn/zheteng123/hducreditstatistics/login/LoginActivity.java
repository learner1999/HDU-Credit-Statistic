package cn.zheteng123.hducreditstatistics.login;

import android.os.Bundle;

import cn.zheteng123.hducreditstatistics.R;
import cn.zheteng123.hducreditstatistics.base.BaseActivity;

public class LoginActivity extends BaseActivity implements LoginView {

    LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginPresenter = new LoginPresenter(this);
        mLoginPresenter.bindView(this);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_login;
    }
}
