package im.boss66.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import im.boss66.com.Constants;
import im.boss66.com.services.ChatServices;

/**
 * Created by Johnny on 2017/3/24.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constants.Action.CHAT_SERVICE_CLOSE)) {
            ChatServices.startChatService(context);
        }
    }
}
