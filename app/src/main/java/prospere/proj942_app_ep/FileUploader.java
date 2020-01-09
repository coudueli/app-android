package prospere.proj942_app_ep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

/**
 * Classe dont le seul objectif est d'uploader le fichier qu'on lui passe dans son constructeur
 */
public class FileUploader implements Runnable {

    private TextView messageText;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    private Activity caller;
    private String urlToUploadTo = null;
    final String TAG = "FileUploader";

    private File fileToUpload = null;


    public FileUploader(File fileToUpload, String urlToUploadTo, Activity caller) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.fileToUpload = fileToUpload;
        this.urlToUploadTo = urlToUploadTo;
        this.caller = caller;
    }

    /**
     * Methode principale qui doit uploader le file présent dans sa description
     */
    private void uploadFile() {
        // Le code de réponse HTML
        int respCode = 0;
        DataInputStream response = null;
        HttpURLConnection connection = null;
        try {
            // On initialise une nouvelle URL et une nouvelle connexion
            URL url = new URL(urlToUploadTo);
            connection = (HttpURLConnection) url.openConnection();

            // On prépare la requête POST
            connection.setRequestMethod("POST");
            connection.setDoOutput(true); // On dit qu'on va écrire dans la connexion
            // Un "Content-Type: multipart/" doit avoir une delimitation entre les différentes parties
            // La délimitation est une chaine de caractères que l'on prend au hasard.
            String boundary = UUID.randomUUID().toString();
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // On prépare l'entrée de la connexion
            DataOutputStream request = new DataOutputStream(connection.getOutputStream());
            request.writeBytes("--" + boundary + "\r\n");
            request.writeBytes("Content-Disposition: form-data; name=\"monfichier\"; filename=\"" + fileToUpload.getName() + "\"\r\n\r\n");

            // Morceau de code lisant le File et l'écrivant dans le Stream de connexion
            FileInputStream fileInputStream = new FileInputStream(fileToUpload);
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                request.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // On termine l'envoie: Un retour a la ligne, et une délimitation finale
            request.writeBytes("\r\n");
            request.writeBytes("--" + boundary + "--\r\n");
            request.flush(); // Forcer l'écriture des choses restantes dans le stream
            respCode = connection.getResponseCode();
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
        }

        switch (respCode) {
            case 200:
                Log.i(TAG, "Got response 200");
                //all went ok - read response
                break;
            default:
                Log.e(TAG, "Got Something OTHER than 200");
                //do something sensible
        }
        StringWriter writer = new StringWriter();
        try {
            response = new DataInputStream(connection.getInputStream());
            IOUtils.copy(response, writer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.e(TAG, "Oups.");
            e.printStackTrace();
        }
        String theString = writer.toString();
        Log.i(TAG, theString);
        AlertDialog.Builder builder = new AlertDialog.Builder(caller);
        builder.setMessage(theString)
                .setTitle("Send Result")
                .show();
    } // End else block

    @Override
    public void run() {
        uploadFile();
    }
}
