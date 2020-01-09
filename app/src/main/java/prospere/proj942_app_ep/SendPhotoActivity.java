package prospere.proj942_app_ep;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

public class SendPhotoActivity extends AppCompatActivity {
    private ImageView imagePreview;
    private String imageInViewPath;
    private File imageinView;
    private Button cancelBtn, sendBtn;
    private ProgressBar spinner;
    private static final String TAG = "SendPhotoActivity";
    private String urlstr = "http://192.168.118.106/PROJ942/reception.php";
    TextView messageText;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_photo);
        imagePreview = findViewById(R.id.previewPane);
        imageInViewPath = getIntent().getExtras().getString("lastFile");
        imageinView = new File(imageInViewPath);
        spinner = findViewById(R.id.sendSpinner);
        showPicture(imageInViewPath, imagePreview);

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
                spinner.setVisibility(View.VISIBLE);
                sendFile(new File(imageInViewPath), urlstr);
                spinner.setVisibility(View.INVISIBLE);
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
     * @param urladdr
     */
    public void sendFile(final File myFile, String urladdr) {

        FileUploader fu = new FileUploader(myFile, urladdr, this);
        fu.run();
    }


}
