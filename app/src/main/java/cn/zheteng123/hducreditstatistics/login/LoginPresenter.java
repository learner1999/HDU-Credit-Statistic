package cn.zheteng123.hducreditstatistics.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import cn.zheteng123.hducreditstatistics.api.Networks;
import cn.zheteng123.hducreditstatistics.base.interfaces.BasePresenter;
import cn.zheteng123.hducreditstatistics.base.interfaces.BaseView;
import cn.zheteng123.hducreditstatistics.util.MD5;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created on 2017/3/10.
 */


public class LoginPresenter implements BasePresenter {
    private static final String TAG = "LoginPresenter";

    private LoginView mView;
    private Context mContext;

    public LoginPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void bindView(BaseView view) {
        mView = (LoginView) view;
    }

    public void loadCaptcha() {
        Networks.getInstance()
                .getLoginService()
                .downloadCaptcha()
                .subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, Bitmap>() {
                    @Override
                    public Bitmap call(ResponseBody responseBody) {
                        InputStream is = responseBody.byteStream();
                        return BitmapFactory.decodeStream(is);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        mView.showCaptcha(bitmap);
                    }
                });
    }

    public void login(final String username, String password, final String captcha) {
        password = MD5.md5(password);
        final String finalPassword = password;
        Networks.getInstance()
                .getLoginService()
                .getToken()
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        String lt = "";
                        String body = "";
                        try {
                            body = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Document doc = Jsoup.parse(body);
                        Elements elem = doc.select("input[name=lt]");
                        lt = elem.get(0).val();

                        return Networks.getInstance()
                                .getLoginService()
                                .login(lt, username, finalPassword, captcha);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String body = "";
                        try {
                            body = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onNext: " + body);
                        if (body.contains("错误的用户名或密码")) {
                            mView.showPasswordError();
                        } else if (body.contains("输入的验证码不正确")) {
                            mView.showCaptchaError();
                        } else if (body.contains("错误信息提示")) {
                            mView.showUnKnowError();
                        } else {
                            mView.jumpToMain();
                        }
                    }
                });
    }
}
