package com.ws.ac;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Util {
    public static float dpToPx(Context context, int dp) {
        //获取屏蔽的像素密度系数
        float density = context.getResources().getDisplayMetrics().density;
        return dp * density;
    }

    /**
     * px to dp
     * @param context 上下文
     * @param px px
     * @return dp
     */
    public static float pxTodp(Context context, int px) {
        //获取屏蔽的像素密度系数
        float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static void wrongToast(Context context,String str){
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_layout_error, null);
        LinearLayout relativeLayout = (LinearLayout)toastView.findViewById(R.id.toast_linear);
        //动态设置toast控件的宽高度，宽高分别是130dp
        //这里用了一个将dp转换为px的工具类Util
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) Util.dpToPx(context, 130), (int)Util.dpToPx(context, 130));
        relativeLayout.setLayoutParams(layoutParams);
        TextView textView = (TextView)toastView.findViewById(R.id.tv_toast_clear);
        textView.setText(str);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(toastView);
        toast.show();
    }

}
