package cn.finalteam.rxgalleryfinalprovider.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import cn.finalteam.rxgalleryfinalprovider.BuildConfig;
import cn.finalteam.rxgalleryfinalprovider.Configuration;
import cn.finalteam.rxgalleryfinalprovider.R;
import cn.finalteam.rxgalleryfinalprovider.RxGalleryFinal;
import cn.finalteam.rxgalleryfinalprovider.anim.Animation;
import cn.finalteam.rxgalleryfinalprovider.anim.AnimationListener;
import cn.finalteam.rxgalleryfinalprovider.anim.SlideInUnderneathAnimation;
import cn.finalteam.rxgalleryfinalprovider.anim.SlideOutUnderneathAnimation;
import cn.finalteam.rxgalleryfinalprovider.bean.BucketBean;
import cn.finalteam.rxgalleryfinalprovider.bean.ImageCropBean;
import cn.finalteam.rxgalleryfinalprovider.bean.MediaBean;
import cn.finalteam.rxgalleryfinalprovider.di.component.DaggerMediaGridComponent;
import cn.finalteam.rxgalleryfinalprovider.di.component.MediaGridComponent;
import cn.finalteam.rxgalleryfinalprovider.di.component.RxGalleryFinalComponent;
import cn.finalteam.rxgalleryfinalprovider.di.module.MediaGridModule;
import cn.finalteam.rxgalleryfinalprovider.presenter.impl.MediaGridPresenterImpl;
import cn.finalteam.rxgalleryfinalprovider.rxbus.RxBus;
import cn.finalteam.rxgalleryfinalprovider.rxbus.RxBusSubscriber;
import cn.finalteam.rxgalleryfinalprovider.rxbus.event.CloseMediaViewPageFragmentEvent;
import cn.finalteam.rxgalleryfinalprovider.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinalprovider.rxbus.event.MediaCheckChangeEvent;
import cn.finalteam.rxgalleryfinalprovider.rxbus.event.OpenMediaPageFragmentEvent;
import cn.finalteam.rxgalleryfinalprovider.rxbus.event.OpenMediaPreviewFragmentEvent;
import cn.finalteam.rxgalleryfinalprovider.rxbus.event.RequestStorageReadAccessPermissionEvent;
import cn.finalteam.rxgalleryfinalprovider.rxbus.event.SendMediaPageFragmentDataEvent;
import cn.finalteam.rxgalleryfinalprovider.ui.activity.MediaActivity;
import cn.finalteam.rxgalleryfinalprovider.ui.adapter.BucketAdapter;
import cn.finalteam.rxgalleryfinalprovider.ui.adapter.MediaGridAdapter;
import cn.finalteam.rxgalleryfinalprovider.ui.widget.FooterAdapter;
import cn.finalteam.rxgalleryfinalprovider.ui.widget.HorizontalDividerItemDecoration;
import cn.finalteam.rxgalleryfinalprovider.ui.widget.MarginDecoration;
import cn.finalteam.rxgalleryfinalprovider.ui.widget.RecyclerViewFinal;
import cn.finalteam.rxgalleryfinalprovider.utils.CameraUtils;
import cn.finalteam.rxgalleryfinalprovider.utils.EmptyViewUtils;
import cn.finalteam.rxgalleryfinalprovider.utils.FilenameUtils;
import cn.finalteam.rxgalleryfinalprovider.utils.Logger;
import cn.finalteam.rxgalleryfinalprovider.utils.MediaScanner;
import cn.finalteam.rxgalleryfinalprovider.utils.MediaUtils;
import cn.finalteam.rxgalleryfinalprovider.utils.PermissionCheckUtils;
import cn.finalteam.rxgalleryfinalprovider.utils.ThemeUtils;
import cn.finalteam.rxgalleryfinalprovider.view.MediaGridView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/7 上午10:02
 */
