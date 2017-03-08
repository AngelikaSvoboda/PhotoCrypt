package com.example.angi.photoCrypt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class ConvertToGrayscale extends AppCompatActivity {

    private String fileName, photoPath;
    private Uri originalPictureUri;
    private Bitmap originalPicture, scaledPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle b = new Bundle();
        fileName = b.getString("fileName");
        photoPath = b.getString("imagePath");


        setContentView(R.layout.activity_convert_to_grayscale);
        /*originalPicture = BitmapFactory.decodeFile(photoPath);

        ImageView mimageView = (ImageView) findViewById(R.id.scalablePicture);

        mimageView.setImageBitmap(originalPicture);*/
        ImageView oldPicture = (ImageView) findViewById(R.id.cropImageView);

    }

    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        //Nachdem die Höhe und Breite für alle Views berechnet/festgelegt ist, kann
        // getWidth() bzw getHeight() aufgerufen werden, in onCreate aber noch nicht
        scaleImage.setPic((ImageView) findViewById(R.id.scalablePicture), photoPath);
        Log.w("onWindowFocusChanged", "wurde aufgerufen, setPic() wurde aufgerufen");
    }
}
