package uk.aber.dcs.ac.uk.cs13620.CameraApp;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.Image;

import static android.content.Context.CAMERA_SERVICE;

public class AppCamera {
    // For use later when adding ability to select which camera to get image from.
    private String[] cameraList;

    public AppCamera(Context context) {
        CameraManager manager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        try {
            if (manager != null) {
                cameraList = manager.getCameraIdList();
            } else {
                System.out.println("[Debug] Manager is none existent at AppCamera Creation");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    public Image captureImage() {
        return new Image();
    }

    public String[] getCameraList() {
        return cameraList;
    }
}
