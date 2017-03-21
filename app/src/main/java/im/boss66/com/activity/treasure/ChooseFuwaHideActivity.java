package im.boss66.com.activity.treasure;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.ChooseFuwaHideAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.FuwaEntity;
import im.boss66.com.http.HttpUrl;

/**
 * 选择福娃
 */
public class ChooseFuwaHideActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back;
    private RecyclerView rv_content;
    private ChooseFuwaHideAdapter adapter;
    private List<FuwaEntity.Data> fuwaList;
    private Dialog dialog;
    private TextView tv_dia_name, tv_serial_dia_number;
    private int selectPos;
    private String selectId;
    private static File imgFile;
    private String userId, geohash, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_fuwa_hide);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                address = bundle.getString("address");
                geohash = bundle.getString("geohash");
            }
        }
        tv_back = (TextView) findViewById(R.id.tv_back);
        rv_content = (RecyclerView) findViewById(R.id.rv_content);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_content.setLayoutManager(layoutManager);
        AccountEntity sAccount = App.getInstance().getAccount();
        userId = sAccount.getUser_id();
        tv_back.setOnClickListener(this);
        fuwaList = new ArrayList<>();
        adapter = new ChooseFuwaHideAdapter(this, fuwaList);
        rv_content.setAdapter(adapter);
        rv_content.addOnItemTouchListener(new ChooseFuwaHideAdapter.RecyclerItemClickListener(this,
                new ChooseFuwaHideAdapter.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (selectPos >= 0 && selectPos < fuwaList.size()) {
                            FuwaEntity.Data lastitem = fuwaList.get(selectPos);
                            lastitem.setSel(false);
                            adapter.notifyItemChanged(selectPos);
                        }
                        selectPos = position;
                        FuwaEntity.Data item = fuwaList.get(position);
                        item.setSel(true);
                        adapter.notifyItemChanged(selectPos);
                        showDialog(item);
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {
                    }
                }));
        getSeverData();
    }

    private void getSeverData() {
        String url = HttpUrl.QUERY_MY_FUWA + userId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    FuwaEntity entity = JSON.parseObject(res, FuwaEntity.class);
                    List<FuwaEntity.Data> data = entity.getData();
                    if (data != null && data.size() > 0) {
                        showData(data);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("onFailure", s);
            }
        });
    }

    private void showData(List<FuwaEntity.Data> data) {
        for (FuwaEntity.Data bill : data) {
            boolean state = false;
            for (FuwaEntity.Data bills : fuwaList) {
                if (bills.getId().equals(bill.getId())) {
                    List<String> list = bills.getIdList();
                    String id = bill.getGid();
                    if (!TextUtils.isEmpty(id) && !list.contains(id)) {
                        list.add(id);
                        bills.setIdList(list);
                    }
                    int num = bills.getNum();
                    num += bill.getNum();
                    bills.setNum(num);
                    state = true;
                }
            }
            if (!state) {
                List<String> list = bill.getIdList();
                String id = bill.getGid();
                if (!TextUtils.isEmpty(id) && !list.contains(id)) {
                    list.add(id);
                    bill.setIdList(list);
                }
                fuwaList.add(bill);
            }
        }
        adapter.onDataChange(fuwaList);
    }

    private void showDialog(FuwaEntity.Data item) {
        if (dialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_choose_fuwa, null);
            int sceenW = UIUtils.getScreenWidth(this);
            int sceenH = UIUtils.getScreenHeight(this);
            ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
            tv_dia_name = (TextView) view.findViewById(R.id.tv_name);
            tv_serial_dia_number = (TextView) view.findViewById(R.id.tv_serial_number);
            Button bt_catch = (Button) view.findViewById(R.id.bt_catch);
            bt_catch.setOnClickListener(this);
            iv_close.setOnClickListener(this);

            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (sceenW * 0.8);
            lp.height = (int) (sceenH * 0.6);
            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
        }
        if (item != null) {
            String num = item.getId();
            tv_dia_name.setText(num + "号福娃");
            tv_serial_dia_number.setText("" + num);
            List<String> idList = item.getIdList();
            if (idList != null && idList.size() > 0) {
                selectId = idList.get(0);
            }
        }
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_catch:
                dialog.dismiss();
                hideFuwaServer();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_close:
                dialog.dismiss();
                break;
        }
    }

    public static void getImgFile(File file) {
        imgFile = file;
    }

    private void hideFuwaServer() {
        String url = HttpUrl.HIDE_MY_FUWA + userId + "&fuwagid=" +
                selectId + "&pos=" + address + "&geohash=" + geohash;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        RequestParams params = new RequestParams();
        params.addBodyParameter("file", imgFile);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    BaseResult data = BaseResult.parse(res);
                    if (data != null) {
                        int code = data.getCode();
                        if (code == 0) {
                            EventBus.getDefault().post(selectId);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                } else {
                    showToast("藏福娃失败TAT，请重试", false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(s, false);
            }
        });
    }

}
