package uk.aber.dcs.ac.uk.cs13620.CameraApp;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AppCamera m_camera;
    private TextureView m_previewView;
    private String m_currentCameraID;
    private Button m_camButton;
    private Spinner m_camIdSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_previewView = findViewById(R.id.textureView);
        m_camButton = findViewById(R.id.camButton);
        m_camIdSpinner = findViewById(R.id.camIdSpinner);

        m_camera = new AppCamera(this.getApplicationContext(), m_previewView);

        populateCamIdSpinner();
        createCamPreview();
    }

    public void camButtonClicked(View view) {
        //Image image = camera.captureImage();
        System.out.println("[Debug] Cam button clicked!");
    }

    private class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            m_currentCameraID = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // This shouldn't occur so log
            System.out.println("[Debug] Nothing selected in spinner!");
        }
    }

    private void populateCamIdSpinner() {
        String[] camIDs = m_camera.getCameraList();
        if (camIDs.length < 1) {
            System.out.println("[Debug] No CamIDs found");
            return;
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.support_simple_spinner_dropdown_item, camIDs);
        m_camIdSpinner.setAdapter(adapter);
        m_camIdSpinner.setOnItemSelectedListener(new SpinnerActivity());
        m_currentCameraID = camIDs[0];
    }

    private class SurfaceTextureListener extends Activity implements TextureView.SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            m_camera.startCameraPreviewSession(m_currentCameraID, this);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Do nothing
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // Do nothing
        }
    }

    private void createCamPreview() {
        m_previewView.setSurfaceTextureListener(new SurfaceTextureListener());
    }
}
