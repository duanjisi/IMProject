package im.boss66.com.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.activity.MainAct;
import im.boss66.com.activity.SplashActivity;
import im.boss66.com.entity.ActionEntity;

/**
 * Created by Johnny on 2017/5/15.
 */
public class ChatReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("info", "==================action:" + action);
        if (action.equals(Constants.Action.ACTIVITY_CLOSE)) {
            Log.i("info", "==================关闭通知栏");
            if (!App.getInstance().isForground()) {
                goForground(context);
            } else {
                EventBus.getDefault().post(new ActionEntity(Constants.Action.SWITCH_CHAT_MESSAGE));
            }
        }
    }

    private void goForground(Context context) {
        Log.i("info", "==================goForground()");
        //获取ActivityManager
        ActivityManager mAm = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            Log.i("info", "================PackageName:" + rti.topActivity.getPackageName());
            if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
                mAm.moveTaskToFront(rti.id, 0);
                EventBus.getDefault().post(new ActionEntity(Constants.Action.SWITCH_CHAT_MESSAGE));
                return;
            }
        }
        Log.i("info", "=======================11111111111111111");
        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
        Intent resultIntent = null;
        if (App.getInstance().isLogin()) {
            resultIntent = new Intent(context, MainAct.class);
        } else {
            resultIntent = new Intent(context, SplashActivity.class);
        }
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(resultIntent);
    }
}
