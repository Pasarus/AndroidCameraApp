package uk.aber.dcs.ac.uk.cs13620.CameraApp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;

import static android.content.Context.CAMERA_SERVICE;

public class AppCamera {
    // For use later when adding ability to select which camera to get image from.
    private String[] m_cameraList;
    private TextureView m_previewView;
    private CameraCaptureSession m_previewCaptureSession;
    private CameraDevice m_currentCameraDevice;
    private CameraManager m_cameraManager;
    private CaptureRequest m_previewRequest;
    private Context m_context;

    private static final int CAMERA_PREVIEW = 0;
    private int m_cameraState;

    private static final int PERMISSION_REQUEST_USE_CAMERA = 1;

    public AppCamera(Context context, TextureView textureView) {
        m_previewView = textureView;
        m_cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        try {
            if (m_cameraManager != null) {
                m_cameraList = m_cameraManager.getCameraIdList();
            } else {
                System.out.println("[Debug] Manager is none existent at AppCamera Creation");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        m_cameraState = CAMERA_PREVIEW;
        m_context = context;
    }


    private final CameraDevice.StateCallback m_cameraDeviceStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            m_currentCameraDevice = camera;
            // startCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            m_currentCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            m_currentCameraDevice = null;
        }
    };

    private CameraCaptureSession.CaptureCallback m_captureCallback = new CameraCaptureSession.CaptureCallback() {
        private void processCaptureCallback() {
            // At present nothing should happen regardless of current state. Switch statement for future expansion.
            switch (m_cameraState) {
                case (CAMERA_PREVIEW): {
                    //TODO: This is where processing of facial recognition would happen, or at least a callback would be called that is an optional plugin for preview.
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            processCaptureCallback();
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            processCaptureCallback();
        }
    };

    public void startCameraPreviewSession(String cameraID, Activity activity) {
        //TODO: Ensure that camera permissions are gained.
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (m_context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_USE_CAMERA);
                }
                // Check again and confirm else return and give up.
                if (m_context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("[DEBUG] Permission for Camera not granted");
                    return;
                }
            }
            // Open the camera and have it assigned to m_currentCameraDevice using the CallBack
            m_cameraManager.openCamera(cameraID, m_cameraDeviceStateCallBack, null);

            SurfaceTexture texture = m_previewView.getSurfaceTexture();

            Surface surface = new Surface(texture);

            if (m_currentCameraDevice == null) {
                System.out.println("[Debug] Current camera device is null, when starting preview session.");
                return;
            }

            final CaptureRequest.Builder previewBuilder = m_currentCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(surface);


            CameraCaptureSession.StateCallback captureSession = new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    // The camera doesn't exist/isn't open
                    if (m_currentCameraDevice == null) {
                        return;
                    }

                    m_previewCaptureSession = session;
                    try {
                        // Set autofocus to be on
                        previewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // TODO: Consider adding flash to be on when required i.e. AutoFlash.

                        m_previewRequest = previewBuilder.build();
                        m_previewCaptureSession.setRepeatingRequest(m_previewRequest, m_captureCallback, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    System.out.println("[Debug] capture session callback onConfigureFailed called.");
                }
            };

            m_currentCameraDevice.createCaptureSession(Arrays.asList(surface), captureSession, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

//    public Image captureImage() {
//        return new Image();
//    }

    public String[] getCameraList() {
        return m_cameraList;
    }
}
