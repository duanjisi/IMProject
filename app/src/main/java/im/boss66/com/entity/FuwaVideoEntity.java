package im.boss66.com.entity;

import java.util.List;

/**
 * Created by liw on 2017/6/1.
 */

public class FuwaVideoEntity {

    /**
     * message : OK
     * code : 0
     * data : [{"distance":12926.701358262384,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"ee939bb5a18cffe8707907fb2c638cde","height":"1280","width":"720","video":"http://wsim.66boss.com/fuwa/ee939bb5a18cffe8707907fb2c638cde.mp4","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":362.6843658606203,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"cce4d0382a1715f79e11846ef5229f8d","height":"1280","width":"720","video":"http://wsim.66boss.com/fuwa/cce4d0382a1715f79e11846ef5229f8d.mp4","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":364.26,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"cce4d0382a1715f79e11846ef5229f8d","height":"1280","width":"720","video":"http://wsim.66boss.com/fuwa/cce4d0382a1715f79e11846ef5229f8d.mp4","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":2972.3598,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"7a4ab326ecec6e649ee135beac3ad9d4","height":"284","width":"160","video":"http://wsim.66boss.com/fuwa/7a4ab326ecec6e649ee135beac3ad9d4.mov","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":5197.0507,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"e458a19ba78bba894d945b1a0063c87c","height":"1280","width":"720","video":"http://wsim.66boss.com/fuwa/e458a19ba78bba894d945b1a0063c87c.mp4","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":6309.2552,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"ea803075e3aa5b2709dce067d9c58898","height":"960","width":"540","video":"http://wsim.66boss.com/fuwa/ea803075e3aa5b2709dce067d9c58898.mov","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":8533.6643,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"36dba4bca11fc7070e9f146c7ff67e0c","height":"960","width":"540","video":"http://wsim.66boss.com/fuwa/36dba4bca11fc7070e9f146c7ff67e0c.mov","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":9645.8689,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"e67347662679810d1cb32b904ba85c76","height":"1280","width":"720","video":"http://wsim.66boss.com/fuwa/e67347662679810d1cb32b904ba85c76.mp4","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":10758.3554,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"7bad429e22be82826ce6ba4d9ea54884","height":"640","width":"640","video":"http://wsim.66boss.com/fuwa/7bad429e22be82826ce6ba4d9ea54884.mp4","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":11870.56,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"8d1f6e9c975314428dac008cf0374c11","height":"1280","width":"720","video":"http://wsim.66boss.com/fuwa/8d1f6e9c975314428dac008cf0374c11.mp4","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"},{"distance":12982.7646,"name":"谢长才","gender":"男","userid":"100000320","filemd5":"ee939bb5a18cffe8707907fb2c638cde","height":"1280","width":"720","video":"http://wsim.66boss.com/fuwa/ee939bb5a18cffe8707907fb2c638cde.mp4","avatar":"https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg"}]
     */

    private String message;
    private int code;
    private List<DataBean> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * distance : 12926.701358262384
         * name : 谢长才
         * gender : 男
         * userid : 100000320
         * filemd5 : ee939bb5a18cffe8707907fb2c638cde
         * height : 1280
         * width : 720
         * video : http://wsim.66boss.com/fuwa/ee939bb5a18cffe8707907fb2c638cde.mp4
         * avatar : https://imgcdn.66boss.com/imagesu/avatar/20170510072023129515.jpg
         */

        private double distance;
        private String name;
        private String gender;
        private String userid;
        private String filemd5;
        private String height;
        private String width;
        private String video;
        private String avatar;

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getFilemd5() {
            return filemd5;
        }

        public void setFilemd5(String filemd5) {
            this.filemd5 = filemd5;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
