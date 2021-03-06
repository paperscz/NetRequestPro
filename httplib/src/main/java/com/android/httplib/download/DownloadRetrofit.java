package com.android.httplib.download;


import com.android.httplib.baserx.RxUtil;
import com.android.httplib.retrofit.IApiService;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DownloadRetrofit Create on 2017/9/30 21:33
 * @author :<a href="liujc_love@163.com">liujc</a>
 * @version :1.0
 * @Description : TODO
 */

public class DownloadRetrofit implements IApiService{

    private static DownloadRetrofit instance;
    private Retrofit mRetrofit;

    public DownloadRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://localhost")
                .build();
    }

    public static DownloadRetrofit getInstance() {
        if (instance == null) {
            synchronized (DownloadRetrofit.class) {
                if (instance == null) {
                    instance = new DownloadRetrofit();
                }
            }
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public static Observable<ResponseBody> downloadFile(String fileUrl) {
        return DownloadRetrofit
                .getInstance()
                .getRetrofit()
                .create(DownloadApi.class)
                .downloadFile(fileUrl)
                .compose(RxUtil.<ResponseBody>switchSchedulers());
    }

    @Override
    public <T> T getApiService(Class<T> service) {
        return mRetrofit.create(service);
    }
}
