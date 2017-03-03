package im.boss66.com.entity;

import java.io.Serializable;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public class PhotoInfo implements Serializable {

    public String file_url;//原图
    public String file_thumb;//小图
    public int resourceid;
    public int w;
    public int h;
    public int type;//0:加载string file_url 1:加载resource id
}
