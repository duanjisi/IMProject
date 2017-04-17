package im.boss66.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtil;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.Utils.SoundUtil;
import im.boss66.com.Utils.TimeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.discover.ImagePagerActivity;
import im.boss66.com.activity.discover.PersonalNearbyDetailActivity;
import im.boss66.com.activity.player.VideoPlayerNewActivity;
import im.boss66.com.db.dao.EmoHelper;
import im.boss66.com.entity.EmoEntity;
import im.boss66.com.entity.EmotionEntity;
import im.boss66.com.entity.MessageItem;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.EmoParseRequest;
import im.boss66.com.widget.dialog.ChatDialog;
import im.boss66.com.xlistview.GifTextView;

/**
 * @author pangzf
 * @desc发送消息的adapter
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
@SuppressLint("NewApi")
public class MessageAdapter extends BaseAdapter {
    private static final String TAG = MessageAdapter.class.getSimpleName();
    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    public static final int MESSAGE_TYPE_INVALID = -1;

    public static final int MESSAGE_TYPE_MINE_EMOTION = 0x00;
    public static final int MESSAGE_TYPE_MINE_IMAGE = 0x01;
    public static final int MESSAGE_TYPE_MINE_VIDEO = 0x02;
    public static final int MESSAGE_TYPE_MINE_TXT = 0x06;
    public static final int MESSAGE_TYPE_MINE_AUDIO = 0x08;

    public static final int MESSAGE_TYPE_OTHER_EMOTION = 0x03;
    public static final int MESSAGE_TYPE_OTHER_IMAGE = 0x04;
    public static final int MESSAGE_TYPE_OTHER_VIDEO = 0x05;
    public static final int MESSAGE_TYPE_OTHER_TXT = 0x07;
    public static final int MESSAGE_TYPE_OTHER_AUDIO = 0x09;

    public static final int MESSAGE_TYPE_TIME_TITLE = 0x07;
    public static final int MESSAGE_TYPE_HISTORY_DIVIDER = 0x08;
    private static final int VIEW_TYPE_COUNT = 10;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MessageItem> mMsgList;
    private long mPreDate;
    private Resources resources;
    private ImageLoader imageLoader;
    private int widthScreen;
    private int widthMin;
    private SoundUtil mSoundUtil = SoundUtil.getInstance();
    private Handler mHandler = new Handler();
    private int mImageHeight;
    private String toUid;
    private ChatDialog chatDialog;

    public MessageAdapter(Context context, List<MessageItem> msgList) {
        this.mContext = context;
        widthScreen = UIUtils.getScreenWidth(context) / 2;
        widthMin = UIUtils.getScreenWidth(context) / 3;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 3;
        resources = context.getResources();
        mMsgList = msgList;
        mInflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
//        mSpUtil = PushApplication.getInstance().getSpUtil();
//        mSoundUtil = SoundUtil.getInstance();
    }

    public MessageAdapter(Context context, List<MessageItem> msgList, String toUid) {
        this.mContext = context;
        this.toUid = toUid;
        widthScreen = UIUtils.getScreenWidth(context) / 2;
        widthMin = UIUtils.getScreenWidth(context) / 4;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 3;
        resources = context.getResources();
        mMsgList = msgList;
        mInflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
//        mSpUtil = PushApplication.getInstance().getSpUtil();
//        mSoundUtil = SoundUtil.getInstance();
    }

    public void removeHeadMsg() {
        if (mMsgList.size() - 10 > 10) {
            for (int i = 0; i < 10; i++) {
                mMsgList.remove(i);
            }
            notifyDataSetChanged();
        }
    }

    public void removeItem(MessageItem item) {
        Iterator iterator = mMsgList.iterator();
        while (iterator.hasNext()) {
            MessageItem mode = (MessageItem) iterator.next();
            if (mode.getMsgId().equals(item.getMsgId())) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }


    public void setmMsgList(List<MessageItem> msgList) {
        mMsgList = msgList;
        notifyDataSetChanged();
    }

    public void upDateMsg(MessageItem msg) {
        mMsgList.add(msg);
        notifyDataSetChanged();
    }

    public void upDateMsgByList(List<MessageItem> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                mMsgList.add(list.get(i));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMsgList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMsgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        MessageHolderBase holder = null;
        if (null == convertView && null != mInflater) {
            holder = new MessageHolderBase();
            switch (type) {
                case MESSAGE_TYPE_MINE_EMOTION: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_mine_emotion_message_item, parent, false);
                    holder = new EmotionMessageHolder();
                    convertView.setTag(holder);
                    fillEmotionMessageHolder((EmotionMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_MINE_IMAGE: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_mine_image_message_item, parent, false);
                    holder = new ImageMessageHolder();
                    convertView.setTag(holder);
                    // fillTextMessageHolder(holder, convertView);
                    fillImageMessageHolder((ImageMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_MINE_VIDEO: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_mine_video_message_item, parent, false);
                    holder = new VideoMessageHolder();
                    convertView.setTag(holder);
                    fillVideoMessageHolder((VideoMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_MINE_TXT: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_mine_text_message_item, parent, false);
                    holder = new TextMessageHolder();
                    convertView.setTag(holder);
                    fillTextMessageHolder((TextMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_MINE_AUDIO: {//声音
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_mine_audio_message_item, parent, false);
                    holder = new AudioMessageHolder();
                    convertView.setTag(holder);
                    fillAudioMessageHolder((AudioMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_EMOTION: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_other_emotion_message_item, parent, false);
                    holder = new EmotionMessageHolder();
                    convertView.setTag(holder);
                    fillEmotionMessageHolder((EmotionMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_IMAGE: {
                    convertView = mInflater
                            .inflate(R.layout.zf_chat_other_image_message_item,
                                    parent, false);
                    holder = new ImageMessageHolder();
                    convertView.setTag(holder);
                    fillImageMessageHolder((ImageMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_VIDEO: {
                    convertView = mInflater
                            .inflate(R.layout.zf_chat_other_video_message_item,
                                    parent, false);
                    holder = new VideoMessageHolder();
                    convertView.setTag(holder);
                    fillVideoMessageHolder((VideoMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_TXT: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_other_text_message_item, parent, false);
                    holder = new TextMessageHolder();
                    convertView.setTag(holder);
                    fillTextMessageHolder((TextMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_AUDIO: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_other_audio_message_item, parent, false);
                    holder = new AudioMessageHolder();
                    convertView.setTag(holder);
                    fillAudioMessageHolder((AudioMessageHolder) holder,
                            convertView);
                    break;
                }
                default:
                    break;
            }
        } else {
            holder = (MessageHolderBase) convertView.getTag();
        }
        final MessageItem mItem = mMsgList.get(position);
        if (mItem != null) {
            int msgType = mItem.getMsgType();
            if (msgType == MessageItem.MESSAGE_TYPE_EMOTION) {
                handleEmotionMessage((EmotionMessageHolder) holder, mItem, parent);
            } else if (msgType == MessageItem.MESSAGE_TYPE_IMG) {
                handleImageMessage((ImageMessageHolder) holder, mItem, parent);
            } else if (msgType == MessageItem.MESSAGE_TYPE_TXT) {
                handleTextMessage((TextMessageHolder) holder, mItem, parent);
            } else if (msgType == MessageItem.MESSAGE_TYPE_AUDIO) {
                handleAudioMessage((AudioMessageHolder) holder, mItem, parent);
            } else if (msgType == MessageItem.MESSAGE_TYPE_VIDEO) {
                handleVideoMessage((VideoMessageHolder) holder, mItem, parent);
            }
        }
        return convertView;
    }

    private void handleTextMessage(final TextMessageHolder holder,
                                   final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);
        holder.tvMsg.setText(mItem.getMessage());
        holder.tvMsg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence msg = ((TextView) v).getText();
                if (msg != null && !msg.equals("")) {
//                    Intent intent = new Intent(mContext, CopyTextActivity.class);
//                    intent.putExtra("msg", msg);
//                    intent.putExtra("toUid", toUid);
//                    intent.putExtra("item", mItem);
//                    intent.putExtra("is_txt", true);
//                    mContext.startActivity(intent);
                    Intent intent = new Intent();
                    intent.putExtra("msg", msg);
                    intent.putExtra("toUid", toUid);
                    intent.putExtra("item", mItem);
                    intent.putExtra("is_txt", true);
                    chatDialog = new ChatDialog(mContext, intent);
                    chatDialog.showDialog();
                }
                return false;
            }
        });
    }

    private void handleEmotionMessage(final EmotionMessageHolder holder,
                                      final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);
//        Map<String, Integer> map = App.getInstance().getFaceMap();
//        if (map.containsKey(mItem.getMessage())) {
//            int resId = map.get(mItem.getMessage());
//            Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
//            if (bitmap != null) {
//                holder.gifView.getLayoutParams().width = bitmap.getWidth();
//                holder.gifView.getLayoutParams().height = bitmap.getHeight();
//            }
//            holder.gifView.setMovieResource(resId);
//        }
        String emo_code = mItem.getMessage();
        EmoEntity entity = EmoHelper.getInstance().queryByCode(emo_code);
        Log.i("info", "==========emotion:  " + "EmoEntity:" + entity);
        if (entity != null) {
            int width = Integer.parseInt(entity.getWidth());
            int height = Integer.parseInt(entity.getHeight());
            scaleSize(holder.gifView, width, height);
            String format = entity.getEmo_format();
            if (format.equals("gif")) {
//                try {
//                    GifDrawable drawable = new GifDrawable(getPath(entity));
//                    holder.gifView.setImageDrawable(drawable);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.i("info", "============IOException:" + e.getMessage());
//                    holder.gifView.setImageResource(R.drawable.emo_default_img);
//                }
                File file = FileUtil.getFileByPath(getPath(entity));
                if (file != null) {
                    Glide.with(mContext).load(FileUtil.getBytesFromFile(file)).
                            placeholder(R.drawable.zf_default_message_image)
                            .crossFade().into(holder.gifView);
                }
            } else {
                holder.gifView.setImageBitmap(FileUtils.getBitmapByPath(getPath(entity)));
            }
        } else {
            requestParseEmo(holder, emo_code);
        }

        holder.rlMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Intent intent = new Intent(mContext, CopyTextActivity.class);
//                intent.putExtra("toUid", toUid);
//                intent.putExtra("item", mItem);
//                intent.putExtra("is_txt", false);
//                mContext.startActivity(intent);
                Intent intent = new Intent();
                intent.putExtra("toUid", toUid);
                intent.putExtra("item", mItem);
                intent.putExtra("is_txt", false);
                chatDialog = new ChatDialog(mContext, intent);
                chatDialog.showDialog();
                return false;
            }
        });
    }

    private Bitmap getBitmap(EmoEntity entity) {
        String path = Constants.EMO_DIR_PATH + File.separator +
                entity.getEmo_cate_id() + File.separator +
                entity.getEmo_group_id() + File.separator +
                entity.getEmo_code() + "." +
                entity.getEmo_format();
        return FileUtils.getBitmapByPath(path);
    }

    private String getPath(EmoEntity entity) {
        String path = Constants.EMO_DIR_PATH + File.separator +
                entity.getEmo_cate_id() + File.separator +
                entity.getEmo_group_id() + File.separator +
                entity.getEmo_code() + "." +
                entity.getEmo_format();
        return path;
    }


    private void requestParseEmo(final EmotionMessageHolder holder, String code) {
        EmoParseRequest request = new EmoParseRequest(TAG, code);
        request.send(new BaseDataRequest.RequestCallback<EmotionEntity>() {
            @Override
            public void onSuccess(EmotionEntity pojo) {
                bindEmo(holder, pojo);
            }

            @Override
            public void onFailure(String msg) {
                Log.i("info", "============msg:" + msg);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.gifView.setImageResource(R.drawable.emo_default_img);
                    }
                });
//                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindEmo(final EmotionMessageHolder holder, EmotionEntity entity) {
        if (entity != null) {
            String emo_url = entity.getEmo_url();
            String name = FileUtils.getFileNameFromPath(emo_url);
            int width = Integer.parseInt(entity.getWidth());
            int height = Integer.parseInt(entity.getHeight());
            scaleSize(holder.gifView, width, height);
            if (name.contains(".gif")) {
                Glide.with(mContext).load(entity.getEmo_url()).
                        placeholder(R.drawable.zf_default_message_image).
                        crossFade().into(holder.gifView);
            } else {
                imageLoader.displayImage(entity.getEmo_url(), holder.gifView, ImageLoaderUtils.getDisplayImageOptions());
            }
        }
    }

    private void saveEmoEntity(EmotionEntity model) {
        EmoEntity entity = new EmoEntity();
        entity.setEmo_group_id(model.getEmo_group());
        entity.setEmo_id(model.getEmo_id());
        entity.setEmo_code(model.getEmo_code());
        entity.setEmo_cate_id(model.getEmo_cate());
        entity.setEmo_format(model.getEmo_format());
        entity.setEmo_name(model.getEmo_name());
        entity.setEmo_desc(model.getEmo_name());
        EmoHelper.getInstance().save(entity);
    }

    /**
     * @param holder
     * @param parent
     * @Description 处理图片消息
     */
    private void handleImageMessage(final ImageMessageHolder holder,
                                    final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);
        String imageUrl = mItem.getMessage();
        // 图片文件
        if (imageUrl != null && !imageUrl.equals("")) {
            Log.i("info", "=====imageUrl:" + imageUrl);
            scalLoadImage(holder, imageUrl);
            holder.ivphoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                    List<String> photoUrls = new ArrayList<String>();
                    for (MessageItem item : mMsgList) {
                        if (item.getMsgType() == MessageItem.MESSAGE_TYPE_IMG) {
                            photoUrls.add(item.getMessage());
                        }
                    }
                    int position = 0;
                    for (int i = 0; i < photoUrls.size(); i++) {
                        if (photoUrls.get(i).equals(mItem.getMessage())) {
                            position = i;
                        }
                    }
                    ImagePagerActivity.startImagePagerActivity(mContext, photoUrls, position, imageSize, false);
                }
            });
            holder.flPickLayout.setVisibility(View.VISIBLE);

            holder.ivphoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    Intent intent = new Intent(mContext, CopyTextActivity.class);
