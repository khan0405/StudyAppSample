package com.studygroup.studyappsample.app.common;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.*;

/**
 * background work controller
 * Created by KHAN on 2015-07-15.
 */
public class BackgroundWorker {
    private ExecutorService executor;

    public BackgroundWorker() {
        executor = Executors.newFixedThreadPool(2);
    }

    public <T> Future<T> executeTask(Callable<T> task, NetworkListener<T> callback) {
        final Future<T> future = executor.submit(task);
        executor.submit(new TimeoutTask<T>(future, callback));
        return future;
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    static class TimeoutTask<T> implements Runnable {
        private Future<T> future;
        private NetworkListener<T> callback;
        private Handler handler = new Handler(Looper.getMainLooper());

        TimeoutTask(Future<T> future, NetworkListener<T> callback) {
            this.future = future;
            this.callback = callback;
        }

        public void run() {
            try {
                notifyCallback(future.get(5000, TimeUnit.MILLISECONDS), null);
            } catch (Exception e) {
                notifyCallback(null, e);
            }
        }

        void notifyCallback(final T data, final Exception e) {
            handler.post(new Runnable() {
               public void run() {
                   if (e != null) {
                       callback.onFailure(e);
                   }
                   else {
                       callback.onSuccess(data);
                   }
               }
            });
        }
    }
}
