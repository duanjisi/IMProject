package im.boss66.com.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.connection.MyMessageActivity;
import im.boss66.com.activity.connection.SendInformationActivity;
import im.boss66.com.listener.CommunityMsgListener;

/**
 * Created by admin on 2017/2/21.
 */
public class MyNewsPop extends BasePopuWindow implements View.OnClickListener {
    private View view;
    private Context context;
    private TextView tv_add_people;
    private TextView tv_personal_center;
    private CommunityMsgListener listener;

    public MyNewsPop(Context context, CommunityMsgListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected View getRootView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.pop_connection, null);
        tv_add_people = (TextView) view.findViewById(R.id.tv_add_people);
        tv_add_people.setText("发布消息");
        tv_personal_center = (TextView) view.findViewById(R.id.tv_personal_center);
        tv_personal_center.setText("我的消息");
        tv_add_people.setOnClickListener(this);
        tv_personal_center.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tv_add_people:
//                intent = new Intent(context, SendInformationActivity.class);
                dismiss();
                listener.goSendMsg();
                break;
            case R.id.tv_personal_center:
//                intent = new Intent(context, MyMessageActivity.class);
                dismiss();
                listener.goMyMsg();
                break;

        }
        // context.startActivity(intent);
    }
}
