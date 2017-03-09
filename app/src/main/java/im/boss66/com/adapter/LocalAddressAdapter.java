package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.R;

/**
 * 地区
 */
public class LocalAddressAdapter extends RecyclerView.Adapter<LocalAddressAdapter.AddressViewHolder> {

    private int type;
    private Context context;
    //    private List<LocalAddressEntity.ThreeChild> privincelist;
//    private List<LocalAddressEntity.FourChild> cityList;
//    private List<LocalAddressEntity.LastChild> districList;
    protected MyItemClickListener mItemClickListener;

    public LocalAddressAdapter(Context context) {
        this.context = context;
    }

//    public void getPrivincedList(List<LocalAddressEntity.ThreeChild> list, int Type){
//        this.type = Type;
//        this.privincelist = list;
//    }
//
//    public void getCityList(List<LocalAddressEntity.FourChild> list, int Type){
//        this.type = Type;
//        this.cityList = list;
//    }
//
//    public void getDistrictList(List<LocalAddressEntity.LastChild> list, int Type){
//        this.type = Type;
//        this.districList = list;
//    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_address, parent, false);
        return new AddressViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
//        if (type == 1){
//            LocalAddressEntity.ThreeChild item = privincelist.get(position);
//            if (item != null){
//                holder.tv_address.setText("" + item.getRegion_name());
//            }
//        }else if(type == 2){
//            LocalAddressEntity.FourChild item = cityList.get(position);
//            if (item != null){
//                holder.tv_address.setText("" + item.getRegion_name());
//            }
//        }else {
//            LocalAddressEntity.LastChild item = districList.get(position);
//            if (item != null){
//                holder.iv_go.setVisibility(View.GONE);
//                holder.tv_address.setText("" + item.getRegion_name());
//            }
//        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
//        if (type == 1){
//            size = privincelist != null?privincelist.size():0;
//        }else if(type == 2){
//            size = cityList != null?cityList.size():0;
//        }else {
//            size = districList != null?districList.size():0;
//        }
        return size;
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_address;
        private ImageView iv_go;
        private MyItemClickListener listener;

        public AddressViewHolder(View itemView, MyItemClickListener listener) {
            super(itemView);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            iv_go = (ImageView) itemView.findViewById(R.id.iv_go);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

}