//                    intent.putExtra("toUid", toUid);
//                    intent.putExtra("item", mItem);
//                    intent.putExtra("is_txt", false);
//                    mContext.startActivity(intent);
                    Intent intent = new Intent();
                    intent.putExtra("toUid", toUid);
                    intent.putExtra("item", mItem);
                    intent.putExtra("is_txt", false);
                    chatDialog = new ChatDialog(mContext, intent);
                    chatDialog.showDialog();
                    return false;
                }
            });
        } else {
            holder.flPickLayout.setVisibility(View.GONE);
        }
        holder.rlMessage.setVisibility(View.GONE);
    }

//    private void scalImage(ImageMessageHolder holder, Bitmap bitmap) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        if (width > widthScreen / 2) {
//            Log.i("info", "=====width:" + width + "\n" + "height:" + height);
//            holder.ivphoto.getLayoutParams().width = width / 2;
//            holder.ivphoto.getLayoutParams().height = height / 2;
//        }
//        holder.ivphoto.setImageBitmap(bitmap);
//    }

    private void scalLoadImage(ImageMessageHolder holder, String url) {
        String[] size = getSize(url);
        int width = Integer.parseInt(size[0]);
        int height = Integer.parseInt(size[1]);
//        if (width > widthScreen / 2) {
//            Log.i("info", "=====width:" + width + "\n" + "height:" + height);
//            holder.ivphoto.getLayoutParams().width = width / 2;
//            holder.ivphoto.getLayoutParams().height = height / 2;
//        }
        scaleSize(holder.ivphoto, width, height);
        imageLoader.displayImage(url, holder.ivphoto, ImageLoaderUtils.getDisplayImageOptions());
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
//    private void scaleSize(ImageView iv, int w, int h) {
//        int width = w;
//        int height = h;
//        while (width > widthScreen) {
//            width = width / 2;
//            height = height / 2;
//        }
//        iv.getLayoutParams().width = width;
//        iv.getLayoutParams().height = height;
//    }

    private String[] getSize(String url) {
        Log.i("liwya", url);
        String str = url.substring(url.indexOf("-"), url.length());
        String size = str.substring(str.indexOf("-") + 1, str.indexOf("."));
        return size.split("x");
    }

    private void scalLoadImageVideo(VideoMessageHolder holder, String url) {
        String[] size = getSize(url);
        int width = Integer.parseInt(size[0]);
        int height = Integer.parseInt(size[1]);
//        if (width > widthScreen / 2) {
//            Log.i("info", "=====width:" + width + "\n" + "height:" + height);
//            holder.iv_cover.getLayoutParams().width = width / 2;
//            holder.iv_cover.getLayoutParams().height = height / 2;
//        }
        scaleSize(holder.iv_cover, width, height);
        imageLoader.displayImage(url, holder.iv_cover, ImageLoaderUtils.getDisplayImageOptions());
    }

//    private void scalImageVideo(VideoMessageHolder holder, Bitmap bitmap) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        if (width > widthScreen / 2) {
//            Log.i("info", "=====width:" + width + "\n" + "height:" + height);
//            holder.iv_cover.getLayoutParams().width = width / 2;
//            holder.iv_cover.getLayoutParams().height = height / 2;
//        }
//        holder.iv_cover.setImageBitmap(bitmap);
//    }

    //    /**
//     * @param holder
//     * @param info
//     * @param isMine
//     * @param parent
//     * @param position
//     * @Description 处理语音消息
//     */
    private void handleAudioMessage(final AudioMessageHolder holder,
                                    final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);
        // 语音
//        holder.msg.setCompoundDrawablesWithIntrinsicBounds(0, 0,
//                R.drawable.chatto_voice_playing, 0);
        holder.voiceTime.setText(TimeUtil.getVoiceRecorderTime(mItem
                .getVoiceTime()));
        holder.msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItem.getMsgType() == MessageItem.MESSAGE_TYPE_AUDIO) {
                    if (mItem.isComMeg()) {
                        startAnimation(holder.msg, R.drawable.voice_other_animlist);
                    } else {
                        startAnimation(holder.msg, R.drawable.voice_my_animlist);
                    }
                    // 播放语音
                    mSoundUtil.playRecorder(mContext, mItem.getMessage(), new SoundUtil.CompletionListener() {
                        @Override
                        public void onCompletion() {
                            if (mItem.isComMeg()) {
                                stopAnimation(holder.msg, R.drawable.voice_other_animlist);
                                holder.msg.setImageResource(R.drawable.chatto_se_playing);
                            } else {
                                stopAnimation(holder.msg, R.drawable.voice_my_animlist);
                                holder.msg.setImageResource(R.drawable.chatto_voice_playing);
                            }
                        }
                    });
                }
            }
        });
        holder.msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("toUid", toUid);
                intent.putExtra("item", mItem);
                intent.putExtra("isVoice", true);
                chatDialog = new ChatDialog(mContext, intent);
                chatDialog.showDialog();
                return false;
            }
        });
    }

    private void handleVideoMessage(final VideoMessageHolder holder,
                                    final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);
