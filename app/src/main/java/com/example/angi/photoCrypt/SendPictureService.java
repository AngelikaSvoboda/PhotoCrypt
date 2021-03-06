package com.example.angi.photoCrypt;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Service zum Senden des Bildes an den angegeben Server.
 */
public class SendPictureService extends IntentService {

    public SendPictureService() {
        super("SendPictureService");
    }

    /**
     * Funktion die beim Start durch den Intent aufgerufen wird. es wird zunächst das Bitmap aus dem
     * Pfad und danach ein Socket für die Verbindung zum Server erzeugt. Das Bitmap wird über einen
     * {@link ByteArrayOutputStream} in ein byte-Array konvertiert, um es dann in einen @{@link OutputStream}
     * zu schreiben.
     * @param intent Intent mit einem Bundle, in dem die Server-Ip und Port, der Bildpfad und die
     *               eingestellte TelefonId übergeben worden sind.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle b = intent.getExtras();
        String serverIp = b.getString("ip");
        int serverPort = b.getInt("port");
        String filePath = b.getString("filePath");
        String folder = Environment.getExternalStorageDirectory() + "/photoCrypt/temp/";
        Bitmap img = BitmapFactory.decodeFile(folder + "conv_" + filePath);
        String phoneId = b.getString("phoneId");

        try {
            Log.w("Socket","Connecting...");
            Socket client = null;
            try {
                InetAddress serverAdr = InetAddress.getByName(serverIp);
                client = new Socket(serverAdr, serverPort);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imgByte = stream.toByteArray();


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
}
