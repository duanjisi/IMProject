package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import im.boss66.com.R;
import im.boss66.com.entity.CellEntity;

/**
 * Created by Johnny on 2017/2/20.
 */
public class CellAdapter extends ABaseAdapter<CellEntity> {

    public CellAdapter(Context context) {
        super(context);
    }

    @Override
    protected View setConvertView(int position, CellEntity entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_cell, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (entity != null) {
            holder.image.setImageResource(entity.getResId());
            holder.tv_name.setText(entity.getName());
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView tv_name;

        public ViewHolder(View view) {
            this.image = (ImageView) view.findViewById(R.id.iv_icon);
            this.tv_name = (TextView) view.findViewById(R.id.tv_name);
        }
    }
}
