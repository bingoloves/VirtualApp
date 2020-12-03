package com.bingoloves.plugin_core.widget;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bingoloves.plugin_core.utils.ActivityStackManager;

/**
 * Created by bingo on 2020/11/12.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/12
 */

public class TouchImageView extends ImageView {

    //图片的尺寸
    private float imgHeight = 0;
    private float imgWidth = 0;

    private Context context;

    //View的尺寸
    private int viewHeight = 0;
    private int viewWidth = 0;

    //图片的缩放最大值
    private float maxHeight;
    private float maxWidth;
    private float minHeight;
    private float minWidth;

    //移动前两指直接的距离
    private double beginDistance;
    private boolean isOnePointer = true;

    //下滑退出的控制变量
    private boolean canQuit = false;
    private boolean tryQuit = false;

    //图片缩放时居中过程产生的位移量
    private float tempdy = 0;
    private float tempdx = 0;

    //两指的中点坐标，用于设置缩放的中心点
    private float xMid,yMid;
    //第一根手指按下的初始坐标
    private float xDown,yDown;

    //目前操作的Matrix对象
    private Matrix matrix = new Matrix();
    //上一次操作的Matrix对象
    private Matrix preMatrix = new Matrix();

    private boolean isMovePic = false;
    private boolean isZoomPic = false;

    //退出时需要缩放到的位置
    private int preWidth = 0;
    private int preHeight = 0;
    private int xLocation = -1;
    private int yLocation = -1;

    public TouchImageView(Context context) {
        super(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setBackgroundColor(Color.BLACK);
        setScaleType(ScaleType.FIT_CENTER); //在没有获得View尺寸来进行initBitmap前，先通过这个进行居中显示
    }

    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {//单击事件
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {//双击事件
            doubleClickZoom();
            return super.onDoubleTap(e);
        }
    });

    /**
     * 设置TouchImageView的尺寸，在onCreate阶段展示图片，需要手动设置
     * @param h
     * @param w
     */
    public void setViewSize(int h, int w){
        System.out.println("setViewSize");
        viewHeight = h;
        viewWidth = w;
        //获得View尺寸后初始化图片
        initBitmap();

    }

    //初始化图片
    public void initBitmap(){
        System.out.println("initBitmap");
        preMatrix.reset(); //必须有，否则会受上一张图片的影响

        //缩放到宽与控件平齐
        float scaleX = (float) viewWidth / imgWidth;
        float scaleY = (float) viewHeight / imgHeight;
        float defaultScale = scaleX < scaleY ? scaleX : scaleY;
        preMatrix.postScale(defaultScale, defaultScale);
        //平移到居中
        float tranY = (viewHeight - imgHeight*defaultScale)/2;
        preMatrix.postTranslate(0, tranY);

        //获取最大最小缩放尺寸
        maxHeight = imgHeight * defaultScale * 3;
        maxWidth = imgWidth * defaultScale * 3;
        minHeight = imgHeight* defaultScale / 2;
        minWidth = imgWidth * defaultScale / 2;
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(preMatrix);
    }


