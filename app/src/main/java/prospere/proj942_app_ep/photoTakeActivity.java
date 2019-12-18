package prospere.proj942_app_ep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class    photoTakeActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private FloatingActionButton takePhotoBtn;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;

    public File lastFile;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    // Camera Stuff
    private Camera camera;
    private SurfaceView surfaceCamera;
    private Boolean isPreview;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    final String myTAG = "photoTakeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_take);

        mVisible = true;
        mContentView = findViewById(R.id.surfacePhoto); //surface preview de la photo
        takePhotoBtn = findViewById(R.id.takePhoto); //bouton photo
        surfaceCamera = (SurfaceView) mContentView;


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle();
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null,null,mPicture);
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        isPreview = false;
        InitializeCamera();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Nous prenons le contrôle de la camera
        if (camera == null)
            camera = Camera.open();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Si le mode preview est lancé alors nous le stoppons
        if (isPreview) {
            camera.stopPreview();
        }
        // Nous récupérons les paramètres de la caméra
        Camera.Parameters parameters = camera.getParameters();

        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

        Camera.Size previewSize = previewSizes.get(0);

        // Nous changeons la taille
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        // Nous appliquons nos nouveaux paramètres
        camera.setParameters(parameters);

        try {
            // Nous attachons notre prévisualisation de la caméra au holder de la
            // surface
            camera.setPreviewDisplay(surfaceCamera.getHolder());
        } catch (IOException e) {
        }

        // Avant de lancer la preview: Mettre la caméra en mode portrait
        camera.setDisplayOrientation(90);
        // Nous lançons la preview
        camera.startPreview();

        isPreview = true;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Nous arrêtons la camera et nous rendons la main
        if (camera != null) {
            camera.stopPreview();
            isPreview = false;
            camera.release();
        }
    }

    // Retour sur l'application
    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    // Mise en pause de l'application
    @Override
    public void onPause() {
        super.onPause();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public void InitializeCamera() {
        // Nous attachons nos retours du holder à notre activité
        surfaceCamera.getHolder().addCallback(this);
        // Nous spécifiions le type du holder en mode SURFACE_TYPE_PUSH_BUFFERS
        surfaceCamera.getHolder().setType(
                SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * Méthode qui prépare le File où sera mis l'image finale
     *
     * @return
     */
    private static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        // Quel nom donner à l'image, où l'enregistrer.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }

    /**
     * Classe (Camera.pictureCallback) qui est appellée quand la caméra viens de prendre
     * et d'enregistrer une photo
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        /**
         * Methode particulière de la classe qui est appellé quand la photo est prise et
         * enregistrée
         * @param data
         * @param camera
         */
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            // On prépare le File où sera mis l'image.
            File pictureFile = getOutputMediaFile();

            // On récupère la photo elle-même (en Bitmap)
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            // Rotate image 90 degree
            Bitmap bOutput;
            float degrees = 90;//rotation degree
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees);
            bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


            if (pictureFile == null) {
                Log.d(myTAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                // On enregistre l'image dans le File qu'on a fait plus tôt
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bOutput.compress(Bitmap.CompressFormat.PNG, 100,fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(myTAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(myTAG, "Error accessing file: " + e.getMessage());
            }
            lastFile = pictureFile;
            launchSendPhotoActivity();
        }
    };
    private void launchSendPhotoActivity(){
        Intent i = new Intent(photoTakeActivity.this, SendPhotoActivity.class);
        i.putExtra("lastFile",lastFile.getPath());
        startActivity(i);
        // On termine l'activité après avoir lancé la suivante.
        finish();
    }
}
