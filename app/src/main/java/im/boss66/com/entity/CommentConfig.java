package im.boss66.com.entity;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public class CommentConfig {
    public static enum Type{
        PUBLIC("public"), REPLY("reply");

        private String value;
        private Type(String value){
            this.value = value;
        }

    }
    public int feedid;
    public int circlePosition;
    public int commentPosition;
    public Type commentType;
    public String uid_to_name;
    public String commentId;
    public boolean isReply;
    public String commentFromId;
    public String pid;
    //public User replyUser;

    @Override
    public String toString() {
        String replyUserStr = "";
//        if(replyUser != null){
//            replyUserStr = replyUser.toString();
//        }
        return "circlePosition = " + circlePosition
                + "; commentPosition = " + commentPosition
                + "; commentType ＝ " + commentType
                + "; replyUser = " + uid_to_name;
    }
}
