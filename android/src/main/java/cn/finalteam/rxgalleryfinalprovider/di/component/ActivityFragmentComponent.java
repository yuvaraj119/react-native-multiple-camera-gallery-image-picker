package cn.finalteam.rxgalleryfinalprovider.di.component;

import cn.finalteam.rxgalleryfinalprovider.di.module.ActivityFragmentModule;
import cn.finalteam.rxgalleryfinalprovider.di.scope.RxGalleryFinalScope;
import cn.finalteam.rxgalleryfinalprovider.ui.activity.MediaActivity;
import cn.finalteam.rxgalleryfinalprovider.ui.fragment.MediaGridFragment;
import dagger.Component;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/15 上午1:17
 */
@Component(dependencies = RxGalleryFinalComponent.class, modules = ActivityFragmentModule.class)
@RxGalleryFinalScope
public interface ActivityFragmentComponent {
    void inject(MediaActivity mediaActivity);

    MediaGridFragment provideImageGridFragment();
}
