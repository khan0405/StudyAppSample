package com.studygroup.studyappsample.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;
import com.studygroup.studyappsample.app.common.*;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Splash Activity
 * Created by KHAN on 2015-07-15.
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME = 1500l;
    private static final long BACK_BUTTON_FINISH_TIME = 1500l;

    private ProgressDialog dialog;

    private Handler handler = new Handler(Looper.getMainLooper());

    private BackgroundWorker worker = new BackgroundWorker();

    private static final int SERVER_CHECK = 0;
    private static final int LOGIN_FAILURE = 1;
    private static final int LOGIN_SUCCESS = 2;

    enum ErrorType {
        LOW_VERSION,
        NETWORK_UNAVAILABLE,
        SERVER_ERROR
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initializeProgress();

        processAppTask();
    }

    void processAppTask() {
        // 1. 네트워크 체크
        if (!DummyApiManager.isNetworkConnected(getApplicationContext())) {
            handleError(ErrorType.NETWORK_UNAVAILABLE, null);
            return;
        }

        // 2. 버전 체크
        if (!VersionUtil.isAvailableVersion(VersionUtil.getAppVersion(getApplicationContext()), Const.APP_VERSION)) {
            handleError(ErrorType.LOW_VERSION, Uri.parse("market://details?id=" + getPackageName()));
            return;
        }

        // 3. 가상통신
        worker.executeTask(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return DummyApiManager.doCallApi();
            }
        }, new NetworkListener<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                Class<?> c = null;
                switch (result) {
                    case SERVER_CHECK:
                        handleError(ErrorType.SERVER_ERROR, null);
                        return;
                    case LOGIN_FAILURE:
                        c = JoinActivity.class;
                        break;
                    case LOGIN_SUCCESS:
                        c = MainActivity.class;
                        break;
                }
                moveActivity(c);
            }

            @Override
            public void onFailure(Throwable e) {
                moveActivity(JoinActivity.class);
            }
        });
    }

    private void handleError(ErrorType type, final Object data) {
        isShowDialog.set(false);
        dialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogInterface.OnClickListener btnFinishListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SplashActivity.this.finish();
            }
        };

        builder.setPositiveButton(R.string.btn_ok, btnFinishListener);

        int messageId = 0;
        switch(type) {
            case LOW_VERSION:
                // 최신버전 있음
                messageId = R.string.err_need_update;
                builder.setPositiveButton(R.string.btn_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 업뎃 고
                        Intent intent = new Intent(Intent.ACTION_VIEW, ((Uri) data));
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.btn_cancel, btnFinishListener);
                break;
            case NETWORK_UNAVAILABLE:
                // 네트웍 안됨
                messageId = R.string.err_network_unavailable;
                break;
            case SERVER_ERROR:
                // 서버에러
                messageId = R.string.err_check_server;
                break;
        }

        builder.setCancelable(false);
        builder.setMessage(messageId);
        builder.create().show();
    }

    public void moveActivity(Class<?> c) {
        Intent intent = new Intent(SplashActivity.this, c);
        startActivity(intent);
        finish();
    }

    /**
     * 다이얼로그를 보여줄 것인지에 대한 변수
     */
    private AtomicBoolean isShowDialog = new AtomicBoolean(true);
    /**
     * 프로그레스 다이얼로그 세팅
     */
    private void initializeProgress() {
        // 프로그레스 다이얼로그 세팅
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    SplashActivity.this.onBackPressed();
                }
                return true;
            }
        });

        // 일정시간 뒤 프로그레스 보여줌
        handler.postDelayed(new Runnable() {
            public void run() {
                if (dialog != null && isShowDialog.get()) {
                    dialog.show();
                }
            }
        }, SPLASH_TIME);
    }

    /**
     * 프로그레스 다이얼로그 캔슬
     */
    void cancelDialog() {
        isShowDialog.set(false);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelDialog();
        worker.shutdown();
    }

    /** 백버튼 이벤트 */
    private AtomicBoolean backPressed = new AtomicBoolean(false);
    @Override
    public void onBackPressed() {
        if (!backPressed.get()) {
            backPressed.set(true);
            // 종료할거냐고 토스트 보여줌
            Toast.makeText(this, R.string.try_exit, Toast.LENGTH_SHORT).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed.set(false);
                }
            }, BACK_BUTTON_FINISH_TIME);
        }
        else {
            super.onBackPressed();
        }
    }
}
