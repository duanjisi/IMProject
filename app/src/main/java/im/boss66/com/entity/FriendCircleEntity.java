package im.boss66.com.entity;

import java.util.List;

/**
 * 朋友圈实体类
 */
public class FriendCircleEntity {

    private String name;
    private String version;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;
    private int status;
    private String type;

    public List<FriendCircle> getResult() {
        return result;
    }

    public void setResult(List<FriendCircle> result) {
        this.result = result;
    }

    private List<FriendCircle> result;

    public class FriendCircle {

        private int did;
        private String user_id; //用户ID(当前用户ID)

        private int feed_id;//朋友圈ID
        private String feed_uid; //朋友圈发布者ID

        private String content; //文字内容
        private int feed_type; //朋友圈类型  1:图+文 2:视频+文字 3:从别处分享的
        private String share_url; //分享的URL
        private String position; //定位信息
        private String remind_user; //提醒人看的user_id,逗号隔开
        private String feed_avatar;//朋友圈发布者头像
        private String feed_username;//朋友圈发布者用户名称

        private List<PhotoInfo> files;//图片或视频文件
        private String add_time;//发布时间
        private List<FriendCircleCommentEntity> comment_list;//评论列表
        private List<FriendCirclePraiseEntity> praise_list;//点赞列表
        private int is_praise;//0:是否赞过，为1表示赞过

        public int getDid() {
            return did;
        }

        public void setDid(int did) {
            this.did = did;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getFeed_id() {
            return feed_id;
        }

        public void setFeed_id(int feed_id) {
            this.feed_id = feed_id;
        }

        public String getFeed_uid() {
            return feed_uid;
        }

        public void setFeed_uid(String feed_uid) {
            this.feed_uid = feed_uid;
        }

        public int getFeed_type() {
            return feed_type;
        }

        public void setFeed_type(int feed_type) {
            this.feed_type = feed_type;
        }

        public String getShare_url() {
            return share_url;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getRemind_user() {
            return remind_user;
        }

        public void setRemind_user(String remind_user) {
            this.remind_user = remind_user;
        }

        public String getFeed_avatar() {
            return feed_avatar;
        }

        public void setFeed_avatar(String feed_avatar) {
            this.feed_avatar = feed_avatar;
        }

        public String getFeed_username() {
            return feed_username;
        }

        public void setFeed_username(String feed_username) {
            this.feed_username = feed_username;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public List<PhotoInfo> getFiles() {
            return files;
        }

        public void setFiles(List<PhotoInfo> files) {
            this.files = files;
        }

        public List<FriendCircleCommentEntity> getComment_list() {
            return comment_list;
        }

        public void setComment_list(List<FriendCircleCommentEntity> comment_list) {
            this.comment_list = comment_list;
        }

        public List<FriendCirclePraiseEntity> getPraise_list() {
            return praise_list;
        }

        public void setPraise_list(List<FriendCirclePraiseEntity> praise_list) {
            this.praise_list = praise_list;
        }

        public int getIs_praise() {
            return is_praise;
        }

        public void setIs_praise(int is_praise) {
            this.is_praise = is_praise;
        }

        public boolean hasFavort(){
            if(praise_list!=null && praise_list.size()>0){
                return true;
            }
            return false;
        }

        public boolean hasComment(){
            if(comment_list!=null && comment_list.size()>0){
                return true;
            }
            return false;
        }

        private boolean isExpand;
        public void setExpand(boolean isExpand){
            this.isExpand = isExpand;
        }

        public boolean isExpand(){
            return this.isExpand;
        }

    }

}
