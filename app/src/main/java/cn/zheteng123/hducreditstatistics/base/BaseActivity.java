package cn.zheteng123.hducreditstatistics.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import cn.zheteng123.hducreditstatistics.base.interfaces.BaseView;

/**
 * Created on 2017/3/10.
 */


public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        ButterKnife.bind(this);
    }
}
