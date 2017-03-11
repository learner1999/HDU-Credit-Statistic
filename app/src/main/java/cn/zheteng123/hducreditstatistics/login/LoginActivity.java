package cn.zheteng123.hducreditstatistics.login;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zheteng123.hducreditstatistics.R;
import cn.zheteng123.hducreditstatistics.base.BaseActivity;

public class LoginActivity extends BaseActivity implements LoginView {

    LoginPresenter mLoginPresenter;

    @BindView(R.id.iv_captcha)
    ImageView mIvCaptcha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginPresenter = new LoginPresenter(this);
        mLoginPresenter.bindView(this);

        initView();
    }

    private void initView() {
        // 加载验证码图片
        mLoginPresenter.loadCaptcha();
    }

    @OnClick(R.id.iv_captcha)
    void reloadCaptcha() {
        mLoginPresenter.loadCaptcha();
    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    public void showCaptcha(Bitmap bitmap) {
        mIvCaptcha.setImageBitmap(bitmap);
    }
}
