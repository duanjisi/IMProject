package im.boss66.com.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.connection.AddPeopleActivity;
import im.boss66.com.activity.connection.PeopleCenterActivity;
import im.boss66.com.activity.connection.PersonalDataActivity;

/**
 * Created by admin on 2017/2/21.
 */
public class PeopleConnectionPop extends BasePopuWindow implements View.OnClickListener {
    private View view;
    private Context context;

    private boolean flag;
    public PeopleConnectionPop(Context context,boolean flag) {
        super(context);
        this.flag =flag;
        this.context = context;
    }




    @Override
    protected View getRootView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.pop_connection, null);
        view.findViewById(R.id.tv_add_people).setOnClickListener(this);
        view.findViewById(R.id.tv_personal_center).setOnClickListener(this);
        TextView tv_personal_center = (TextView) view.findViewById(R.id.tv_personal_center);

        if(flag){ //没数据
            tv_personal_center.setText("完善资料");
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tv_add_people:
                intent = new Intent(context, AddPeopleActivity.class);
                dismiss();

                break;
            case R.id.tv_personal_center:
                if(flag){
                    intent = new Intent(context, PersonalDataActivity.class);
                    context.startActivity(intent);
                    return;
                }
                intent = new Intent(context, PeopleCenterActivity.class);
                dismiss();
                break;

        }
        context.startActivity(intent);


    }
}
