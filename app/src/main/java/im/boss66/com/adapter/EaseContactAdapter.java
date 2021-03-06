package im.boss66.com.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.util.EMLog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.SessionInfo;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.AddFriendRequest;

public class EaseContactAdapter extends ArrayAdapter<EaseUser> implements SectionIndexer, Observer {
    private static final String TAG = "ContactAdapter";
    List<String> list;
    List<EaseUser> userList;
    List<EaseUser> copyUserList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    private MyFilter myFilter;
    private boolean notiyfyByFilter;
    private ImageLoader imageLoader;
    private boolean isShow = false;
    private boolean isPhoneContact = false;

    public EaseContactAdapter(Context context, int resource, List<EaseUser> objects) {
        super(context, resource, objects);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        this.res = resource;
        this.userList = objects;
        copyUserList = new ArrayList<EaseUser>();
        copyUserList.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
        Session.getInstance().addObserver(this);
    }


    public List<EaseUser> getUserList() {
        return userList;
    }

    public void setPhoneContact(boolean phoneContact) {
        isPhoneContact = phoneContact;
    }

    public void addAll(List<EaseUser> objects) {
        this.userList = objects;
        copyUserList.clear();
        copyUserList.addAll(objects);
        addAll(objects);
        notifyDataSetChanged();
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    private static class ViewHolder {
        ImageView avatar;
        TextView nameView;
        TextView contact;
        TextView headerView;
        TextView notice;
        TextView addFriend;
        ImageView ivSelected;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (res == 0)
                convertView = layoutInflater.inflate(R.layout.ease_row_contact, null);
            else
                convertView = layoutInflater.inflate(res, null);
            if (res == 0) {
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                holder.nameView = (TextView) convertView.findViewById(R.id.name);
                holder.headerView = (TextView) convertView.findViewById(R.id.header);
                holder.ivSelected = (ImageView) convertView.findViewById(R.id.iv_selected_tag);
            } else {
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                holder.nameView = (TextView) convertView.findViewById(R.id.name);
                holder.contact = (TextView) convertView.findViewById(R.id.tv_contact);
                holder.headerView = (TextView) convertView.findViewById(R.id.header);
                holder.notice = (TextView) convertView.findViewById(R.id.tv_notice);
                holder.addFriend = (TextView) convertView.findViewById(R.id.btn_add);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final EaseUser user = getItem(position);
        if (user == null)
            Log.d("ContactAdapter", position + "");
//        String username = user.getUsername();
        String contactName = user.getContactName();
        String username;
        if (isPhoneContact) {
            username = "嗨萌:" + user.getUsername();
        } else {
            username = user.getUsername();
        }
        String header = user.getInitialLetter();

        boolean ischecked = user.isChecked();

        if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                holder.headerView.setVisibility(View.GONE);
            } else {
                holder.headerView.setVisibility(View.VISIBLE);
                holder.headerView.setText(header);
            }
        } else {
            holder.headerView.setVisibility(View.GONE);
        }

        setUserNick(username, holder.nameView);
        setUserNick(contactName, holder.contact);
        if (username != null && !username.equals("")) {
            holder.nameView.setText(username);
        }

        imageLoader.displayImage(user.getAvatar(), holder.avatar, ImageLoaderUtils.getDisplayImageOptions());
        if (primaryColor != 0)
            holder.nameView.setTextColor(primaryColor);
        if (primarySize != 0)
            holder.nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if (initialLetterBg != null)
            holder.headerView.setBackgroundDrawable(initialLetterBg);
        if (initialLetterColor != 0)
            holder.headerView.setTextColor(initialLetterColor);

        if (res != 0) {
            if (user.getIs_friends().equals("1")) {
                holder.addFriend.setVisibility(View.INVISIBLE);
                holder.notice.setVisibility(View.VISIBLE);
                holder.notice.setText("已添加");
            } else {
//                if (user.getRequest_status().equals("1")) {
//                    holder.addFriend.setVisibility(View.INVISIBLE);
//                    holder.notice.setVisibility(View.VISIBLE);
//                    holder.notice.setText("等待验证");
//                } else if (user.getRequest_status().equals("0")) {
//                    holder.addFriend.setVisibility(View.INVISIBLE);
//                    holder.notice.setVisibility(View.VISIBLE);
//                    holder.notice.setText("已拒绝");
//                } else {
//                    holder.addFriend.setVisibility(View.VISIBLE);
//                    holder.notice.setVisibility(View.INVISIBLE);
//                }
                holder.addFriend.setVisibility(View.VISIBLE);
                holder.notice.setVisibility(View.INVISIBLE);
            }
            holder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFriend(user);
                }
            });
        } else {
            if (isShow) {
                holder.ivSelected.setVisibility(View.VISIBLE);
                boolean isAdded = user.isAdded();
                if (isAdded) {
                    holder.ivSelected.setImageResource(R.drawable.sp_check_selected);
                } else {
                    if (ischecked) {
                        holder.ivSelected.setImageResource(R.drawable.sp_check_press);
                    } else {
                        holder.ivSelected.setImageResource(R.drawable.sp_check_default);
                    }
                }
            } else {
                holder.ivSelected.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public void setUserNick(String username, TextView textView) {
        if (username != null && !username.equals("") && textView != null) {
            textView.setText(username);
        }
    }

    private void addFriend(final EaseUser user) {
        if (user.getUserid() != null && !user.getUserid().equals("")) {
            AddFriendRequest request = new AddFriendRequest(TAG, user.getUserid(), "");
            request.send(new BaseDataRequest.RequestCallback<String>() {
                @Override
                public void onSuccess(String pojo) {
//                    user.setIs_friends("1");
//                    notifyDataSetChanged();
//                    sendNotification(user.getUserid());
                    Toast.makeText(getContext(), "已发送", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String msg) {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public EaseUser getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getInitialLetter();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }

    protected class MyFilter extends Filter {
        List<EaseUser> mOriginalList = null;

        public MyFilter(List<EaseUser> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mOriginalList == null) {
                mOriginalList = new ArrayList<EaseUser>();
            }
            EMLog.d(TAG, "contacts original size: " + mOriginalList.size());
            EMLog.d(TAG, "contacts copy size: " + copyUserList.size());

            if (prefix == null || prefix.length() == 0) {
                results.values = copyUserList;
                results.count = copyUserList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<EaseUser> newValues = new ArrayList<EaseUser>();
                for (int i = 0; i < count; i++) {
                    final EaseUser user = mOriginalList.get(i);
//                    String username = user.getUsername();
                    String username;
                    if (isPhoneContact) {
                        username = user.getContactName();
                    } else {
                        username = user.getUsername();
                    }
                    if (username.startsWith(prefixString)) {
                        newValues.add(user);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(user);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            EMLog.d(TAG, "contacts filter results size: " + results.count);
            return results;
        }

        @Override
        protected synchronized void publishResults(CharSequence constraint,
                                                   FilterResults results) {
            userList.clear();
            userList.addAll((List<EaseUser>) results.values);
            EMLog.d(TAG, "publish contacts filter results size: " + results.count);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
                notiyfyByFilter = false;
            } else {
                notifyDataSetInvalidated();
            }
        }
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            copyUserList.clear();
            copyUserList.addAll(userList);
        }
    }

    protected int primaryColor;
    protected int primarySize;
    protected Drawable initialLetterBg;
    protected int initialLetterColor;

    public EaseContactAdapter setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }


    public EaseContactAdapter setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
        return this;
    }

    public EaseContactAdapter setInitialLetterBg(Drawable initialLetterBg) {
        this.initialLetterBg = initialLetterBg;
        return this;
    }

    public EaseContactAdapter setInitialLetterColor(int initialLetterColor) {
        this.initialLetterColor = initialLetterColor;
        return this;
    }


    @Override
    public void update(Observable observable, Object o) {
        SessionInfo sin = (SessionInfo) o;
        if (sin.getAction() == Session.ACTION_REFRESH_VIEWS) {
            int position = sin.getTag();
            Log.i("info", "==========position:" + position);
            getItem(position).setChecked(false);
            setNotifyOnChange(true);
//            notifyDataSetChanged();
            Log.i("info", "=======update()");
        }
    }

}
