package im.boss66.com.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.connection.PersonalDataActivity;

/**
 * Created by admin on 2017/2/20.
 */
public class PeopleDataDialog extends BaseDialog implements View.OnClickListener {

    private TextView cancle,ok;

    public PeopleDataDialog(Context context) {
        super(context);
        cancle = (TextView) dialog.findViewById(R.id.cancel);
        ok = (TextView) dialog.findViewById(R.id.ok);
        cancle.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    @Override
    protected int getView() {
        return R.layout.dialog_people_data;
    }

    @Override
    protected int getDialogStyleId() {
        return R.style.dialog_ios_style;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                Intent intent = new Intent(context, PersonalDataActivity.class);
                context.startActivity(intent);
                dismiss();
                break;
        }

    }
}
