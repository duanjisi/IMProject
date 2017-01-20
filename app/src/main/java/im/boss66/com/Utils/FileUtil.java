package im.boss66.com.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 文件操作工具类
 *
 * @author zyu
 */
public final class FileUtil {

    /**
     * 检查SD卡是否挂载
     *
     * @return
     */
    public static boolean checkSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取存储卡路径
     *
     * @return
     */
    public static String getExternalStoragePath() {
        if (checkSDCard()) {
            if (Environment.getExternalStorageDirectory().canWrite()) {
                return Environment.getExternalStorageDirectory().getPath();
            }
        }

        return "";
    }

    /**
     * 获取存储卡的剩余容量，单位为K。
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getSDCardAvailableStore() {
        File path = Environment.getExternalStorageDirectory();
        StatFs statfs = new StatFs(path.getPath());
        long blockSize = statfs.getBlockSize();
        long availableBlocks = statfs.getAvailableBlocks();
        long availableStore = blockSize * availableBlocks;
        // (availableBlocks * blockSize)/1024 KIB 单位
        // (availableBlocks * blockSize)/1024 /1024 MIB单位

        return availableStore >> 10;
    }

    /**
     * 显示内存信息
     *
     * @param activity
     */
    private void showMemory(Activity activity) {
        final ActivityManager activityManager = (ActivityManager) activity
                .getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        MycsLog.i("系统剩余内存:" + (info.availMem >> 20) + "MB");
        MycsLog.i("系统是否处于低内存运行：" + info.lowMemory);
        MycsLog.i("当系统剩余内存低于" + (info.threshold >> 20) + "MB时就看成低内存运行");
    }

    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    /**
     * 把文件转换为byte数据
     *
     * @param file 文件实例
     * @return
     */
    public static byte[] file2Byte(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 是否存在SD卡
     *
     * @return
     */
    private static boolean ExistSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    public static String Path = "mnt/sdcard/imageCache";

    public static File getCacheFile(String imageUri) {
        File cacheFile = null;
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File sdCardDir = Environment.getExternalStorageDirectory();
                String fileName = getFileName(imageUri);
                File dir = new File(sdCardDir.getCanonicalPath() + Path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                cacheFile = new File(dir, fileName);
                // MycsLog.i("exists:" + cacheFile.exists() + ",dir:" + dir
                // + ",file:" + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e(TAG, "getCacheFileError:" + e.getMessage());
        }

        return cacheFile;
    }

    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    public static Bitmap getBitmapByPath(String filePath) {
        return getBitmapByPath(filePath, null);
    }

    public static Bitmap getBitmapByPath(String filePath,
                                         BitmapFactory.Options opts) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis, null, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    /**
     * 把bitmap保存到本地
     *
     * @param mBitmap
     * @param path
     */
    public static void saveMyBitmap(Bitmap mBitmap, String path) {
        File f = new File(path);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap copressImage(String imgPath, int imagew, int imageh) {
        File picture = new File(imgPath);
        BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
        // 下面这个设置是将图片边界不可调节变为可调节
        bitmapFactoryOptions.inJustDecodeBounds = true;
        bitmapFactoryOptions.inSampleSize = 2;
        int outWidth = bitmapFactoryOptions.outWidth;
        int outHeight = bitmapFactoryOptions.outHeight;
        Bitmap bmap = BitmapFactory.decodeFile(picture.getAbsolutePath(),
                bitmapFactoryOptions);
        int yRatio = (int) Math.ceil(bitmapFactoryOptions.outHeight / imageh);
        int xRatio = (int) Math.ceil(bitmapFactoryOptions.outWidth / imagew);
        if (yRatio > 1 || xRatio > 1) {
            if (yRatio > xRatio) {
                bitmapFactoryOptions.inSampleSize = yRatio;
            } else {
                bitmapFactoryOptions.inSampleSize = xRatio;
            }
        }
        bitmapFactoryOptions.inJustDecodeBounds = false;
        bmap = BitmapFactory.decodeFile(picture.getAbsolutePath(),
                bitmapFactoryOptions);
        if (bmap != null) {
            return bmap;
        }
        return null;
    }

    public static boolean isDirExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        } else {
            return true;
        }
    }



    private static String getFilePath(String url) {
        int index = url.lastIndexOf('.');
        return url.substring(0, index);
    }



    /**
     * 读取文件的大小
     *
     * @param file 文件路径
     * @return
     */
    public static long getFolderSize(File file) {
        long size = 0;
        File[] fileList = file.listFiles();
        if (fileList != null && fileList.length != 0) {
            for (int i = 0; i < fileList.length; i++) {
                //如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        }
        return size;
    }

    /**
     * 判断文件夹是否存在
     *
     * @param file
     * @return
     */
    public static boolean getFileStat(File file) {
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static File getFilePath(String filePath,
                                   String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }


}
