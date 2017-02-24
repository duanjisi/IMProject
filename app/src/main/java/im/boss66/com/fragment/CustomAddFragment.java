package im.boss66.com.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;

/**
 * Created by liw on 2017/2/21.
 */
public class CustomAddFragment extends BaseFragment implements View.OnClickListener {
    private TextView tv_create;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customadd, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        tv_create = (TextView) view.findViewById(R.id.tv_create);
        tv_create.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
        tv_create.getPaint().setAntiAlias(true);//抗锯齿

        tv_create.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.tv_create:
                ToastUtil.show(getActivity(),"创建", Toast.LENGTH_SHORT);
                break;

        }
    }
}
