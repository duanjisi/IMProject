package im.boss66.com.Utils;

import android.graphics.Bitmap;
import android.util.Log;

public class GifUtil {

	public static final String TAG = GifUtil.class.getSimpleName();

	private static GifUtil instance;

	public synchronized static GifUtil getInstance() {
		if (null == instance) {
			instance = new GifUtil();
		}
		return instance;
	}

	/**
	 * 合成Gif图片
	 * 
	 * @param fileName
	 *            待合成Gif图片路径
	 * @param bitmaps
	 *            图片集合
	 * @param delay
	 *            Gif图片播放延迟时间
	 * @return true 合成成功<br>
	 *         false 合成失败
	 */
	public boolean encode(String fileName, Bitmap[] bitmaps, int delay) {
		if (bitmaps == null || bitmaps.length == 0) {
			throw new NullPointerException("Bitmaps should have content!!!");
		}
		try {
			int width = bitmaps[0].getWidth();
			int height = bitmaps[0].getHeight();

			if (Init(fileName, width, height, 256, 100, delay) != 0) {
				Log.e(TAG, "GifUtil init failed");
				return false;
			}

			for (Bitmap bp : bitmaps) {

				int pixels[] = new int[width * height];

				bp.getPixels(pixels, 0, width, 0, 0, width, height);
				AddFrame(pixels);
			}
			Close();
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Encode error", e);
		}
		return false;
	}

	/**
	 * 初始化
	 * 
	 * @param gifName
	 *            gif图片路径
	 * @param w
	 *            生成Gif图片默认宽
	 * @param h
	 *            生成Gif图片默认高
	 * @param numColors
	 *            色彩,默认256
	 * @param quality
	 *            图片质量,默认100
	 * @param frameDelay
	 *            播放间隔
	 * @return
	 */
	public native int Init(String gifName, int w, int h, int numColors, int quality, int frameDelay);

	public native int AddFrame(int[] pixels);

	public native void Close();
	
	static {
		System.loadLibrary("gifflen");
	}

}
