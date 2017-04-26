package im.boss66.com.widget.dialog;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.im.SelectConversationActivity;
import im.boss66.com.db.MessageDB;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.MessageItem;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/4/14.
 */
public class ChatDialog extends BaseDialog implements View.OnClickListener {
    private TextView tvCopy;
    private TextView tvRewarding;
    private TextView tvStore;
    private TextView tvDelete;
    private MessageDB mMsgDB;// 保存消息的数据库
    private String MsgTab;
    private String toUid;
    private MessageItem messageItem;
    private CharSequence message;
    private boolean isTxt = false;
    private boolean isVoice = false;
    private boolean isEmo = false;

    public ChatDialog(Context context, Intent intent) {
        super(context);
        dialog.setCanceledOnTouchOutside(true);
        initViews(intent);
    }


    private void initViews(Intent intent) {
        mMsgDB = App.getInstance().getMessageDB();
        tvCopy = (TextView) dialog.findViewById(R.id.tv_copy);
        tvRewarding = (TextView) dialog.findViewById(R.id.tv_rewarding);
        tvStore = (TextView) dialog.findViewById(R.id.tv_store);
        tvDelete = (TextView) dialog.findViewById(R.id.tv_delete);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvCopy.getLayoutParams();
        params.width = UIUtils.getScreenWidth(context) * 8 / 10;
        tvCopy.setLayoutParams(params);

        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) tvRewarding.getLayoutParams();
        params2.width = UIUtils.getScreenWidth(context) * 8 / 10;
        tvRewarding.setLayoutParams(params2);

        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams) tvStore.getLayoutParams();
        params4.width = UIUtils.getScreenWidth(context) * 8 / 10;
        tvStore.setLayoutParams(params4);

        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) tvDelete.getLayoutParams();
        params3.width = UIUtils.getScreenWidth(context) * 8 / 10;
        tvDelete.setLayoutParams(params3);

        tvCopy.setPadding(40, 0, 0, 0);
        tvRewarding.setPadding(40, 0, 0, 0);
        tvStore.setPadding(40, 0, 0, 0);
        tvDelete.setPadding(40, 0, 0, 0);
        tvCopy.setOnClickListener(this);
        tvRewarding.setOnClickListener(this);
        tvStore.setOnClickListener(this);
        tvDelete.setOnClickListener(this);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            toUid = bundle.getString("toUid", "");
            messageItem = (MessageItem) bundle.getSerializable("item");
            message = bundle.getCharSequence("msg", "");
            isTxt = bundle.getBoolean("is_txt", false);
            isVoice = bundle.getBoolean("isVoice", false);
            isEmo = bundle.getBoolean("isEmo", false);
            MsgTab = App.getInstance().getUid() + "_" + toUid;
        }

        if (isVoice) {
            tvCopy.setVisibility(View.GONE);
            tvRewarding.setVisibility(View.GONE);
            tvStore.setVisibility(View.GONE);
            tvDelete.setBackgroundResource(R.drawable.shape_nearby_bt_bg_white_dark);
            tvCopy.setPadding(40, 0, 0, 0);
            tvRewarding.setPadding(40, 0, 0, 0);
            tvStore.setPadding(40, 0, 0, 0);
            tvDelete.setPadding(40, 0, 0, 0);
        } else {
            if (isTxt) {
                tvCopy.setVisibility(View.VISIBLE);
                tvCopy.setBackgroundResource(R.drawable.actionsheet_top_selector);
                tvRewarding.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                tvStore.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                tvDelete.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                tvCopy.setPadding(40, 0, 0, 0);
                tvRewarding.setPadding(40, 0, 0, 0);
                tvStore.setPadding(40, 0, 0, 0);
                tvDelete.setPadding(40, 0, 0, 0);
            } else {
                if (!isEmo) {
                    tvCopy.setVisibility(View.GONE);
                    tvStore.setVisibility(View.VISIBLE);
                    tvRewarding.setBackgroundResource(R.drawable.actionsheet_top_selector);
                    tvStore.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                    tvDelete.setBackgroundResource(R.drawable.actionsheet_bottom_selector);

                    tvCopy.setPadding(40, 0, 0, 0);
                    tvRewarding.setPadding(40, 0, 0, 0);
                    tvStore.setPadding(40, 0, 0, 0);
                    tvDelete.setPadding(40, 0, 0, 0);
                } else {
                    tvCopy.setVisibility(View.GONE);
                    tvStore.setVisibility(View.GONE);
                    tvRewarding.setBackgroundResource(R.drawable.actionsheet_top_selector);
//                    tvStore.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                    tvDelete.setBackgroundResource(R.drawable.actionsheet_bottom_selector);

                    tvCopy.setPadding(40, 0, 0, 0);
                    tvRewarding.setPadding(40, 0, 0, 0);
//                    tvStore.setPadding(40, 0, 0, 0);
                    tvDelete.setPadding(40, 0, 0, 0);
                }
            }
        }
    }


    @Override
    protected int getView() {
        return R.layout.dialog_chat;
    }

    @Override
    protected int getDialogStyleId() {
        return R.style.dialog_ios_style;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy:
                if (!message.equals("")) {
                    ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(message);
                    Toast.makeText(context, "已复制", Toast.LENGTH_SHORT);
                }
                dismiss();
                break;
            case R.id.tv_rewarding:
                if (messageItem != null) {
                    Intent intent = new Intent(context, SelectConversationActivity.class);
                    intent.putExtra("item", messageItem);
                    context.startActivity(intent);
                }
                dismiss();
                break;
            case R.id.tv_store:
                if (messageItem != null) {
                    storeResourse(messageItem);
                }
                break;
            case R.id.tv_delete:
                if (messageItem != null) {
                    mMsgDB.clearMsgItem(messageItem, MsgTab);
                    Intent intent = new Intent(Constants.Action.REMOVE_CHAT_MESSAGE_DATA);
                    intent.putExtra("msg_obj", messageItem);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                dismiss();
                break;
        }
    }

    public void showDialog() {
        if (dialog != null && !isShowing()) {
            show();
        }
    }

    private void storeResourse(MessageItem messageItem) {
        switch (messageItem.getMsgType()) {
            case MessageItem.MESSAGE_TYPE_TXT:
                addCollectToServer(messageItem.getMessage(), "", 0, messageItem.getUserid());
                break;
            case MessageItem.MESSAGE_TYPE_IMG:
                addCollectToServer(messageItem.getMessage(), messageItem.getMessage(), 1, messageItem.getUserid());
                break;
            case MessageItem.MESSAGE_TYPE_VIDEO:
                String videoPath = messageItem.getMessage();
                String imageUrl = "";
                if (videoPath.contains(".mp4")) {
                    imageUrl = videoPath.replace(".mp4", ".jpg");
                } else if (videoPath.contains(".mov")) {
                    imageUrl = videoPath.replace(".mov", ".jpg");
                }
                if (!TextUtils.isEmpty(imageUrl)) {
                    addCollectToServer(videoPath, imageUrl, 2, messageItem.getUserid());
                }
                break;
        }
    }


    private void addCollectToServer(String imgUrl, String thumUrl, int type, String fromid) {
        showLoadingDialog();
        String access_token = App.getInstance().getAccount().getAccess_token();
        String url = HttpUrl.ADD_PERSONAL_COLLECT;
        HttpUtils httpUtils = new HttpUtils(30 * 1000);
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        switch (type) {
            case 0://文字
                url = url + "?fromid=" + fromid + "&type=" + type + "&text=" + imgUrl;
                break;
            case 1://图片
                url = url + "?fromid=" + fromid + "&type=" + type + "&url=" + imgUrl + "&thum=" + thumUrl;
                break;
            case 2://视频
                url = url + "?fromid=" + fromid + "&type=" + type + "&url=" + imgUrl + "&thum=" + thumUrl;
                break;
        }
        Log.i("url:", url);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                Log.i("onSuccess:", "" + result);
                if (result != null) {
                    BaseResult personalCollect = JSON.parseObject(result, BaseResult.class);
                    if (personalCollect != null) {
                        if (personalCollect.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                            dismiss();
                        } else {
                            if (personalCollect.getCode() == 1) {
                                showToast("收藏成功", false);
                                dismiss();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    cancelLoadingDialog();
                    showToast(s, false);
                    dismiss();
                }
            }
        });
    }

    private void goLogin() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGOUT_RESETING);
        App.getInstance().sendBroadcast(intent);
        dismiss();
    }
}
