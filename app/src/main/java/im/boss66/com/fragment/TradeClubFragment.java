package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import im.boss66.com.activity.connection.ClubDetailActivity;
import im.boss66.com.entity.MyInfo;

/**
 * 行业商会
 * Created by liw on 2017/2/22.
 */
public class TradeClubFragment extends ClubBaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected void initlist() {

    }

    @Override
    protected void click() {
        Intent intent = new Intent(getActivity(), ClubDetailActivity.class);
        startActivity(intent);
    }


}
