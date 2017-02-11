package im.boss66.com.entity;

import java.util.List;

/**
 * 个人相册实体类
 */
public class PersonalPhotoAlbumItem {
    private String time;
    private int type;//1:parent  2:child
    private List<PhotoAlbumChildItem> photoAlbumChildList;
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public List<PhotoAlbumChildItem> getList() {
        return photoAlbumChildList;
    }

    public void setList(List<PhotoAlbumChildItem> list) {
        this.photoAlbumChildList = list;
    }
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return type;
    }

//    public class PhotoAlbumChild{
//        private String content;
//        private String createTime;
//        private String linkImg;
//        private String linkTitle;
//        private List<PhotoInfo> photos;
//        private List<FavortItem> favorters;
//        private List<FriendCircleItem> comments;
//        private String contenType;//1:链接  2:图片 3:视频
//        private String videoUrl;
//        private String videoImgUrl;
//        public String getContent() {
//            return content;
//        }
//
//        public void setContent(String content) {
//            this.content = content;
//        }
//
//        public String getCreateTime() {
//            return createTime;
//        }
//
//        public void setCreateTime(String createTime) {
//            this.createTime = createTime;
//        }
//        public String getContenType() {
//            return contenType;
//        }
//
//        public void setContenType(String contenType) {
//            this.contenType = contenType;
//        }
//
//        public String getLinkImg() {
//            return linkImg;
//        }
//
//        public void setLinkImg(String linkImg) {
//            this.linkImg = linkImg;
//        }
//
//        public String getLinkTitle() {
//            return linkTitle;
//        }
//
//        public void setLinkTitle(String linkTitle) {
//            this.linkTitle = linkTitle;
//        }
//
//        public List<PhotoInfo> getPhotos() {
//            return photos;
//        }
//
//        public void setPhotos(List<PhotoInfo> photos) {
//            this.photos = photos;
//        }
//
//        public List<FavortItem> getFavorters() {
//            return favorters;
//        }
//
//        public void setFavorters(List<FavortItem> favorters) {
//            this.favorters = favorters;
//        }
//
//        public List<FriendCircleItem> getComments() {
//            return comments;
//        }
//
//        public void setComments(List<FriendCircleItem> comments) {
//            this.comments = comments;
//        }
//
//        public String getVideoUrl() {
//            return videoUrl;
//        }
//
//        public void setVideoUrl(String videoUrl) {
//            this.videoUrl = videoUrl;
//        }
//
//        public String getVideoImgUrl() {
//            return videoImgUrl;
//        }
//
//        public void setVideoImgUrl(String videoImgUrl) {
//            this.videoImgUrl = videoImgUrl;
//        }
//    }

}
