package cn.finalteam.rxgalleryfinalprovider.presenter.impl;

import android.content.Context;

import java.util.List;

import cn.finalteam.rxgalleryfinalprovider.bean.BucketBean;
import cn.finalteam.rxgalleryfinalprovider.bean.MediaBean;
import cn.finalteam.rxgalleryfinalprovider.interactor.MediaBucketFactoryInteractor;
import cn.finalteam.rxgalleryfinalprovider.interactor.MediaSrcFactoryInteractor;
import cn.finalteam.rxgalleryfinalprovider.interactor.impl.MediaBucketFactoryInteractorImpl;
import cn.finalteam.rxgalleryfinalprovider.interactor.impl.MediaSrcFactoryInteractorImpl;
import cn.finalteam.rxgalleryfinalprovider.presenter.MediaGridPresenter;
import cn.finalteam.rxgalleryfinalprovider.view.MediaGridView;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/14 上午10:58
 */
public class MediaGridPresenterImpl implements MediaGridPresenter, MediaSrcFactoryInteractor.OnGenerateMediaListener,
        MediaBucketFactoryInteractor.OnGenerateBucketListener{

    MediaSrcFactoryInteractor mediaSrcFactoryInteractor;
    MediaBucketFactoryInteractor mediaBucketFactoryInteractor;

    Context context;
    MediaGridView mediaGridView;

    public MediaGridPresenterImpl(Context context, boolean isImage) {
        this.context = context;
        this.mediaSrcFactoryInteractor = new MediaSrcFactoryInteractorImpl(context, isImage, this);
        this.mediaBucketFactoryInteractor = new MediaBucketFactoryInteractorImpl(context, isImage, this);
    }

    /**
     * 设置MVP view(操作UI接口)
     * @param mediaGridView
     */
    @Override
    public void setMediaGridView(MediaGridView mediaGridView) {
        this.mediaGridView = mediaGridView;
    }

    /**
     * 分页获取media
     * @param bucketId
     * @param pageSize
     * @param currentOffset
     */
    @Override
    public void getMediaList(String bucketId, int pageSize, int currentOffset) {
        mediaSrcFactoryInteractor.generateMedias(bucketId, pageSize, currentOffset);
    }

    @Override
    public void getBucketList() {
        mediaBucketFactoryInteractor.generateBuckets();
    }

    /**
     * Media获取事件回调
     * @param pageSize
     * @param currentOffset
     * @param list
     */
    @Override
    public void onFinished(String bucketId, int pageSize, int currentOffset, List<MediaBean> list) {
        mediaGridView.onRequestMediaCallback(list);
    }

    /**
     * BUCKET获取事件回调
     * @param list
     */
    @Override
    public void onFinished(List<BucketBean> list) {
        mediaGridView.onRequestBucketCallback(list);
    }
}
