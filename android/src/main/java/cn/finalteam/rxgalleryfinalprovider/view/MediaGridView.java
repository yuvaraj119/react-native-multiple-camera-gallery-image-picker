package cn.finalteam.rxgalleryfinalprovider.view;

import java.util.List;

import cn.finalteam.rxgalleryfinalprovider.bean.BucketBean;
import cn.finalteam.rxgalleryfinalprovider.bean.MediaBean;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/14 上午11:00
 */
public interface MediaGridView {
    void onRequestMediaCallback(List<MediaBean> list);
    void onRequestBucketCallback(List<BucketBean> list);
}
