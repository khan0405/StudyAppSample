package com.studygroup.studyappsample.app.common;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.*;

/**
 * background work controller
 * Created by KHAN on 2015-07-15.
 */
public class BackgroundWorker {
    ExecutorService executor;
    List<Future<?>> futures = new CopyOnWriteArrayList<Future<?>>();

    public BackgroundWorker() {
        executor = Executors.newFixedThreadPool(2);
    }

    public <T> Future<T> executeTask(Callable<T> task, NetworkListener<T> callback) {
        final Future<T> future = executor.submit(task);
        futures.add(future);
        futures.add(executor.submit(new TimeoutTask<T>(future, callback)));
        return future;
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    static class TimeoutTask<T> implements Runnable {
        private Future<T> future;
        private NetworkListener<T> callback;
        Handler handler;

        TimeoutTask(Future<T> future, NetworkListener<T> callback) {
            handler = new Handler(Looper.getMainLooper());
            this.future = future;
            this.callback = callback;
        }

        public void run() {
            try {
                callback.onSuccess(future.get(5000, TimeUnit.MILLISECONDS));
            } catch (Exception e) {
                callback.onFailure(e);
            }
        }

    }
}
