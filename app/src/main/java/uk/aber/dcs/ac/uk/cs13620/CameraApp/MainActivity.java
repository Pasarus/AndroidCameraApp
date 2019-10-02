package uk.aber.dcs.ac.uk.cs13620.CameraApp;

import android.media.Image;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AppCamera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createCamPreview();
    }

    public void camButtonClicked(View view) {
        Image image = camera.captureImage();
    }

    private void createCamPreview() {
    }
}
