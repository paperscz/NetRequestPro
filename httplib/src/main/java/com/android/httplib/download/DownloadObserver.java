package com.android.httplib.download;

import com.android.httplib.basebean.ApiException;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * DownloadObserver Create on 2017/9/30 21:32
 * @author :<a href="liujc_love@163.com">liujc</a>
 * @version :1.0
 * @Description : 文件下载
 */

public abstract class DownloadObserver extends BaseDownloadObserver {

    private String fileName;
    private String targetPath;

    public DownloadObserver(String fileName,String targetPath) {
        this.fileName = fileName;
        this.targetPath = targetPath;
    }
    public DownloadObserver(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取disposable 在onDestroy方法中取消订阅disposable.dispose()
     */
    protected abstract void getDisposable(Disposable d);

    /**
     * 失败回调
     *
     * @param errorMsg
     */
    protected abstract void onError(ApiException errorMsg);

    /**
     * 成功回调
     *
     * @param filePath
     */
    protected abstract void onSuccess(long bytesRead, long contentLength, float progress, boolean done, String filePath);

    @Override
    protected void doOnError(ApiException errorMsg) {
        onError(errorMsg);
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        getDisposable(d);
    }

    @Override
    public void onNext(@NonNull ResponseBody responseBody) {
        Observable.just(responseBody)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(@NonNull ResponseBody responseBody) throws Exception {
                        try {
                            new DownloadManager().saveFile(responseBody,targetPath, fileName, new ProgressListener() {
                                @Override
                                public void onResponseProgress(final long bytesRead, final long contentLength, final int progress, final boolean done, final String filePath) {
                                    Observable.just(progress)
                                            .distinctUntilChanged()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<Integer>() {
                                                @Override
                                                public void accept(@NonNull Integer integer) throws Exception {
                                                    onSuccess(bytesRead, contentLength, progress, done, filePath);
                                                }
                                            });
                                }
                            });
                        } catch (IOException e) {
                            doOnError(new ApiException(ApiException.SHOWTOAST,e.getMessage()));
                        }
                    }
                });
    }

    @Override
    public void onComplete() {

    }
}
