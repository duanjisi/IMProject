package im.boss66.com.widget;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneStateReceiver extends BroadcastReceiver {

	private PhoneListener mpl;

	public void setPhoneListener(PhoneListener l) {
		mpl = l;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// 如果是来电
		if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);

			tm.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
		}
	}

	class MyPhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if (mpl != null) {
					mpl.idle();
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				if (mpl != null) {
					mpl.calling();
				}
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}
	
	public interface PhoneListener{
		public void calling();
		public void idle();
	}
}
