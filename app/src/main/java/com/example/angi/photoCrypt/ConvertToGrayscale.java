package com.example.angi.photoCrypt;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketAddress;

public class ConvertToGrayscale extends AppCompatActivity {

    private String fileName, photoPath;
    private Uri originalPictureUri;
    private Bitmap originalPicture, scaledPicture;
    private int threshhold;

    /*public class SendPicture extends IntentService
    {
        public SendPicture(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            Bundle b = intent.getBundleExtra("info");
            String serverIp = b.getString("Ip");
            int serverPort = b.getInt("port");
            String filePath = b.getString("filePath");
            String phoneId = b.getString("phoneId");

            try {
                Log.w("Socket","Connecting...");
                Socket client = null;
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaledPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] imgByte = stream.toByteArray();

                    InetAddress serverAdr = InetAddress.getByName(serverIp);
                    client = new Socket(serverAdr, serverPort);
                    OutputStream output = client.getOutputStream();
                    output.write(imgByte);
                    output.flush();
                    //client.connect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.close();
            }
            catch (Exception e) {
                Log.e("Socket", "Fehler bei Socket");
                e.printStackTrace();
            }

        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        fileName = b.getString("fileName");
        photoPath = b.getString("imagePath");

        originalPicture = BitmapFactory.decodeFile(photoPath);
        originalPicture = toGrayscale(threshhold, 200, 150);

        threshhold = 128;
        scaledPicture = changeGrayscale(threshhold);
        System.out.println(photoPath);
        //Log.w("Pfad", photoPath);


        setContentView(R.layout.activity_convert_to_grayscale);
        SeekBar s = (SeekBar) findViewById(R.id.thresholdbar);
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Beim Ändern des Wertes der Seekbar(Threshhold) soll das Bild neu berechnet werden
                scaledPicture = changeGrayscale(i);
                ImageView view = (ImageView)findViewById(R.id.scalablePicture);
                view.setImageBitmap(scaledPicture);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button forwardButton = (Button) findViewById(R.id.buttonOk2);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // scaledPicture soll nun gespeichert werden, umgewandelt als JSON Datei und dann zum
                // Server übertragen werden

                File file = new File(Environment.getExternalStorageDirectory()+"/photoCrypt/temp", "conv_" + fileName);
                try {
                    //File finalPicture = File.createTempFile(fileName + "_conv", ".jpg", file);
                    OutputStream os = new FileOutputStream(file);
                    scaledPicture.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                    MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

                    /*int width = scaledPicture.getWidth(),  height = scaledPicture.getHeight();

                    for (int x=0; x<height; x++) {
                        for(int y=0; y<width; y++) {

                        }
                    }*/
                    // TCP Verbindung aufbauen und übertragen
                    sendPicture();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                /*
                    File finalPicture = File.createTempFile("temp", ".jpg", storageFinalPicture);
                */


            }
        });

        //ImageView oldPicture = (ImageView) findViewById(R.id.scalablePicture);

    }

    private void sendPicture() {
        SharedPreferences settings = getSharedPreferences("settings", 0);
        String serverIp = settings.getString("IP", "");
        int serverPort = settings.getInt("Port", 0);
        String phoneId = settings.getString("TelefonId", "phone");
        Intent sendIntent = new Intent(this, SendPictureService.class);
        Bundle b = new Bundle();
        b.putString("ip", serverIp);
        b.putInt("port", serverPort);
        b.putString("filePath", fileName);
        b.putString("phoneId", phoneId);
        sendIntent.putExtras(b);
        startService(sendIntent);
        /*try {
            Log.w("Socket","Connecting...");
            Socket client = null;
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imgByte = stream.toByteArray();

                InetAddress serverAdr = InetAddress.getByName(serverIp);
                client = new Socket(serverAdr, serverPort);
                OutputStream output = client.getOutputStream();
                output.write(imgByte);
                output.flush();
                //client.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }
            client.close();
        }
        catch (Exception e) {
            Log.e("Socket", "Fehler bei Socket");
            e.printStackTrace();
        }*/

    }

    //Bild runterskalieren, Graustufen erzeugen
    public Bitmap toGrayscale(int threshhold, int optHeight, int optWidth)
    {
        int width, height;
        height = originalPicture.getHeight();
        width = originalPicture.getWidth();

        Bitmap temp = Bitmap.createScaledBitmap(originalPicture,optWidth, optHeight, true);

        Bitmap bmpGrayscale = Bitmap.createBitmap(optWidth, optHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(temp, 0, 0, paint);
        originalPicture = bmpGrayscale;
        return bmpGrayscale;
    }


    public Bitmap changeGrayscale(int threshhold)
    {

        int height, width;
        height = originalPicture.getHeight();
        width = originalPicture.getWidth();

        scaledPicture = originalPicture.copy(Bitmap.Config.ARGB_8888, true);
        System.out.println(height + " " +width);
        Bitmap changedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int i=0; i<width; i++)
        {
            for(int j=0; j<height; j++)
            {
                // Grauwert abfragen: getPixel gibt negative Werte zurück, aus denen man RGB Werte
                // berechnen kann. Da bei Grauwerten alle Werte gleich sind, kann man hier bpsw. den
                // roten (also grauen) Anteil errechnen, der einem Wert 0-255 entspricht
                int pixel = Color.red(scaledPicture.getPixel(i,j));
                //System.out.print(pixel + " ");
                if(pixel <= threshhold)
                {
                    changedBitmap.setPixel(i,j, Color.BLACK);
                }
                else
                {
                    changedBitmap.setPixel(i, j, Color.WHITE);
                }
            }
        }

        return changedBitmap;
    }

    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        //Nachdem die Höhe und Breite für alle Views berechnet/festgelegt ist, kann
        // getWidth() bzw getHeight() aufgerufen werden, in onCreate aber noch nicht
        //originalPicture = toGrayscale(threshhold, 200, 150);
        //originalPicture = scaleImage.setPic((ImageView) findViewById(R.id.scalablePicture), photoPath);
        ImageView mimageView = (ImageView) findViewById(R.id.scalablePicture);
        mimageView.setImageBitmap(scaledPicture);
        Log.w("onWindowFocusChanged", "wurde aufgerufen, setPic() wurde aufgerufen");


    }

}
