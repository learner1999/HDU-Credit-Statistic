package cn.zheteng123.hducreditstatistics.login;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.zheteng123.hducreditstatistics.R;
import cn.zheteng123.hducreditstatistics.base.BaseActivity;
import cn.zheteng123.hducreditstatistics.main.MainActivity;

public class LoginActivity extends BaseActivity implements LoginView {

    LoginPresenter mLoginPresenter;

    @BindView(R.id.iv_captcha)
    ImageView mIvCaptcha;

    @BindView(R.id.et_username)
    EditText mEtUsername;

    @BindView(R.id.et_password)
    EditText mEtPassword;

    @BindView(R.id.et_captcha)
    EditText mEtCaptcha;

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

    @OnClick(R.id.btn_login)
    void login() {
        mLoginPresenter.login(
                mEtUsername.getText().toString(),
                mEtPassword.getText().toString(),
                mEtCaptcha.getText().toString()
        );
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

    @Override
    public void showPasswordError() {
        Toast.makeText(this, "账号或密码错误！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCaptchaError() {
        Toast.makeText(this, "验证码错误！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUnKnowError() {
        Toast.makeText(this, "登录失败，请检查您的账号密码及验证码！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void jumpToMain() {
        MainActivity.actionStart(this);
        finish();
    }
}
