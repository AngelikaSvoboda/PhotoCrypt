package com.example.angi.photoCrypt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String savedSettings = "settings";
    private String mCurrentPhotoPath;
    private final String storagePath = Environment.getExternalStorageDirectory()+"/photoCrypt";
    private String imageFileName; //Dateiname ohne Pfad und Dateiendung
    private Uri photoURI;
    ImageView mImageView;

    // Speichern des von der Kamera geschossenen Fotos
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "PNG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File storageFile = new File(storagePath);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageFile      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        imageFileName = image.getName();
        return image;
    }

    //static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    void startConvertToGrayscaleActivity() {
        Intent intent = new Intent(this, ConvertToGrayscale.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ordner für Bilder erstellen
        File dir = new File(Environment.getExternalStorageDirectory()+"/photoCrypt");
        File temp = new File(dir.getAbsolutePath()+"/temp");
        if(!dir.exists()) {
            Log.w("Ordner photoCrypt","Ordner wird erstellt");
            dir.mkdirs();
        }
        //Ordner für temporäre Graustufenbilder erstellen

        if(!temp.exists()) {
            Log.w("Ordner temp","Ordner wird erstellt");
            temp.mkdirs();
        }

        //Einstellungen (Server-IP, Bildgröße,..) abrufbar machen.
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        if(!settings.contains("Breite")) {
            //  Standard für die Dastellung im Optionsmenü festlegen
            editor.putInt("Breite", 150);
            editor.putInt("Höhe", 200);
            editor.putString("IP", "172.0.0.1");
            editor.putInt("Port", 8000);

            String s = Build.MODEL;
            Log.w("TelefonId", s);
            editor.putString("TelefonId", s);
            editor.commit();
        }


        //settings = PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fromGallery);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startConvertToGrayscaleActivity();

            }
        });

        FloatingActionButton newPhoto = (FloatingActionButton) findViewById(R.id.newPhoto);
        newPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.w("Error", "Fehler beim Erstellen der Bilddatei");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                /*photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);*/
                photoURI = Uri.fromFile(photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    /*void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }*/

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_TAKE_PHOTO){
            if(resultCode == RESULT_OK) {
                //galleryAddPic();
                //Intent intent = new Intent(this, CropPictureActivity.class);
                Intent intent = new Intent(this, ConvertToGrayscale.class);
                Bundle b = new Bundle();
                b.putString("imagePath", mCurrentPhotoPath);
                b.putString("fileName", imageFileName);
                Log.w("fileName", imageFileName);
                Log.w("imagePath", mCurrentPhotoPath);
                intent.putExtras(b);

                startActivity(intent);

            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
