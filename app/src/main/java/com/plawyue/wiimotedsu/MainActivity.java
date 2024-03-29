package com.plawyue.wiimotedsu;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.CompactDecimalFormat;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.shy.rockerview.MyRockerView;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    MontionServer ms=new MontionServer();
    static double PI = 3.1415926535897932;
    Button L3,R3,OPkey,SHARE,Colorx;
    Boolean locked=false;
    Button button_A,button_B,button_DUP,button_DDOWN,button_DLEFT,button_DRight,button_PLUS,button_DEDUCE,BUTTON_home,L2,R2,Touch,button_X,button_Y;
    float Gyrosensitvity=0.9f;
    float Accsensitvity=1f;
    Boolean isEditMode=false;
    Dialog yourDialog;
    Dialog EdittextDialog;
    SeekBar yourDialogSeekBar;
    EditText Editbtntext;
    SeekBar accseekbar;
    static float METER_PER_SECOND_SQUARED_TO_G = (float) 9.8066;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mtoorbar=findViewById(R.id.toolbar);
        mtoorbar.setOnMenuItemClickListener(onMenuItemClick);
        mtoorbar.inflateMenu(R.menu.menu);
        mtoorbar.setFitsSystemWindows(true);

        registerReceiver(mBatInfoReveiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
//        Switch editmodeswitch=findViewById(R.id.editmode);
//        editmodeswitch.setOnCheckedChangeListener(this);
        Window window =this.getWindow();
        /*如果之前是办透明模式，要加这一句需要取消半透明的Flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.senseseekbar, (ViewGroup) findViewById(R.id.elementseek));
        Button yourDialogButton = (Button) layout.findViewById(R.id.your_dialog_button);
        yourDialogSeekBar = layout.findViewById(R.id.your_dialog_seekbar);
        accseekbar=layout.findViewById(R.id.seekBar);
        TextView acctext = layout.findViewById(R.id.acctext);
        accseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                layout.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
                acctext.setText("Accelerometer sensitive set as:" + i/10.0);
                Accsensitvity= (float) (i/10.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        yourDialog = new Dialog(this);
        yourDialog.setContentView(layout);
        yourDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView lighttext = layout.findViewById(R.id.lighttext);
        SeekBar.OnSeekBarChangeListener yourSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //add code here
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //add code here

            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                //add code here
                layout.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
                lighttext.setText("Gyroscope sensitive set as:" + progress/10.0);
                Gyrosensitvity= (float) (progress/10.0);
            }
        };
        yourDialogButton.setOnClickListener(this);
        yourDialogSeekBar.setOnSeekBarChangeListener(yourSeekBarListener);
        yourDialog.setCanceledOnTouchOutside(false);
        yourDialog.setCancelable(false);


        LayoutInflater inflater2 = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout2 = inflater2.inflate(R.layout.editlayout, (ViewGroup) findViewById(R.id.elementseek2));

        joystick_show();
        ms.right_stick_x=128;
        ms.left_stick_y=128;
        ms.left_stick_x=128;
        ms.right_stick_y=128;
        EdittextDialog = new Dialog(this);
        EdittextDialog.setContentView(layout2);
        EdittextDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button yourDialogButton2 = (Button) layout2.findViewById(R.id.okbtn);
        Button canclebtn=layout2.findViewById(R.id.cancelbtn);
        canclebtn.setOnClickListener(this);
        Editbtntext=layout2.findViewById(R.id.btncontent);
        yourDialogButton2.setOnClickListener(this);
        EdittextDialog.setCanceledOnTouchOutside(false);
        EdittextDialog.setCancelable(false);

        TextView Locktips=findViewById(R.id.LOCK);
        Locktips.setOnClickListener(this);
        Locktips.setOnLongClickListener(this);
        Locktips.setOnTouchListener(this);
        Touch=findViewById(R.id.Button_touchlock);
        Touch.setOnClickListener(this);
        button_A=findViewById(R.id.button_A);
        button_X=findViewById(R.id.Square);
        button_Y=findViewById(R.id.Tri);
        button_B=findViewById(R.id.button_B);
        button_DUP=findViewById(R.id.button_dUp);
        button_DDOWN=findViewById(R.id.button_Ddown);
        button_DLEFT=findViewById(R.id.button_Dleft);
        button_DRight=findViewById(R.id.button_Dright);
        button_PLUS=findViewById(R.id.button_PLUS);
        button_DEDUCE=findViewById(R.id.button_DEDUCE);
        BUTTON_home=findViewById(R.id.button_home);
        L3=findViewById(R.id.buttonL3);
        R3=findViewById(R.id.buttonR3);
        SHARE=findViewById(R.id.buttonShare);
        OPkey=findViewById(R.id.buttonOption);
        button_Y.setOnTouchListener(this);
        button_X.setOnTouchListener(this);
        L3.setOnTouchListener(this);
        R3.setOnTouchListener(this);
        SHARE.setOnTouchListener(this);
        OPkey.setOnTouchListener(this);
        L2=findViewById(R.id.button_L2);
        R2=findViewById(R.id.button_R2);
        L2.setOnTouchListener(this);
        R2.setOnTouchListener(this);
        button_A.setOnTouchListener(this);
        button_B.setOnTouchListener(this);
        button_DUP.setOnTouchListener(this);
        button_DDOWN.setOnTouchListener(this);
        button_DLEFT.setOnTouchListener(this);
        button_DRight.setOnTouchListener(this);
        button_PLUS.setOnTouchListener(this);
        button_DEDUCE.setOnTouchListener(this);
        BUTTON_home.setOnTouchListener(this);

        button_Y.setOnClickListener(this);
        button_X.setOnClickListener(this);
        L3.setOnClickListener(this);
        R3.setOnClickListener(this);
        SHARE.setOnClickListener(this);
        OPkey.setOnClickListener(this);
        L2.setOnClickListener(this);
        R2.setOnClickListener(this);
        button_A.setOnClickListener(this);
        button_B.setOnClickListener(this);
        button_DUP.setOnClickListener(this);
        button_DDOWN.setOnClickListener(this);
        button_DLEFT.setOnClickListener(this);
        button_DRight.setOnClickListener(this);
        button_PLUS.setOnClickListener(this);
        button_DEDUCE.setOnClickListener(this);
        BUTTON_home.setOnClickListener(this);

        SharedPreferences userInfo = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        if(userInfo.contains("sensisavin")==false){
            editor.putFloat("sensisavin",0.9F);
            editor.commit();
        }
        if(userInfo.contains("accsensisavin")==false){
            editor.putFloat("accsensisavin",1F);
            editor.commit();
        }
        Accsensitvity=userInfo.getFloat("accsensisavin",1f);
        Gyrosensitvity=userInfo.getFloat("sensisavin",0.9F);
        accseekbar.setProgress((int)(Accsensitvity*10));
        yourDialogSeekBar.setProgress((int) (Gyrosensitvity*10));
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = (ipAddress & 0xff) + "." + (ipAddress>>8 & 0xff) + "." + (ipAddress>>16 & 0xff) + "." + (ipAddress >> 24 & 0xff);

        mtoorbar.setTitle(ip);
        mtoorbar.setTitleTextColor(getResources().getColor(R.color.spical));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ms.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorEventListener sensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
            public void onSensorChanged(SensorEvent event) {
                Boolean conver=true;
                Boolean noconver=false;
                float accX = -Accsensitvity*event.values[2] * METER_PER_SECOND_SQUARED_TO_G / 100;
                float accY =  -Accsensitvity*event.values[0] * METER_PER_SECOND_SQUARED_TO_G / 100;
                float accZ =Accsensitvity*event.values[1] * METER_PER_SECOND_SQUARED_TO_G / 100;
                DecimalFormat df = new DecimalFormat("#.00");
                ms.accX= Float.parseFloat(df.format(accY));
                ms.accY=Float.parseFloat(df.format(accX));
                ms.accZ=Float.parseFloat(df.format(accZ));


            }
        };
        SensorEventListener gyrolinster=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                ms.gyroR= (float) (radToDeg(sensorEvent.values[1]) * Gyrosensitvity);
                ms.gyroY= (float) (-radToDeg(sensorEvent.values[2]) *Gyrosensitvity);
                ms.gyroP= (float) (radToDeg(sensorEvent.values[0]) * Gyrosensitvity);
            }
            double radToDeg(double radians) {
                return radians * 180 / PI;
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(sensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(gyrolinster,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);

        loadbutton();
        getWindow().setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.background));

    }
    public static boolean isNumeric(String str){

        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;


    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId()==R.id.LOCK&&motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(200).start();

        }else if(view.getId()==R.id.LOCK&&motionEvent.getAction() == MotionEvent.ACTION_UP){
            view.animate().scaleX(1f).scaleY(1f).setDuration(200).start();

        }
    if(locked==false){
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            switch (view.getId()){
                case R.id.button_A: button_A.setBackground(getDrawable(R.drawable.pressed));ms.A=255;break;
                case R.id.button_B: button_B.setBackground(getDrawable(R.drawable.pressed)); ms.B=255;break;
                case R.id.Square: button_X.setBackground(getDrawable(R.drawable.pressed)); ms.X=255;break;
                case R.id.Tri: button_Y.setBackground(getDrawable(R.drawable.pressed)); ms.Y=255;break;
                case R.id.button_dUp: button_DUP.setBackground(getDrawable(R.drawable.pressed)); ms.Dpad_UP=255;break;
                case R.id.button_Ddown: button_DDOWN.setBackground(getDrawable(R.drawable.pressed)); ms.Dpad_Down=255;break;
                case R.id.button_Dleft: button_DLEFT.setBackground(getDrawable(R.drawable.pressed)); ms.Dpad_Left=255;break;
                case R.id.button_Dright:button_DRight.setBackground(getDrawable(R.drawable.pressed)); ms.Dpad_Right=255;break;
                case R.id.button_PLUS:button_PLUS.setBackground(getDrawable(R.drawable.pressed)); ms.R1=255;break;
                case R.id.button_DEDUCE:button_DEDUCE.setBackground(getDrawable(R.drawable.pressed)); ms.L1=255;break;
                case R.id.button_home:BUTTON_home.setBackground(getDrawable(R.drawable.pressed));ms.PS=1;break;
                case R.id.button_L2:L2.setBackground(getDrawable(R.drawable.pressed));ms.L2=255;break;
                case R.id.button_R2:R2.setBackground(getDrawable(R.drawable.pressed));ms.R2=255;break;
                case R.id.buttonR3:R3.setBackground(getDrawable(R.drawable.pressed));ms.R3=1;break;
                case R.id.buttonL3:L3.setBackground(getDrawable(R.drawable.pressed));ms.L3=1;break;
                case R.id.buttonOption:OPkey.setBackground(getDrawable(R.drawable.pressed));ms.OPKEY=1;break;
                case R.id.buttonShare:SHARE.setBackground(getDrawable(R.drawable.pressed));ms.SHARE=1;break;
            }
        }
        else if( motionEvent.getAction() == MotionEvent.ACTION_UP){
            view.animate().scaleX(1).scaleY(1).setDuration(100).start();
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            switch (view.getId()){
                case R.id.button_A: button_A.setBackground(getDrawable(R.drawable.unpress));ms.A=0;break;
                case R.id.button_B: button_B.setBackground(getDrawable(R.drawable.unpress)); ms.B=0;break;
                case R.id.Square: button_X.setBackground(getDrawable(R.drawable.unpress)); ms.X=0;break;
                case R.id.Tri: button_Y.setBackground(getDrawable(R.drawable.unpress)); ms.Y=0;break;
                case R.id.button_dUp: button_DUP.setBackground(getDrawable(R.drawable.unpress)); ms.Dpad_UP=0;break;
                case R.id.button_Ddown: button_DDOWN.setBackground(getDrawable(R.drawable.unpress)); ms.Dpad_Down=0;break;
                case R.id.button_Dleft: button_DLEFT.setBackground(getDrawable(R.drawable.unpress)); ms.Dpad_Left=0;break;
                case R.id.button_Dright: button_DRight.setBackground(getDrawable(R.drawable.unpress)); ms.Dpad_Right=0;break;
                case R.id.button_PLUS:button_PLUS.setBackground(getDrawable(R.drawable.unpress)); ms.R1=0;break;
                case R.id.button_DEDUCE:button_DEDUCE.setBackground(getDrawable(R.drawable.unpress)); ms.L1=0;break;
                case R.id.button_home:BUTTON_home.setBackground(getDrawable(R.drawable.unpress));ms.PS=0;break;
                case R.id.button_L2:L2.setBackground(getDrawable(R.drawable.unpress));ms.L2=0;break;
                case R.id.button_R2:R2.setBackground(getDrawable(R.drawable.unpress));ms.R2=0;break;
                case R.id.buttonR3:R3.setBackground(getDrawable(R.drawable.unpress));ms.R3=0;break;
                case R.id.buttonL3:L3.setBackground(getDrawable(R.drawable.unpress));ms.L3=0;break;
                case R.id.buttonOption:OPkey.setBackground(getDrawable(R.drawable.unpress));ms.OPKEY=0;break;
                case R.id.buttonShare:SHARE.setBackground(getDrawable(R.drawable.unpress));ms.SHARE=0;break;
            }
        }
        }
        return false;
    }
    Timer timerx = new Timer();
    long lasttime = 0;
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {

        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect  = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, frontRect, baseRect, null);
        return bitmap;
    }



    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.your_dialog_button){
            getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            SharedPreferences userInfo = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
            SharedPreferences.Editor editor = userInfo.edit();//获取Editor
            ImageView backg=findViewById(R.id.imageView);
            backg.setVisibility(View.INVISIBLE);
            yourDialog.cancel();

            editor.putFloat("sensisavin", Gyrosensitvity);
            editor.putFloat("accsensisavin",Accsensitvity);
             editor.commit();

            Toast.makeText(MainActivity.this,"Sensitive has saved", Toast.LENGTH_LONG).show();

        }
        if(view.getId()==R.id.Button_touchlock){
            if(locked==true){

            }else{
                getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                //锁状态
                Touch.setText("UNLOCK");
                ImageView backg=findViewById(R.id.imageView);
                Button lockbtn=findViewById(R.id.Button_touchlock);

                View viewx = findViewById(R.id.linearLayout);
                Bitmap bitmap2 = Bitmap.createBitmap(viewx.getWidth(), viewx.getHeight(), Bitmap.Config.ARGB_8888);
                Bitmap bitmap=Bitmap.createBitmap(viewx.getWidth(),viewx.getHeight(),Bitmap.Config.ARGB_8888);
                Canvas canvasback=new Canvas(bitmap);
                canvasback.drawColor(ContextCompat.getColor(MainActivity.this, R.color.background));
                Canvas canvas = new Canvas();
                canvas.setBitmap(bitmap2);
                viewx.draw(canvas);
                bitmap2=blur(bitmap2,25);
                bitmap2=mergeBitmap(bitmap,bitmap2);
                bitmap2=handleImageEffect(bitmap2,0.2f);


                Animation alphaAnimation = new AlphaAnimation(0f, 1f);
                alphaAnimation.setDuration(100);//设置动画持续时间为500毫秒
                alphaAnimation.setFillAfter(false);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
                backg.setAnimation(alphaAnimation);
                backg.setImageBitmap(bitmap2);
                backg.setVisibility(View.VISIBLE);

                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
//                findViewById(R.id.editmode).setVisibility(View.INVISIBLE);
                findViewById(R.id.LOCK).setVisibility(View.VISIBLE);
                findViewById(R.id.LOCK).startAnimation(alphaAnimation);
//                findViewById(R.id.sensitivelay).setVisibility(View.INVISIBLE);
                Toolbar mtool=findViewById(R.id.toolbar);
                mtool.setVisibility(View.INVISIBLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                locked=!locked;
            }

        }
        if(view.getId()==R.id.okbtn){
            String result =Editbtntext.getText().toString();
            buttontemp.setText(result);
            SharedPreferences userInfo = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
            SharedPreferences.Editor editor = userInfo.edit();//获取Editor
            editor.putString(Buttonnamex,result);
            editor.commit();
            ImageView backg=findViewById(R.id.imageView);
            backg.setVisibility(View.INVISIBLE);
            EdittextDialog.cancel();
            getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.CONFIRM);
        }
        if(view.getId()==R.id.cancelbtn){
            ImageView backg=findViewById(R.id.imageView);
            backg.setVisibility(View.INVISIBLE);
            EdittextDialog.cancel();
            getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
        }
        if(isEditMode&&locked==false) {
            switch (view.getId()) {
                case R.id.button_A:Inputbox(button_A,"ButtonA");break;
                case R.id.button_B:Inputbox(button_B,"ButtonB");break;
                case R.id.Square: Inputbox(button_X,"ButtonX");break;
                case R.id.Tri: Inputbox(button_Y,"ButtonY");break;
                case R.id.button_dUp: Inputbox(button_DUP,"ButtonDup");break;
                case R.id.button_Ddown: Inputbox(button_DDOWN,"ButtonDdown");break;
                case R.id.button_Dleft: Inputbox(button_DLEFT,"ButtonDleft");break;
                case R.id.button_Dright:Inputbox(button_DRight,"ButtonDright");break;
                case R.id.button_PLUS:Inputbox(button_PLUS,"Buttonplus");break;
                case R.id.button_DEDUCE:Inputbox(button_DEDUCE,"Buttondeduce");break;
                case R.id.button_home:Inputbox(BUTTON_home,"Buttonhome");break;
                case R.id.button_L2:Inputbox(L2,"ButtonL2");break;
                case R.id.button_R2:Inputbox(R2,"ButtonR2");break;
                case R.id.buttonR3:Inputbox(R3,"ButtonR3");break;
                case R.id.buttonL3:Inputbox(L3,"ButtonL3");break;
                case R.id.buttonOption:Inputbox(OPkey,"ButtonOP");break;
                case R.id.buttonShare:Inputbox(SHARE,"ButtonSh");break;
            }
        }
    }

    private Bitmap blur(Bitmap bmp, @IntRange(from = 1, to = 25) int radius) {
        RenderScript rs = RenderScript.create(this);
        Allocation allocFromBmp = Allocation.createFromBitmap(rs, bmp);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, allocFromBmp.getElement());
        blur.setInput(allocFromBmp);
        blur.setRadius(radius);
        blur.forEach(allocFromBmp);
        allocFromBmp.copyTo(bmp);
        rs.destroy();
        return bmp;
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        switch (keyCode) {
// 音量减小
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                button_A.setBackground(getDrawable(R.drawable.pressed));ms.A=255;
                return true;
// 音量增大
            case KeyEvent.KEYCODE_VOLUME_UP:
                button_B.setBackground(getDrawable(R.drawable.pressed)); ms.B=255;
                return true;
            case KeyEvent.KEYCODE_BACK:
                if(locked){
                return true;}
        }
        return super.onKeyDown (keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                button_A.setBackground(getDrawable(R.drawable.unpress));ms.A=0;
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                button_B.setBackground(getDrawable(R.drawable.unpress)); ms.B=0;
                return true;
        }
        return super.onKeyUp (keyCode, event);
    }
    private void onBatteryInfoReceiver(int intLevel, int intScale) {
        // TODO Auto-generated method stub
        int percent = intLevel*100/ intScale;
        if(percent>90){
            ms.battery=0x05;
        }
        else if(percent>80&&percent<90){
            ms.battery=0x04;
        }
        else if(percent<80&&percent>40){
            ms.battery=0x03;
        }else if(percent<40&&percent>20){
            ms.battery=0x02;
        }else if(percent<20){
            ms.battery=0x01;
        }
    };
    //创建BroadcastReceiver
    private BroadcastReceiver mBatInfoReveiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //如果捕捉到的Action是ACTION_BATTERY_CHANGED则运行onBatteryInforECEIVER()
            if(intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                //获得当前电量
                int intLevel = intent.getIntExtra("level",0);
                //获得手机总电量
                int intScale = intent.getIntExtra("scale",100);
                // 在下面会定义这个函数，显示手机当前电量
                onBatteryInfoReceiver(intLevel, intScale);
            }
        }
    };

    private Button buttontemp;
    private String Buttonnamex;
//    @Override
//    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//    }
    private void Inputbox(Button mbutton,String Buttonname){
        EdittextDialog.show();
        View viewx = findViewById(R.id.linearLayout);
        Bitmap bitmap2 = Bitmap.createBitmap(viewx.getWidth(), viewx.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap=Bitmap.createBitmap(viewx.getWidth(),viewx.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvasback=new Canvas(bitmap);
        canvasback.drawColor(ContextCompat.getColor(MainActivity.this, R.color.background));
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap2);
        viewx.draw(canvas);
        bitmap2=blur(bitmap2,25);
        bitmap2=mergeBitmap(bitmap,bitmap2);
        Animation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(100);//设置动画持续时间为500毫秒
        alphaAnimation.setFillAfter(false);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
        ImageView backg=findViewById(R.id.imageView);
        backg.setAnimation(alphaAnimation);
        backg.setImageBitmap(bitmap2);
        backg.setVisibility(View.VISIBLE);
        Editbtntext.setText(mbutton.getText());
        buttontemp=mbutton;
        Buttonnamex=Buttonname;

    }
    private void loadbutton(){
        loadbuttontext(button_A,"ButtonA");
        loadbuttontext(button_B,"ButtonB");
        loadbuttontext(button_X,"ButtonX");
        loadbuttontext(button_Y,"ButtonY");
        loadbuttontext(button_DUP,"ButtonDup");
        loadbuttontext(button_DDOWN,"ButtonDdown");
        loadbuttontext(button_DLEFT,"ButtonDleft");
        loadbuttontext(button_DRight,"ButtonDright");
        loadbuttontext(button_PLUS,"Buttonplus");
        loadbuttontext(button_DEDUCE,"Buttondeduce");
        loadbuttontext(BUTTON_home,"Buttonhome");
        loadbuttontext(L2,"ButtonL2");
        loadbuttontext(R2,"ButtonR2");
        loadbuttontext(R3,"ButtonR3");
        loadbuttontext(L3,"ButtonL3");
        loadbuttontext(OPkey,"ButtonOP");
        loadbuttontext(SHARE,"ButtonSh");
    }
    private void loadbuttontext(Button mbutton,String Buttonname){
        SharedPreferences userInfo = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        String text;
        text=userInfo.getString(Buttonname,"NonexistFLAG@");
        String Dont="NonexistFLAG@";
        if(text.equals(Dont)){
            return;
        }
        mbutton.setText(text);
    }


    boolean edit_flag=true;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.sensitivelay:
                        getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                        yourDialog.show();

                        View viewx = findViewById(R.id.linearLayout);
                        Bitmap bitmap2 = Bitmap.createBitmap(viewx.getWidth(), viewx.getHeight(), Bitmap.Config.ARGB_8888);
                        Bitmap bitmap = Bitmap.createBitmap(viewx.getWidth(), viewx.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvasback = new Canvas(bitmap);
                        canvasback.drawColor(ContextCompat.getColor(MainActivity.this, R.color.background));
                        Canvas canvas = new Canvas();
                        canvas.setBitmap(bitmap2);
                        viewx.draw(canvas);
                        bitmap2 = blur(bitmap2, 25);
                        bitmap2 = mergeBitmap(bitmap, bitmap2);
                        Animation alphaAnimation = new AlphaAnimation(0f, 1f);
                        alphaAnimation.setDuration(100);//设置动画持续时间为500毫秒
                        alphaAnimation.setFillAfter(false);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
                        ImageView backg = findViewById(R.id.imageView);
                        backg.setAnimation(alphaAnimation);
                        backg.setImageBitmap(bitmap2);
                        backg.setVisibility(View.VISIBLE);
                        break;
                    case R.id.app_bar_switch:
                        if (edit_flag) {
                            isEditMode = true;
                            Toast.makeText(MainActivity.this, "Enter Edit Mode", Toast.LENGTH_LONG).show();
                            edit_flag = !edit_flag;
                        } else {
                            isEditMode = false;
                            Toast.makeText(MainActivity.this, "Button Text has saved", Toast.LENGTH_LONG).show();
                            edit_flag = !edit_flag;
                        }
                        break;
                    case R.id.joystick_sw:
                        LinearLayout Joysticklayout=findViewById(R.id.joystick_layout);
                        if( Joysticklayout.getVisibility()==View.VISIBLE){
                            TranslateAnimation   mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                                    -1.0f);
                            mHiddenAction.setDuration(200);
                            mHiddenAction.setInterpolator(new AccelerateInterpolator());
                            Joysticklayout.startAnimation(mHiddenAction);
                            Joysticklayout.setVisibility(View.GONE);
                        }else{
                            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                            mShowAction.setDuration(200);
                            mShowAction.setInterpolator(new AccelerateInterpolator());
                            Joysticklayout.startAnimation(mShowAction);
                            Joysticklayout.setVisibility(View.VISIBLE);
                        }
                        break;
                    case R.id.about_menu:
                        Toast.makeText(MainActivity.this,"Thank For Using,Please report bugs in github.Playxboxtion963",Toast.LENGTH_LONG).show();
                        break;
                }
                return true;

        }
    };
    //oled防烧代码
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(){
        public void handleMessage(Message message){
            Random r = new Random();
            ConstraintLayout mview= findViewById(R.id.main);
            switch (message.what){
                case 1:
                    mview.scrollTo( r.nextInt(18)+3,0);
                    break;
                case 2:
                    mview.scrollTo(0,r.nextInt(18)+3);
                    break;
                case 3:
                    mview.scrollTo( r.nextInt(21) - 20,0);
                    break;
                case 4:
                    mview.scrollTo(0,r.nextInt(21) - 20);
                    break;
            }
        }
    };

    @Override
    public boolean onLongClick(View view) {
        if(view.getId()==R.id.LOCK){
            if(locked){
                getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Touch.setText("LOCK");
                ImageView backg=findViewById(R.id.imageView);
                Animation alphaAnimation = new AlphaAnimation(1f, 0f);
                alphaAnimation.setDuration(100);//设置动画持续时间为500毫秒
                alphaAnimation.setFillAfter(false);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
                backg.setAnimation(alphaAnimation);
                backg.setVisibility(View.INVISIBLE);
                timerx.cancel();
                ConstraintLayout mview= findViewById(R.id.main);
                mview.scrollTo(0,0);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(1);
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
//                findViewById(R.id.sensitivelay).setVisibility(View.VISIBLE);
                findViewById(R.id.LOCK).startAnimation(alphaAnimation);
                findViewById(R.id.LOCK).setVisibility(View.INVISIBLE);
//                findViewById(R.id.editmode).setVisibility(View.VISIBLE);
                Toolbar mtool=findViewById(R.id.toolbar);
                mtool.setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                locked=!locked;
            }
        }
        return false;
    }
    public static Bitmap handleImageEffect(Bitmap bitmap,float lum) {
        //传进来的bitmap默认不能修改  所以再创建一个bm
        Bitmap bm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //画布
        Canvas canvas = new Canvas(bm);
        //抗锯齿
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //修改亮度
        ColorMatrix lumMatrix = new ColorMatrix();
        //r g b a    1 表示全不透明
        lumMatrix.setScale(lum, lum, lum, 1);
        //组合Matrix
        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(lumMatrix);
        //为画笔设置颜色过滤器
        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
        //在canvas上照着bitmap画
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bm;
    }
    public void joystick_show(){
        final double[] angle_all = new double[1];
        final int[] distance = new int[1];
        MyRockerView myRockerView=findViewById(R.id.rocker_view);
        myRockerView.setOnShakeListener(MyRockerView.DirectionMode.DIRECTION_8, new MyRockerView.OnShakeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void direction(MyRockerView.Direction direction) {
                switch (direction){
                    case DIRECTION_LEFT:
                        break;
                    case DIRECTION_RIGHT:
                        break;
                    case DIRECTION_UP:
                        break;
                    case DIRECTION_DOWN:
                        break;
                    case DIRECTION_UP_LEFT:
                        break;
                    case DIRECTION_UP_RIGHT:
                        break;
                    case DIRECTION_DOWN_LEFT:
                        break;
                    case DIRECTION_DOWN_RIGHT:
                        break;
                    case DIRECTION_CENTER:
                        calculator_joystick(0,0);
                        break;
                }
            }

            @Override
            public void onFinish() {

            }
        });
        myRockerView.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void angle(double angle) {
                calculator_joystick(distance[0], angle);
            }

            @Override
            public void onFinish() {

            }
        });
        myRockerView.setOnDistanceLevelListener(new MyRockerView.OnDistanceLevelListener() {
            @Override
            public void onDistanceLevel(int level) {
                distance[0] =level;
                calculator_joystick(level, angle_all[0]);

            }
        });
        MyRockerView myRockerView_2=findViewById(R.id.rocker_view_2);
        myRockerView_2.setOnShakeListener(MyRockerView.DirectionMode.DIRECTION_8, new MyRockerView.OnShakeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void direction(MyRockerView.Direction direction) {
                switch (direction){
                    case DIRECTION_LEFT:
                        break;
                    case DIRECTION_RIGHT:
                        break;
                    case DIRECTION_UP:
                        break;
                    case DIRECTION_DOWN:
                        break;
                    case DIRECTION_UP_LEFT:
                        break;
                    case DIRECTION_UP_RIGHT:
                        break;
                    case DIRECTION_DOWN_LEFT:
                        break;
                    case DIRECTION_DOWN_RIGHT:
                        break;
                    case DIRECTION_CENTER:
                        calculator_joystick_2(0,0);
                        break;
                }
            }

            @Override
            public void onFinish() {

            }
        });
        myRockerView_2.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void angle(double angle) {
                calculator_joystick_2(distance[0], angle);
            }

            @Override
            public void onFinish() {

            }
        });
        myRockerView_2.setOnDistanceLevelListener(new MyRockerView.OnDistanceLevelListener() {
            @Override
            public void onDistanceLevel(int level) {
                distance[0] =level;
                calculator_joystick_2(level, angle_all[0]);

            }
        });
    }
    public void calculator_joystick(int distance,double angle){
        if(locked){
            return;
        }
        double radians = Math.toRadians(angle);
        double joystick_x=(int) (Math.sin(radians)*distance+128);
        double joystick_y=(int) (Math.cos(radians)*distance+128);
        ms.left_stick_x= (int) joystick_x;
        ms.left_stick_y= (int) joystick_y;
    }
    public void calculator_joystick_2(int distance,double angle){
        if(locked){
            return;
        }
        double radians = Math.toRadians(angle);
        double joystick_x=(int) (Math.sin(radians)*distance+128);
        double joystick_y=(int) (Math.cos(radians)*distance+128);
        ms.right_stick_x= (int) joystick_x;
        ms.right_stick_y= (int) joystick_y;
    }
}