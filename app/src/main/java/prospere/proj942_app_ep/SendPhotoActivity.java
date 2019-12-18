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
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
    private static final String TAG = "SendPhotoActivity";
    private String urlstr = "http://192.168.118.107/PROJ942/reception.php";

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
    public void sendFile(File myFile, String urladdr) {
        URL url = OpenURL(urladdr);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        } finally {
            urlConnection.disconnect();
        }
        // Send some File to url.
        //TODO: Do it.
    }

    private URL OpenURL(String urlstr) {
        URL myURL = null;
        try {
            myURL = new URL(urlstr);
            HttpURLConnection urlConnection = (HttpURLConnection) myURL.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "Malformed URL.", Toast.LENGTH_LONG);
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Could not read/open connection.", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
        return myURL;
    }
}
