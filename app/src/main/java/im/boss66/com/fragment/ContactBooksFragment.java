package im.boss66.com.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.boss66.com.R;
import im.boss66.com.widget.EaseContactList;
import im.boss66.com.widget.TopNavigationBar;

/**
 * Created by Johnny on 2017/1/14.
 */
public class ContactBooksFragment extends BaseFragment implements
        TopNavigationBar.OnRightBtnClickedListener {
    private TopNavigationBar topNavigationBar;
    private EaseContactList easeContactList;

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
        topNavigationBar = (TopNavigationBar) view.findViewById(R.id.top_navigation_bar);
        topNavigationBar.setRightBtnOnClickedListener(this);
        easeContactList = (EaseContactList) view.findViewById(R.id.contact_list);
    }

    @Override
    public void onRightBtnClicked() {

    }
}
