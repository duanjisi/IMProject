package im.boss66.com.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.im.VerifyApplyActivity;
import im.boss66.com.entity.FriendState;
import im.boss66.com.entity.NearByChildEntity;
import im.boss66.com.entity.PersonEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.AddFriendRequest;
import im.boss66.com.http.request.DeleteFriendRequest;
import im.boss66.com.http.request.FriendShipRequest;
import im.boss66.com.http.request.PersonInformRequest;
import im.boss66.com.widget.ActionSheet;

/**
 * 附近的人详细资料
 */
public class PersonalNearbyDetailActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {
    private static final String TAG = PersonalNearbyDetailActivity.class.getSimpleName();
    private TextView tv_back, tv_name, tv_sex, tv_distance, tv_set_notes_labels,
            tv_area, tv_personalized_signature, tv_source;
    private ImageView iv_set, iv_head;
    private Button bt_greet, bt_complaint;
    private ImageLoader imageLoader;
    private LinearLayout ll_area, rl_privacy, rl_general;
    private FriendState friendState;
    private String classType;
    private String userid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_nearby_detail);
        initView();
        initData();
    }

    private void initData() {
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                userid = bundle.getString("userid", "");
                classType = bundle.getString("classType");
                if (!TextUtils.isEmpty(classType)) {
                    if ("SharkItOffActivity".equals(classType)) {
                        tv_source.setText("来自摇一摇");
                    } else if ("PeopleNearbyActivity".equals(classType)) {
                        tv_source.setText("附近的人");
                    } else {
                        requestFriendShip();
                    }
//                    else if ("CaptureActivity".equals(classType)) {
////                        tv_source.setText("通过扫一扫添加");
//                        requestPersonInform();
//                    }
                }
                NearByChildEntity item = (NearByChildEntity) bundle.getSerializable("people");
                if (item != null) {
                    tv_name.setText("" + item.getUser_name());
                    int sex = item.getSex();
                    if (sex == 1) {
                        tv_sex.setText("" + "男");
                    } else if (sex == 2) {
                        tv_sex.setText("" + "女");
                    }
                    imageLoader.displayImage(item.getAvatar(), iv_head,
                            ImageLoaderUtils.getDisplayImageOptions());
                    int dis = item.getDistance();
                    tv_distance.setText("" + dis + "米以内");
                    rl_general.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void initView() {
        rl_privacy = (LinearLayout) findViewById(R.id.rl_privacy);
        rl_general = (LinearLayout) findViewById(R.id.rl_general);
        ll_area = (LinearLayout) findViewById(R.id.ll_area);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_set_notes_labels = (TextView) findViewById(R.id.tv_set_notes_labels);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_personalized_signature = (TextView) findViewById(R.id.tv_personalized_signature);
        tv_source = (TextView) findViewById(R.id.tv_source);

        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);

        iv_head = (ImageView) findViewById(R.id.iv_head);
        bt_greet = (Button) findViewById(R.id.bt_greet);
        bt_complaint = (Button) findViewById(R.id.bt_complaint);

        iv_set.setImageResource(R.drawable.hp_chat_more);
        tv_back.setOnClickListener(this);
        iv_set.setVisibility(View.VISIBLE);
        iv_set.setOnClickListener(this);

        iv_head.setOnClickListener(this);
        tv_set_notes_labels.setOnClickListener(this);
        bt_greet.setOnClickListener(this);
        bt_complaint.setOnClickListener(this);
    }

    private void requestFriendShip() {
        if (!userid.equals("")) {
            showLoadingDialog();
            FriendShipRequest request = new FriendShipRequest(TAG, userid);
            request.send(new BaseDataRequest.RequestCallback<FriendState>() {
                @Override
                public void onSuccess(FriendState pojo) {
                    initDatas(pojo);
                }

                @Override
                public void onFailure(String msg) {
                    cancelLoadingDialog();
                    showToast(msg, true);
                }
            });
        }
    }

    private void initDatas(FriendState state) {
        this.friendState = state;
        requestPersonInform();
    }

    private void requestPersonInform() {
        if (!userid.equals("")) {
            PersonInformRequest request = new PersonInformRequest(TAG, userid);
            request.send(new BaseDataRequest.RequestCallback<PersonEntity>() {
                @Override
                public void onSuccess(PersonEntity pojo) {
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
    }

    private void bindDatas(PersonEntity entity) {
        if (entity != null) {
            tv_name.setText(entity.getUser_name());
            tv_sex.setText(entity.getSex());
            UIUtils.hindView(tv_distance);
            UIUtils.hindView(bt_complaint);
            UIUtils.showView(rl_privacy);
            tv_area.setText(entity.getDistrict());
            tv_personalized_signature.setText(entity.getSignature());
            if (friendState.equals("1")) {
                bt_greet.setText("发消息");
            } else {
                bt_greet.setText("添加到通讯录");
            }

            if ("CaptureActivity".equals(classType)) {
                tv_source.setText("通过扫一扫添加");
            } else if ("ChatGroupInformActivity".equals(classType)) {
                tv_source.setText("群消息");
            } else {
                tv_source.setText("来源于~");
            }
            imageLoader.displayImage(entity.getAvatar(), iv_head,
                    ImageLoaderUtils.getDisplayImageOptions());
        }
    }

    @Override

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:
                showActionSheet();
                break;
            case R.id.iv_head://头像
                break;
            case R.id.tv_set_notes_labels://设置标签
                break;
            case R.id.bt_greet://打招呼
                String str = getText(bt_greet);
                if (str.equals("添加到通讯录")) {
                    Intent intent = new Intent(context, VerifyApplyActivity.class);
                    intent.putExtra("userid", userid);
                    startActivity(intent);
                }
                break;
            case R.id.bt_complaint://投诉
                break;
        }
    }

    private void addFriendRequest(String userid) {
        showLoadingDialog();
        AddFriendRequest request = new AddFriendRequest(TAG, userid);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                finish();
                showToast("已发送好友请求!", true);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
                cancelLoadingDialog();
            }
        });
    }

    private void deleteFriendRequest(String userid) {
        showLoadingDialog();
        DeleteFriendRequest request = new DeleteFriendRequest(TAG, userid);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                finish();
                showToast("已删除该好友!", true);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
                cancelLoadingDialog();
            }
        });
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(PersonalNearbyDetailActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem(getString(R.string.delete), ActionSheet.SheetItemColor.Black, PersonalNearbyDetailActivity.this);
        actionSheet.show();
    }


    @Override
    public void onClick(int which) {
        deleteFriendRequest(userid);
    }
}
