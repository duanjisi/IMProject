package im.boss66.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.discover.WebViewActivity;
import im.boss66.com.entity.FuwaMsgItem;
import im.boss66.com.widget.RoundImageView;
import im.boss66.com.widget.slideListView.SlideBaseAdapter;
import im.boss66.com.widget.slideListView.SlideListView;

/**
 * Created by GMARUnity on 2017/3/20.
 */
public class FuwaMessageAdapter extends SlideBaseAdapter {

    private ImageLoader imageLoader;
    private Context context;
    private List<FuwaMsgItem> list;
    private int sceenW;
    private DbUtils mDbUtils;

    public FuwaMessageAdapter(Context context, List<FuwaMsgItem> list) {
        super(context);
        this.list = list;
        this.context = context;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        sceenW = UIUtils.getScreenWidth(context);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FuwaMessageView holder;
        if (convertView == null) {
            convertView = createConvertView(position);
            holder = new FuwaMessageView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (FuwaMessageView) convertView.getTag();
        }

        FuwaMsgItem item = list.get(position);
        if (item != null) {
            String title = item.nick;
            holder.tv_title.setText("" + title);
            int type = item.type;
            if (type == 0) {
                holder.v_point.setVisibility(View.GONE);
                holder.tv_content.setText("" + item.content);
            } else {
                if (!item.isee) {
                    holder.v_point.setVisibility(View.VISIBLE);
                } else {
                    holder.v_point.setVisibility(View.GONE);
                }
                holder.tv_content.setText("" + item.title);
            }
            imageLoader.displayImage(item.snap, holder.iv_head,
                    ImageLoaderUtils.getDisplayImageOptions());
        }

        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list != null && list.size() > position) {
                    FuwaMsgItem item = list.get(position);
                    if (item != null && item.type == 1) {
                        String url = item.url;
                        item.isee = true;
                        notifyDataSetChanged();
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("url", url);
                        context.startActivity(intent);
                        try {
                            mDbUtils.update(item, WhereBuilder.b("id", "=", item.id), "isee");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        Log.i("s", "");
                    }
                }
            }
        });
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("onClick:", "delete");
                if (list != null && list.size() > position) {
                    String id = list.get(position).id;
                    list.remove(position);
                    try {
                        mDbUtils.delete(FuwaMsgItem.class, WhereBuilder.b("id", "=", id));
                    } catch (DbException e) {
                        e.printStackTrace();

                    }
                    notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }

    public void onDataChange(List<FuwaMsgItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getFrontViewId(int position) {
        return R.layout.item_fuwa_msg;
    }

    @Override
    public int getLeftBackViewId(int position) {
        return 0;
    }

    @Override
    public int getRightBackViewId(int position) {
        return R.layout.item_fuwa_right;
    }

    class FuwaMessageView {

        RoundImageView iv_head;
        View v_point;
        TextView tv_title, tv_content, tv_time, tv_delete;
        RelativeLayout rl_item;

        FuwaMessageView(View itemView) {
            rl_item = (RelativeLayout) itemView.findViewById(R.id.rl_item);
            tv_delete = (TextView) itemView.findViewById(R.id.tv_delete);
            iv_head = (RoundImageView) itemView.findViewById(R.id.iv_head);
            v_point = itemView.findViewById(R.id.v_point);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    public void getDb(DbUtils mDbUtils) {
        this.mDbUtils = mDbUtils;
    }

}
