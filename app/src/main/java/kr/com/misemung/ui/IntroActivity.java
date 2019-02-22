package kr.com.misemung.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import kr.com.misemung.R;
import kr.com.misemung.common.CommonPopup;
import kr.com.misemung.realm.entity.CityRecord;
import kr.com.misemung.realm.repository.CityRepository;
import kr.com.misemung.util.NetworkConnections;

import static kr.com.misemung.ui.MainActivity.getNearStation;

public class IntroActivity extends AppCompatActivity {

    private Dialog mNetworkErrorDialog;

    private final int HANDLER_INTRO_START = 0;

    private ProgressBar loadingProgressBar;

    @SuppressLint("HandlerLeak")
    private Handler mIntroHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        startIntro();

    }

    private void startIntro() {
        if (NetworkConnections.isConnected() && NetworkConnections.isOnline()) {
            if (getPermission()) {
                // 2초 후 인트로 액티비티 제거
                mIntroHandler = new Handler();
                mIntroHandler.postDelayed(this::goNextActivity, 1000);
            } else {
                // 퍼미션 요청
                getDeviceLocation();
            }
        } else {
            showNetworkErrorPopup();
        }
    }

    private boolean getPermission() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            Log.d("IntroActivity", "getPermission");
            String[] checkPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            boolean locationPermission = this.checkSelfPermission(checkPermissions[0]) == PackageManager.PERMISSION_GRANTED;

            List<String> list = new ArrayList<>();
            if (!locationPermission) {
                list.add(checkPermissions[0]);
            }

            return list.size() <= 0;
        } else {
            return true;
        }
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                for (String permission : permissions) {
                    if (ContextCompat.checkSelfPermission(this, permission)
                            == PackageManager.PERMISSION_GRANTED) {
                        goNextActivity();
                    } else {
                        reCheckPermissions();
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void reCheckPermissions() {
        // 권한 허용 체크
        boolean locationPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        String errMsg = "";
        if(!locationPermission) {
            errMsg = getString(R.string.location_permission_error);

        }

        CommonPopup.showConfirmDialog(this, getString(R.string.noti_popup_title), errMsg, view -> finish());
    }

    /**
     * 특정 시간동안 Intro 노출 후 다음 화면으로 이동
     */
    private void goNextActivity() {
        //인트로 화면 노출
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 네트워크 불가상태 알림 팝업
     */
    private void showNetworkErrorPopup() {
        mNetworkErrorDialog = CommonPopup.showConfirmDialog(this,
                getString(R.string.noti_popup_title),
                getString(R.string.network_error_msg),
                view -> {
                    mNetworkErrorDialog.dismiss();
                    IntroActivity.this.finish();
                });
    }

}
