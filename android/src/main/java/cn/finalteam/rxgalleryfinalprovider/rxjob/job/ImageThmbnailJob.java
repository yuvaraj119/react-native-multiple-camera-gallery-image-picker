package cn.finalteam.rxgalleryfinalprovider.rxjob.job;

import android.content.Context;

import java.io.File;

import cn.finalteam.rxgalleryfinalprovider.bean.MediaBean;
import cn.finalteam.rxgalleryfinalprovider.rxjob.Job;
import cn.finalteam.rxgalleryfinalprovider.utils.BitmapUtils;
import cn.finalteam.rxgalleryfinalprovider.utils.MediaUtils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/7/31 上午11:46
 */
public class ImageThmbnailJob implements Job {

    private MediaBean mediaBean;
    private Context context;

    public ImageThmbnailJob(Context context, Params params) {
        this.context = context;
        this.mediaBean = (MediaBean) params.getRequestData();
    }

    @Override
    public Result onRunJob() {
        String originalPath = mediaBean.getOriginalPath();
        File bigThumFile = MediaUtils.createThumbnailBigFileName(context, originalPath);
        File smallThumFile = MediaUtils.createThumbnailSmallFileName(context, originalPath);
        if(!smallThumFile.exists()){
            BitmapUtils.createThumbnailBig(bigThumFile, originalPath);
        }
        if(!bigThumFile.exists()){
            BitmapUtils.createThumbnailSmall(smallThumFile, originalPath);
        }
        Result result = Result.SUCCESS;
        result.setResultData(mediaBean);
        return result;
    }
}
