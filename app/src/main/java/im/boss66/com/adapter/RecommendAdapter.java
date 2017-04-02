package im.boss66.com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.entity.LocalAddressEntity;
import im.boss66.com.entity.MyFollow;
import im.boss66.com.entity.SchoolmateListEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.AddFriendRequest;

/**
 * Created by admin on 2017/2/24.
 */
public class RecommendAdapter extends BaseRecycleViewAdapter {
    private static final String TAG = "CommendAdapter";
    private Context context;

    private HashMap<String, String> map1;
    private HashMap<String, String> map2;
    private HashMap<String, String> map3;

    private boolean isFirst = true;

    private LocalAddressEntity jsonDate;
    private int from = -1; // 1 学校 2家乡 3自定义

    public RecommendAdapter(Context context) {
        this.context = context;
        initCityData();
    }

    public void setFrom(int from) {
        this.from = from;
    }

    private void initCityData() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("province.json"), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            jsonDate = JSON.parseObject(stringBuilder.toString(), LocalAddressEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //遍历省市区，存三个map
        LocalAddressEntity.SecondChild result1 = jsonDate.getResult();
        List<LocalAddressEntity.ThreeChild> list = result1.getList();
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map1.put(list.get(i).getRegion_id(), list.get(i).getRegion_name()); //省存map里
            List<LocalAddressEntity.FourChild> list2 = list.get(i).getList();
            for (int j = 0; j < list2.size(); j++) {
                map2.put(list2.get(j).getRegion_id(), list2.get(j).getRegion_name()); //市
                List<LocalAddressEntity.LastChild> list3 = list2.get(j).getList();
                for (int k = 0; k < list3.size(); k++) {
                    map3.put(list3.get(k).getRegion_id(), list3.get(k).getRegion_name()); //区
                }
            }
        }

    }


    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final RecommendAdapter.MyRecommendHolder holder1 = (RecommendAdapter.MyRecommendHolder) holder;
        final SchoolmateListEntity.ResultBean item = (SchoolmateListEntity.ResultBean) datas.get(position);

        if (item != null) {
            Glide.with(context).load(item.getAvatar()).into(holder1.img_follow);
            holder1.tv_follow_name.setText(item.getUser_name());
            holder1.tv_same.setText("相似度"+item.getSimilar()+"%"); //相似度
            List<SchoolmateListEntity.ResultBean.SchoolBean> school = item.getSchool();

            //来源于
            switch (from) {
                case 1:
                    if (school != null && school.size() > 0) {
                        String edu_year = school.get(0).getEdu_year();
                        String departments = school.get(0).getDepartments();
                        String name = school.get(0).getName();
                        holder1.tv_follow_content.setText(edu_year+"  "+name);

                    }

                    break;
                case 2:
                    String ht_province = item.getHt_province();
                    String ht_city = item.getHt_city();
                    String province = item.getProvince();

                    if(map1.get(ht_province) != null&&map2.get(ht_city) != null){

                        holder1.tv_follow_content.setText(map1.get(ht_province)+"  "+map2.get(ht_city));
                    }

                    break;
                case 3:

                    holder1.tv_follow_content.setText(item.getIndustry()+"  "+item.getIndustry());
                    break;
            }

            if(item.getIsFriend()==0){

                holder1.tv_add_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isFirst) {
                            isFirst = false;
                            AddFriendRequest request = new AddFriendRequest(TAG, item.getUser_id(), "");
                            request.send(new BaseDataRequest.RequestCallback<String>() {
                                @Override
                                public void onSuccess(String str) {
                                    Toast.makeText(context, "已发送", Toast.LENGTH_SHORT).show();
                                    holder1.tv_add_follow.setText("    已发送");
                                    holder1.tv_add_follow.setBackgroundResource(R.drawable.shape_null);
                                    holder1.tv_add_follow.setTextColor(Color.GRAY);
                                }

                                @Override
                                public void onFailure(String msg) {
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            ToastUtil.showShort(context, "请不要重复添加");
                        }

                    }
                });
            }else{
                holder1.tv_add_follow.setClickable(false);
                holder1.tv_add_follow.setText("已经添加");
                holder1.tv_add_follow.setBackgroundResource(R.drawable.shape_null);
                holder1.tv_add_follow.setTextColor(Color.GRAY);

            }

        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_my_follow2, parent, false);
        return new RecommendAdapter.MyRecommendHolder(view);
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public static class MyRecommendHolder extends RecyclerView.ViewHolder {
        public ImageView img_follow;
        public TextView tv_follow_name;
        public TextView tv_follow_content;

        public TextView tv_same;
        public TextView tv_add_follow;

        public MyRecommendHolder(View itemView) {
            super(itemView);
            img_follow = (ImageView) itemView.findViewById(R.id.img_follow);
            tv_follow_name = (TextView) itemView.findViewById(R.id.tv_follow_name);
            tv_follow_content = (TextView) itemView.findViewById(R.id.tv_follow_content);

            tv_same = (TextView) itemView.findViewById(R.id.tv_same);
            tv_add_follow = (TextView) itemView.findViewById(R.id.tv_add_follow);
        }

    }
}
