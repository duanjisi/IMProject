package im.boss66.com.fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import im.boss66.com.R;
import im.boss66.com.activity.personage.PersonalInformationActivity;
/**
 * 个人中心
 */
public class MineFragment extends BaseFragment implements View.OnClickListener{

    private RelativeLayout rl_information,rl_photo,rl_collect,rl_wallet,rl_expression,rl_set;
    private ImageView iv_head;
    private TextView tv_name,tv_number;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        rl_information = (RelativeLayout) view.findViewById(R.id.rl_information);
        rl_photo = (RelativeLayout) view.findViewById(R.id.rl_photo);
        rl_collect = (RelativeLayout) view.findViewById(R.id.rl_collect);
        rl_wallet = (RelativeLayout) view.findViewById(R.id.rl_wallet);
        rl_expression = (RelativeLayout) view.findViewById(R.id.rl_expression);
        rl_set = (RelativeLayout) view.findViewById(R.id.rl_set);
        iv_head = (ImageView) view.findViewById(R.id.iv_head);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_number = (TextView) view.findViewById(R.id.tv_number);
        rl_information.setOnClickListener(this);
        rl_photo.setOnClickListener(this);
        rl_collect.setOnClickListener(this);
        rl_wallet.setOnClickListener(this);
        rl_expression.setOnClickListener(this);
        rl_set.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_information://个人信息
                Intent intent = new Intent(getActivity(),PersonalInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_photo://相册
                break;
            case R.id.rl_collect://收藏
                break;
            case R.id.rl_wallet://钱包
                break;
            case R.id.rl_expression://表情
                break;
            case R.id.rl_set://设置
                break;
        }
    }
}
