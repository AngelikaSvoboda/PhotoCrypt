package com.example.angi.photoCrypt;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class CropPictureActivity extends AppCompatActivity {

    private ImageView mImageView;
    private String mCurrentPhotoPath;
    private String imageFileName;
    public static final int PIC_CROP = 10;
    private Uri photoUri;

    private void setPic() {

        mImageView = (ImageView) findViewById(R.id.cropImageView);

        if(mImageView == null) {
            System.err.print("FEHLER");
            Log.e("imageview", "Nicht gefunden");
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
        Intent intent = new Intent(this, ConvertToGrayscale.class);
        Bundle b = new Bundle();
        b.putString("imagePath", mCurrentPhotoPath);
        b.putString("fileName", imageFileName);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        //mCurrentPhotoPath = i.getStringExtra("imagePath");
        //imageFileName = i.getStringExtra("fileName");
        mCurrentPhotoPath = b.getString("imagePath");
        imageFileName = b.getString("fileName");

        //photoUri = i.getData();
        Log.w("Fotospeicherort", mCurrentPhotoPath);
        Log.w("Dateiname", imageFileName);

        setContentView(R.layout.activity_crop_picture);
        //setPic();
        cropPicture();
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

    private void cropPicture() {

        try {
            Log.w("CropPicture", "Rufe CropPicture() auf");
            File file = File.createTempFile(imageFileName, ".jpg", Environment.getExternalStorageDirectory());
            Uri picUri = Uri.fromFile(file);
            //Uri picUri2 = Uri.parse(mCurrentPhotoPath);
            //Uri picUri = picture;

            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setData(picUri);
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 3);
            cropIntent.putExtra("aspectY", 4);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //respond to users whose devices do not support the crop action
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            Log.w("Crop", "Crop existiert nicht.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PIC_CROP) {
            if(data != null) {
                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                //retrieve a reference to the ImageView
                ImageView picView = (ImageView) findViewById(R.id.cropImageView);
                //display the returned cropped image
                picView.setImageBitmap(thePic);
            }
            else
            {
                /*Bitmap b = BitmapFactory.decodeFile(mCurrentPhotoPath);
                ImageView imageView = (ImageView) findViewById(R.id.cropImageView);
                imageView.setImageBitmap(b);*/
                //setPic();
                scaleImage.setPic((ImageView)findViewById(R.id.cropImageView), mCurrentPhotoPath);
            }
        }
    }

    /*@Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        //Nachdem die Höhe und Breite für alle Views berechnet/festgelegt ist, kann
        // getWidth() bzw getHeight() aufgerufen werden, in onCreate aber noch nicht
        scaleImage.setPic((ImageView)findViewById(R.id.cropImageView), mCurrentPhotoPath);
        Log.w("onWindowFocusChanged", "wurde aufgerufen, setPic() wurde aufgerufen");
    }*/
}
