package me.jessyan.retrofiturlmanager.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.DomainHolder;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import me.jessyan.retrofiturlmanager.onUrlChangeListener;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

import static me.jessyan.retrofiturlmanager.demo.api.Api.DOUBAN_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GANK_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GITHUB_DOMAIN_NAME;

public class MainActivity extends AppCompatActivity {

    private TextView mDisplay;
    private EditText mUrl1;
    private EditText mUrl2;
    private EditText mUrl3;
    private ProgressDialog mProgressDialog;
    private ChangeListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mDisplay = (TextView) findViewById(R.id.display);
        mUrl1 = (EditText) findViewById(R.id.et_url1);
        mUrl2 = (EditText) findViewById(R.id.et_url2);
        mUrl3 = (EditText) findViewById(R.id.et_url3);
        mProgressDialog = new ProgressDialog(this);
        mUrl1.setSelection(mUrl1.getText().toString().length());
    }


    private void initListener() {
        this.mListener = new ChangeListener();
        //如果有需要可以注册监听器,当一个 Url 的 BaseUrl 被新的 Url 替代,则会会调这个监听器
        RetrofitUrlManager.getInstance().registerUrlChangeListener(mListener);
        //如果你已经确定了最终的 BaseUrl ,不需要在动态改变 BaseUrl ,请 RetrofitUrlManager.getInstance().setRun(false);

        findViewById(R.id.bt_request1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DomainHolder holder = RetrofitUrlManager.getInstance().fetchDomain(GITHUB_DOMAIN_NAME);
                if (holder == null || !holder.getUrlStr().equals(mUrl1.getText().toString())) { //可以在 App 运行时随意切换某个接口的 BaseUrl
                    RetrofitUrlManager.getInstance().putDomain(GITHUB_DOMAIN_NAME, mUrl1.getText().toString());
                }
                NetWorkManager
                        .getInstance()
                        .getOneApiService()
                        .getUsers(1, 10)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                mProgressDialog.show();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                mProgressDialog.dismiss();
                            }
                        })
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody response) throws Exception {
                                String string = response.string();
                                Log.d("users_test", string);
                                mDisplay.setText(string);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                mDisplay.setText(throwable.getMessage());
                            }
                        });
            }
        });

        findViewById(R.id.bt_request2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DomainHolder holder = RetrofitUrlManager.getInstance().fetchDomain(GANK_DOMAIN_NAME);
                if (holder == null || !holder.getUrlStr().equals(mUrl2.getText().toString())) { //可以在 App 运行时随意切换某个接口的 BaseUrl
                    RetrofitUrlManager.getInstance().putDomain(GANK_DOMAIN_NAME, mUrl2.getText().toString());
                }
                NetWorkManager
                        .getInstance()
                        .getTwoApiService()
                        .getData(10, 1)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                mProgressDialog.show();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                mProgressDialog.dismiss();
                            }
                        })
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody response) throws Exception {
                                String string = response.string();
                                Log.d("data_test", string);
                                mDisplay.setText(string);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                mDisplay.setText(throwable.getMessage());
                            }
                        });
            }
        });

        findViewById(R.id.bt_request3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DomainHolder holder = RetrofitUrlManager.getInstance().fetchDomain(DOUBAN_DOMAIN_NAME);
                if (holder == null || !holder.getUrlStr().equals(mUrl3.getText().toString())) { //可以在 App 运行时随意切换某个接口的 BaseUrl
                    RetrofitUrlManager.getInstance().putDomain(DOUBAN_DOMAIN_NAME, mUrl3.getText().toString());
                }
                NetWorkManager
                        .getInstance()
                        .getThreeApiService()
                        .getBook(1220562)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                mProgressDialog.show();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                mProgressDialog.dismiss();
                            }
                        })
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody response) throws Exception {
                                String string = response.string();
                                Log.d("book_test", string);
                                mDisplay.setText(string);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                mDisplay.setText(throwable.getMessage());
                            }
                        });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitUrlManager.getInstance().unregisterUrlChangeListener(mListener); //记住注销监听器
    }

    private class ChangeListener implements onUrlChangeListener {

        @Override
        public void onUrlChange(final HttpUrl newUrl, HttpUrl oldUrl) {
            Observable.just(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            Toast.makeText(getApplicationContext(), "The newUrl is { " + newUrl.toString() + " }", Toast.LENGTH_SHORT).show();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
        }
    }
}
