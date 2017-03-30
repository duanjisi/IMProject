package im.boss66.com.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;

import im.boss66.com.R;
import im.boss66.com.Utils.AppUtil;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.services.UpdateService;

public class AutoUpdateUtil {

    /**
     * @param maxVersion 最高版本
     * @param context
     * @param updateUrl  更新路径
     * @param content    内容提示
     * @param isForce    是否强制更新
     * @param isShowTip  是否显示提示
     */
    public synchronized static void update(final String maxVersion,
                                           final Context context, final String updateUrl, final String content, boolean isForce, boolean isShowTip) {
        MycsLog.i("version:" + maxVersion);
        if (maxVersion.compareTo(AppUtil.getVersionName(context)) > 0)// 是否必须更新
        {
            if (!("").equals(updateUrl) && null != updateUrl) // 如果不同版本，则更新
            {
                AlertDialog alert;
                Builder builder = new Builder(context)
                        .setTitle(maxVersion + "版本更新")
                        .setMessage("更新内容:\n" + content + "\n是否更新？").setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface paramDialogInterface,
                                    int paramInt) {
                                paramDialogInterface.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra("url", updateUrl);
                                intent.setClass(context, UpdateService.class);
                                context.startService(intent);
                            }
                        });
                if (isForce) {
                    builder.setCancelable(false)
                            .setNegativeButton(context.getString(R.string.opera_quit), new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (context instanceof Activity) {
                                        ((Activity) context).finish();
                                    }
                                }
                            });
                } else {
                    builder.setNegativeButton("取消", new OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface paramDialogInterface,
                                int paramInt) {
                            paramDialogInterface.dismiss();
                        }
                    });
                }
                alert = builder.create();
                alert.show();
            }
        } else {
            if (isShowTip) {
                Toast.makeText(context, "已经是最新版本，无需更新", Toast.LENGTH_LONG).show();
            }
        }
    }
}
