package com.example.angi.photoCrypt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CropPictureActivity extends AppCompatActivity {

    private ImageView mImageView;
    private String mCurrentPhotoPath;

    private void setPic() {

        mImageView = (ImageView) findViewById(R.id.cropImageView);

        if(mImageView == null) {
            System.err.print("FEHLER");
            Log.e("imageview", "fehler");
        }
        else {
            // Get the dimensions of the View
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();
            Log.w("size of imageview", ""+targetH + " " +targetW);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            //bmOptions.inPurgeable = true;
            Uri imageUri = Uri.parse(mCurrentPhotoPath);

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            mImageView.setImageBitmap(bitmap);
        }
    }

    void callPictureScalerActivity() {
        Intent intent = new Intent(this, selectPictureFrame.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        mCurrentPhotoPath = i.getStringExtra("imagePath");
        Log.w("Fotospeicherort", mCurrentPhotoPath);

        setContentView(R.layout.activity_crop_picture);
        //setPic();
        Button backButton = (Button) findViewById(R.id.buttonBackCrop);
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button buttonOk = (Button) findViewById(R.id.buttonOkCrop);
        buttonOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                callPictureScalerActivity();

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        //Nachdem die Höhe und Breite für alle Views berechnet/festgelegt ist, kann
        // getWidth() bzw getHeight() aufgerufen werden, in onCreate aber noch nicht
        setPic();
    }
}
