package im.boss66.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.Session;
import im.boss66.com.activity.LoginActivity;

/**
 * Created by Johnny on 2017/3/4.
 */
public class LogoutReceiver extends BroadcastReceiver {
    private Handler handler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constants.ACTION_LOGOUT_RESETING)) {
            Session.getInstance().stopChatService();
            App.getInstance().logout();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("info", "===============启动登录界面");
                    Intent it = new Intent(context, LoginActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                    App.getInstance().exit();
                }
            }, 100);
        }
    }
}
