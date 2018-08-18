package cn.finalteam.rxgalleryfinalprovider.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cn.finalteam.rxgalleryfinalprovider.Configuration;
import cn.finalteam.rxgalleryfinalprovider.R;
import cn.finalteam.rxgalleryfinalprovider.bean.MediaBean;
import cn.finalteam.rxgalleryfinalprovider.rxbus.RxBus;
import cn.finalteam.rxgalleryfinalprovider.rxbus.event.MediaCheckChangeEvent;
import cn.finalteam.rxgalleryfinalprovider.rxjob.Job;
import cn.finalteam.rxgalleryfinalprovider.rxjob.RxJob;
import cn.finalteam.rxgalleryfinalprovider.rxjob.job.ImageThmbnailJobCreate;
import cn.finalteam.rxgalleryfinalprovider.ui.widget.RecyclerImageView;
import cn.finalteam.rxgalleryfinalprovider.utils.ThemeUtils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/18 下午7:48
 */
public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.GridViewHolder> {

    private Context mContext;
    private List<MediaBean> mMediaBeanList;
    private LayoutInflater mInflater;
    private int mImageSize;
    private Configuration mConfiguration;
    private Drawable mDefaultImage;
    private List<MediaBean> mCheckedList;

    public MediaGridAdapter(Context context, List<MediaBean> list, List<MediaBean> checkedList, int screenWidth, Configuration configuration) {
        this.mContext = context;
        this.mMediaBeanList = list;
        this.mCheckedList = checkedList;
        this.mInflater = LayoutInflater.from(context);
        this.mImageSize = screenWidth/3;
        int defaultResId = ThemeUtils.resolveDrawableRes(context, R.attr.gallery_default_image, R.drawable.gallery_default_image);
        this.mDefaultImage = context.getResources().getDrawable(defaultResId);
        this.mConfiguration = configuration;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.gallery_adapter_media_grid_item, parent, false);
        return new GridViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        MediaBean mediaBean = mMediaBeanList.get(position);
        if(mediaBean.getId() == Integer.MIN_VALUE) {
            holder.mCbCheck.setVisibility(View.GONE);
            holder.mIvMediaImage.setVisibility(View.GONE);
            holder.mLlCamera.setVisibility(View.VISIBLE);
        } else {
            if(mConfiguration.isRadio()) {
                holder.mCbCheck.setVisibility(View.GONE);
            } else{
                holder.mCbCheck.setVisibility(View.VISIBLE);
                holder.mCbCheck.setOnClickListener(new OnCheckBoxClickListener(mediaBean));
            }
            holder.mIvMediaImage.setVisibility(View.VISIBLE);
            holder.mLlCamera.setVisibility(View.GONE);
            if(mCheckedList != null && mCheckedList.contains(mediaBean)){
                holder.mCbCheck.setChecked(true);
            } else {
                holder.mCbCheck.setChecked(false);
            }
            String bitPath = mediaBean.getThumbnailSmallPath();
            String smallPath = mediaBean.getThumbnailSmallPath();

            if(!new File(bitPath).exists() || !new File(smallPath).exists()) {
                Job job = new ImageThmbnailJobCreate(mContext, mediaBean).create();
                RxJob.getDefault().addJob(job);
            }
            String path = mediaBean.getThumbnailSmallPath();
            if(TextUtils.isEmpty(path)) {
                path = mediaBean.getThumbnailBigPath();
            }
            if(TextUtils.isEmpty(path)) {
                path = mediaBean.getOriginalPath();
            }
            mConfiguration.getImageLoader()
                    .displayImage(mContext, path, holder.mIvMediaImage, mDefaultImage, mImageSize, mImageSize);
        }
    }

    @Override
    public int getItemCount() {
        return mMediaBeanList.size();
    }

    class OnCheckBoxClickListener implements View.OnClickListener {

        private MediaBean mediaBean;

        public OnCheckBoxClickListener(MediaBean bean) {
            this.mediaBean = bean;
        }

        @Override
        public void onClick(View view) {
            if(mConfiguration.getMaxSize() == mCheckedList.size() && !mCheckedList.contains(mediaBean)) {
                AppCompatCheckBox checkBox = (AppCompatCheckBox) view;
                checkBox.setChecked(false);
                Toast.makeText(mContext, mContext.getResources()
                        .getString(R.string.gallery_image_max_size_tip, mConfiguration.getMaxSize()), Toast.LENGTH_SHORT).show();
            } else {
                RxBus.getDefault().post(new MediaCheckChangeEvent(mediaBean));
            }
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

        RecyclerImageView mIvMediaImage;
        AppCompatCheckBox mCbCheck;

        LinearLayout mLlCamera;

        public GridViewHolder(Context context, View itemView) {
            super(itemView);
            mIvMediaImage = (RecyclerImageView) itemView.findViewById(R.id.iv_media_image);
            mCbCheck = (AppCompatCheckBox) itemView.findViewById(R.id.cb_check);
            mLlCamera = (LinearLayout) itemView.findViewById(R.id.ll_camera);

            int checkTint = ThemeUtils.resolveColor(context, R.attr.gallery_checkbox_button_tint_color, R.color.gallery_default_checkbox_button_tint_color);
            CompoundButtonCompat.setButtonTintList(mCbCheck, ColorStateList.valueOf(checkTint));
        }
    }

}