package im.boss66.com.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

/**
 * Created by Johnny on 2017/5/18.
 */
public class BitmapCache {
    static BitmapCache cache;
    Hashtable bitmapRefs;
    ReferenceQueue q;

    class BtimapRef extends SoftReference {
        Integer _key = 0;

        public BtimapRef(Bitmap bmp, ReferenceQueue q, int key) {
            super(bmp, q);
            _key = key;
        }
    }

    BitmapCache() {
        bitmapRefs = new Hashtable();
        q = new ReferenceQueue();
    }


    public static BitmapCache getInstance() {
        if (cache == null) {
            cache = new BitmapCache();
        }
        return cache;

    }


    private void addCacheBitmap(Bitmap bmp, Integer key) {
        cleanCache();// 清除垃圾引用
        BtimapRef ref = new BtimapRef(bmp, q, key);
        bitmapRefs.put(key, ref);
    }


    public Bitmap getBitmap(int resId, Context context) {
        Bitmap bmp = null;
        // 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
//      android加载大量图片内存溢出的三种解决办法
        http:
//www.360doc.com/content/13/0409/11/7857928_277107102.shtml 2/2
        if (bitmapRefs.containsKey(resId)) {
            BtimapRef ref = (BtimapRef) bitmapRefs.get(resId);
            bmp = (Bitmap) ref.get();
        }
        // 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
        // 并保存对这个新建实例的软引用
        if (bmp == null) {
            bmp = BitmapFactory.decodeResource(context.getResources(), resId);
            this.addCacheBitmap(bmp, resId);
        }
        return bmp;
    }

    private void cleanCache() {
        BtimapRef ref = null;
        while ((ref = (BtimapRef) q.poll()) != null) {
            bitmapRefs.remove(ref._key);
        }
    }

    // 清除Cache内的全部内容
    public void clearCache() {
        cleanCache();
        bitmapRefs.clear();
        System.gc();
        System.runFinalization();
    }
}
