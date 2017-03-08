package im.boss66.com.activity.connection;

import im.boss66.com.activity.base.ABaseActivity;

//import com.bigkoo.pickerview.OptionsPickerView;

/**
 * 添加人脉
 * Created by liw on 2017/2/21.
 */
public class AddPeopleActivity extends ABaseActivity {

//    private TextView tv_location;
//    private TextView tv_cancle_search;
//    private LocalAddressEntity jsonDate;
////    private OptionsPickerView pvOptions;
//
//    private ArrayList<LocalAddressEntity.ThreeChild> list = new ArrayList<>(); //1级
//    private ArrayList<ArrayList<LocalAddressEntity.FourChild>> list2 = new ArrayList<>(); //2级
//
//    private ArrayList<ArrayList<ArrayList<LocalAddressEntity.LastChild>>> list3 = new ArrayList<>(); //3级
//
//    private RecyclerView rcv_search;
//    private PeopleSearchAdapter adapter;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_people);
//
//
//        initCityData();
//
//        initOptionPicker();
//        initViews();
//
//    }
//
//    private void initOptionPicker() {
//
//        LocalAddressEntity.SecondChild result = jsonDate.getResult();
//        list = (ArrayList<LocalAddressEntity.ThreeChild>) result.getList(); //省
//        for (int i = 0; i < list.size(); i++) {
//            List<LocalAddressEntity.FourChild> list = this.list.get(i).getList(); //市
//            list2.add((ArrayList<LocalAddressEntity.FourChild>) list);
//        }
//        for (int j = 0; j < list.size(); j++) {
//            List<LocalAddressEntity.FourChild> list = this.list.get(j).getList(); //市
//            ArrayList<ArrayList<LocalAddressEntity.LastChild>> list3_1 = new ArrayList<>();
//            for (int j1 = 0; j1 < list.size(); j1++) {
//                ArrayList<LocalAddressEntity.LastChild> list1 = (ArrayList<LocalAddressEntity.LastChild>) list.get(j1).getList();//县
//
//                list3_1.add(list1);
//            }
//            list3.add(list3_1);
//        }
//
//
//        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3, View v) {
//                //返回的分别是三个级别的选中位置
//                String tx = list.get(options1).getPickerViewText()
//                        + list2.get(options1).get(option2).getRegion_name()
//                        + list3.get(options1).get(option2).get(options3).getPickerViewText();
//                tv_location.setText(tx);
//
//            }
//        })
//                /*.setSubmitText("确定")
//                .setCancelText("取消")
//                .setTitleText("城市选择")
//                .setSubCalSize(18)
//                .setTitleSize(20)
//                .setTitleColor(Color.BLACK)
//                .setSubmitColor(Color.BLUE)
//                .setCancelColor(Color.BLUE)
//                .setBackgroundColor(Color.WHITE)
//                .setContentTextSize(18)
//                .setLinkage(false)//default true
//                .setLabels("省", "市", "区")//设置选择的三级单位
//                .setCyclic(false, false, false)//循环与
//                .setOutSideCancelable(false)//点击外部dismiss, default true*/
//                /*.setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
//                .setBgColor(0xFF000000)//滚轮背景颜色 Night mode*/
//                .setSubmitColor(Color.RED)
//                .setCancelColor(Color.GRAY)
//                .setSubCalSize(18)
//                .setContentTextSize(20)
//                .setSelectOptions(0, 0, 0)  //设置默认选中项
//                .build();
//
//        pvOptions.setPicker(list, list2, list3);
//
//    }
//
//    private LocalAddressEntity initCityData() {
//        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("province.json"), "UTF-8");
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            String line;
//            StringBuilder stringBuilder = new StringBuilder();
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//            bufferedReader.close();
//            inputStreamReader.close();
//            jsonDate = JSON.parseObject(stringBuilder.toString(), LocalAddressEntity.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return jsonDate;
//
//    }
//
//
//    private void initViews() {
//        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
//        tv_headcenter_view.setText("查找人脉");
//        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
//        tv_headlift_view.setOnClickListener(this);
//
//        tv_location = (TextView) findViewById(R.id.tv_location);
//        tv_location.setOnClickListener(this);
//
//        tv_cancle_search = (TextView) findViewById(R.id.tv_cancle_search);
//        tv_cancle_search.setOnClickListener(this);
//
//        rcv_search = (RecyclerView) findViewById(R.id.rcv_search);
//        rcv_search.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new PeopleSearchAdapter(this);
//
//        rcv_search.setAdapter(adapter);
//
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.tv_headlift_view:
//                finish();
//                break;
//            case R.id.tv_location:
//                pvOptions.show();
//                break;
//            case R.id.tv_cancle_search:
//
//                break;
//
//        }
//
//    }
}
