package im.boss66.com.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.connection.EditSchoolActivity;
import im.boss66.com.activity.connection.SchoolListActivity;

/**
 * Created by liw on 2017/3/2.
 */
public class DeleteDialog extends BaseDialog implements View.OnClickListener {

    private TextView cancle,ok,tv_save_edit;

    public DeleteDialog(Context context) {
        super(context);
        cancle = (TextView) dialog.findViewById(R.id.cancel);
        ok = (TextView) dialog.findViewById(R.id.ok);

        tv_save_edit = (TextView) dialog.findViewById(R.id.tv_save_edit);
        tv_save_edit.setText("是否删除");
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
                listener.delete();
                break;
        }

    }

    private CallBack listener;

    public interface CallBack{
       void delete();
    }
    public void setListener(CallBack listener){
        this.listener = listener;
    }
}