    @Override
    public void setImageBitmap(Bitmap bm){  //先执行这个方法，再执行initBitmap
        super.setImageBitmap(bm);
        imgWidth = bm.getWidth();
        imgHeight = bm.getHeight();
        System.out.println("setImageBitmap: imgWidth="+imgWidth);
        System.out.println("setImageBitmap: TimgHeightH="+imgHeight);
        if(viewHeight!=0 && viewWidth!=0) {
            initBitmap();
        } else{
            viewHeight = getHeight();
            viewWidth = getWidth();
            if(viewHeight!=0 && viewWidth!=0){
                initBitmap();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        gestureDetector.onTouchEvent(event);
        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                matrix.set(preMatrix); //时序问题，doubleClickZoom方法中设置该项时，前面会通过ACTION_UP将preMatrix设置成matrix，故提前设置该项
                xDown = event.getX();
                yDown = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                isOnePointer = false;
                if(!isMovePic){         // 第二根手指放下时可能已经进入了移动模式，已经进入移动模式则不激发缩放模式
                    isZoomPic = true;
                }
                if(event.getPointerCount() == 2) {  // 两根手指才进行测量与计算距离，避免缩放过程中第三根手指放下激发重新测量
                    float downX1 = event.getX(0);
                    float downX2 = event.getX(1);
                    float downY1 = event.getY(0);
                    float downY2 = event.getY(1);
                    float xDistance = Math.abs(downX1-downX2);
                    float yDistance = Math.abs(downY1-downY2);
                    xMid = (downX1+downX2)/2;
                    yMid = (downY1+downY2)/2;
                    beginDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 1) {
                    if(!isZoomPic) {
                        float moveX = event.getX();
                        float moveY = event.getY();
                        if(Math.abs(moveX - xDown)>10 || Math.abs(moveY - yDown)>10){
                            isZoomPic = false;
                            isMovePic = true;
                        }
                    }
                    isOnePointer = true;
                }
                else if(event.getPointerCount() == 2){
                    if(!isMovePic){
                        isZoomPic = true;
                        isMovePic = false;
                    }
                    isOnePointer = false;
                }
                else {
                    isZoomPic = false;
                    isMovePic = false;
                    isOnePointer = false;
                }
                if(isZoomPic && !isOnePointer) { //双指缩放图片
                    matrix.set(preMatrix);
                    float moveX1 = event.getX(0);
                    float moveX2 = event.getX(1);
                    float moveY1 = event.getY(0);
                    float moveY2 = event.getY(1);
                    float xDistance2 = Math.abs(moveX1-moveX2);
                    float yDistance2 = Math.abs(moveY1-moveY2);
                    //移动后两指间的距离
                    double moveDistance = Math.sqrt(xDistance2 * xDistance2 + yDistance2 * yDistance2);
                    float zoomScale = (float) ((float)moveDistance / beginDistance);
                    matrix.postScale(zoomScale, zoomScale,xMid,yMid);
                    float[] values =  new float[9];
                    //缩放最大最小的限制
                    limitZoomSize(values);
                    //放大或缩小时，若高或宽没有填充完控件则过程需要居中
                    placeXCenter(values);
                    placeYCenter(values);

                    //检查图片边缘有没离开边界，返回需要移动的位移量
                    float dx = checkXBorder(values, 0);
                    float dy = checkYBorder(values, 0);
                    matrix.postTranslate(dx, dy);
                    setImageMatrix(matrix);
                }
                if(isMovePic && isOnePointer){ //单手指移动图片
                    matrix.set(preMatrix);
                    float moveX = event.getX();
                    float moveY = event.getY();
                    //计算位移量
                    float dy = moveY - yDown;
                    float dx = moveX - xDown;
                    float[] values =  new float[9];
                    quitViewPicture(values,dx,dy);
                    //检查图片边缘有没离开边界，返回需要移动的位移量
                    dx = checkXBorder(values, dx);
                    dy = checkYBorder(values, dy);
                    matrix.postTranslate(dx, dy);
                    setImageMatrix(matrix);
                }
                break;
            case MotionEvent.ACTION_UP:
                float[] values =  new float[9];
                if(tryQuit) {
                    checkQuit(values);
                } else {
                    //缩小离开屏幕边缘时，松手自动放大到屏幕边缘
                    autoMatchParent(values);
                }
                preMatrix.set(matrix);
                isOnePointer = true;
                isMovePic = false;
                isZoomPic = false;
                //将前面居中过程产生的位移量置0
                tempdy = 0;
                tempdx = 0;
                tryQuit = false;
                break;
        }
        return true;
    }

    //下滑退出操作，松手时是否达到可以退出的条件（位移量）
    public void checkQuit(float[] values) {
        matrix.getValues(values);
        if(canQuit) {
            if(preWidth != 0 && preHeight != 0 && xLocation != -1 && yLocation != -1) {
                setBackgroundColor(Color.argb(0, 0, 0, 0));
                float toScale;
                if(preWidth > preHeight) {
                    toScale = preWidth / (values[Matrix.MSCALE_X]*imgWidth);
                } else {
                    toScale = preHeight / (values[Matrix.MSCALE_Y]*imgHeight);
                }
                matrix.getValues(values);
                float dx = xLocation - values[Matrix.MTRANS_X];
                float dy = yLocation - values[Matrix.MTRANS_Y];
                setMyAnimation(dx,dy,toScale,toScale);
            }
            else {
                ActivityStackManager.getActivityStack().pop();
            }
        }
        else {
            float scale = getWidth()/(values[Matrix.MSCALE_X] * imgWidth);
            matrix.postScale(scale,scale);
            placeXCenter(values);
            placeYCenter(values);
            setImageMatrix(matrix);
        }
    }

    //当图片未缩放状态时，向下滑可以退出图片浏览
    public void quitViewPicture(float[] values, float dx, float dy){
        matrix.getValues(values);
        float beforeZoom = values[Matrix.MTRANS_Y];
        if((imgWidth * values[Matrix.MSCALE_X] <= getWidth()) && (imgHeight * values[Matrix.MSCALE_Y] <= getHeight()) ) {
            if(dy > 0) {
                if(dy>5) { //防止双击放大时被认为是想退出
                    tryQuit = true;
                }
                // 设置背景色透明程度
                int alpha = 255- (int) ((255 * dy)/getHeight());
                setBackgroundColor(Color.argb(alpha, 0, 0, 0));
                //float scale = values[Matrix.MSCALE_X] - dy * values[Matrix.MSCALE_X] / getHeight(); //目标缩放尺寸
                float scale = 1 - dy  / getHeight(); //需要Post的缩放尺寸
                matrix.postScale(scale, scale);
                matrix.getValues(values);
                float dyZoom = beforeZoom - values[Matrix.MTRANS_Y]; //缩小过程会产生一定回缩的位移
                float dxZoom = getWidth()/2 -imgWidth * values[Matrix.MSCALE_X]/2 ;
                matrix.postTranslate(dx + dxZoom, dy + dyZoom);
                //placeXCenter(values);
                if(dy > 200) {
                    canQuit = true;
                } else {
                    canQuit = false;
                }
            }
        }
    }

    //双击放大一倍
    public void doubleClickZoom(){
        matrix.set(preMatrix);
        float[] values = new float[9];
        matrix.getValues(values);
        float currentWidth = values[Matrix.MSCALE_X] * imgWidth;
        if(currentWidth > getWidth()+ 5) {
            float scale = getWidth()/currentWidth;
            matrix.postScale(scale,scale);
            placeXCenter(values);
            placeYCenter(values);
            setImageMatrix(matrix);
            preMatrix.set(matrix);
        } else {
            matrix.postScale(2.0f,2.0f);
            matrix.getValues(values);
            float dx = getWidth()/2 - (imgWidth*values[Matrix.MSCALE_X]/2 + values[Matrix.MTRANS_X]);
            float dy = getHeight()/2 - (imgHeight*values[Matrix.MSCALE_Y]/2 + values[Matrix.MTRANS_Y]);
            matrix.postTranslate(dx, dy);
            setImageMatrix(matrix);
            preMatrix.set(matrix);
        }
    }

    //缩小离开屏幕边缘时，松手自动放大到屏幕边缘
    public void autoMatchParent(float[] values) {
        matrix.getValues(values);
        float currentHeight = values[Matrix.MSCALE_Y] * imgHeight;
        float currentWidth = values[Matrix.MSCALE_X] * imgWidth;
        if(currentHeight < (getHeight()-5) && currentWidth < (getWidth()-5)) {
            float scale = getWidth()/currentWidth;
            matrix.postScale(scale,scale);
            placeXCenter(values);
            placeYCenter(values);
            setImageMatrix(matrix);
        }
    }

    //缩放最大最小的限制
    public void limitZoomSize(float[] values) {
        matrix.getValues(values);
        if(values[Matrix.MSCALE_Y] * imgHeight > maxHeight || values[Matrix.MSCALE_X] * imgWidth > maxWidth) {
            float scaleX = maxWidth / (imgWidth*values[Matrix.MSCALE_X]);
            float scaleY = maxHeight / (imgHeight*values[Matrix.MSCALE_Y]);
            matrix.postScale(scaleX, scaleY,xMid,yMid);
        } else if(values[Matrix.MSCALE_Y] * imgHeight < minHeight || values[Matrix.MSCALE_X] * imgWidth < minWidth) {
            float scaleX = minWidth / (imgWidth*values[Matrix.MSCALE_X]);
            float scaleY = minHeight / (imgHeight*values[Matrix.MSCALE_Y]);
            matrix.postScale(scaleX, scaleY,xMid,yMid);
        }
    }

    //X方向上缩放过程中若未充满控件宽度，那么居中缩放
    public void placeXCenter(float[] values){
        //获得最新的values
        matrix.getValues(values);
        //图片横向能完全显示时，需要居中
        if(imgWidth*values[Matrix.MSCALE_X] < getWidth() + 1) {
            System.out.println("placeXCenter:横向正在居中");
            float dx = getWidth()/2 - (imgWidth*values[Matrix.MSCALE_X]/2 + values[Matrix.MTRANS_X]);
            tempdx = dx;
            matrix.postTranslate(dx, 0);
        }
        else {
            matrix.postTranslate(tempdx, 0);    //图像的横向向从未充满屏幕到充满屏幕过程中，由于居中会产生的一定位移tempdx，需要补上，否则会跳变
        }
    }

    //Y方向上缩放过程中若未充满控件宽度，那么居中缩放
    public void placeYCenter(float[] values){
        //获得最新的values
        matrix.getValues(values);
        //图片纵向能完全显示时，需要居中
        if(imgHeight*values[Matrix.MSCALE_Y] < getHeight() + 1) {
            System.out.println("placeYCenter:纵向正在居中");
            float dy = getHeight()/2 - (imgHeight*values[Matrix.MSCALE_Y]/2 + values[Matrix.MTRANS_Y]);
            tempdy = dy;
            matrix.postTranslate(0, dy);
        } else {
            matrix.postTranslate(0, tempdy);    //图像的纵向从未充满屏幕到充满屏幕过程中，由于居中会产生的一定位移tempdy，需要补上，否则会跳变
        }
    }

    //X方向上的边缘检测
    public float checkXBorder(float[] values, float dx) {
        //获得最新的values
        matrix.getValues(values);
        if(imgWidth*values[Matrix.MSCALE_X] < getWidth()){ //图片宽度小于控件宽度时，不移动
            dx = 0;
        }
        else if(values[Matrix.MTRANS_X]+dx>0) { //图片右移后若离开控件左边缘，那么将图片移动对齐到左边缘
            dx = -values[Matrix.MTRANS_X];
        }
        else if(imgWidth*values[Matrix.MSCALE_X]+values[Matrix.MTRANS_X]+ dx< getWidth()){ //图片左移后若离开控件右边缘，那么将图片移动对齐到右边缘
            dx = - imgWidth*values[Matrix.MSCALE_X] + getWidth() - values[Matrix.MTRANS_X];
        }
        return dx;
    }

    //Y方向上的边缘检测
    public float checkYBorder(float[] values, float dy) {
        //获得最新的values
        matrix.getValues(values);
        if(imgHeight*values[Matrix.MSCALE_Y] < getHeight()){ //图片高度小于控件宽度时，不移动
            dy = 0;
        }
        else if(values[Matrix.MTRANS_Y]+dy>0) { //图片下移后若离开控件上边缘，那么将图片移动对齐到上边缘
            dy = -values[Matrix.MTRANS_Y];
        }
        else if(imgHeight * values[Matrix.MSCALE_Y] + values[Matrix.MTRANS_Y] + dy < getHeight()){ //图片上移后若离开控件下边缘，那么将图片移动对齐到下边缘
            dy = - imgHeight*values[Matrix.MSCALE_Y] + getHeight() - values[Matrix.MTRANS_Y];
        }
        return dy;
    }

    /**
     * 设置退出动画需要缩放到的位置 , 需要设置才有这个效果
     * @param x 相对屏幕的绝对位置坐标x
     * @param y 相对屏幕的绝对位置坐标y
     * @param height
     * @param width
     */
    public void setQuitAnimation(int x, int y, int height, int width) {
        preWidth = width;
        preHeight = height;
        xLocation = x;
        yLocation = y;
    }

    public void setMyAnimation (float toTranslateX, float toTranslateY, float toScaleX, float toScaleY) {
        PropertyValuesHolder translateX = PropertyValuesHolder.ofFloat("translateX", 0.0f,toTranslateX);
        PropertyValuesHolder translateY = PropertyValuesHolder.ofFloat("translateY", 0.0f,toTranslateY);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f,toScaleX);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f,toScaleY);
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(translateX,translateY,scaleX,scaleY);
        animator.addUpdateListener(new MyAnimatorListener(getImageMatrix()));
        animator.setDuration ( 150 );
        animator.setInterpolator ( new LinearInterpolator() );
        animator.setStartDelay ( 0 );
        animator.start ();
        animator.addListener(new Animator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ActivityStackManager.getActivityStack().pop();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
    }

