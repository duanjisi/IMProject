package im.boss66.com.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.listener.CommunityMsgListener;

/**
 * Created by admin on 2017/2/21.
 */
public class SearchPop extends BasePopuWindow implements View.OnClickListener {
    private View view;
    private Context context;
    private EditText et_search;

    public SearchPop(Context context) {
        super(context);
        this.context = context;
        this.getBackground().setAlpha(150);
    }

    @Override
    protected View getRootView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.pop_search, null);
        et_search = (EditText) view.findViewById(R.id.et_search);
        return view;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void dismiss() {

        super.dismiss();
        listener.setVisible();

    }
    public interface  ICallBack{
        void setVisible();
    }
    private ICallBack listener;
    public void setListener(ICallBack listener){
        this.listener = listener;
    }


}
