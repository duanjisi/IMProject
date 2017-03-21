package im.boss66.com.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * listviewAdapter基类
 *
 * @param <t>
 */
public abstract class BaseLvAdapter<t> extends android.widget.BaseAdapter {

    protected List<t> list;
    protected Context context;

    public BaseLvAdapter(Context context, List<t> list) {
        super();
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public t getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView,
                                 ViewGroup parent);

    /**
     * 加载布局
     *
     * @param layoutResID
     * @return
     */
    protected View inflate(int layoutResID) {
        View view = View.inflate(context, layoutResID, null);
        return view;
    }

    /**
     * 数据变化更新ListView
     *
     * @param list
     */
    public void onDataChange(List<t> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    /**
     * 清除数据
     */
    protected void clear() {
        this.list.clear();
        this.notifyDataSetChanged();
    }

    /**
     * 新增数据
     *
     * @param list
     */
    protected void addAll(List<t> list) {
        if (list == null) {
            list = new ArrayList<t>();
        }
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

}
