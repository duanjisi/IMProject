package im.boss66.com.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtil;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.db.dao.EmoHelper;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.EmoEntity;
import im.boss66.com.entity.EmotionEntity;
import im.boss66.com.entity.MessageItem;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.EmoParseRequest;
import im.boss66.com.util.Utils;

/**
 * Created by admin on 2017/2/20.
 */
public class SelectSingleDialog extends BaseDialog implements View.OnClickListener {
    private static final String TAG = SelectSingleDialog.class.getSimpleName();
    private TextView cancle, ok;
    private TextView tvMsg, tvName;
    private EditText etMsg;
    private ImageView ivAvatar;
    private EaseUser user;
    private MessageItem item;
    private ImageLoader imageLoader;

    private RelativeLayout rl_video;
    private ImageView iv_video_bg;
    private int widthScreen;
    private int widthMin;
    private Handler mHandler = new Handler();
    private ImageView iv_image;
    private ImageView iv_emo;
    private ArrayList<View> views = new ArrayList<>();

    public SelectSingleDialog(Context context) {
        super(context);
        widthScreen = UIUtils.getScreenWidth(context) / 2;
        widthMin = UIUtils.getScreenWidth(context) / 3;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        cancle = (TextView) dialog.findViewById(R.id.tv_cancel);
        ok = (TextView) dialog.findViewById(R.id.tv_send);
        cancle.setOnClickListener(this);
        ok.setOnClickListener(this);
        initViews();
    }

    public SelectSingleDialog(Context context, EaseUser user) {
        super(context);
        this.user = user;
        widthScreen = UIUtils.getScreenWidth(context) / 2;
        widthMin = UIUtils.getScreenWidth(context) / 3;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        cancle = (TextView) dialog.findViewById(R.id.tv_cancel);
        ok = (TextView) dialog.findViewById(R.id.tv_send);
        cancle.setOnClickListener(this);
        ok.setOnClickListener(this);
        initViews();
    }

    private void initViews() {
        tvMsg = (TextView) dialog.findViewById(R.id.tv_txt);
        tvName = (TextView) dialog.findViewById(R.id.tv_name);
        etMsg = (EditText) dialog.findViewById(R.id.et_msg);
        ivAvatar = (ImageView) dialog.findViewById(R.id.iv_avatar);

        rl_video = (RelativeLayout) dialog.findViewById(R.id.rl_bg);
        iv_video_bg = (ImageView) dialog.findViewById(R.id.iv_video_bg);
        iv_image = (ImageView) dialog.findViewById(R.id.iv_image);
        iv_emo = (ImageView) dialog.findViewById(R.id.iv_emo);
        initDatas();
    }

    private void initDatas() {
        views.add(tvMsg);
        views.add(rl_video);
        views.add(iv_image);
        views.add(iv_emo);
    }

    @Override
    protected int getView() {
        return R.layout.dialog_select_single;
    }

