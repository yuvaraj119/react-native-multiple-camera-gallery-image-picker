package cn.finalteam.rxgalleryfinal;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yalantis.ucrop.UCropActivity;
import com.yalantis.ucrop.model.AspectRatio;

import java.util.List;

import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.AbsImageLoader;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.imageloader.PicassoImageLoader;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.BaseResultEvent;
import cn.finalteam.rxgalleryfinal.utils.MediaType;

/**
 * Desction:配置信息
 * Author:pengjianbo
 * Date:16/5/7 下午3:58
 */
public class Configuration {

    protected Configuration() {
    }

    private boolean image = true;
    private Context context;
    private MediaType []filterMimes;
    private List<MediaBean> selectedList;
    private boolean radio;
    private boolean crop;
    private int maxSize = 1;
    private ImageLoaderType imageLoaderType;
    private PauseOnScrollListener pauseOnScrollListener;
    private RxBusResultSubscriber<BaseResultEvent> resultSubscriber;

    //==========UCrop START==========
    //是否隐藏裁剪页面底部控制栏,默认显示
    private boolean hideBottomControls;
    //图片压缩质量,默认不压缩
    private int compressionQuality;
    //手势方式,默认all
    private int []gestures;
    //设置图片最大值,默认根据屏幕得出
    private int maxBitmapSize;
    //设置最大缩放值,默认10.f
    private float maxScaleMultiplier;
    //宽高比
    private float aspectRatioX;
    private float aspectRatioY;
    //等比缩放默认值索引,默认原图比例
    private int selectedByDefault;
    //等比缩放值表,默认1:1,3:4,原图比例,3:2,16:9
    private AspectRatio []aspectRatio;
    //是否允许改变裁剪大小
    private boolean freestyleCropEnabled;
    //是否显示裁剪框半透明椭圆浮层
    private boolean ovalDimmedLayer;
    //==========UCrop END==========

    public boolean isImage() {
        return image;
    }

    protected void setImage(boolean image) {
        this.image = image;
    }

    public Context getContext() {
        return context;
    }

    protected void setContext(Context context) {
        this.context = context;
    }

    public MediaType[] getFilterMimes() {
        return filterMimes;
    }

    protected void setFilterMimes(MediaType[] filterMimes) {
        this.filterMimes = filterMimes;
    }

    public List<MediaBean> getSelectedList() {
        return selectedList;
    }

    protected void setSelectedList(List<MediaBean> selectedList) {
        this.selectedList = selectedList;
    }

    public boolean isRadio() {
        return radio;
    }

    protected void setRadio(boolean radio) {
        this.radio = radio;
    }

    public int getMaxSize() {
        return maxSize;
    }

    protected void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public AbsImageLoader getImageLoader() {
        AbsImageLoader imageLoader = null;
        if(imageLoaderType == ImageLoaderType.PICASSO) {
            imageLoader = new PicassoImageLoader();
        }
        return imageLoader;
    }

    protected void setImageLoaderType(ImageLoaderType imageLoaderType) {
        this.imageLoaderType = imageLoaderType;
    }

    public PauseOnScrollListener getPauseOnScrollListener() {
        return pauseOnScrollListener;
    }

    protected void setPauseOnScrollListener(PauseOnScrollListener pauseOnScrollListener) {
        this.pauseOnScrollListener = pauseOnScrollListener;
    }

    public boolean isHideBottomControls() {
        return hideBottomControls;
    }

    public void setHideBottomControls(boolean hideBottomControls) {
        this.hideBottomControls = hideBottomControls;
    }

    public int getCompressionQuality() {
        return compressionQuality;
    }

    public void setCompressionQuality(int compressionQuality) {
        this.compressionQuality = compressionQuality;
    }

    public void setAllowedGestures(@UCropActivity.GestureTypes int []gestures) {
        this.gestures = gestures;
    }

    public int[] getAllowedGestures() {
        return gestures;
    }

    public int getMaxBitmapSize() {
        return maxBitmapSize;
    }

    public void setMaxBitmapSize(int maxBitmapSize) {
        this.maxBitmapSize = maxBitmapSize;
    }

    public float getMaxScaleMultiplier() {
        return maxScaleMultiplier;
    }

    public void setMaxScaleMultiplier(float maxScaleMultiplier) {
        this.maxScaleMultiplier = maxScaleMultiplier;
    }

    public float getAspectRatioX() {
        return aspectRatioX;
    }

    public void setAspectRatioX(float aspectRatioX) {
        this.aspectRatioX = aspectRatioX;
    }

    public float getAspectRatioY() {
        return aspectRatioY;
    }

    public void setAspectRatioY(float aspectRatioY) {
        this.aspectRatioY = aspectRatioY;
    }

    public void setAspectRatioOptions(int selectedByDefault, AspectRatio... aspectRatio) {
        this.selectedByDefault = selectedByDefault;
        this.aspectRatio = aspectRatio;
    }

    public int getSelectedByDefault() {
        return selectedByDefault;
    }

    public void setSelectedByDefault(int selectedByDefault) {
        this.selectedByDefault = selectedByDefault;
    }

    public AspectRatio[] getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(AspectRatio[] aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public boolean isFreestyleCropEnabled() {
        return freestyleCropEnabled;
    }

    public void setFreestyleCropEnabled(boolean freestyleCropEnabled) {
        this.freestyleCropEnabled = freestyleCropEnabled;
    }

    public boolean isOvalDimmedLayer() {
        return ovalDimmedLayer;
    }

    public void setOvalDimmedLayer(boolean ovalDimmedLayer) {
        this.ovalDimmedLayer = ovalDimmedLayer;
    }

    public boolean isCrop() {
        return crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }

    public RxBusResultSubscriber<BaseResultEvent> getResultSubscriber() {
        return resultSubscriber;
    }

    public void setResultSubscriber(@NonNull RxBusResultSubscriber<BaseResultEvent> resultSubscriber) {
        this.resultSubscriber = resultSubscriber;
    }
}
