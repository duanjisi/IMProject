package im.boss66.com.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import im.boss66.com.Constants;
import im.boss66.com.entity.ActionEntity;
import im.boss66.com.util.Utils;

/**
 * Created by Johnny on 2017/5/12.
 */
public class CoreService extends Service {
    private TimerTask task;
    private MyThread myThread;

    @Override
    public void onCreate() {
        super.onCreate();
        myThread = new MyThread();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        test();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void test() {
        task = new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new ActionEntity("com.duanjisi.test.service"));
                boolean isRunning = Utils.isServiceWork(CoreService.this, "im.boss66.com.services.CoreService");
                Log.i("info", "======================isRunningCoreService:" + isRunning);
                System.out.println("======================Hello !!!");
            }
        };
        Timer timer = new Timer();
        long delay = 0;
        long intevalPeriod = 1 * 1000;
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
//        myThread.start();
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    EventBus.getDefault().post(new ActionEntity("com.duanjisi.test.service"));
                    boolean isRunning = Utils.isServiceWork(CoreService.this, "im.boss66.com.services.CoreService");
                    Log.i("info", "======================isRunningCoreService:" + isRunning);
                    System.out.println("======================Hello !!!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void actionStart(Context ctx) {
        //System.out.println("---- Notification service started!");
        Intent i = new Intent(ctx, CoreService.class);
        i.setAction(Constants.Action.ACTION_START);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startService(i);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
