package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.boss66.com.R;

/**
 * Created by admin on 2017/2/27.
 */
public class PeopleSearchAdapter extends BaseRecycleViewAdapter {
    private Context context;

    public PeopleSearchAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_people_search, parent, false);
        return new PeopleSearchHolder(view);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class PeopleSearchHolder extends RecyclerView.ViewHolder{

        public PeopleSearchHolder(View itemView) {
            super(itemView);
        }

    }
}
