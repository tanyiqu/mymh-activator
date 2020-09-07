package com.ws.ac.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ws.ac.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Implementation of App Widget functionality.
 */
public class Widget_2x1 extends AppWidgetProvider {

    static String N = "";
    static String rule = "1234567890abcdefABCDEF";
    static String example = "XXXXXXXXXXXXXXXX";
    static String settingFileName = "seetings.ini";
    static String Format =
            "~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                    "亲爱的！\n" +
                    "您的订单已经送达\n" +
                    "\n" +
                    "您的激活码: " + example + "\n" +
                    "全5星+10字文字描述好评，赠送一款价值15.8元的电脑版Rom修改器哦/:^_^/:^_^！！\n" +
                    "\n" +
                    "修改器使用说明\n" +
                    "https://bilibili.com/video/av49413063\n" +
                    "\n" +
                    "安卓版ROM修改器使用说明\n" +
                    "https://bilibili.com/video/av52133586\n" +
                    "\n" +
                    "电脑版ROM修改器使用说明\n" +
                    "https://bilibili.com/video/av51258987" +
                    "\n" +
                    "~~~~~~~~~~~~~~~~~~~~~~~~~";
    static String f1;
    static String f2;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_2x1);

        remoteViews.setOnClickPendingIntent(R.id.generate_ac,getPendingIntent(context,R.id.generate_ac));
        remoteViews.setOnClickPendingIntent(R.id.only_ac,getPendingIntent(context,R.id.only_ac));

        appWidgetManager.updateAppWidget(appWidgetIds,remoteViews);
    }

    private PendingIntent getPendingIntent(Context context, int resID){
        Intent intent = new Intent();
        intent.setClass(context, Widget_2x1.class);
        intent.setData(Uri.parse("harvic:" + resID));
        return PendingIntent.getBroadcast(context, 0,intent,0);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        int resID = -1;
        if(data != null){
            resID = Integer.parseInt(data.getSchemeSpecificPart());
        }
        // 通过远程对象将按钮的文字设置为”一个随机数”
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_2x1);
        switch (resID) {
            case R.id.generate_ac:
                generate_ac(context);
                break;

            case R.id.only_ac:
                only_ac(context);
                break;
        }

        // 获得appwidget管理实例，用于管理appwidget以便进行更新操作
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        // 相当于获得所有本程序创建的appwidget
        ComponentName componentName = new ComponentName(context, Widget_2x1.class);
        // 更新appwidget
        appWidgetManager.updateAppWidget(componentName, remoteViews);

        super.onReceive(context, intent);

    }

    private String readClipboard(Context context) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        assert clipboard != null;
        ClipData clipData = clipboard.getPrimaryClip();
        assert clipData != null;
        return clipData.getItemAt(0).getText().toString();
    }


    private void generate_ac(Context context){
        String ac = only_ac(context);
        if(ac == null){
            return;
        }
        //读取设置
        readSetting(context);
        splitFormat();
        //拼接
        String str = f1 + ac + "\n" + f2 ;
        //
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null,str);
        assert clipboard != null;
        clipboard.setPrimaryClip(data);
    }

    private String only_ac(Context context){
        String mc = readClipboard(context).trim();
        StringBuilder err = new StringBuilder();
        String err01 = "长度错误";
        String err02 = "非法字符";

        if(mc.length() != 16){
            for(int i=0;i<10;i++){
                err.append(err01);
                if(i<9)
                    err.append("\n");
            }
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
            return null;
        }

        for(int i=0;i<mc.length();i++){
            if(!rule.contains(String.valueOf(mc.charAt(i)))){
                for(int j=0;j<10;j++){
                    err.append(err02);
                    if(j<9)
                        err.append("\n");
                }
                Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        //合法
        String ac = activeCode(mc);

        //复制到剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null,ac);
        assert clipboard != null;
        clipboard.setPrimaryClip(data);
        Toast.makeText(context, ac, Toast.LENGTH_SHORT).show();
        return ac;
    }


    private void readSetting(Context context) {
        //获取内部存储路径
        String path = null;
        boolean flag = false;
        try {
            path = context.getFilesDir().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert path != null;
        File file = new File(path,settingFileName);
        //检查文件是否已经存在
        if(!file.exists()) {//文件不存在
            //创建文件，并写入初始内容
            try {
                flag = file.createNewFile();
                FileWriter out = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(out);
                bw.write(Format);
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{//文件存在
            //从文件里读取信息
            try {
                FileReader in = new FileReader(file);
                BufferedReader br = new BufferedReader(in);
                StringBuffer sb = new StringBuffer("");
                String line = null;
                while((line = br.readLine()) != null){
                    sb.append(line);
                    sb.append("\n");
                }
                Format = sb.toString();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void splitFormat() {
        int pos = Format.indexOf(example);
        int len = Format.length();
        f1 = Format.substring(0,pos);
        f2 = Format.substring(pos+17,len-1);
    }

    public String activeCode(String m_code) {
        String a_code = "";
        //使用字节数组存放
        byte[] bs = new byte[8];
        byte[] bx = {-85, -51, -104, 118, -104, 118, -85, -51};//AB CD 98 76 98 76 AB CD
        //将16进制字符串 转换成 8个字节值
        short s = 0;
        for (int i = 0; i < 8; i++) {
            s = Short.valueOf(m_code.substring(i * 2, i * 2 + 2), 16);
            bs[i] = (byte) s;
        }
        //进行异或运算
        for (int i = 0; i < 8; i++)
            bs[i] ^= bx[i];
        //将有符号byte类型 转为 16进制字符串
        for (int i = 0; i < 8; i++) {
            String str = null;
            int n = 0;
            if (bs[i] < 0)
                n = (int) bs[i] & 0x000000FF;//取低8位
            else
                n = (int) bs[i];
            str = Integer.toHexString(n).toUpperCase();
            while (str.length() < 2)
                str = "0" + str;
            a_code += str;
        }
        return a_code;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }


    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Toast.makeText(context, "销毁", Toast.LENGTH_SHORT).show();
    }
}

