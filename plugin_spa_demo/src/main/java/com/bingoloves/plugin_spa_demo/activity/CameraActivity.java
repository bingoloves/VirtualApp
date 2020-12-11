package com.bingoloves.plugin_spa_demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
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
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.camera.AutoFitTextureView;
import com.bingoloves.plugin_spa_demo.camera.AutoTextureView;
import com.bingoloves.plugin_spa_demo.camera.BitmapUtils;
import com.bingoloves.plugin_spa_demo.camera.Camera2Util;
import com.bingoloves.plugin_spa_demo.camera.CameraHelper;
import com.bingoloves.plugin_spa_demo.camera.ICamera2;
import com.gyf.immersionbar.ImmersionBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.OnClick;
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
public class CameraActivity extends BaseActivity{
    @BindView(R.id.textureView)
    AutoFitTextureView mTextureView;
    @BindView(R.id.toolbar)
    CustomToolbar toolbar;
    //底部操作视图
    @BindView(R.id.rl_opt)
    RelativeLayout optRl;
    @BindView(R.id.iv_photo)
    ImageView previewIv;

    @SuppressLint("MissingPermission")
    @OnClick({R.id.iv_change_camera,R.id.btn_takePhoto})
    public void clickEvent(View view){
        switch (view.getId()){
            case R.id.iv_change_camera:
                toast("切换摄像头");
                if ("0".equals(mCameraId)) {
                    mCameraId = "1";
                } else {
                    mCameraId = "0";
                }
                //关闭相机再开启另外个摄像头
                if (mCaptureSession != null) {
                    mCaptureSession.close();
                    mCaptureSession = null;
                }
                if (mCameraDevice != null) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
                try {
                    manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_takePhoto:
                //takePhoto();
                break;
             default:
                 break;
        }
    }


    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    /**
     * 相机预览状态
     */
    private static final int STATE_PREVIEW = 0;
    /**
     * 等待相机锁定
     */
    private static final int STATE_WAITING_LOCK = 1;
    /**
     * 相机状态：等待曝光处于预捕获状态。
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;
    /**
     * 相机状态：等待曝光状态不是预捕获。
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    /**
     * 相机状态：已拍摄照片。
     */
    private static final int STATE_PICTURE_TAKEN = 4;
    /**
     * Camera2 API保证的最大预览宽度
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;
    /**
     * Camera2 API保证的最大预览高度
     */
    private static final int MAX_PREVIEW_HEIGHT = 1280;
    /**
     * 当前拍照id
     */
    private String mCameraId;
    /**
     * 相机预览
     */
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    /**
     * 预览
     */
    private Size mPreviewSize;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    /**
     * 相机 管理类
     */
    private CameraManager manager;
    /**
     * 当前相机状态.
     */
    private int mState = STATE_PREVIEW;
    /**
     * 静态大小
     */
    private Size largest;
    private Surface surface;
    /**
     * 在关闭相机之前阻止应用程序退出
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    /**
     * 相机传感器的方向
     */
    private int mSensorOrientation;

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    /**
     * 视频对象
     */
    private MediaRecorder mMediaRecorder;

    private ImageReader mImageReader;
    /**
     * 相机配置
     */
    private CameraCharacteristics characteristics;
    private File mFile;
    private String fileName;
    /**
     * 相机拍摄帮助类
     */
    //private CameraHelper cameraHelper;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initData() {

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(toolbar).init();
        toolbar.setCenterTitle("相机");
        toolbar.back(v -> finish());
        fileName = System.currentTimeMillis() + ".jpg";
        mFile = new File(getExternalFilesDir(null), fileName);

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        cameraHelper = new CameraHelper(this);
//        cameraHelper.setTakePhotoListener(this);
//        cameraHelper.setCameraReady(this);
//        cameraHelper.setTextureView(mTextureView);
//        cameraHelper.openCamera(ICamera2.CameraType.BACK);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        // 存在关联则打开相机，没有则绑定事件
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }
    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }
    /**
     * SurfaceTextureListener  监听事件
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };
    /**
     * 相机 曝光 范围
     */
    private Range<Integer> range1;
    /**
     * 曝光时间
     */
    private Range<Long> etr;
    /**
     * 相机 iso 范围
     */
    private Range<Integer> isoRange;
    /**
     * zoom 是否显示标识
     */
    private boolean showZoomFlag = false;
    private boolean mFlashSupported;
    /**
     * 设置与摄像头相关的成员变量。
     *
     * @param width  摄像机预览的可用大小宽度
     * @param height 相机预览的可用尺寸高度
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                characteristics = manager.getCameraCharacteristics(cameraId);
                // 仅适用后置摄像头
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                //得到相机支持的流配置(包括支持的图片分辨率等),不支持就返回
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }
                List<CaptureRequest.Key<?>> ss = characteristics.getAvailableCaptureRequestKeys();
                LogUtils.e(ss.toString());
                // 曝光增益 范围
                range1 = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
                //获取支持的iso范围
                isoRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
                // 曝光时长
                int[] avails = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
                // 白平衡
                int[] aa = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
                // 最大白平衡数
                Integer maxAwb = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB);
                //获取曝光时间
                etr = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);

                // 静态图像捕获，选择最大可用大小。
                largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new Comparator<Size>() {
                            @Override
                            public int compare(Size lhs, Size rhs) {
                                // 在这里投射以确保乘法不会溢出
                                return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                                        (long) rhs.getWidth() * rhs.getHeight());
                            }
                        });
                // w: 720  h :960
//                Size largest = Collections.max(
//                        Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)),
//                        new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.YUV_420_888, 2);
//                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
//                        ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                //了解我们是否需要交换尺寸以获得相对于传感器的预览尺寸
                int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        LogUtils.e("Display rotation is invalid: " + displayRotation);
                }
                Point displaySize = new Point();
                mActivity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;
                //如果需要颠倒方向
                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // 将TextureView的宽高比与我们选择的预览大小相匹配。
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // 检查 远光灯
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * 打开相机
     *
     * @param width  宽度
     * @param height 长度
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2300, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }
    /**
     * CameraDevice 改变状态时候 调用
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            //打开相机时会调用此方法。 我们在这里开始相机预览。
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            finish();
        }

    };

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }



    /**
     * 打开 BackgroundThread
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * 关闭 BackgroundThread
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个新的相机预览
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            //将默认缓冲区的大小配置为相机预览的大小。
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            surface = new Surface(texture);
            //使用Surface设置CaptureRequest.Builder
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            //添加这句话 可以在 mImageReader 监听回调中持续获取 预览图片
            //mPreviewRequestBuilder.addTarget(mImageReader.getSurface());
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if (null == mCameraDevice) {
                                return;
                            }
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // 自动变焦是连续的
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                setAutoFlash(mPreviewRequestBuilder);
                                //禁用所有自动设置
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);
                                //而 ISO 和 Exposure Time 与之相反，仅在 aeMode 关闭时才起作用
                                //只是禁用自动曝光，白平衡继续开启,自己设置iso等值，必须禁用曝光
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
                                //设置曝光时间 ms单位
//                                mPreviewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, 1000L);
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 1);
                                // 设置 iso 灵敏度
                                mPreviewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, 800);
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
                                //设置曝光补偿
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 10);
                                // 设置帧 持续时长
//                                mPreviewRequestBuilder.set(CaptureRequest.SENSOR_FRAME_DURATION, 1000L);
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Matrix 转换配置为 mTextureView
     *
     * @param viewWidth  mTextureView 宽度
     * @param viewHeight mTextureView 高度
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * 锁定焦点设置
     */
    public void lockFocus() {
        try {
            // 相机锁定的方法
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // mCaptureCallback 等待锁定
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            LogUtils.e("lockFocus: " + e.getMessage());
        }
    }

    /**
     * 运行预捕获序列以捕获静止图像。 在调用此方法时调用
     */
    private void runPrecaptureSequence() {
        try {
            // 相机触发的方法
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // mCaptureCallback等待设置预捕获序列。
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍摄静止图片。 当我们得到响应时，应该调用此方法
     */
    private void captureStillPicture() {
        try {
            if (null == mActivity || null == mCameraDevice) {
                return;
            }
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // 使用与预览相同的AE和AF模式。
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            //对于方向为90的设备，我们只需从ORIENTATIONS返回我们的映射
            //对于方向为270的设备，我们需要将JPEG旋转180度
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360);

            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解锁焦点 在静止图像捕获序列时调用此方法
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }
    /**
     * 更新 Preview
     */
    private void updatePreview() {
        try {
            mPreviewRequest = mPreviewRequestBuilder.build();
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    /**
     * ImageReader 的回调对象。 静止图像已准备好保存。
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            LogUtils.e("11111111111");
//            Image mImage = reader.acquireNextImage();
//            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
//            byte[] bytes = new byte[buffer.remaining()];
//            buffer.get(bytes);
//            FileOutputStream output = null;
//            try {
//                output = new FileOutputStream(mFile);
//                output.write(bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                mImage.close();
//                if (null != output) {
//                    try {
//                        output.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            // 其次把文件插入到系统图库
//            try {
//                MediaStore.Images.Media.insertImage(getContentResolver(), mFile.getAbsolutePath(), fileName, null);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            // 最后通知图库更新
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(mFile.getPath())));
//            toast("保存成功");
//            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
//            Image image = reader.acquireLatestImage();
//            if (image != null) {
//                int imageWidth = image.getWidth();
//                int imageHeight = image.getHeight();
//                byte[] data68 = Camera2Util.getBytesFromImageAsType(image, 2);
//                int rgb[] = Camera2Util.decodeYUV420SP(data68, imageWidth, imageHeight);
//                Bitmap bitmap2 = Bitmap.createBitmap(rgb, 0, imageWidth, imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
//                Bitmap d65bitmap = BitmapUtils.rotateMyBitmap(BitmapUtils.ImgaeToNegative(bitmap2));
//                previewIv.setImageBitmap(d65bitmap);
//                image.close();
//            }
//            setResult(AppConstant.RESULT_CODE.RESULT_OK);
//            finish();
        }
    };
    /**
     * 处理与jpg文件捕捉的事件监听(预览)
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // 预览正常
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * 给定摄像机支持的尺寸 否则选择最小的一个尺寸
     *
     * @param choices           相机支持预期输出的尺寸列表
     * @param textureViewWidth  纹理视图相对于传感器坐标的宽度
     * @param textureViewHeight 纹理视图相对于传感器坐标的高度
     * @param maxWidth          最大宽度
     * @param maxHeight         最大高度
     * @param aspectRatio       纵横比
     * @return size
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // 收集至少与预览Surface一样大的支持的分辨率
        List<Size> bigEnough = new ArrayList<>();
        // 收集小于预览Surface的支持的分辨率
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }
        // 挑选适合的尺寸
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            LogUtils.e("Couldn't find any suitable preview size");
            return choices[0];
        }
    }


    private static class CompareSizesByArea implements Comparator<Size> {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(Size lhs, Size rhs) {
            // 在这里投射以确保乘法不会溢出
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
