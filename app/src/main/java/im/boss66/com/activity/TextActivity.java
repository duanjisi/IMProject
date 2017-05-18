package im.boss66.com.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import im.boss66.com.R;
import im.boss66.com.widget.AutoVerticalScrollTextView;

public class TextActivity extends Activity {

    private int number = 0;
    private boolean isRunning = true;

    private String[] strings = {"我的剑，就是你的剑!", "俺也是从石头里蹦出来得!", "我用双手成就你的梦想!", "人在塔在!", "犯我德邦者，虽远必诛!", "我会让你看看什么叫残忍!", "我的大刀早已饥渴难耐了!"};
    private String string = "我的剑，就是你的剑!   俺也是从石头里蹦出来得!    我用双手成就你的梦想!    人在塔在!    犯我德邦者，虽远必诛!    我会让你看看什么叫残忍!    我的大刀早已饥渴难耐了!";

    private AutoVerticalScrollTextView verticalScrollTV;
//    private AutoHorizontalScrollTextView horizontalScrollTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview);

//        initView();
    }

    private void initView() {

//        horizontalScrollTV = (AutoHorizontalScrollTextView) findViewById(R.id.textview);
//        horizontalScrollTV.setText(string);

//        verticalScrollTV = (AutoVerticalScrollTextView) findViewById(R.id.textview_auto_roll);
//        verticalScrollTV.setText(strings[0]);
//
//        new Thread() {
//            @Override
//            public void run() {
//                while (isRunning) {
//                    SystemClock.sleep(3000);
//                    handler.sendEmptyMessage(199);
//                }
//            }
//        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 199) {
                verticalScrollTV.next();
                number++;
                verticalScrollTV.setText(strings[number % strings.length]);
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
