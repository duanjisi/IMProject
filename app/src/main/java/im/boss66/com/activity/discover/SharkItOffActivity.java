package im.boss66.com.activity.discover;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 摇一摇主界面
 */
public class SharkItOffActivity extends BaseActivity implements SensorEventListener, View.OnClickListener {

    private TextView tv_back, tv_bottom;
    private ImageView iv_set;
    private ProgressBar pb_shark;

    private Sensor mAccelerometerSensor;
    private SensorManager sensorManager = null;
    private Vibrator vibrator = null;
    private boolean isShake = false;

    private static final int START_SHAKE = 0x1;
    private static final int AGAIN_SHAKE = 0x2;
    private static final int END_SHAKE = 0x3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shark_it_off);
        initView();
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //获取 SensorManager 负责管理传感器
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        if (sensorManager != null) {
            //获取加速度传感器
            mAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometerSensor != null) {
                sensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        pb_shark = (ProgressBar) findViewById(R.id.pb_shark);
        tv_back.setOnClickListener(this);
        iv_set.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAccelerometerSensor != null && sensorManager != null) {
            sensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SHAKE:
                    vibrator.vibrate(300);
                    //发出提示音
                    pb_shark.setVisibility(View.VISIBLE);
                    tv_bottom.setText(getResources().getString(R.string.shark_it_off_ing));
                    Log.i("onSensorChanged:", "开始");
                    break;
                case AGAIN_SHAKE:
                    vibrator.vibrate(300);
                    Log.i("onSensorChanged:", "ing");
                    break;
                case END_SHAKE:
                    //整体效果结束, 将震动设置为false
                    isShake = false;
                    pb_shark.setVisibility(View.GONE);
                    tv_bottom.setText(getResources().getString(R.string.shark_it_off));
                    Log.i("onSensorChanged:", "结束");
                    break;
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            if ((Math.abs(x) > 19 || Math.abs(y) > 19 || Math
                    .abs(z) > 19) && !isShake) {
                isShake = true;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Log.d("onSensorChangedxyz---", "onSensorChanged: 摇动");

                            //开始震动 发出提示音 展示动画效果
                            handler.obtainMessage(START_SHAKE).sendToTarget();
                            //Thread.sleep(500);
                            //再来一次震动提示
                            handler.obtainMessage(AGAIN_SHAKE).sendToTarget();
                            Thread.sleep(2000);
                            handler.obtainMessage(END_SHAKE).sendToTarget();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }

//        int sensorType = event.sensor.getType();
//        // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
//        float[] values = event.values;
//        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
//            if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
//                    .abs(values[2]) > 17) && !isShake) {
//                isShake = true;
//                new Thread() {
//                    public void run() {
//                        try {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    // 摇动手机后，再伴随震动提示~~
//                                    vibrator.vibrate(300);
//                                    pb_shark.setVisibility(View.VISIBLE);
//                                    tv_bottom.setText(getResources().getString(R.string.shark_it_off_ing));
//                                }
//                            });
//                            Thread.sleep(500);
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    // 摇动手机后，再伴随震动提示~~
//                                    vibrator.vibrate(300);
//                                }
//                            });
//                           // Thread.sleep(500);
//                            runOnUiThread(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    // TODO Auto-generated method stub
//                                    isShake = false;
//                                    pb_shark.setVisibility(View.GONE);
//                                    tv_bottom.setText(getResources().getString(R.string.shark_it_off));
//                                }
//                            });
//                        } catch (InterruptedException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    };
//                }.start();
//            }
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:
                openActivity(SharkItOffSetActivity.class);
                break;
        }
    }
}
