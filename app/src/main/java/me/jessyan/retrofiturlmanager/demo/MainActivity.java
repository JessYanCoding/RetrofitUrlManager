/**
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.jessyan.retrofiturlmanager.demo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import me.jessyan.retrofiturlmanager.onUrlChangeListener;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

import static me.jessyan.retrofiturlmanager.demo.api.Api.DOUBAN_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GANK_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.demo.api.Api.GITHUB_DOMAIN_NAME;

/**
 * ================================================
 * Created by JessYan on 18/07/2017 17:03
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class MainActivity extends AppCompatActivity {

    private EditText mUrl1;
    private EditText mUrl2;
    private EditText mUrl3;
    private EditText mGlobalUrl;
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
        mUrl1 = (EditText) findViewById(R.id.et_url1);
        mUrl2 = (EditText) findViewById(R.id.et_url2);
        mUrl3 = (EditText) findViewById(R.id.et_url3);
        mGlobalUrl = (EditText) findViewById(R.id.et_global_url);
        mProgressDialog = new ProgressDialog(this);
        mUrl1.setSelection(mUrl1.getText().toString().length());
    }


    private void initListener() {
        this.mListener = new ChangeListener();
        //如果有需要可以注册监听器,当一个 Url 的 BaseUrl 被新的 Url 替代,则会回调这个监听器,调用时间是在接口请求服务器之前
        RetrofitUrlManager.getInstance().registerUrlChangeListener(mListener);
        //如果你已经确定了最终的 BaseUrl ,不需要在动态改变 BaseUrl ,请 RetrofitUrlManager.getInstance().setRun(false);

        findViewById(R.id.bt_request1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUrl httpUrl = RetrofitUrlManager.getInstance().fetchDomain(GITHUB_DOMAIN_NAME);
                if (httpUrl == null || !httpUrl.toString().equals(mUrl1.getText().toString())) { //可以在 App 运行时随意切换某个接口的 BaseUrl
                    RetrofitUrlManager.getInstance().putDomain(GITHUB_DOMAIN_NAME, mUrl1.getText().toString());
                }
                NetWorkManager
                        .getInstance()
                        .getOneApiService()
                        .getUsers(1, 10)
                        .compose(MainActivity.this.<ResponseBody>getDefaultTransformer())
                        .subscribe(getDefaultObserver());
            }
        });

        findViewById(R.id.bt_request2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUrl httpUrl = RetrofitUrlManager.getInstance().fetchDomain(GANK_DOMAIN_NAME);
                if (httpUrl == null || !httpUrl.toString().equals(mUrl2.getText().toString())) { //可以在 App 运行时随意切换某个接口的 BaseUrl
                    RetrofitUrlManager.getInstance().putDomain(GANK_DOMAIN_NAME, mUrl2.getText().toString());
                }
                NetWorkManager
                        .getInstance()
                        .getTwoApiService()
                        .getData(10, 1)
                        .compose(MainActivity.this.<ResponseBody>getDefaultTransformer())
                        .subscribe(getDefaultObserver());
            }
        });

        findViewById(R.id.bt_request3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUrl httpUrl = RetrofitUrlManager.getInstance().fetchDomain(DOUBAN_DOMAIN_NAME);
                if (httpUrl == null || !httpUrl.toString().equals(mUrl3.getText().toString())) { //可以在 App 运行时随意切换某个接口的 BaseUrl
                    RetrofitUrlManager.getInstance().putDomain(DOUBAN_DOMAIN_NAME, mUrl3.getText().toString());
                }
                NetWorkManager
                        .getInstance()
                        .getThreeApiService()
                        .getBook(1220562)
                        .compose(MainActivity.this.<ResponseBody>getDefaultTransformer())
                        .subscribe(getDefaultObserver());
            }
        });

    }

    private void showResult(String result) {
        new AlertDialog.Builder(this)
                .setMessage(result)
                .setCancelable(true)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    // 请求默认 BaseUrl，请求的接口没有配置 DomainHeader，所以只受全局 BaseUrl的影响
    public void btnRequestDefault(View view) {
        NetWorkManager
                .getInstance()
                .getOneApiService()
                .requestDefault()
                .compose(this.<ResponseBody>getDefaultTransformer())
                .subscribe(getDefaultObserver());
    }

    // 设置全局替换的 BaseUrl
    public void btnSetGlobalUrl(View view) {
        //当你项目中只有一个 BaseUrl ,但需要动态改变,全局 BaseUrl 显得非常方便
        HttpUrl httpUrl = RetrofitUrlManager.getInstance().getGlobalDomain();
        if (null == httpUrl || !httpUrl.toString().equals(mGlobalUrl.getText().toString().trim()))
            RetrofitUrlManager.getInstance().setGlobalDomain(mGlobalUrl.getText().toString().trim());

        Toast.makeText(getApplicationContext(), "全局替换baseUrl成功", Toast.LENGTH_SHORT).show();
    }

    // 移除全局的 BaseUrl
    public void btnRmoveGlobalUrl(View view) {
        //不想再使用全局 BaseUrl ,想用之前传入 Retrofit 的默认 BaseUrl ,就Remove
        RetrofitUrlManager.getInstance().removeGlobalDomain();
        Toast.makeText(getApplicationContext(), "移除了全局baseUrl", Toast.LENGTH_SHORT).show();
    }

    private <T> ObservableTransformer<T, T> getDefaultTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
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
                        });
            }
        };
    }

    private Observer<ResponseBody> getDefaultObserver() {
        return new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody response) {
                try {
                    String string = response.string();
                    Log.d("test", string);
                    showResult(string);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                showResult(throwable.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitUrlManager.getInstance().unregisterUrlChangeListener(mListener); //记住注销监听器
    }

    private class ChangeListener implements onUrlChangeListener {

        @Override
        public void onUrlChangeBefore(HttpUrl oldUrl, String domainName) {
            Log.d("MainActivity", String.format("The oldUrl is <%s>, ready fetch <%s> from DomainNameHub",
                    oldUrl.toString(),
                    domainName));
        }

        @Override
        public void onUrlChanged(final HttpUrl newUrl, HttpUrl oldUrl) {
            Observable.just(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            Toast.makeText(getApplicationContext(), "The newUrl is { " + newUrl.toString() + " }", Toast.LENGTH_LONG).show();
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
