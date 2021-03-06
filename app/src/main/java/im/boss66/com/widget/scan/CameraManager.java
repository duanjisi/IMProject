/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package im.boss66.com.widget.scan;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import java.io.IOException;
import im.boss66.com.Utils.MycsLog;

/**
 * 该类主要负责对相机的操作
 */
public final class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();

    private final CameraConfigurationManager configManager;

    private Camera camera;
    private boolean initialized;

    public CameraManager(Context context) {
        this.configManager = new CameraConfigurationManager(context);
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @throws IOException Indicates the camera driver failed to open.
     */
    public synchronized void openDriver() throws IOException, RuntimeException {
        Camera theCamera = camera;
        if (theCamera == null) {
            try {
                theCamera = Camera.open();
            } catch (RuntimeException runtimeException) {
                throw runtimeException;
            }

            if (theCamera == null) {
                throw new IOException();
            }
            camera = theCamera;
        }

        if (!initialized) {
            initialized = true;
            configManager.initFromCameraParameters(theCamera);
        }

        Camera.Parameters parameters = theCamera.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save
        // these,
        // temporarily
        try {
            configManager.setDesiredCameraParameters(theCamera, false);
        } catch (RuntimeException re) {
            // Driver failed
            MycsLog.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
            MycsLog.i(TAG, "Resetting to saved camera params: " + parametersFlattened);
            // Reset:
            if (parametersFlattened != null) {
                parameters = theCamera.getParameters();
                parameters.unflatten(parametersFlattened);
                try {
                    theCamera.setParameters(parameters);
                    configManager.setDesiredCameraParameters(theCamera, true);
                } catch (RuntimeException re2) {
                    // Well, darn. Give up
                    MycsLog.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
                }
            }
        }

    }

    public synchronized boolean isOpen() {
        return camera != null;
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Closes the camera driver if still in use.
     */
    public synchronized void closeDriver() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    /**
     * 获取相机分辨率
     *
     * @return
     */
    public Point getCameraResolution() {
        return configManager.getCameraResolution();
    }
}
