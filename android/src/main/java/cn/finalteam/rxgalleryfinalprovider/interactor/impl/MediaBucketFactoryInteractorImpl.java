package cn.finalteam.rxgalleryfinalprovider.interactor.impl;

import android.content.Context;

import java.util.List;

import cn.finalteam.rxgalleryfinalprovider.bean.BucketBean;
import cn.finalteam.rxgalleryfinalprovider.interactor.MediaBucketFactoryInteractor;
import cn.finalteam.rxgalleryfinalprovider.utils.MediaUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/4 下午8:29
 */
public class MediaBucketFactoryInteractorImpl implements MediaBucketFactoryInteractor {

    private Context context;
    private boolean isImage;
    private OnGenerateBucketListener onGenerateBucketListener;

    public MediaBucketFactoryInteractorImpl(Context context, boolean isImage, OnGenerateBucketListener onGenerateBucketListener) {
        this.context = context;
        this.isImage = isImage;
        this.onGenerateBucketListener = onGenerateBucketListener;
    }

    public void generateBuckets() {
        Observable.create(new Observable.OnSubscribe<List<BucketBean>>() {

            @Override
            public void call(Subscriber<? super List<BucketBean>> subscriber) {
                List<BucketBean> bucketBeanList = null;
                if(isImage) {
                    bucketBeanList = MediaUtils.getAllBucketByImage(context);
                } else {
                    bucketBeanList = MediaUtils.getAllBucketByVideo(context);
                }
                subscriber.onNext(bucketBeanList);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<BucketBean>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                onGenerateBucketListener.onFinished(null);
            }

            @Override
            public void onNext(List<BucketBean> bucketBeanList) {
                onGenerateBucketListener.onFinished(bucketBeanList);
            }
        });
    }
}