    @Override
    protected int getDialogStyleId() {
        return R.style.dialog_ios_style;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_send:
                showLoadingDialog();
                String msg = etMsg.getText().toString().trim();
                Utils.sendMessage(context, item, user, msg);
                cancelLoadingDialog();
                dismiss();
                Intent intent = new Intent(Constants.Action.EXIT_CURRENT_ACTIVITY);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                break;
        }
    }


    public void showDialog(EaseUser user, MessageItem item) {
        bindData(user, item);
        if (dialog != null && !isShowing()) {
            show();
        }
    }

    private void bindData(EaseUser user, MessageItem item) {
        this.user = user;
        this.item = item;
        setUpViews(item);
    }

    private void setUpViews(MessageItem item) {
        etMsg.setText("");
        tvName.setText(user.getNick());
        imageLoader.displayImage(user.getAvatar(), ivAvatar, ImageLoaderUtils.getDisplayImageOptions());
        hideViews();
        switch (item.getMsgType()) {
            case MessageItem.MESSAGE_TYPE_TXT:
                UIUtils.showView(tvMsg);
                tvMsg.setText(item.getMessage());
                break;
            case MessageItem.MESSAGE_TYPE_IMG:
                UIUtils.showView(iv_image);
                String imageUrl = item.getMessage();
                if (imageUrl != null && !imageUrl.equals("")) {
                    scalLoadImage(iv_image, imageUrl);
                }
                break;
            case MessageItem.MESSAGE_TYPE_EMOTION:
                UIUtils.showView(iv_emo);
                showEmo(item, iv_emo);
                break;
            case MessageItem.MESSAGE_TYPE_VIDEO:
                UIUtils.showView(rl_video);
                final String videoPath = item.getMessage();
                Log.i("info", "===================videoPath:" + videoPath);
                String imageurl = "";
                if (videoPath.contains(".mp4")) {
                    imageurl = videoPath.replace(".mp4", ".jpg");
                } else if (videoPath.contains(".mov")) {
                    imageurl = videoPath.replace(".mov", ".jpg");
                }
                if (!imageurl.equals("")) {
                    scalLoadImage(iv_video_bg, imageurl);
                }
                break;
        }
    }

    private void hideViews() {
        if (views != null && views.size() != 0) {
            for (View view : views) {
                UIUtils.hindView(view);
            }
        }
    }

    private void scalLoadImage(ImageView iv, String url) {
        String[] size = getSize(url);
        int width = Integer.parseInt(size[0]);
        int height = Integer.parseInt(size[1]);
        scaleSize(iv, width, height);
        imageLoader.displayImage(url, iv, ImageLoaderUtils.getDisplayImageOptions());
    }

    private String[] getSize(String url) {
        Log.i("liwya", url);
        String str = url.substring(url.indexOf("-"), url.length());
        String size = str.substring(str.indexOf("-") + 1, str.indexOf("."));
        return size.split("x");
    }

    private void scaleSize(ImageView iv, int w, int h) {
        int width = w;
        int height = h;
        if (w > widthScreen) {
            while (width > widthScreen) {
                width = width / 2;
                height = height / 2;
            }
        } else if (w < widthMin) {
            while (width < widthMin) {
                width = width * 2;
                height = height * 2;
            }
        }
        iv.getLayoutParams().width = width;
        iv.getLayoutParams().height = height;
    }


    private void showEmo(MessageItem mItem, ImageView gifView) {
        String emo_code = mItem.getMessage();
        EmoEntity entity = EmoHelper.getInstance().queryByCode(emo_code);
        if (entity != null) {
            int width = Integer.parseInt(entity.getWidth());
            int height = Integer.parseInt(entity.getHeight());
            scaleSize(gifView, width, height);
            String format = entity.getEmo_format();
            if (format.equals("gif")) {
                File file = FileUtil.getFileByPath(getPath(entity));
                if (file != null) {
                    Glide.with(context).load(FileUtil.getBytesFromFile(file)).
                            placeholder(R.drawable.zf_default_message_image)
                            .crossFade().into(gifView);
                }
            } else {
                gifView.setImageBitmap(FileUtils.getBitmapByPath(getPath(entity)));
            }
        } else {
            requestParseEmo(gifView, emo_code);
        }
    }

    private void requestParseEmo(final ImageView gifView, String code) {
        EmoParseRequest request = new EmoParseRequest(TAG, code);
        request.send(new BaseDataRequest.RequestCallback<EmotionEntity>() {
            @Override
            public void onSuccess(EmotionEntity pojo) {
                bindEmo(gifView, pojo);
            }

            @Override
            public void onFailure(String msg) {
                Log.i("info", "============msg:" + msg);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        gifView.setImageResource(R.drawable.emo_default_img);
                    }
                });
            }
        });
    }

    private void bindEmo(final ImageView gifView, EmotionEntity entity) {
        if (entity != null) {
            String emo_url = entity.getEmo_url();
            String name = FileUtils.getFileNameFromPath(emo_url);
            int width = Integer.parseInt(entity.getWidth());
            int height = Integer.parseInt(entity.getHeight());
            scaleSize(gifView, width, height);
            if (name.contains(".gif")) {
                Glide.with(context).load(entity.getEmo_url()).
                        placeholder(R.drawable.zf_default_message_image).
                        crossFade().into(gifView);
            } else {
                imageLoader.displayImage(entity.getEmo_url(), gifView, ImageLoaderUtils.getDisplayImageOptions());
            }
        }
    }

    private String getPath(EmoEntity entity) {
        String path = Constants.EMO_DIR_PATH + File.separator +
                entity.getEmo_cate_id() + File.separator +
                entity.getEmo_group_id() + File.separator +
                entity.getEmo_code() + "." +
                entity.getEmo_format();
        return path;
    }
}
