/*
 * Barebones implementation of displaying camera preview.
 * 
 * Created by lisah0 on 2012-02-24
 */
package im.boss66.com.widget.scan;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import im.boss66.com.Utils.MycsLog;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private PreviewCallback previewCallback;
    private AutoFocusCallback autoFocusCallback;

    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera, PreviewCallback previewCb, AutoFocusCallback autoFocusCb) {
        super(context);
        mCamera = camera;
        previewCallback = previewCb;
        autoFocusCallback = autoFocusCb;

		/*
         * Set camera to continuous focus if supported, otherwise use software
		 * auto-focus. Only works for API level >=9.
		 */
        /*
         * Camera.Parameters parameters = camera.getParameters(); for (String f
		 * : parameters.getSupportedFocusModes()) { if (f ==
		 * Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
		 * mCamera.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		 * autoFocusCallback = null; break; } }
		 */

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("info", "=================surfaceCreated()");
        // The Surface has been created, now tell the camera where to draw the
        // preview.
        try {
            Log.i("info", "00000000000000000surfaceCreated()");
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            MycsLog.d("DBG", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Camera preview released in activity
        Log.i("info", "=================surfaceDestroyed()");
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.stopPreview();
//                mCamera.release();
//                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /*
         * If your preview can change or rotate, take care of those events here.
		 * Make sure to stop the preview before resizing or reformatting it.
		 */
        Log.i("info", "=================surfaceChanged()");
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            Log.i("info", "00000000000000000surfaceChanged()");
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
            Log.i("info", "00000000000000000surfaceChanged()2");
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        try {
            // Hard code camera surface rotation 90 degs to match Activity view
            // in portrait
            Log.i("info", "00000000000000000surfaceChanged()3");
            mCamera.setDisplayOrientation(90);

            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            MycsLog.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }
}
