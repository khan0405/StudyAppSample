package com.studygroup.studyappsample.app.common;

/**
 * Network callback
 * Created by KHAN on 2015-07-15.
 */
public interface NetworkListener<T> {
    void onSuccess(T result);
    void onFailure(Throwable e);
}
