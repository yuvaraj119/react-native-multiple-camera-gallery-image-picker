package cn.finalteam.rxgalleryfinalprovider.imageloader;

import android.graphics.drawable.Drawable;

import cn.finalteam.rxgalleryfinalprovider.ui.widget.FixImageView;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/6/17 下午1:05
 */
public interface AbsImageLoader {
    void displayImage(Object context, String path, FixImageView imageView, Drawable defaultDrawable, int width, int height);
}
