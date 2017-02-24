package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import im.boss66.com.activity.connection.ClubDetailActivity;
import im.boss66.com.entity.MySchool;

/**
 * 本地商会
 * Created by liw on 2017/2/22.
 */
public class LocalClubFragment extends ClubBaseFragment {
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

        list = new ArrayList<>();
        MySchool mySchool1 = new MySchool();
        MySchool mySchool2 = new MySchool();
        mySchool1.setSchoolinfo("11111111");
        mySchool1.setSchoolname("北京大学");
        mySchool1.setImg("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487667055622&di=12bb18bc7c3c34d7b8f189f09857a5a7&imgtype=0&src=http%3A%2F%2Fwww.hhxx.com.cn%2Fuploads%2Fallimg%2F1609%2F276-160Z5150T4410.jpg");
        mySchool2.setSchoolname("清华大学");
        mySchool2.setSchoolinfo("22222222");
        mySchool2.setImg("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487667055622&di=12bb18bc7c3c34d7b8f189f09857a5a7&imgtype=0&src=http%3A%2F%2Fwww.hhxx.com.cn%2Fuploads%2Fallimg%2F1609%2F276-160Z5150T4410.jpg");
        list.add(mySchool1);
        list.add(mySchool2);
    }

    @Override
    protected void click() {
        Intent intent = new Intent(getActivity(), ClubDetailActivity.class);
        startActivity(intent);
    }
}