public class MediaGridFragment extends BaseFragment implements MediaGridView, RecyclerViewFinal.OnLoadMoreListener,
        FooterAdapter.OnItemClickListener, View.OnClickListener, MediaScanner.ScanCallback, BucketAdapter.OnRecyclerViewItemClickListener {

    private final String IMAGE_STORE_FILE_NAME = "IMG_%s.jpg";
    private final int TAKE_IMAGE_REQUEST_CODE = 1001;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1002;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 106;
    public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 104;

    private final String TAKE_URL_STORAGE_KEY = "take_url_storage_key";
    private final String BUCKET_ID_KEY = "bucket_id_key";

    private final int LIMIT = 23;

    @Inject
    MediaGridPresenterImpl mMediaGridPresenter;
    @Inject
    Configuration mConfiguration;
    @Inject
    DisplayMetrics mScreenSize;

    private List<MediaBean> mMediaBeanList;
    private MediaGridAdapter mMediaGridAdapter;
    private RecyclerViewFinal mRvMedia;
    private LinearLayout mLlEmptyView;
    private RecyclerView mRvBucket;
    private BucketAdapter mBucketAdapter;
    private RelativeLayout mRlBucektOverview;
    private List<BucketBean> mBucketBeanList;
    private TextView mTvFolderName;
    private TextView mTvPreview;
    private RelativeLayout mRlRootView;

    private MediaScanner mMediaScanner;

    private int mPage = 1;
    private File mImageStoreDir;
    private File mImageStoreCropDir;
    private String mImagePath;

    private String mBucketId = String.valueOf(Integer.MIN_VALUE);

    private MediaActivity mMediaActivity;
    private Subscription mSubscrMediaCheckChangeEvent;
    private Subscription mSubscrCloseMediaViewPageFragmentEvent;
    private Subscription mSubscrRequestStorageReadAccessPermissionEvent;

    public static MediaGridFragment newInstance() {
        return new MediaGridFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MediaActivity) {
            mMediaActivity = (MediaActivity) context;
        }
        //mImageStoreDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mImageStoreDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/RxGalleryFinal/");
        mImageStoreCropDir = new File(mImageStoreDir, "crop");
        if (!mImageStoreCropDir.exists()) {
            mImageStoreCropDir.mkdirs();
        }
        mMediaScanner = new MediaScanner(context);
    }

    @Override
    public int getContentView() {
        return R.layout.gallery_fragment_media_grid;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvMedia = (RecyclerViewFinal) view.findViewById(R.id.rv_media);
        mLlEmptyView = (LinearLayout) view.findViewById(R.id.ll_empty_view);
        mRvBucket = (RecyclerView) view.findViewById(R.id.rv_bucket);
        mRlBucektOverview = (RelativeLayout) view.findViewById(R.id.rl_bucket_overview);
        mRlRootView = (RelativeLayout) view.findViewById(R.id.rl_root_view);

        mRvMedia.setEmptyView(mLlEmptyView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRvMedia.addItemDecoration(new MarginDecoration(getContext()));
        mRvMedia.setLayoutManager(gridLayoutManager);
        mRvMedia.setOnLoadMoreListener(this);
        mRvMedia.setFooterViewHide(true);

        if (mConfiguration.getPauseOnScrollListener() != null) {
            mRvMedia.addOnScrollListener(mConfiguration.getPauseOnScrollListener());
            mRvBucket.addOnScrollListener(mConfiguration.getPauseOnScrollListener());
        }

        mTvFolderName = (TextView) view.findViewById(R.id.tv_folder_name);
        mTvFolderName.setOnClickListener(this);
        mTvPreview = (TextView) view.findViewById(R.id.tv_preview);
        mTvPreview.setOnClickListener(this);
        mTvPreview.setEnabled(false);
        if (mConfiguration.isRadio()) {
            view.findViewById(R.id.tv_preview_vr).setVisibility(View.GONE);
            mTvPreview.setVisibility(View.GONE);
        }

        mMediaBeanList = new ArrayList<>();
        mMediaGridAdapter = new MediaGridAdapter(getContext(), mMediaBeanList, mMediaActivity.getCheckedList(),
                mScreenSize.widthPixels, mConfiguration);
        mRvMedia.setAdapter(mMediaGridAdapter);

        mMediaGridPresenter.setMediaGridView(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRvBucket.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .color(getResources().getColor(R.color.gallery_bucket_list_item_normal_color))
                .size(getResources().getDimensionPixelSize(R.dimen.gallery_divider_decoration_height))
                .margin(getResources().getDimensionPixelSize(R.dimen.gallery_bucket_margin),
                        getResources().getDimensionPixelSize(R.dimen.gallery_bucket_margin))
                .build());
        mRvBucket.setLayoutManager(linearLayoutManager);
        mBucketBeanList = new ArrayList<>();
        mBucketAdapter = new BucketAdapter(getContext(), mBucketBeanList, mConfiguration);
        mRvBucket.setAdapter(mBucketAdapter);
        mRvMedia.setOnItemClickListener(this);
        mMediaGridPresenter.getBucketList();
        mBucketAdapter.setOnRecyclerViewItemClickListener(this);

        mRlBucektOverview.setVisibility(View.INVISIBLE);
        new SlideInUnderneathAnimation(mRvBucket)
                .setDirection(Animation.DIRECTION_DOWN)
                .animate();

        subscribeEvent();

        Activity activity = mMediaActivity;
        if (activity == null) {
            activity = getActivity();
        }

        String requestStorageAccessPermissionTips = ThemeUtils.resolveString(getContext(),
                R.attr.gallery_request_storage_access_permission_tips,
                R.string.gallery_default_request_storage_access_permission_tips);
        boolean success = PermissionCheckUtils.checkReadExternalPermission(activity, requestStorageAccessPermissionTips,
                MediaActivity.REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        if (success) {
            mMediaGridPresenter.getMediaList(mBucketId, mPage, LIMIT);
        }
    }

    private void subscribeEvent() {
        mSubscrMediaCheckChangeEvent = RxBus.getDefault().toObservable(MediaCheckChangeEvent.class)
                .subscribe(new RxBusSubscriber<MediaCheckChangeEvent>() {
                    @Override
                    protected void onEvent(MediaCheckChangeEvent mediaCheckChangeEvent) {
                        if (mMediaActivity.getCheckedList().size() == 0) {
                            mTvPreview.setEnabled(false);
                        } else {
                            mTvPreview.setEnabled(true);
                        }

                    }
                });
        RxBus.getDefault().add(mSubscrMediaCheckChangeEvent);

        mSubscrCloseMediaViewPageFragmentEvent = RxBus.getDefault().toObservable(CloseMediaViewPageFragmentEvent.class)
                .subscribe(new RxBusSubscriber<CloseMediaViewPageFragmentEvent>() {
                    @Override
                    protected void onEvent(CloseMediaViewPageFragmentEvent closeMediaViewPageFragmentEvent) throws Exception {
                        mMediaGridAdapter.notifyDataSetChanged();
                    }
                });
        RxBus.getDefault().add(mSubscrCloseMediaViewPageFragmentEvent);

        mSubscrRequestStorageReadAccessPermissionEvent = RxBus.getDefault().toObservable(RequestStorageReadAccessPermissionEvent.class)
                .subscribe(new RxBusSubscriber<RequestStorageReadAccessPermissionEvent>() {
                    @Override
                    protected void onEvent(RequestStorageReadAccessPermissionEvent requestStorageReadAccessPermissionEvent) throws Exception {
                        if (requestStorageReadAccessPermissionEvent.isSuccess()) {
                            mMediaGridPresenter.getMediaList(mBucketId, mPage, LIMIT);
                        } else {
                            getActivity().finish();
                        }
                    }
                });
        RxBus.getDefault().add(mSubscrRequestStorageReadAccessPermissionEvent);

    }

    @Override
    public void setTheme() {
        super.setTheme();
        int pageColor = ThemeUtils.resolveColor(getContext(), R.attr.gallery_page_bg, R.color.gallery_default_page_bg);
        mRlRootView.setBackgroundColor(pageColor);
    }

    @Override
    protected void setupComponent(RxGalleryFinalComponent rxGalleryFinalComponent) {
        MediaGridComponent mediaGridComponent = DaggerMediaGridComponent.builder()
                .rxGalleryFinalComponent(RxGalleryFinal.getRxGalleryFinalComponent())
                .mediaGridModule(new MediaGridModule(getContext(), true))
                .build();
        mediaGridComponent.inject(this);
    }

    @Override
    public void loadMore() {
        mMediaGridPresenter.getMediaList(mBucketId, mPage, LIMIT);
    }

    @Override
    public void onRequestMediaCallback(List<MediaBean> list) {
        if (mPage == 1 && TextUtils.equals(mBucketId, String.valueOf(Integer.MIN_VALUE))) {
            MediaBean takePhotoBean = new MediaBean();
            takePhotoBean.setId(Integer.MIN_VALUE);
            takePhotoBean.setBucketId(String.valueOf(Integer.MIN_VALUE));
            mMediaBeanList.add(takePhotoBean);
        }
        if (list != null && list.size() > 0) {
            mMediaBeanList.addAll(list);
            Logger.i(String.format("得到:%s张图片", list.size()));
        } else {
            Logger.i("没有更多图片");
        }
        mMediaGridAdapter.notifyDataSetChanged();

        mPage++;

        if (list == null || list.size() < LIMIT) {
            mRvMedia.setFooterViewHide(true);
            mRvMedia.setHasLoadMore(false);
        } else {
            mRvMedia.setFooterViewHide(false);
            mRvMedia.setHasLoadMore(true);
        }

        if (mMediaBeanList.size() == 0) {
            String mediaEmptyTils = ThemeUtils.resolveString(getContext(), R.attr.gallery_media_empty_tips, R.string.gallery_default_media_empty_tips);
            EmptyViewUtils.showMessage(mLlEmptyView, mediaEmptyTils);
        }

        mRvMedia.onLoadMoreComplete();
    }

    @Override
    public void onRequestBucketCallback(List<BucketBean> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        mBucketBeanList.addAll(list);
        mBucketAdapter.setSelectedBucket(list.get(0));
        mBucketAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        BucketBean bucketBean = mBucketBeanList.get(position);
        String bucketId = bucketBean.getBucketId();
        mRlBucektOverview.setVisibility(View.GONE);
        if (TextUtils.equals(mBucketId, bucketId)) {
            return;
        }
        mBucketId = bucketId;
        EmptyViewUtils.showLoading(mLlEmptyView);
        mRvMedia.setHasLoadMore(false);
        mMediaBeanList.clear();
        mMediaGridAdapter.notifyDataSetChanged();
        mBucketAdapter.setSelectedBucket(bucketBean);

        mRvMedia.setFooterViewHide(true);
        mPage = 1;
        mMediaGridPresenter.getMediaList(mBucketId, mPage, LIMIT);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, int position) {
        MediaBean mediaBean = mMediaBeanList.get(position);
        if (mediaBean.getId() == Integer.MIN_VALUE) {

            if (!CameraUtils.hasCamera(getContext())) {
                Toast.makeText(getContext(), R.string.gallery_device_no_camera_tips, Toast.LENGTH_SHORT).show();
                return;
            }

            openCamera();

        } else {
            if (mConfiguration.isRadio()) {
                if (!mConfiguration.isCrop()) {
                    ImageCropBean bean = new ImageCropBean();
                    bean.copyMediaBean(mediaBean);
                    RxBus.getDefault().post(new ImageRadioResultEvent(bean));
                    getActivity().finish();
                } else {
                    String ext = FilenameUtils.getExtension(mediaBean.getOriginalPath());
                    Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
                    if (ext != null && TextUtils.equals(ext.toLowerCase(), "png")) {
                        format = Bitmap.CompressFormat.PNG;
                    } else if (ext != null && TextUtils.equals(ext.toLowerCase(), "webp")) {
                        format = Bitmap.CompressFormat.WEBP;
                    }
                    try {
                        String originalPath = mediaBean.getOriginalPath();
                        File file = new File(originalPath);
                        UCrop uCrop = UCrop.of(mediaBean, Uri.fromFile(new File(mImageStoreCropDir, file.getName())));
                        uCrop = uCrop.useSourceImageAspectRatio();
                        UCrop.Options options = new UCrop.Options();
                        options.setHideBottomControls(mConfiguration.isHideBottomControls());
                        options.setCompressionFormat(format);
                        if (mConfiguration.getCompressionQuality() != 0) {
                            options.setCompressionQuality(mConfiguration.getCompressionQuality());
                        }

                        if (mConfiguration.getMaxBitmapSize() != 0) {
                            options.setMaxBitmapSize(mConfiguration.getMaxBitmapSize());
                        }

                        int[] gestures = mConfiguration.getAllowedGestures();
                        if (gestures != null && gestures.length == 3) {
                            options.setAllowedGestures(gestures[0], gestures[1], gestures[2]);
                        }
                        if (mConfiguration.getMaxScaleMultiplier() != 0) {
                            options.setMaxScaleMultiplier(mConfiguration.getMaxScaleMultiplier());
                        }
                        //设置等比缩放
                        if (mConfiguration.getAspectRatioX() != 0 && mConfiguration.getAspectRatioY() != 0) {
                            options.withAspectRatio(mConfiguration.getAspectRatioX(), mConfiguration.getAspectRatioY());
                        }
                        //设置等比缩放默认值索引及等比缩放值列表
                        if (mConfiguration.getAspectRatio() != null && mConfiguration.getSelectedByDefault() > mConfiguration.getAspectRatio().length) {
                            options.setAspectRatioOptions(mConfiguration.getSelectedByDefault(), mConfiguration.getAspectRatio());
                        }
                        options.setFreeStyleCropEnabled(mConfiguration.isFreestyleCropEnabled());
                        options.setOvalDimmedLayer(mConfiguration.isOvalDimmedLayer());

                        uCrop = uCrop.withOptions(options);
                        uCrop.start(getActivity());

                    } catch (Exception e) {
                        Logger.e(e);
                    }
                }
            } else {
                RxBus.getDefault().post(new OpenMediaPageFragmentEvent());
                MediaBean firstBean = mMediaBeanList.get(0);
                List<MediaBean> gridMediaList = mMediaBeanList;
                int pos = position;
                if (firstBean.getId() == Integer.MIN_VALUE) {
                    pos = position - 1;
                    gridMediaList = mMediaBeanList.subList(1, mMediaBeanList.size());
                }
                RxBus.getDefault().postSticky(new SendMediaPageFragmentDataEvent(gridMediaList, pos));
            }
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_STORAGE);
        } else {
            Log.e("else", "else");
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (captureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
                String filename = String.format(IMAGE_STORE_FILE_NAME, dateFormat.format(new Date()));
                //File photoFile = new File(mImageStoreDir, filename);
                Log.e("filename", filename);
                Log.e("mImageStoreDir", mImageStoreDir.getAbsolutePath());
                File photoFile = null;
                try {
                    photoFile = File.createTempFile(
                            filename,  /* prefix */
                            ".jpg",         /* suffix */
                            mImageStoreDir      /* directory */
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    mImagePath = photoFile.getAbsolutePath();
                    Log.e("photoFile", photoFile.getAbsolutePath());
                    if (mImagePath != null) {
                        Uri photoURI = null;
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                            photoURI = Uri.fromFile(photoFile);
                        } else {
                            Log.e("photoUri", BuildConfig.APPLICATION_ID);
                            photoURI = FileProvider.getUriForFile(getActivity(),
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    photoFile);
                            Log.e("photoUri", photoURI.toString());
                        }

                        //captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mImagePath)));
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(captureIntent, TAKE_IMAGE_REQUEST_CODE);
                    } else {
                        Log.e("photoFile", "is null");
                    }
                }
            } else {
                Toast.makeText(getContext(), R.string.gallery_device_camera_unable, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i("onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == TAKE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Logger.i(String.format("拍照成功,图片存储路径:%s", mImagePath));

            //刷新相册数据库
            mMediaScanner.scanFile(mImagePath, "image/jpeg", this);
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            openCamera();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mImagePath)) {
            outState.putString(TAKE_URL_STORAGE_KEY, mImagePath);
        }
        if (!TextUtils.isEmpty(mBucketId)) {
            outState.putString(BUCKET_ID_KEY, mBucketId);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        mImagePath = savedInstanceState.getString(TAKE_URL_STORAGE_KEY);
        mBucketId = savedInstanceState.getString(BUCKET_ID_KEY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaScanner.unScanFile();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_preview) {
            RxBus.getDefault().post(new OpenMediaPreviewFragmentEvent());
        } else if (id == R.id.tv_folder_name) {
            v.setEnabled(false);
            int visibility = mRlBucektOverview.getVisibility();
            if (visibility == View.VISIBLE) {
                new SlideOutUnderneathAnimation(mRvBucket)
                        .setDirection(Animation.DIRECTION_DOWN)
                        .setDuration(Animation.DURATION_DEFAULT)
                        .setListener(new AnimationListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                v.setEnabled(true);
                                mRlBucektOverview.setVisibility(View.GONE);
                            }
                        })
                        .animate();
            } else {
                mRlBucektOverview.setVisibility(View.VISIBLE);
                new SlideInUnderneathAnimation(mRvBucket)
                        .setDirection(Animation.DIRECTION_DOWN)
                        .setDuration(Animation.DURATION_DEFAULT)
                        .setListener(new AnimationListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                v.setEnabled(true);
                            }
                        })
                        .animate();
            }
        }
    }

    @Override
    public void onScanCompleted(String[] images) {
        if (images == null || images.length == 0) {
            Logger.i("images empty");
            return;
        }

        Observable.create((Observable.OnSubscribe<MediaBean>) new Observable.OnSubscribe<MediaBean>() {
            @Override
            public void call(Subscriber<? super MediaBean> subscriber) {
                MediaBean mediaBean = MediaUtils.getMediaBeanWithImage(MediaGridFragment.this.getContext(), images[0]);
                subscriber.onNext(mediaBean);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MediaBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i("获取MediaBean异常");
                    }

                    @Override
                    public void onNext(MediaBean mediaBean) {
                        if (!isDetached() && mediaBean != null) {
                            mMediaBeanList.add(1, mediaBean);
                            mMediaGridAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getDefault().remove(mSubscrMediaCheckChangeEvent);
        RxBus.getDefault().remove(mSubscrCloseMediaViewPageFragmentEvent);
    }
}
