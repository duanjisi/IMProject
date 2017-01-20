package im.boss66.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.TimeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.MessageItem;
import im.boss66.com.widget.GifView;
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
    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    public static final int MESSAGE_TYPE_INVALID = -1;
    public static final int MESSAGE_TYPE_MINE_EMOTION = 0x00;
    public static final int MESSAGE_TYPE_MINE_IMAGE = 0x01;
    public static final int MESSAGE_TYPE_MINE_VIDEO = 0x02;

    public static final int MESSAGE_TYPE_OTHER_EMOTION = 0x03;
    public static final int MESSAGE_TYPE_OTHER_IMAGE = 0x04;
    public static final int MESSAGE_TYPE_OTHER_VIDEO = 0x05;
    public static final int MESSAGE_TYPE_TIME_TITLE = 0x07;
    public static final int MESSAGE_TYPE_HISTORY_DIVIDER = 0x08;
    private static final int VIEW_TYPE_COUNT = 9;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MessageItem> mMsgList;
    private long mPreDate;
    private Resources resources;
    private ImageLoader imageLoader;
    private int widthScreen;

    public MessageAdapter(Context context, List<MessageItem> msgList) {
        this.mContext = context;
        widthScreen = UIUtils.getScreenWidth(context);
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
                            R.layout.zf_chat_mine_text_message_item, parent, false);
                    holder = new TextMessageHolder();
                    convertView.setTag(holder);
                    fillTextMessageHolder((TextMessageHolder) holder,
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
                            R.layout.zf_chat_mine_audio_message_item, parent, false);
                    holder = new AudioMessageHolder();
                    convertView.setTag(holder);
                    fillAudioMessageHolder((AudioMessageHolder) holder,
                            convertView);
                    break;
                }
                case MESSAGE_TYPE_OTHER_EMOTION: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_other_text_message_item, parent, false);
                    holder = new TextMessageHolder();
                    convertView.setTag(holder);
                    fillTextMessageHolder((TextMessageHolder) holder,
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
                            .inflate(R.layout.zf_chat_other_audio_message_item,
                                    parent, false);
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
                handleTextMessage((TextMessageHolder) holder, mItem, parent);
            } else if (msgType == MessageItem.MESSAGE_TYPE_IMG) {
                handleImageMessage((ImageMessageHolder) holder, mItem, parent);
            } else if (msgType == MessageItem.MESSAGE_TYPE_VIDEO) {
                handleAudioMessage((AudioMessageHolder) holder, mItem, parent);
            }
        }
        return convertView;
    }

    private void handleTextMessage(final TextMessageHolder holder,
                                   final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);
        Map<String, Integer> map = App.getInstance().getFaceMap();
        if (map.containsKey(mItem.getMessage())) {
            int resId = map.get(mItem.getMessage());
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
            if (bitmap != null) {
                holder.gifView.getLayoutParams().width = bitmap.getWidth();
                holder.gifView.getLayoutParams().height = bitmap.getHeight();
            }
            holder.gifView.setMovieResource(resId);
        }
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
//            Bitmap bitmap = imageLoader.loadImageSync(imageUrl);
//            Log.i("info", "=====bitmap:" + bitmap);
            imageLoader.displayImage(imageUrl, holder.ivphoto, ImageLoaderUtils.getDisplayImageOptions(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Log.i("info", "=====bitmap:" + bitmap);
                    if (bitmap != null) {
                        scalImage(holder, bitmap);
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
//            imageLoader.displayImage(imageUrl, holder.ivphoto, ImageLoaderUtils.getDisplayScaleImageOptions());
//            Bitmap bitmap = MessageBitmapCache.getInstance().get(
//                    mItem.getMessage());
//            if (!mItem.isComMeg()) {
//                bitmap = BubbleImageHelper.getInstance(mContext)
//                        .getBubbleImageBitmap(bitmap,
//                                R.drawable.zf_mine_image_default_bk);
//            } else {
//                bitmap = BubbleImageHelper.getInstance(mContext)
//                        .getBubbleImageBitmap(bitmap,
//                                R.drawable.zf_other_image_default_bk);
//            }
//
//            if (bitmap != null) {
////                holder.ivphoto.setLayoutParams(new FrameLayout.LayoutParams(
////                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//                holder.ivphoto.setImageBitmap(bitmap);
//            }
            holder.flPickLayout.setVisibility(View.VISIBLE);
        } else {
            holder.flPickLayout.setVisibility(View.GONE);
        }
        holder.rlMessage.setVisibility(View.GONE);
    }

    private void scalImage(ImageMessageHolder holder, Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > widthScreen / 2) {
            Log.i("info", "=====width:" + width + "\n" + "height:" + height);
            holder.ivphoto.getLayoutParams().width = width / 2;
            holder.ivphoto.getLayoutParams().height = height / 2;
        }
        holder.ivphoto.setImageBitmap(bitmap);
    }

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
//        holder.voiceTime.setText(TimeUtil.getVoiceRecorderTime(mItem
//                .getVoiceTime()));
//        holder.msg.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mItem.getMsgType() == MessageItem.MESSAGE_TYPE_RECORD) {
//                    // 播放语音
//                    mSoundUtil.playRecorder(mContext, mItem.getMessage());
//                }
//            }
//        });
    }

    private void handleBaseMessage(MessageHolderBase holder,
                                   final MessageItem mItem) {
//        holder.time.setText(TimeUtil.getChatTime(mItem.getDate()));
        holder.time.setText(TimeUtil.getChatTime(Long.parseLong(mItem.getTemp())));
        holder.time.setVisibility(View.VISIBLE);
        holder.head.setBackgroundResource(R.drawable.ic_launcher);
        holder.progressBar.setVisibility(View.GONE);
        holder.progressBar.setProgress(50);
        holder.time.setVisibility(View.VISIBLE);
        imageLoader.displayImage(mItem.getAvatar(), holder.head, ImageLoaderUtils.getDisplayImageOptions());
        // holder.head.setBackgroundResource(PushApplication.heads[mItem
        // .getHeadImg()]);
    }

    private void fillBaseMessageholder(MessageHolderBase holder,
                                       View convertView) {
        holder.head = (ImageView) convertView.findViewById(R.id.icon);
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
        holder.gifView = (GifView) convertView.findViewById(R.id.textView2);
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
        holder.msg = (GifView) convertView.findViewById(R.id.textView2);
    }

    private static class MessageHolderBase {
        ImageView head;
        TextView time;
        ImageView imageView;
        ProgressBar progressBar;
        RelativeLayout rlMessage;
        FrameLayout flPickLayout;
    }

    private static class TextMessageHolder extends MessageHolderBase {
        /**
         * 文字消息体
         */
        GifView gifView;
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
        GifView msg;
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