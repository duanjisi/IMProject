package im.boss66.com.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.connection.EditSchoolActivity;
import im.boss66.com.activity.connection.PersonalDataActivity;

/**
 * Created by liw on 2017/3/2.
 */
public class CancelEditDialog extends BaseDialog implements View.OnClickListener {

    private TextView cancle,ok;

    public CancelEditDialog(Context context) {
        super(context);
        cancle = (TextView) dialog.findViewById(R.id.cancel);
        ok = (TextView) dialog.findViewById(R.id.ok);


        cancle.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    @Override
    protected int getView() {
        return R.layout.dialog_cancle_edit;
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
                dismiss();
                ((EditSchoolActivity) this.context).finish();
                break;
        }

    }
}
