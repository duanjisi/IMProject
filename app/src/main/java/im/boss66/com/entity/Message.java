package im.boss66.com.entity;


import im.boss66.com.App;
import im.boss66.com.Utils.SharePreferenceUtil;

/**
 * @desc:百度push的消息体
 * @author: pangzf
 * @date: 2015年1月22日 上午11:08:02
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class Message extends BaseData {
    private static final long serialVersionUID = 1L;
    private String user_id;
    private String channel_id;
    private String nick;
    private String avartar;
    private int head_id;
    private String time_samp;
    private String message;
    private int messagetype;
    private int voiceTime;
    private String tag;

    public Message(int msgtype, String time_samp, String message, String tag,
                   int voiceTime) {
        super();
        SharePreferenceUtil util = App.getInstance().getSpUtil();
        this.user_id = util.getUserId();
        this.channel_id = util.getChannelId();
        this.nick = util.getNick();
        this.head_id = util.getHeadIcon();
        this.time_samp = time_samp;
        this.message = message;
        this.tag = tag;
        this.messagetype = msgtype;
        this.voiceTime = voiceTime;
    }

    public Message(int msgtype, String time_samp, String message, String tag,
                   int voiceTime, String user_id, String nickName, String avatar) {
        super();
        SharePreferenceUtil util = App.getInstance().getSpUtil();
        this.user_id = user_id;
        this.channel_id = util.getChannelId();
        this.nick = nickName;
        this.head_id = util.getHeadIcon();
        this.time_samp = time_samp;
        this.message = message;
        this.tag = tag;
        this.messagetype = msgtype;
        this.voiceTime = voiceTime;
        this.avartar = avatar;
    }

    public int getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(int voiceTime) {
        this.voiceTime = voiceTime;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getHead_id() {
        return head_id;
    }

    public void setHead_id(int head_id) {
        this.head_id = head_id;
    }

    public String getTime_samp() {
        return time_samp;
    }

    public void setTime_samp(String time_samp) {
        this.time_samp = time_samp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getMessagetype() {
        return messagetype;
    }

    public void setMessagetype(int messagetype) {
        this.messagetype = messagetype;
    }

    public String getAvartar() {
        return avartar;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

    @Override
    public String toString() {
        return "Message [user_id=" + user_id + ", channel_id=" + channel_id
                + ", nick=" + nick + ", head_id=" + head_id + ", time_samp="
                + time_samp + ", message=" + message + ", tag=" + tag + "]";
    }

}
