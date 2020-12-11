package com.bingoloves.plugin_core.utils;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bingoloves.plugin_core.R;
import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.recyclerview.base.ViewHolder;
import java.util.List;
import per.goweii.anylayer.Align;
import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.DragLayout;
import per.goweii.anylayer.Layer;
import per.goweii.anylayer.ToastLayer;

/**
 * Created by bingo on 2020/9/9 0009.
 * AnyLayer 通用弹窗的集合
 * @description 此为示例代码，可跟据项目实际需要自定义
 */

public class AnyLayerHelper {
    /**
     * 显示左侧抽屉
     * @param context
     * @param layoutId R.layout.xxx
     */
    public static void showLeftDrawer(Context context,int layoutId){
        AnyLayer.dialog(context)
                .contentView(layoutId)
                .backgroundDimDefault()
//                .asStatusBar(R.id.dialog_drag_h_v)
                .gravity(Gravity.LEFT)
                .dragDismiss(DragLayout.DragStyle.Left)
//                .onClickToDismiss(R.id.dialog_drag_h_tv_close)
                .show();
    }
    /**
     * 显示右侧抽屉
     * @param context
     * @param layoutId R.layout.xxx
     */
    public static void showRightDrawer(Context context,int layoutId){
        AnyLayer.dialog(context)
                .contentView(layoutId)
                .backgroundDimDefault()
//                .asStatusBar(R.id.dialog_drag_h_v)
                .gravity(Gravity.RIGHT)
                .dragDismiss(DragLayout.DragStyle.Right)
//                .onClickToDismiss(R.id.dialog_drag_h_tv_close)
                .show();
    }
    /**
     * 显示上方抽屉
     * @param context
     * @param layoutId R.layout.xxx
     */
    public static void showTopDrawer(Context context,int layoutId){
        AnyLayer.dialog(context)
                .contentView(layoutId)
                .backgroundDimDefault()
                .avoidStatusBar(false)//是否选择避开状态栏,默认false
//                .asStatusBar(R.id.dialog_drag_h_v)
                .gravity(Gravity.TOP)
                .dragDismiss(DragLayout.DragStyle.Top)
//                .onClickToDismiss(R.id.dialog_drag_h_tv_close)
                .show();
    }
    /**
     * 显示下方抽屉
     * @param context
     * @param layoutId R.layout.xxx
     */
    public static void showBottomDrawer(Context context,int layoutId){
        AnyLayer.dialog(context)
                .contentView(layoutId)
                .backgroundDimDefault()
                .gravity(Gravity.BOTTOM)
                .dragDismiss(DragLayout.DragStyle.Bottom)
//                .onClickToDismiss(R.id.dialog_drag_h_tv_close)
//                .bindData(new Layer.DataBinder() {
//                    @Override
//                    public void bindData(@NonNull Layer layer) {
//                        layer.getView(R.id.tv_dialog_title).setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                findViewById(R.id.tv_show_top).performClick();
//                                return false;
//                            }
//                        });
//                    }
//                })
                .show();
    }
    /**
     * 显示默认的不带图标的toast
     * @param message
     */
    public static void showToast(@NonNull Context context,@NonNull CharSequence message){
        showToast(context, message, 0, 0.5f, 0, 1500);
    }
    /**
     * 显示toast
     * @param context
     * @param message
     * @param iconRes
     * @param alpha
     * @param backgroundDrawable
     * @param duration
     */
    public static void showToast(@NonNull Context context,@NonNull CharSequence message,int iconRes,float alpha,int backgroundDrawable,long duration){
        ToastLayer toast = new ToastLayer(context);
        toast.duration(duration);
        if (iconRes != 0){
            toast.icon(iconRes);
        }
        if (backgroundDrawable != 0){
            toast.backgroundDrawable(backgroundDrawable);
        }
        //toast.backgroundDrawable(R.drawable.shape_toast_bg);
        toast.message(message)
             .alpha(alpha)
             .gravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
             .marginBottom(dp2px(context,80))
             .animator(new Layer.AnimatorCreator() {
                 @Override
                 public Animator createInAnimator(@NonNull View target) {
                    return AnimatorHelper.createZoomAlphaInAnim(target);
                 }

                 @Override
                 public Animator createOutAnimator(@NonNull View target) {
                     return AnimatorHelper.createZoomAlphaOutAnim(target);
                 }
             })
             .show();
        //动态改变部分样式
        LinearLayout linearLayout = toast.getView(R.id.ll_container);
        linearLayout.setPadding(Utils.dip2px(context,16f),Utils.dip2px(context,6f),Utils.dip2px(context,16f),Utils.dip2px(context,6f));
        ImageView imageView = toast.getView(R.id.iv_icon);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = Utils.dip2px(context,22);
        layoutParams.height = Utils.dip2px(context,22);
    }
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * popup
     * @param view
     * @param layoutId
     * @return
     */
    public static void popup(View view,int layoutId){
        DialogLayer dialogLayer = AnyLayer.popup(view)
                .align(Align.Direction.VERTICAL, Align.Horizontal.ALIGN_RIGHT, Align.Vertical.BELOW, true)
                .offsetYdp(15)
                .outsideTouchedToDismiss(true)
                .outsideInterceptTouchEvent(false)
                .contentView(layoutId)
                .contentAnimator(new DialogLayer.AnimatorCreator() {
                    @Override
                    public Animator createInAnimator(@NonNull View content) {
                        return AnimatorHelper.createDelayedZoomInAnim(content, 1F, 0F);
                    }

                    @Override
                    public Animator createOutAnimator(@NonNull View content) {
                        return AnimatorHelper.createDelayedZoomOutAnim(content, 1F, 0F);
                    }
                });
        if (dialogLayer.isShow()){
            dialogLayer.dismiss();
        } else {
            dialogLayer.show();
        }
    }
    /**
     * popup
     * @param view       当前相对位置View
     * @param menus      数据集
     * @param offsetYdp  Y方向的偏移量
     * @return
     */
    public static DialogLayer popup(View view,List<String> menus,int offsetYdp,OnPopupClickListener popupClickListener){
        Context context = view.getContext();
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_popup_list_meun,null);
        DialogLayer dialogLayer = AnyLayer.popup(view)
                .align(Align.Direction.VERTICAL, Align.Horizontal.ALIGN_RIGHT, Align.Vertical.BELOW, true)
                .offsetYdp(offsetYdp)
                .outsideTouchedToDismiss(true)
                .outsideInterceptTouchEvent(false)
                .contentView(contentView)
                .contentAnimator(new DialogLayer.AnimatorCreator() {
                    @Override
                    public Animator createInAnimator(@NonNull View content) {
                        return AnimatorHelper.createDelayedZoomInAnim(content, 1F, 0F);
                    }

                    @Override
                    public Animator createOutAnimator(@NonNull View content) {
                        return AnimatorHelper.createDelayedZoomOutAnim(content, 1F, 0F);
                    }
                });
        RecyclerView popupRv = contentView.findViewById(R.id.rv_popup_list);
        popupRv.setLayoutManager(new LinearLayoutManager(context));
        popupRv.setAdapter(new CommonAdapter<String>(context,R.layout.layout_popup_meun_item,menus) {
            @Override
            protected void convert(ViewHolder holder, String o, int position) {
                holder.setText(R.id.tv_menu,o);
                holder.setVisible(R.id.line,position != menus.size()-1);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogLayer.dismiss();
                        if (popupClickListener != null){
                            popupClickListener.onClick(position,o);
                        }
                    }
                });
            }
        });
        return dialogLayer;
    }
    /**
     * 加載LoadingView
     */
    public static DialogLayer loading(){
        DialogLayer dialogLayer = AnyLayer.dialog()
                .contentView(R.layout.layout_loading_dialog)
                .outsideTouchedToDismiss(true)
                .outsideInterceptTouchEvent(false)
                .backgroundDimDefault();
        /*dialogLayer.bindData(layer -> {
            ChrysanthemumView chrysanthemumView = layer.getView(R.id.chrysanthemumView);
            chrysanthemumView.startAnimation();
            layer.onDismissListener(new Layer.OnDismissListener() {
                @Override
                public void onDismissing(@NonNull Layer layer) {

                }

                @Override
                public void onDismissed(@NonNull Layer layer) {
                    chrysanthemumView.stopAnimation();
                }
            });
        });*/

        return dialogLayer;
    }
    /**
     * 不依赖上下文的dialog
     * @param layoutId
     */
    public static void dialog(int layoutId){
        AnyLayer.dialog()
                .contentView(layoutId)
                .backgroundDimDefault()
//                .onClickToDismiss(R.id.fl_dialog_yes, R.id.fl_dialog_no)
                .show();
    }
    /**
     * AlertDialog
     */
    public static void showAlertDialog(String title,String message,OnClickListener onClickListener){
        showAlertDialog(title,message,false,onClickListener);
    }

    /**
     * AlertDialog
     * @param title       标题信息（可为空）
     * @param message     消息信息 （不可为空）
     * @param singleBtn   是否显示单个按钮
     * @param onClickListener  确认事件
     */
    public static void showAlertDialog(String title,String message,boolean singleBtn,OnClickListener onClickListener){
        AnyLayer.dialog()
                .contentView(R.layout.layout_common_dialog)
                .backgroundDimDefault()
                .bindData(layer -> {
                    LinearLayout contentLl = layer.getView(R.id.ll_content);
                    TextView onlyTitleTv = layer.getView(R.id.tv_only_title);
                    TextView titleTv = layer.getView(R.id.tv_title);
                    TextView contentTv = layer.getView(R.id.tv_content);
                    Button cancelBtn = layer.getView(R.id.btn_cancel);
                    Button okBtn = layer.getView(R.id.btn_ok);
                    cancelBtn.setVisibility(singleBtn?View.GONE:View.VISIBLE);
                    okBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setText("取消");
                    okBtn.setText("确认");
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)){
                        titleTv.setText(title);
                        contentTv.setText(message);
                        contentLl.setVisibility(View.VISIBLE);
                        onlyTitleTv.setVisibility(View.GONE);
                    }
                    if (TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)){
                        contentLl.setVisibility(View.GONE);
                        onlyTitleTv.setVisibility(View.VISIBLE);
                        onlyTitleTv.setText(message);
                    }
                })
                .onClickToDismiss(R.id.btn_cancel)
                .onClick((layer, v) -> {
                    layer.dismiss();
                    if (onClickListener!=null)onClickListener.onClick();
                },R.id.btn_ok)
                .show();
    }

    public interface OnClickListener {
        void onClick();
    }
    public interface OnPopupClickListener {
        void onClick(int position, String name);
    }
}