//        holder.duration.setText("");
        final String videoPath = mItem.getMessage();
        Log.i("info", "===================videoPath:" + videoPath);
        String imageUrl = "";
        if (videoPath.contains(".mp4")) {
            imageUrl = videoPath.replace(".mp4", ".jpg");
        } else if (videoPath.contains(".mov")) {
            imageUrl = videoPath.replace(".mov", ".jpg");
        }
        if (!imageUrl.equals("")) {
            scalLoadImageVideo(holder, imageUrl);
            final String cover = imageUrl;
            holder.iv_player.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoPlayerNewActivity.class);
                    intent.putExtra("videoPath", videoPath);
                    intent.putExtra("imgurl", cover);
                    mContext.startActivity(intent);
                }
            });

            holder.rlMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    Intent intent = new Intent(mContext, CopyTextActivity.class);
//                    intent.putExtra("toUid", toUid);
//                    intent.putExtra("item", mItem);
//                    intent.putExtra("is_txt", false);
//                    mContext.startActivity(intent);
                    Intent intent = new Intent();
                    intent.putExtra("toUid", toUid);
                    intent.putExtra("item", mItem);
                    intent.putExtra("is_txt", false);
                    chatDialog = new ChatDialog(mContext, intent);
                    chatDialog.showDialog();
                    return false;
                }
            });
        }
    }

    private void startAnimation(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
    }

    private void stopAnimation(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.stop();
    }

    private void handleBaseMessage(MessageHolderBase holder,
                                   final MessageItem mItem) {
//        holder.time.setText(TimeUtil.getChatTime(mItem.getDate()));
        holder.time.setText(TimeUtil.getChatTime(Long.parseLong(mItem.getTemp())));
        holder.time.setVisibility(View.VISIBLE);

        if (mItem.isComMeg() && !toUid.equals("")) {
            boolean showNick = PreferenceUtils.getBoolean(mContext, PrefKey.SHOW_NICK_NAME + toUid, true);
            if (showNick) {
                UIUtils.showView(holder.nick);
            } else {
                UIUtils.hindView(holder.nick);
            }
        }
        holder.nick.setText(mItem.getNick());
//        holder.head.setBackgroundResource(R.drawable.ic_launcher);
        holder.progressBar.setVisibility(View.GONE);
        holder.progressBar.setProgress(50);
//        holder.time.setVisibility(View.VISIBLE);
        imageLoader.displayImage(mItem.getAvatar(), holder.head, ImageLoaderUtils.getDisplayImageOptions());
        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PersonalNearbyDetailActivity.class);
                intent.putExtra("classType", "ChatPanelPager");
                intent.putExtra("userid", mItem.getUserid());
                mContext.startActivity(intent);
            }
        });
        // holder.head.setBackgroundResource(PushApplication.heads[mItem
        // .getHeadImg()]);
    }

    private void fillBaseMessageholder(MessageHolderBase holder,
                                       View convertView) {
        holder.head = (ImageView) convertView.findViewById(R.id.icon);
        holder.nick = (TextView) convertView.findViewById(R.id.tv_nick_name);
        holder.time = (TextView) convertView.findViewById(R.id.datetime);
        // holder.msg = (GifTextView) convertView.findViewById(R.id.textView2);
        holder.rlMessage = (RelativeLayout) convertView
                .findViewById(R.id.relativeLayout1);
//         holder.ivphoto = (ImageView) convertView
//         .findViewById(R.id.iv_chart_item_photo);
        holder.progressBar = (ProgressBar) convertView
                .findViewById(R.id.progressBar1);
        // holder.voiceTime = (TextView) convertView
        // .findViewById(R.id.tv_voice_time);
        holder.flPickLayout = (FrameLayout) convertView
                .findViewById(R.id.message_layout);
    }

    private void fillTextMessageHolder(TextMessageHolder holder,
                                       View convertView) {
        fillBaseMessageholder(holder, convertView);
        holder.tvMsg = (TextView) convertView.findViewById(R.id.textView2);
    }

    private void fillEmotionMessageHolder(EmotionMessageHolder holder,
                                          View convertView) {
        fillBaseMessageholder(holder, convertView);
//        holder.gifView = (GifView) convertView.findViewById(R.id.textView2);
//        holder.gifView = (GifImageView) convertView.findViewById(R.id.textView2);
        holder.gifView = (ImageView) convertView.findViewById(R.id.textView2);
        holder.ivEmo = (ImageView) convertView.findViewById(R.id.iv_emo);
    }

    private void fillImageMessageHolder(ImageMessageHolder holder,
                                        View convertView) {
        fillBaseMessageholder(holder, convertView);
        holder.ivphoto = (ImageView) convertView
                .findViewById(R.id.iv_chart_item_photo);
    }

    private void fillAudioMessageHolder(AudioMessageHolder holder,
                                        View convertView) {
        fillBaseMessageholder(holder, convertView);
        holder.voiceTime = (TextView) convertView
                .findViewById(R.id.tv_voice_time);
        holder.ivphoto = (ImageView) convertView
                .findViewById(R.id.iv_chart_item_photo);
        holder.msg = (ImageView) convertView.findViewById(R.id.textView2);
//        holder.msg = (GifView) convertView.findViewById(R.id.textView2);
    }

    private void fillVideoMessageHolder(VideoMessageHolder holder,
                                        View convertView) {
        fillBaseMessageholder(holder, convertView);
        holder.iv_cover = (ImageView) convertView
                .findViewById(R.id.textView2);
        holder.iv_player = (ImageView) convertView
                .findViewById(R.id.iv_video_play);
        holder.duration = (TextView) convertView.findViewById(R.id.tv_duration);
    }

    private static class MessageHolderBase {
        ImageView head;
        TextView time;
        TextView nick;
        ImageView imageView;
        ProgressBar progressBar;
        RelativeLayout rlMessage;
        FrameLayout flPickLayout;
    }

    private static class TextMessageHolder extends MessageHolderBase {
        /**
         * 文字消息体
         */
        TextView tvMsg;
    }

    private static class EmotionMessageHolder extends MessageHolderBase {
        /**
         * 表情消息体
         */
//        GifView gifView;
//        GifImageView gifView;
        ImageView gifView;
        ImageView ivEmo;
    }

    private static class ImageMessageHolder extends MessageHolderBase {
        /**
         * 图片消息体
         */
        ImageView ivphoto;

    }

    private static class AudioMessageHolder extends MessageHolderBase {
        ImageView ivphoto;
        /**
         * 语音秒数
         */
        TextView voiceTime;
        //        GifView msg;
        ImageView msg;
    }

    private static class VideoMessageHolder extends MessageHolderBase {
        ImageView iv_cover;
        TextView duration;
        ImageView iv_player;
    }

    /**
     * 另外一种方法解析表情将[表情]换成fxxx
     *
     * @param message 传入的需要处理的String
     * @return
     */
