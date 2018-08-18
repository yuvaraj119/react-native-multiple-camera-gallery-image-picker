package cn.finalteam.rxgalleryfinalprovider.di.component;

import android.util.DisplayMetrics;

import javax.inject.Singleton;

import cn.finalteam.rxgalleryfinalprovider.Configuration;
import cn.finalteam.rxgalleryfinalprovider.RxGalleryFinal;
import cn.finalteam.rxgalleryfinalprovider.di.module.RxGalleryFinalModule;
import dagger.Component;

/**
 * Desction:生产组件
 * Author:pengjianbo
 * Date:16/5/7 下午3:56
 */
@Singleton//声明为单例
@Component(modules = {RxGalleryFinalModule.class})
public interface RxGalleryFinalComponent {

    //可以应用范围,需要在多个地方应用需要通过添加多个inject方法(通过Component依赖也可以)
    void inject(RxGalleryFinal rxGalleryFinal);

    //在这里要定义生成,再modules上要做相应的@provides
    Configuration provideConfiguration();

    DisplayMetrics provideScreenPix();

}
