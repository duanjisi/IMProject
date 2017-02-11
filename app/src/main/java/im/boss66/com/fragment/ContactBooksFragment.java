package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import im.boss66.com.R;
import im.boss66.com.activity.AddFriendActivity;
import im.boss66.com.widget.EaseContactList;
import im.boss66.com.widget.TopNavigationBar;

/**
 * Created by Johnny on 2017/1/14.
 */
public class ContactBooksFragment extends BaseFragment {
    private TopNavigationBar topNavigationBar;
    private EaseContactList easeContactList;
    private ImageView iv_add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_books, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
//        topNavigationBar = (TopNavigationBar) view.findViewById(R.id.top_navigation_bar);
//        topNavigationBar.setRightBtnOnClickedListener(this);
        iv_add = (ImageView) view.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        });
        easeContactList = (EaseContactList) view.findViewById(R.id.contact_list);
    }

}
