package cn.zheteng123.hducreditstatistics.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

import cn.zheteng123.hducreditstatistics.api.Networks;
import cn.zheteng123.hducreditstatistics.base.interfaces.BasePresenter;
import cn.zheteng123.hducreditstatistics.base.interfaces.BaseView;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
}
