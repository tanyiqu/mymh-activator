package com.ws.ac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.app_name));
        readSetting();
        splitFormat();
        readClipboard();
    }

    private void readSetting() {
        //获取内部存储路径
        String path = null;
        boolean flag = false;
        try {
            path = getFilesDir().getCanonicalPath();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu7:
                ((TextView)(findViewById(R.id.txtMCode))).setText("72C14D39B4");
                break;
            case R.id.menuD:
                ((TextView)(findViewById(R.id.txtMCode))).setText("DAA14D39B4");
                break;
            case R.id.menuSetting:
                initPopWindow();//显示popup
                break;
            case R.id.menuRandom:
                myToast("随机生成");
                char[] arr = new char[16];
                Random r = new Random();
                for(int i=0;i<16;i++){
                    arr[i] = rule.charAt(r.nextInt(15));
                }
                ((EditText)findViewById(R.id.txtMCode)).setText(String.valueOf(arr).toUpperCase());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readClipboard() {
        /**
         * 获取剪贴板的内容
         * 如果获取的长度为16则一键生成激活码
         */
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        assert clipboard != null;
        ClipData clipData = clipboard.getPrimaryClip();
        assert clipData != null;
        String text = clipData.getItemAt(0).getText().toString();
        if (text.length() == 16){
            ((EditText)findViewById(R.id.txtMCode)).setText(text);
            on_btnOnlyAC_clicked(null);
        }
    }

    public void on_btnGenerateACode_clicked(View view){
        if(!on_btnOnlyAC_clicked(null)){
            return;
        }
        EditText txtMCode = findViewById(R.id.txtMCode);
        EditText txtACode = findViewById(R.id.txtACode);
        String MC = txtMCode.getText().toString();
        String AC = activeCode(MC);
        String str = f1 + AC + "\n" + f2 ;

        txtACode.setText(str);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null,str);
        assert clipboard != null;
        clipboard.setPrimaryClip(data);
    }

    public void on_btnClear_clicked(View view){
        ((EditText)findViewById(R.id.txtMCode)).setText(N);
        ((EditText)findViewById(R.id.txtACode)).setText(N);
        ((TextView)findViewById(R.id.lblStatus)).setText(N);
        ((TextView)findViewById(R.id.lblErrLog)).setText(N);
        myToast("已清空");
    }

    public boolean on_btnOnlyAC_clicked(View view){
        Pair<Boolean,String> p = checkMCode();
        TextView lblStatus = findViewById(R.id.lblStatus);
        TextView lblErrLog = findViewById(R.id.lblErrLog);
        lblErrLog.setText(p.second);
        //显示日志信息
        if(! p.first){//如果检测不通过，提示失败后返回
            lblStatus.setText("失败！未复制！");
            wrongToast("失败");
            return false;
        }
        String AC = activeCode(((EditText)findViewById(R.id.txtMCode)).getText().toString());
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null,AC);
        assert clipboard != null;
        clipboard.setPrimaryClip(data);
        myToast("已复制");
        lblStatus.setText("成功！已复制！");
        return true;
    }

    //检测机器码输入是否正确，如不正确则将错误信息保存在String里
    public Pair<Boolean,String> checkMCode(){
        String mc = ((EditText)findViewById(R.id.txtMCode)).getText().toString();
        String str = "false";

        if(mc.length() < 16){
            str = "机器码长度不足16位";
            return new Pair<>(false,str);
        }else{
            //先截取前16个
            if(mc.length() > 16){
                mc = mc.substring(0,16);
                ((EditText)findViewById(R.id.txtMCode)).setText(mc);
                warnToast("超过16位，已自动截取");
            }
            //mc现在为16为

            for(int i=0;i<mc.length();i++){
                if(!rule.contains(String.valueOf(mc.charAt(i)))){
                    str = "包含非法字符：" + mc.charAt(i);
                    return new Pair<>(false,str);
                }
            }
            return new Pair<>(true,"输入合法");
        }
    }

    public String activeCode(String m_code) {
        String a_code = "";
        //使用字节数组存放
        byte[] bs = new byte[8];
        byte[] bx = {-85,-51,-104,118,-104,118,-85,-51};//AB CD 98 76 98 76 AB CD
        //将16进制字符串 转换成 8个字节值
        short s = 0;
        for(int i=0;i<8;i++) {
            s = Short.valueOf(m_code.substring(i*2, i*2+2), 16);
            bs[i] = (byte)s;
        }
        //进行异或运算
        for(int i=0;i<8;i++)
            bs[i] ^= bx[i];
        //将有符号byte类型 转为 16进制字符串
        for(int i=0;i<8;i++) {
            String str = null;
            int n = 0;
            if(bs[i] < 0)
                n = (int)bs[i] & 0x000000FF;//取低8位
            else
                n = (int)bs[i];
            str = Integer.toHexString(n).toUpperCase();
            while(str.length() < 2)
                str = "0" + str;
            a_code += str;
        }
        return a_code;
    }

    private void myToast(String str){
        View toastView = LayoutInflater.from(MainActivity.this).inflate(R.layout.toast_layout_ok, null);
        LinearLayout relativeLayout = (LinearLayout)toastView.findViewById(R.id.toast_linear);
        //动态设置toast控件的宽高度，宽高分别是130dp
        //这里用了一个将dp转换为px的工具类Util
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) Util.dpToPx(MainActivity.this, 130), (int)Util.dpToPx(MainActivity.this, 130));
        relativeLayout.setLayoutParams(layoutParams);
        TextView textView = (TextView)toastView.findViewById(R.id.tv_toast_clear);
        textView.setText(str);
        Toast toast = new Toast(MainActivity.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(toastView);
        toast.show();
    }

    private void wrongToast(String str){
        View toastView = LayoutInflater.from(MainActivity.this).inflate(R.layout.toast_layout_error, null);
        LinearLayout relativeLayout = (LinearLayout)toastView.findViewById(R.id.toast_linear);
        //动态设置toast控件的宽高度，宽高分别是130dp
        //这里用了一个将dp转换为px的工具类Util
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) Util.dpToPx(MainActivity.this, 130), (int)Util.dpToPx(MainActivity.this, 130));
        relativeLayout.setLayoutParams(layoutParams);
        TextView textView = (TextView)toastView.findViewById(R.id.tv_toast_clear);
        textView.setText(str);
        Toast toast = new Toast(MainActivity.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(toastView);
        toast.show();
    }

    private void warnToast(String str){
        View toastView = LayoutInflater.from(MainActivity.this).inflate(R.layout.toast_layout_warn, null);
        LinearLayout relativeLayout = (LinearLayout)toastView.findViewById(R.id.toast_linear);
        //动态设置toast控件的宽高度，宽高分别是130dp
        //这里用了一个将dp转换为px的工具类Util
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) Util.dpToPx(MainActivity.this, 130), (int)Util.dpToPx(MainActivity.this, 130));
        relativeLayout.setLayoutParams(layoutParams);
        TextView textView = (TextView)toastView.findViewById(R.id.tv_toast_clear);
        textView.setText(str);
        Toast toast = new Toast(MainActivity.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(toastView);
        toast.show();
    }

    private void initPopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.setting_pop_layout, null, false);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高

        final EditText txtFormat = view.findViewById(R.id.txtFormat);
        txtFormat.setText(Format);

        Button btnChFormat = view.findViewById(R.id.btnChFormat);

        //获取屏幕的宽
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;

        final PopupWindow popWindow = new PopupWindow(view,(int)(width*0.75),ViewGroup.LayoutParams.WRAP_CONTENT, true);

        btnChFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String f = txtFormat.getText().toString();
                if(! f.contains(example)){
                    wrongToast("不包含" + example);
                    return;
                }
                String path = null;
                boolean flag = false;
                try {
                    path = getFilesDir().getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert path != null;
                File file = new File(path,settingFileName);

                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    bw.write(f);
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Format  = f;
                splitFormat();
                myToast("修改成功");
                popWindow.dismiss();
            }
        });

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画
        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));//要为popWindow设置一个背景才有效

        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
//        popWindow.showAsDropDown(v);
        popWindow.showAtLocation(MainActivity.this.getWindow().getDecorView(),Gravity.CENTER,0,0);//居中显示
    }

}
