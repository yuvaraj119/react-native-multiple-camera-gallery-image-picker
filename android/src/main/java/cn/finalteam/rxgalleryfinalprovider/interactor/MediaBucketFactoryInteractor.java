package cn.finalteam.rxgalleryfinalprovider.interactor;

import java.util.List;

import cn.finalteam.rxgalleryfinalprovider.bean.BucketBean;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/4 下午8:24
 */
public interface MediaBucketFactoryInteractor {

    interface OnGenerateBucketListener {
        void onFinished(List<BucketBean> list);
    }

    void generateBuckets();
}