    class MyAnimatorListener implements ValueAnimator.AnimatorUpdateListener {
        private Matrix mMatrix;

        public MyAnimatorListener(Matrix matrix) {
            mMatrix = new Matrix(matrix);
        }
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float dx =  (float) animation.getAnimatedValue("translateX");
            float dy =  (float) animation.getAnimatedValue("translateY");
            float scaleX =  (float) animation.getAnimatedValue("scaleX");
            float scaleY =  (float) animation.getAnimatedValue("scaleY");
            Matrix matrix = new Matrix(mMatrix);
            float[] values = new float[9];
            matrix.getValues(values);
            float beforeX = values[Matrix.MTRANS_X];
            float beforeY = values[Matrix.MTRANS_Y];
            matrix.postScale(scaleX,scaleY);
            matrix.getValues(values);
            matrix.postTranslate(beforeX - values[Matrix.MTRANS_X] , beforeY - values[Matrix.MTRANS_Y]);
            matrix.postTranslate(dx, dy);
            setImageMatrix(matrix);
        }

    }

    /**
     * 获取当前图片的高
     * @return
     */
    public float getCurrentImageHeight(){
        float[] values = new float[9];
        matrix.getValues(values);
        return imgHeight*values[Matrix.MSCALE_Y];
    }

    /**
     * 获取当前图片的宽
     * @return
     */
    public float getCurrentImageWidth(){
        float[] values = new float[9];
        matrix.getValues(values);
        return imgWidth*values[Matrix.MSCALE_X];
    }
}