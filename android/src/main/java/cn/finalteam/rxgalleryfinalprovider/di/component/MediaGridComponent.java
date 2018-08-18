package cn.finalteam.rxgalleryfinalprovider.di.component;

import cn.finalteam.rxgalleryfinalprovider.di.module.MediaGridModule;
import cn.finalteam.rxgalleryfinalprovider.di.scope.MediaGridScope;
import cn.finalteam.rxgalleryfinalprovider.presenter.impl.MediaGridPresenterImpl;
import cn.finalteam.rxgalleryfinalprovider.ui.fragment.MediaGridFragment;
import dagger.Component;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/18 下午5:08
 */
@MediaGridScope
@Component(dependencies = RxGalleryFinalComponent.class, modules = MediaGridModule.class)
public interface MediaGridComponent {
    void inject(MediaGridFragment mediaGridFragment);

    MediaGridPresenterImpl provideMediaGridPresenter();
}
