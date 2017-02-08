package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.CaptureActivity;
import im.boss66.com.activity.ChatActivity;
import im.boss66.com.activity.discover.FriendCircleActivity;
import im.boss66.com.activity.discover.PeopleNearbyActivity;
import im.boss66.com.activity.discover.SharkItOffActivity;

/**
 * 发现的主界面
 */
public class DiscoverFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout rl_friends,rl_richScan,rl_shark_it_off,rl_people_nearby,rl_shopping,rl_game;
    private TextView tv_friends_no_read,tv_nearby_no_read;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        rl_friends = (RelativeLayout) view.findViewById(R.id.rl_friends);
        rl_richScan = (RelativeLayout) view.findViewById(R.id.rl_richScan);
        rl_shark_it_off = (RelativeLayout) view.findViewById(R.id.rl_shark_it_off);
        rl_people_nearby = (RelativeLayout) view.findViewById(R.id.rl_people_nearby);
        rl_shopping = (RelativeLayout) view.findViewById(R.id.rl_shopping);
        rl_game = (RelativeLayout) view.findViewById(R.id.rl_game);
        tv_friends_no_read = (TextView) view.findViewById(R.id.tv_friends_no_read);
        tv_nearby_no_read = (TextView) view.findViewById(R.id.tv_nearby_no_read);
        rl_friends.setOnClickListener(this);
        rl_richScan.setOnClickListener(this);
        rl_shark_it_off.setOnClickListener(this);
        rl_people_nearby.setOnClickListener(this);
        rl_shopping.setOnClickListener(this);
        rl_game.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_friends:
                //
                openActivity(FriendCircleActivity.class,null);
                break;
            case R.id.rl_richScan:
                Bundle bundle = new Bundle();
                bundle.putString("classType","DiscoverFragment");
                openActivity(CaptureActivity.class,bundle);
                break;
            case R.id.rl_shark_it_off:
                Bundle bundle1 = new Bundle();
                bundle1.putString("classType","DiscoverFragment");
                openActivity(CaptureActivity.class,bundle1);
                break;
            case R.id.rl_people_nearby:
                openActivity(PeopleNearbyActivity.class,null);
                break;
            case R.id.rl_shopping:
                break;
            case R.id.rl_game:
                break;
        }
    }

    private void openActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

}
