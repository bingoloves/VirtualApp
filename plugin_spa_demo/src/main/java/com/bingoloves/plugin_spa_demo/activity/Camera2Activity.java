package com.bingoloves.plugin_spa_demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.camera.AutoFitTextureView;
import com.bingoloves.plugin_spa_demo.camera.CameraHelper;
import com.bingoloves.plugin_spa_demo.camera.ICamera2;
import com.bingoloves.plugin_spa_demo.camera.IVideoControl;
import com.bingoloves.plugin_spa_demo.camera.VideoPlayer;
import com.gyf.immersionbar.ImmersionBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.util.V;
import cn.cqs.im.base.BaseActivity;

/**
 * Created by bingo on 2020/11/30.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/30
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Activity extends BaseActivity implements ICamera2.TakePhotoListener,SensorEventListener,ICamera2.CameraReady, IVideoControl.PlaySeekTimeListener {
    @BindView(R.id.textureView)
    AutoFitTextureView mTextureView;
    @BindView(R.id.toolbar)
    CustomToolbar toolbar;
    //底部操作视图
    @BindView(R.id.rl_opt)
    RelativeLayout optRl;
    @BindView(R.id.iv_photo)
    ImageView previewIv;
    @BindView(R.id.iv_save)
    ImageView saveIv;
    @BindView(R.id.iv_delete)
    ImageView deleteIv;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    @OnClick({R.id.iv_change_camera,R.id.btn_takePhoto,R.id.iv_save,R.id.iv_delete})
    public void clickEvent(View view){
        switch (view.getId()){
            case R.id.iv_change_camera:
                toast("切换摄像头");
                if (mNowCameraType == ICamera2.CameraType.FRONT) {
                    cameraHelper.switchCamera(ICamera2.CameraType.BACK);
                    mNowCameraType = ICamera2.CameraType.BACK;
                } else {
                    cameraHelper.switchCamera(ICamera2.CameraType.FRONT);
                    mNowCameraType = ICamera2.CameraType.FRONT;
                }
                //mCameraTouch.resetScale();
                break;
            case R.id.btn_takePhoto:
                if (hasRecordClick) {
                    return;
                }
                hasRecordClick = true;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
                        (this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    LogUtils.e("cameraOnClickListener: 动态权限获取失败...");
                    return;
                }
                //拍照
                if (NOW_MODE == VIDEO_TAKE_PHOTO && mCameraPath!=null) {
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    cameraHelper.setDeviceRotation(rotation);
                    cameraHelper.takePhone(mCameraPath, ICamera2.MediaType.JPEG);
                }
                hasRecordClick = false;
                break;
            case R.id.iv_save:
                final Intent data;
                data = new Intent();
                if (NOW_MODE == VIDEO_TAKE_PHOTO){
                    data.putExtra("path", mCameraPath);
                    data.putExtra("mediaType", "image");
                    saveMedia(new File(mCameraPath));
                } else if (NOW_MODE == VIDEO_RECORD_MODE) {
                    data.putExtra("path", mVideoPath);
                    data.putExtra("mediaType", "video");
                    saveMedia(new File(mVideoPath));
                }
                setResult(RESULT_OK, data);
                finish();
                break;
            case R.id.iv_delete:
                if (TEXTURE_STATE == TEXTURE_PLAY_STATE) {
                    mVideoPlayer.stop();
                    cameraHelper.startBackgroundThread();
                    cameraHelper.openCamera(mNowCameraType);
                    File file = new File(mVideoPath);
                    if (file.exists()){
                        file.delete();
                    }
                } else if (TEXTURE_STATE == TEXTURE_PHOTO_STATE) {
                    File file = new File(mCameraPath);
                    if (file.exists()){
                        file.delete();
                    }
                    cameraHelper.resumePreview();
                    mTextureView.setVisibility(View.VISIBLE);
                    previewIv.setVisibility(View.GONE);
                    optRl.setVisibility(View.VISIBLE);
                    saveIv.setVisibility(View.GONE);
                    deleteIv.setVisibility(View.GONE);
                }
                initData();
                TEXTURE_STATE = TEXTURE_PREVIEW_STATE;
                break;
             default:
                 break;
        }
    }
    /**
     * 拍照工具类
     */
    private CameraHelper cameraHelper;
    /**
     * 摄像头模式
     */
    public final static int CAMERA_MODE = 0;
    /**
     * 视频播放器模式
     */
    public final static int VIDEO_MODE = 1;
    /**
     * 视频录像模式
     */
    public final static int VIDEO_RECORD_MODE = 1;
    /**
     * 拍照模式
     */
    public final static int VIDEO_TAKE_PHOTO = 2;
    /**
     * 当前面板是预览状态
     */
    public final static int TEXTURE_PREVIEW_STATE = 0;

    /**
     * 当前面板是录像状态
     */
    public final static int TEXTURE_RECORD_STATE = 1;

    /**
     * 当前面板是图片状态
     */
    public final static int TEXTURE_PHOTO_STATE = 2;
    /**
     * 当前面板是视频播放状态
     */

    public final static int TEXTURE_PLAY_STATE = 3;
    /**
     * 当前的显示面板状态
     */
    public int TEXTURE_STATE = TEXTURE_PREVIEW_STATE;
    /**
     * 视频播放器
     */
    private VideoPlayer mVideoPlayer;
    /**
     * 相机模式
     */
    private int MODE = CAMERA_MODE;
    /**
     * 视频保存路径
     */
    private String mVideoPath;

    /**
     * 当前拍照模式
     */
    private int NOW_MODE = VIDEO_TAKE_PHOTO;
    /**
     * 前 后 摄像头标识
     */
    private ICamera2.CameraType mNowCameraType = ICamera2.CameraType.BACK;
    /**
     * 单点标识
     */
    private boolean hasRecordClick = false;
    /**
     * 是否在 录制中
     */
    private boolean hasRecording = false;
    /**
     * 是否正在播放 标识
     */
    private boolean hasPlaying = false;
    /**
     * 图片路径
     */
    private String mCameraPath;
    /**
     * 是否有拍照权限
     */
    private boolean isNoPremissionPause;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera2;
    }

    @Override
    protected void initData() {

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(toolbar).init();
        toolbar.setCenterTitle("相机2");
        toolbar.back(v -> finish());
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
        mVideoPlayer = new VideoPlayer();
        //设置时间戳回调
        mVideoPlayer.setPlaySeekTimeListener(this);
        if (MODE == CAMERA_MODE) {
            //摄像头模式
            initCameraMode();
        } else if (MODE == VIDEO_MODE) {
            //视频播放模式
            mVideoPath = getIntent().getStringExtra("videoPath");
            //initVideoMode();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        if (cameraHelper != null) {
            cameraHelper.startBackgroundThread();
        }

        if (mTextureView.isAvailable()) {
            if (MODE == CAMERA_MODE) {
                if (TEXTURE_STATE == TEXTURE_PREVIEW_STATE) {
                    //预览状态
                    initCamera(mNowCameraType);
                } else if (TEXTURE_STATE == TEXTURE_PLAY_STATE) {
                    //视频播放状态
                    mVideoPlayer.play();
                }
                mVideoPlayer.setVideoPlayWindow(new Surface(mTextureView.getSurfaceTexture()));
            }
        } else {
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    if (MODE == CAMERA_MODE) {
                        if (TEXTURE_STATE == TEXTURE_PREVIEW_STATE) {
                            //预览状态
                            initCamera(mNowCameraType);
                        } else if (TEXTURE_STATE == TEXTURE_PLAY_STATE) {
                            //视频播放状态
                            mVideoPlayer.play();
                        }
                        mVideoPlayer.setVideoPlayWindow(new Surface(mTextureView.getSurfaceTexture()));
                    } else if (MODE == VIDEO_MODE) {
                        mVideoPlayer.setVideoPlayWindow(new Surface(mTextureView.getSurfaceTexture()));
                        Log.e("videoPath", "path:" + mVideoPath);
                        mVideoPlayer.setDataSourceAndPlay(mVideoPath);
                        hasPlaying = true;
                        //视频播放状态
                        TEXTURE_STATE = TEXTURE_PLAY_STATE;
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isNoPremissionPause) {
            isNoPremissionPause = false;
            return;
        }
        Log.e("camera", "mode:" + MODE);
        if (MODE == CAMERA_MODE) {
            if (TEXTURE_STATE == TEXTURE_PREVIEW_STATE) {
                cameraHelper.closeCamera();
                cameraHelper.stopBackgroundThread();
            } else if (TEXTURE_STATE == TEXTURE_PLAY_STATE) {
                mVideoPlayer.pause();
            }
        }
    }

    /**
     * 初始化 拍照
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    private void initCameraMode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            isNoPremissionPause = true;
        }
        initCamera(mNowCameraType);
        cameraHelper = new CameraHelper(this);
        cameraHelper.setTakePhotoListener(this);
        cameraHelper.setCameraReady(this);
        mCameraPath = cameraHelper.getPhotoFilePath();
        mVideoPath = cameraHelper.getVideoFilePath();
        registerSensor();

    }

    /**
     * 初始化摄像头
     *
     * @param cameraType
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initCamera(ICamera2.CameraType cameraType) {
        if (cameraHelper == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cameraHelper.setTextureView(mTextureView);
        cameraHelper.openCamera(cameraType);
    }
    /**
     * 注册陀螺仪传感器
     */
    private void registerSensor() {
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        Sensor mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mSensor == null) {
            return;
        }
        mSensorManager.registerListener(this, mSensor, Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mLightSensor, Sensor.TYPE_LIGHT);
    }
    /**
     * 传感器继承方法 重力发生改变
     * 根据重力方向 动态旋转拍照图片角度(暂时关闭该方法)
     *
     * 使用以下方法
     * int rotation = getWindowManager().getDefaultDisplay().getRotation();
     * @param event event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
//            Log.e(TAG, "onSensorChanged: x: " + x +"   y: "+y +"  z : "+z);
            if (z > 55.0f) {
                //向左横屏
            } else if (z < -55.0f) {
                //向右横屏
            } else if (y > 60.0f) {
                //是倒竖屏
            } else {
                //正竖屏
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float light = event.values[0];
            cameraHelper.setLight(light);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    /**
     * 拍照完成回调
     *
     * @param file          文件
     * @param photoRotation 角度
     * @param width         宽度
     * @param height        高度
     */
    @Override
    public void onTakePhotoFinish(File file, int photoRotation, int width, int height) {
        runOnUiThread(() -> {
            optRl.setVisibility(View.GONE);
            saveIv.setVisibility(View.VISIBLE);
            deleteIv.setVisibility(View.VISIBLE);
            TEXTURE_STATE = TEXTURE_PHOTO_STATE;
            mTextureView.setVisibility(View.GONE);
            previewIv.setImageURI(cameraHelper.getUriFromFile(mActivity, file));
            previewIv.setVisibility(View.VISIBLE);
        });
    }
    /**
     * 相机准备完毕
     */
    @Override
    public void onCameraReady() {

    }

    @Override
    public void onSeekTime(int allTime, int time) {

    }
    /**
     * 刷新相册
     *
     * @param mediaFile 文件
     */
    private void saveMedia(File mediaFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(mediaFile);
        intent.setData(uri);
        sendBroadcast(intent);
    }
}
