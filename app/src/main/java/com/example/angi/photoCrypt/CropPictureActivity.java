package com.example.angi.photoCrypt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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

        mImageView = (ImageView) findViewById(R.id.mImageView);

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
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
        //setPic();
        setContentView(R.layout.activity_crop_picture);

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
}
