package im.boss66.com.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.connection.EditSchoolActivity;

/**
 * Created by liw on 2017/3/2.
 */
public class CancelChosseDialog extends BaseDialog implements View.OnClickListener {

    private TextView cancle,ok,tv_save_edit;
    private ChooseYourLikeDialog chooseYourLikeDialog;

    public CancelChosseDialog(Context context,ChooseYourLikeDialog chooseYourLikeDialog) {
        super(context);
        this.chooseYourLikeDialog = chooseYourLikeDialog;
        cancle = (TextView) dialog.findViewById(R.id.cancel);
        ok = (TextView) dialog.findViewById(R.id.ok);
        tv_save_edit = (TextView) dialog.findViewById(R.id.tv_save_edit);
        tv_save_edit.setText("是否保存修改");
        ok.setText("保存并退出");
        cancle.setText("直接退出");
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
                chooseYourLikeDialog.setForceCancel(true);
                chooseYourLikeDialog.dismiss();
                break;
            case R.id.ok:
                dismiss();
                chooseYourLikeDialog.setForceCancel(false);
                chooseYourLikeDialog.dismiss();
                break;
        }

    }


}
