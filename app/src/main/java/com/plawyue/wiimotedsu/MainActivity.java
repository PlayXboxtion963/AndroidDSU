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
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnLongClickListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    MontionServer ms=new MontionServer();
    static double PI = 3.1415926535897932;
    Button L3,R3,OPkey,SHARE,Colorx;
    Boolean locked=false;
    Button button_A,button_B,button_DUP,button_DDOWN,button_DLEFT,button_DRight,button_PLUS,button_DEDUCE,BUTTON_home,L2,R2,Touch,button_X,button_Y;
    float accSensitivity=0.9f;
    Boolean isEditMode=false;
    Dialog yourDialog;
    SeekBar yourDialogSeekBar;
    static float METER_PER_SECOND_SQUARED_TO_G = (float) 9.8066;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mtoorbar=findViewById(R.id.toolbar);
        mtoorbar.setOnMenuItemClickListener(onMenuItemClick);
        mtoorbar.inflateMenu(R.menu.menu);
        registerReceiver(mBatInfoReveiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        Switch editmodeswitch=findViewById(R.id.editmode);
        editmodeswitch.setOnCheckedChangeListener(this);


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.senseseekbar, (ViewGroup) findViewById(R.id.elementseek));
        Button yourDialogButton = (Button) layout.findViewById(R.id.your_dialog_button);
        yourDialogSeekBar = layout.findViewById(R.id.your_dialog_seekbar);
        yourDialog = new Dialog(this);
        yourDialog.setContentView(layout);
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
                lighttext.setText("sensitive set as:" + progress/10.0);
                accSensitivity= (float) (progress/10.0);
            }
        };
        yourDialogButton.setOnClickListener(this);
        yourDialogSeekBar.setOnSeekBarChangeListener(yourSeekBarListener);
        Lightlayout = layout;
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
        accSensitivity=userInfo.getFloat("sensisavin",0.9F);
        yourDialogSeekBar.setProgress((int) (accSensitivity*10));
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
                float accX = -accSensitivity * event.values[2] * METER_PER_SECOND_SQUARED_TO_G / 100;
                float accY =  -accSensitivity * event.values[0] * METER_PER_SECOND_SQUARED_TO_G / 100;
                float accZ =accSensitivity * event.values[1] * METER_PER_SECOND_SQUARED_TO_G / 100;
                DecimalFormat df = new DecimalFormat("#.00");
                ms.accX= Float.parseFloat(df.format(accY));
                ms.accY=Float.parseFloat(df.format(accX));
                ms.accZ=Float.parseFloat(df.format(accZ));


            }
        };
        SensorEventListener gyrolinster=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                ms.gyroR= (float) (radToDeg(sensorEvent.values[1]) * 0.8);
                ms.gyroY= (float) (-radToDeg(sensorEvent.values[2]) * 0.8);
                ms.gyroP= (float) (radToDeg(sensorEvent.values[0]) * 0.8);
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
                SensorManager.SENSOR_DELAY_UI);

        loadbutton();

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

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.LOCK){
            if(locked){
//                getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//                Touch.setText("LOCK");
//                ImageView backg=findViewById(R.id.imageView);
//                Animation alphaAnimation = new AlphaAnimation(1f, 0f);
//                alphaAnimation.setDuration(100);//设置动画持续时间为500毫秒
//                alphaAnimation.setFillAfter(false);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
//                backg.setAnimation(alphaAnimation);
//                backg.setVisibility(View.INVISIBLE);
//                timerx.cancel();
//                ConstraintLayout mview= findViewById(R.id.main);
//                mview.scrollTo(0,0);
//                Paint paint = new Paint();
//                ColorMatrix cm = new ColorMatrix();
//                cm.setSaturation(1);
//                paint.setColorFilter(new ColorMatrixColorFilter(cm));
//                getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
//                findViewById(R.id.sensitivelay).setVisibility(View.VISIBLE);
//                findViewById(R.id.LOCK).startAnimation(alphaAnimation);
//                findViewById(R.id.LOCK).setVisibility(View.INVISIBLE);
//
//                findViewById(R.id.editmode).setVisibility(View.VISIBLE);
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                locked=!locked;
            }
        }
        if(view.getId()==R.id.your_dialog_button){
            SharedPreferences userInfo = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
            SharedPreferences.Editor editor = userInfo.edit();//获取Editor
            yourDialog.cancel();
            editor.putFloat("sensisavin", accSensitivity);
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

                View viewx = getWindow().getDecorView();
                Bitmap bitmap2 = Bitmap.createBitmap(viewx.getWidth(), viewx.getHeight()+80, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas();
                canvas.setBitmap(bitmap2);
                viewx.draw(canvas);
                bitmap2=blur(bitmap2,25);
                Animation alphaAnimation = new AlphaAnimation(0f, 1f);
                alphaAnimation.setDuration(100);//设置动画持续时间为500毫秒
                alphaAnimation.setFillAfter(false);//设置动画结束后保持当前的位置（即不返回到动画开始前的位置）
                backg.setAnimation(alphaAnimation);
                backg.setImageBitmap(bitmap2);
                backg.setVisibility(View.VISIBLE);
                timerx=new Timer();
                TimerTask timertask = new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        long currentTime = System.currentTimeMillis();
                        if (currentTime-lasttime>20000){
                            lasttime = currentTime;
                            message.what = 4;
                        }else if(currentTime-lasttime>15000){
                            message.what = 3;
                        }else if(currentTime-lasttime>10000){
                            message.what = 2;
                        }
                        else if(currentTime-lasttime>5000){
                            message.what = 1;
                        }
                        handler.sendMessage(message);
                    }
                };
                timerx.schedule(timertask,1000,5000);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(1);
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
                findViewById(R.id.editmode).setVisibility(View.INVISIBLE);
                findViewById(R.id.LOCK).setVisibility(View.VISIBLE);
                findViewById(R.id.LOCK).startAnimation(alphaAnimation);
                findViewById(R.id.sensitivelay).setVisibility(View.INVISIBLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                locked=!locked;
            }

        }
        if(isEditMode) {
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


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            isEditMode=true;
        }else{
            isEditMode=false;
            Toast.makeText(this,"Button Text has saved",Toast.LENGTH_LONG).show();
        }
    }
    private void Inputbox(Button mbutton,String Buttonname){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Button Text").setIcon(R.drawable.ic_editmode).setView(inputServer)
                .setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String result =inputServer.getText().toString();
                mbutton.setText(result);
                SharedPreferences userInfo = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                SharedPreferences.Editor editor = userInfo.edit();//获取Editor
                editor.putString(Buttonname,result);
                editor.commit();
            }
        });
        builder.show();
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
    private View Lightlayout;
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.sensitivelay:
                    yourDialog.show();
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
                findViewById(R.id.sensitivelay).setVisibility(View.VISIBLE);
                findViewById(R.id.LOCK).startAnimation(alphaAnimation);
                findViewById(R.id.LOCK).setVisibility(View.INVISIBLE);
                findViewById(R.id.editmode).setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                locked=!locked;
            }
        }
        return false;
    }
}