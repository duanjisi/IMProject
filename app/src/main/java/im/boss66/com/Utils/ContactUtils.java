package im.boss66.com.Utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Johnny on 2016/8/31.
 * 手机联系人工具类
 */
public class ContactUtils {
    public static String getPhoneNumbers(Context context) {
        StringBuffer sb = new StringBuffer();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        while (cursor.moveToNext()) {
            //读取通讯录的号码
            String number = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
            Log.i("info", "===============number:" + number);
            if (number.contains("-")) {
                number = number.replace("-", "");
            } else if (number.contains(" ")) {
                number = number.replace(" ", "");
            }
            if (number != null && !number.equals("")) {
                if (number.contains("+86")) {
                    String str = number.substring(3, number.length());
                    if (UIUtils.isMobile(str)) {
                        sb.append(str);
                        sb.append(",");
                    }
//                    sb.append(number.substring(3, number.length()));
                } else {
                    if (UIUtils.isMobile(number)) {
                        sb.append(number);
                        sb.append(",");
                    }
                }
            }
        }
        String string = sb.toString();
        if (string.contains(",")) {
            return string.substring(0, string.lastIndexOf(","));
        } else {
            return null;
        }
    }

    public static HashMap<String, String> getContactMap(Context context) {
        HashMap<String, String> map = new HashMap<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        while (cursor.moveToNext()) {
            //读取通讯录的姓名
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //读取通讯录的号码
            String number = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (number.contains("-")) {
                number = number.replace("-", "");
            } else if (number.contains(" ")) {
                number = number.replace(" ", "");
            }

            if (number != null && !number.equals("")) {
                if (number.contains("+86")) {
                    map.put(number.substring(3, number.length()), name);
                } else {
                    map.put(number, name);
                }
            }
        }
        return map;
    }
}
