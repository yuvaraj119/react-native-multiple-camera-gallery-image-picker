package cn.finalteam.rxgalleryfinalprovider.presenter;

import cn.finalteam.rxgalleryfinalprovider.view.MediaGridView;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/14 上午10:53
 */
public interface MediaGridPresenter {

    void setMediaGridView(MediaGridView mediaGridView);
    void getMediaList(String bucketId, int pageSize, int currentOffset);
    void getBucketList();
}
