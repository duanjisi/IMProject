package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import im.boss66.com.R;
import im.boss66.com.entity.PersonalPhotoAlbumItem;
import im.boss66.com.widget.MyListView;

/**
 * 个人相册adapter
 */
public class PersonalPhotoAlbumAdapter extends BaseRecycleViewAdapter {

    private int PARENT_TYPE = 0;
    private int CHILD_TYPE = 1;
    private Context context;

    public PersonalPhotoAlbumAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        int viewtype = 0;
        PersonalPhotoAlbumItem item = (PersonalPhotoAlbumItem) getDatas().get(position);
        if (item.getType() == PARENT_TYPE) {
            viewtype = PARENT_TYPE;
        } else if (item.getType() == CHILD_TYPE) {
            viewtype = CHILD_TYPE;
        }
        return viewtype;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == PARENT_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_album_parent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_album_child, parent, false);
        }
        return new PhotoAlbumHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PhotoAlbumHolder holder1 = (PhotoAlbumHolder) holder;
        PersonalPhotoAlbumItem item = (PersonalPhotoAlbumItem) getDatas().get(position);
        if (item.getType() == PARENT_TYPE){
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            if (item.getTime().equals(String.valueOf(year))){
                holder1.tv_years.setVisibility(View.GONE);
            }else {
                holder1.tv_years.setVisibility(View.VISIBLE);
            }
            holder1.tv_years.setText("" + item.getTime());
        }else {
            String time = item.getList().get(0).getCreateTime();
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class PhotoAlbumHolder extends RecyclerView.ViewHolder {

        private TextView tv_years, tv_day, tv_month, tv_address;
        private MyListView lv_content;

        public PhotoAlbumHolder(View itemView, int type) {
            super(itemView);
            if (type == 0) {
                tv_years = (TextView) itemView.findViewById(R.id.tv_years);
            } else {
                tv_day = (TextView) itemView.findViewById(R.id.tv_day);
                tv_month = (TextView) itemView.findViewById(R.id.tv_month);
                tv_address = (TextView) itemView.findViewById(R.id.tv_address);
                lv_content = (MyListView) itemView.findViewById(R.id.lv_content);
            }
        }
    }

}
