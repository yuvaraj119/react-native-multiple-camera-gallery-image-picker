package cn.finalteam.rxgalleryfinalprovider.rxbus;

import cn.finalteam.rxgalleryfinalprovider.rxbus.event.BaseResultEvent;
import cn.finalteam.rxgalleryfinalprovider.rxbus.event.ImageMultipleResultEvent;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/8/1 下午11:11
 */
public abstract class RxBusResultSubscriber<T extends BaseResultEvent> extends RxBusSubscriber<T>  {
}
