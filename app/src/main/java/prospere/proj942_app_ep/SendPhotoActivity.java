package prospere.proj942_app_ep;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;

public class SendPhotoActivity extends AppCompatActivity {
    private ImageView imagePreview;
    private String imageInViewPath;
    private File imageinView;
    private Button cancelBtn, sendBtn;
    private static final String TAG = "SendPhotoActivity";
    private String url = "http://192.168.118.107/PROJ942/reception.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_photo);
        imagePreview = findViewById(R.id.previewPane);
        // imagePreview.setImageURI(Uri.encode(getIntent().getExtras().getString("lastFile"))); // WHY DON'T YOU WORKKKKKKKKKKKKKKKKKKKK
        imageInViewPath = getIntent().getExtras().getString("lastFile");
        imageinView = new File(imageInViewPath);
        showPicture(imageInViewPath, imagePreview);

        //imagePreview.setImageURI(Uri.parse()); // WHY DON'T YOU WORKKKKKKKKKKKKKKKKKKKK
        cancelBtn = findViewById(R.id.btnCancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageInViewPath != null){
                    File file = new File(imageInViewPath);
                    file.delete();
                }
                navigateUpTo(new Intent(getApplicationContext(), MainActivity.class));
                //finish();
            }
        });

        sendBtn = findViewById(R.id.btnSend);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile(new File(imageInViewPath), url);
            }
        });
    }

    /**
     * Method that should draw/show the selected picture on the given surface
     * @param path
     */
    public void showPicture(String path, ImageView myView){
        Log.d(TAG, path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        myView.setImageBitmap(bitmap);
    }

    /**
     * Send the File to the server specified @url
     * @param myFile
     * @param url
     */
    public void sendFile(File myFile, String url){
        // Send some File to url.
        //TODO: Do it.
    }
}
