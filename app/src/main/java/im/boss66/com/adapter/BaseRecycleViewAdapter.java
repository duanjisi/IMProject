package im.boss66.com.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public abstract class BaseRecycleViewAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected RecycleViewItemListener itemListener;
    protected List<T> datas = new ArrayList<T>();

    public List<T> getDatas() {
        if (datas==null)
            datas = new ArrayList<T>();
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public void setItemListener(RecycleViewItemListener listener){
        this.itemListener = listener;
    }

}
