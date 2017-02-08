package im.boss66.com.fragment;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import im.boss66.com.R;

public class MyFragment extends BaseFragment {
    private static final String TAG = MyFragment.class.getSimpleName();
    private String title;
    private View view;
    /**
     * 设置布局显示目标最大化
     */
    private LinearLayout.LayoutParams WClayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams FFlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
    private ProgressBar progressBar;
    private View rootView;

    private void initRootView() {
        LinearLayout layout = new LinearLayout(getActivity());
        //设置布局 水平方向
        layout.setOrientation(LinearLayout.HORIZONTAL);
        //进度条
        progressBar = new ProgressBar(getActivity());
        //进度条显示位置
        progressBar.setPadding(0, 0, 15, 0);

        layout.addView(progressBar, WClayoutParams);

        TextView textView = new TextView(getActivity());
        textView.setText("加载中...");
        textView.setGravity(Gravity.CENTER_VERTICAL);

        layout.addView(textView, FFlayoutParams);
        layout.setGravity(Gravity.CENTER);

        LinearLayout loadingLayout = new LinearLayout(getActivity());
        loadingLayout.addView(layout, WClayoutParams);
        loadingLayout.setGravity(Gravity.CENTER);
        rootView = loadingLayout;
    }

    public static MyFragment newInstance(String title) {
        MyFragment fragment = new MyFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 这里我只是简单的用num区别标签，其实具体应用中可以使用真实的fragment对象来作为叶片
        title = getArguments() != null ? getArguments().getString("title") : "";
    }

    /**
     * 为Fragment加载布局时调用
     **/
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_pager_list, null);
            initRootView();
            initViews(view);
        }
        return view;
    }

    private void initViews(View view) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
    }
}
