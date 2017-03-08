package im.boss66.com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.im.EmojiEditActivity;
import im.boss66.com.adapter.EmojiAdapter;
import im.boss66.com.entity.BaseEditEmo;
import im.boss66.com.entity.BaseEmo;
import im.boss66.com.entity.EmoEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.EditEmosAllRequest;
import im.boss66.com.http.request.EditEmosCateRequest;
import im.boss66.com.http.request.EmosSearchRequest;
import im.boss66.com.widget.MyGridView;

public class MyFragment extends BaseFragment {
    private static final String TAG = MyFragment.class.getSimpleName();
    private String title, cateid;
    private View view;
    private RelativeLayout rlNotify;
    private Button btnRefresh;
    private MyGridView gridView;
    private EmojiAdapter adapter;


    public void setCateid(String cateid) {
        this.cateid = cateid;
    }

    /**
     * 设置布局显示目标最大化
     */
//    private LinearLayout.LayoutParams WClayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//    private LinearLayout.LayoutParams FFlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
//    private ProgressBar progressBar;
//    private View rootView;
//    private void initRootView() {
//        LinearLayout layout = new LinearLayout(getActivity());
//        //设置布局 水平方向
//        layout.setOrientation(LinearLayout.HORIZONTAL);
//        //进度条
//        progressBar = new ProgressBar(getActivity());
//        //进度条显示位置
//        progressBar.setPadding(0, 0, 15, 0);
//
//        layout.addView(progressBar, WClayoutParams);
//
//        TextView textView = new TextView(getActivity());
//        textView.setText("加载中...");
//        textView.setGravity(Gravity.CENTER_VERTICAL);
//
//        layout.addView(textView, FFlayoutParams);
//        layout.setGravity(Gravity.CENTER);
//
//        LinearLayout loadingLayout = new LinearLayout(getActivity());
//        loadingLayout.addView(layout, WClayoutParams);
//        loadingLayout.setGravity(Gravity.CENTER);
//        rootView = loadingLayout;
//    }
    public static MyFragment newInstance(String title, String cateid) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("cateid", cateid);
        fragment.setArguments(args);
        return fragment;
    }

    public void search(String key) {
        showLoadingDialog();
        EmosSearchRequest request = new EmosSearchRequest(TAG, key, cateid, "1", "20");
        request.send(new BaseDataRequest.RequestCallback<BaseEmo>() {
            @Override
            public void onSuccess(BaseEmo pojo) {
                cancelLoadingDialog();
                bindDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void bindDatas(BaseEmo baseEmo) {
        if (baseEmo != null) {
            ArrayList<EmoEntity> list = baseEmo.getResult();
            if (list != null && list.size() != 0) {
                adapter.initData(list);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 这里我只是简单的用num区别标签，其实具体应用中可以使用真实的fragment对象来作为叶片
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString("title", "");
            cateid = bundle.getString("cateid", "");
            Log.i("info", "=======title:" + title + "\n" + "cateid:" + cateid);
        }
//        title = getArguments() != null ? getArguments().getString("title") : "";
//        cateid = getArguments() != null ? getArguments().getString("cateid") : "";
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
//            initRootView();
            initViews(view);
        }
        return view;
    }

    private void initViews(View view) {
        gridView = (MyGridView) view.findViewById(R.id.listView);
        rlNotify = (RelativeLayout) view.findViewById(R.id.rl_notify);
        btnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        adapter = new EmojiAdapter(getActivity());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new ItemClickListener());
        requestEmos();
    }

    private void requestEmos() {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        BaseDataRequest request;
        if (title.equals("全部")) {
            request = new EditEmosAllRequest(TAG, "1", "20");
        } else {
            request = new EditEmosCateRequest(TAG, cateid, "1", "20");
        }
        showLoadingDialog();
        request.send(new BaseDataRequest.RequestCallback<BaseEditEmo>() {
            @Override
            public void onSuccess(BaseEditEmo pojo) {
                cancelLoadingDialog();
                bindDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void bindDatas(BaseEditEmo baseEditEmo) {
        if (baseEditEmo != null) {
            ArrayList<EmoEntity> jokes = baseEditEmo.getEmo();
            if (jokes != null && jokes.size() != 0) {
                UIUtils.hindView(rlNotify);
                UIUtils.showView(gridView);
                adapter.initData(jokes);
            } else {
                UIUtils.hindView(gridView);
                UIUtils.showView(rlNotify);
            }
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            EmoEntity entity = (EmoEntity) adapterView.getItemAtPosition(i);
//            intent.putExtra("camera_path", "http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg");
            String fromat = entity.getEmo_format();
            if (!fromat.equals("gif")) {
                Intent intent = new Intent(getActivity(), EmojiEditActivity.class);
                intent.putExtra("camera_path", entity.getEmo_url());
                startActivity(intent);
            } else {
                showToast("暂不支持gif图编辑", true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
    }
}
