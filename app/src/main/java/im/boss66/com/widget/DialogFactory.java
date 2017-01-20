package im.boss66.com.widget;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

/**
 * 根据不同的版本创建对话框
 * 
 * @ClassName: DialogFactory
 * @Description: TODO
 * @author: zyu
 * @date: 2014-11-23 下午10:25:26
 */
public class DialogFactory {
	/**
	 * 根据不同的android版本创建不同风格的对话框builder
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static AlertDialog.Builder createAlertDialogBuilder(Context context) {
		AlertDialog.Builder builder = null;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
		} else {
			builder = new AlertDialog.Builder(context);
		}
		return builder;
	}

	/**
	 * 根据不同的android版本创建不同风格的ProgressDialog
	 * 
	 * @param context
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static ProgressDialog createProgressDialog(Context context) {
		ProgressDialog dialog = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
		} else {
			dialog = new ProgressDialog(context);
		}

		return dialog;
	}
}