package im.boss66.com.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;


/**
 * Created by bin on 2016/3/17.
 * 通用dialog工具类
 */
public class CommonDialogUtils {

    private static AlertDialog dialog;

    public static void showDialog(View.OnClickListener onCancelClickListener, View.OnClickListener onConfirmClickListener, Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_dialog_notification, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(false);
        ((TextView) view.findViewById(R.id.title)).setText("提示");
        ((TextView) view.findViewById(R.id.message)).setText(msg);
        view.findViewById(R.id.option).setOnClickListener(onConfirmClickListener);
        view.findViewById(R.id.cancel).setOnClickListener(onCancelClickListener);
        dialog.show();
    }

    public static void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }
}
