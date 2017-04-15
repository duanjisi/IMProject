package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.entity.ChildEntity;

/**
 * Created by Johnny on 2017/4/13.
 */
public class FuwaAdapter extends ABaseAdapter<ChildEntity> {

    public FuwaAdapter(Context context) {
        super(context);
    }

    @Override
    protected View setConvertView(int position, final ChildEntity entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_treasure_fuwa, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (entity != null) {
            holder.tvName.setText(getName(entity.getId()));
            holder.address.setText(entity.getPos());
            holder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Session.getInstance().selectedFuwa(entity);
                }
            });
        }
        return convertView;
    }

    private String getName(String num) {
//        int number = 0;
        String str = "";
        if (num != null && !num.equals("")) {
            int number = Integer.parseInt(num);
            if (number < 10) {
                str = "0" + number + "号福娃";
            } else {
                str = number + "号福娃";
            }
        }
        return str;
    }

    private class ViewHolder {
        TextView tvName;
        TextView address;
        TextView details;

        public ViewHolder(View view) {
            this.tvName = (TextView) view.findViewById(R.id.tv_name);
            this.address = (TextView) view.findViewById(R.id.tv_address);
            this.details = (TextView) view.findViewById(R.id.tv_details);
        }
    }
}
