package im.boss66.com.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import im.boss66.com.widget.SlideView;

/**
 * Created by GMARUnity on 2017/3/20.
 */
@Table(name = "FuwaMsgItem")
public class FuwaMsgItem {
    @Column(column = "_id")
    @Id(column = "_id")
    private int _id;
    @Column(column = "id")
    public String id;
    @Column(column = "type")
    public int type;
    @Column(column = "nick")
    public String nick;
    @Column(column = "snap")
    public String snap;
    @Column(column = "title")
    public String title;
    @Column(column = "url")
    public String url;
    @Column(column = "content")
    public String content;
    @Column(column = "isee")
    public boolean isee = false;
}
