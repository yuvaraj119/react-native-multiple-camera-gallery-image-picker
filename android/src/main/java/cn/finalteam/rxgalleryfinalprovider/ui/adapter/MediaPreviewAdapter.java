package cn.finalteam.rxgalleryfinalprovider.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.finalteam.rxgalleryfinalprovider.Configuration;
import cn.finalteam.rxgalleryfinalprovider.R;
import cn.finalteam.rxgalleryfinalprovider.bean.MediaBean;
import cn.finalteam.rxgalleryfinalprovider.utils.ThemeUtils;
import uk.co.senab.photoview.PhotoView;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/21 下午10:12
 */
public class MediaPreviewAdapter extends RecyclingPagerAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MediaBean> mMediaList;
    private Configuration mConfiguration;
    private Drawable mDefaultImage;
    private int mScreenWidth, mScreenHeight;
    private int mPageColor;

    public MediaPreviewAdapter(Context context, List<MediaBean> list, int screenWidth, int screenHeight, Configuration configuration) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mMediaList = list;
        this.mScreenWidth = screenWidth;
        this.mScreenHeight = screenHeight;
        this.mConfiguration = configuration;
        this.mPageColor = ThemeUtils.resolveColor(context, R.attr.gallery_page_bg, R.color.gallery_default_page_bg);
        int defaultResId = ThemeUtils.resolveDrawableRes(context, R.attr.gallery_default_image, R.drawable.gallery_default_image);
        this.mDefaultImage = context.getResources().getDrawable(defaultResId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        MediaBean mediaBean = mMediaList.get(position);
        View view = mInflater.inflate(R.layout.gallery_media_image_preview_item, null);
        PhotoView ivImage = (PhotoView) view.findViewById(R.id.iv_media_image);
        String path = mediaBean.getThumbnailSmallPath();
        if(TextUtils.isEmpty(path)) {
            path = mediaBean.getThumbnailBigPath();
        }
        if(TextUtils.isEmpty(path)) {
            path = mediaBean.getOriginalPath();
        }
        ivImage.setBackgroundColor(mPageColor);
        mConfiguration.getImageLoader().displayImage(mContext, path, ivImage,mDefaultImage, mScreenWidth, mScreenHeight);
        return view;
    }

    @Override
    public int getCount() {
        return mMediaList.size();
    }
}
