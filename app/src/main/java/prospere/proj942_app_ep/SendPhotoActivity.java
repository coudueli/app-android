package prospere.proj942_app_ep;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class SendPhotoActivity extends AppCompatActivity {
    private ImageView imagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_photo);
        imagePreview = findViewById(R.id.imageView);
        // imagePreview.setImageURI(Uri.encode(getIntent().getExtras().getString("lastFile"))); // WHY DON'T YOU WORKKKKKKKKKKKKKKKKKKKK
        showPicture(getIntent().getExtras().getString("lastFile"));

        imagePreview.setImageURI(Uri.parse()); // WHY DON'T YOU WORKKKKKKKKKKKKKKKKKKKK
    }

    public void showPicture(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
    }
}
