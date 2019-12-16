package prospere.proj942_app_ep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Button launchPhotoBtn;
    private static final int  MY_PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView helloTxt = (TextView) findViewById(R.id.textView);
        launchPhotoBtn = (Button) findViewById(R.id.launchPhotoBtn);
        launchPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                } else{
                    launchPhotoActivity();
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    launchPhotoActivity();
                } else {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setText("Error: Permission to use Camera Denied");
                    toast.show();
                    // permission denied, boo! Don't do anything.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void launchPhotoActivity(){
        startActivity(new Intent(MainActivity.this, photoTakeActivity.class));
    }
}