//    private String convertNormalStringToSpannableString(String message) {
//        String hackTxt;
//        if (message.startsWith("[") && message.endsWith("]")) {
//            hackTxt = message + " ";
//        } else {
//            hackTxt = message;
//        }
//
//        Matcher localMatcher = EMOTION_URL.matcher(hackTxt);
//        while (localMatcher.find()) {
//            String str2 = localMatcher.group(0);
//            if (PushApplication.getInstance().getFaceMap().containsKey(str2)) {
//                String faceName = mContext.getResources().getString(
//                        PushApplication.getInstance().getFaceMap().get(str2));
//                CharSequence name = options(faceName);
//                message = message.replace(str2, name);
//            }
//        }
//        return message;
//    }
    private String convertNormalStringToSpannableString(String message) {
        String hackTxt;
        if (message.startsWith("[") && message.endsWith("]")) {
            hackTxt = message + " ";
        } else {
            hackTxt = message;
        }

        Matcher localMatcher = EMOTION_URL.matcher(hackTxt);
        while (localMatcher.find()) {
            String str2 = localMatcher.group(0);
            if (App.getInstance().getFaceMap().containsKey(str2)) {
                String faceName = mContext.getResources().getString(
                        App.getInstance().getFaceMap().get(str2));
                CharSequence name = options(faceName);
                message = message.replace(str2, name);
            }
        }
        return message;
    }

    /**
     * 取名字f010
     *
     * @param faceName
     */
    private CharSequence options(String faceName) {
        int start = faceName.lastIndexOf("/");
        CharSequence c = faceName.subSequence(start + 1, faceName.length() - 4);
        return c;
    }

    static class ViewHolder {

        ImageView head;
        TextView time;
        GifTextView msg;
        ImageView imageView;
        ProgressBar progressBar;
        TextView voiceTime;
        ImageView ivphoto;
        RelativeLayout rlMessage;
        FrameLayout flPickLayout;
    }

    @Override
    public int getItemViewType(int position) {
        // logger.d("chat#getItemViewType -> position:%d", position);
        try {
            if (position >= mMsgList.size()) {
                return MESSAGE_TYPE_INVALID;
            }
            MessageItem item = mMsgList.get(position);
            if (item != null) {
                boolean comMeg = item.isComMeg();
                int type = item.getMsgType();
                if (comMeg) {
                    // 接受的消息
                    switch (type) {
                        case MessageItem.MESSAGE_TYPE_EMOTION: {
                            return MESSAGE_TYPE_OTHER_EMOTION;
                        }

                        case MessageItem.MESSAGE_TYPE_IMG: {
                            return MESSAGE_TYPE_OTHER_IMAGE;
                        }

                        case MessageItem.MESSAGE_TYPE_VIDEO: {
                            return MESSAGE_TYPE_OTHER_VIDEO;
                        }
                        case MessageItem.MESSAGE_TYPE_TXT: {
                            return MESSAGE_TYPE_OTHER_TXT;
                        }
                        case MessageItem.MESSAGE_TYPE_AUDIO: {
                            return MESSAGE_TYPE_OTHER_AUDIO;
                        }
                        default:
                            break;
                    }
                } else {
                    // 发送的消息
                    switch (type) {
                        case MessageItem.MESSAGE_TYPE_EMOTION: {
                            return MESSAGE_TYPE_MINE_EMOTION;

                        }
                        case MessageItem.MESSAGE_TYPE_IMG: {
                            return MESSAGE_TYPE_MINE_IMAGE;

                        }
                        case MessageItem.MESSAGE_TYPE_VIDEO: {
                            return MESSAGE_TYPE_MINE_VIDEO;
                        }
                        case MessageItem.MESSAGE_TYPE_TXT: {
                            return MESSAGE_TYPE_MINE_TXT;
                        }
                        case MessageItem.MESSAGE_TYPE_AUDIO: {
                            return MESSAGE_TYPE_MINE_AUDIO;
                        }
                        default:
                            break;
                    }
                }
            }
            return MESSAGE_TYPE_INVALID;
        } catch (Exception e) {
            Log.e("fff", e.getMessage());
            return MESSAGE_TYPE_INVALID;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

}