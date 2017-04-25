package com.example.angi.photoCrypt;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Die Haupt-Activity, die beim Start der App als erstes angezeigt wird. Hier hat der Nutzer Zugriff
 * auf die Navigation-View, über die auf die App-Einstellungen gelangt wird. Um nun ein Bild zu verschlüsseln,
 * kann der Nutzer über die Navigations-View mit der Option "Neues Foto" oder den schwebenden Kamera-Button
 * die Kamera starten und ein neues Foto zu schießen. Andererseits kann der Nutzer über die Option
 * "aus Galerie" oder den Galerie-Button auf die Galerie zugreifen, um ein bestehendes Bild zu verschlüsseln.
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /** Konstanten zum Unterscheiden der ausgeführten Aufgaben **/
    /**  Fotoaufnahme **/
    static final int REQUEST_PICK_IMAGE = 1;
    /** Auswahl aus Galerie **/
    static final int REQUEST_TAKE_PHOTO = 2;
    /**
     * Foto wird an die Activity ConvertToGrayscale weitergeleitet, wo das Schwarzweißbild erstellt
     * und dann an den Server gesendet wird
     **/
    static final int REQUEST_PHOTO_SEND = 3;
    /** Gesamter Pfad des Fotos **/
    private String photoPath;
    /** Verzeichnis zum Speichern aller aufgenommenen und bearbeiteten Fotos **/
    private final String storagePath = Environment.getExternalStorageDirectory()+"/photoCrypt";
    /** Dateiname ohne Pfad und Dateiendung**/
    private String imageFileName;
    //private Uri photoURI;

    /**
     * Speichern des von der Kamera geschossenen Fotos. Es wird ein neues File erzeugt und erhält als
     * Dateinamen einen timeStamp. Der absolute Pfad des Fotos wird in der Klassenvariable @photoPath
     * und der Name der Bilddatei in @imageFileName gespeichert.
     * @return Bild als File-Objekt
     * @throws IOException Fehler beim Erstellen des Files
     */
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
        photoPath = image.getAbsolutePath();
        imageFileName = image.getName();
        return image;
    }

    /**
     * Erzeugt einen Intent, der die Activity zum Skalieren der Graustufen startet. Dem Intent wird
     * hier der Pfad zum Bild sowie der Dateiname übergeben, mit dem die Verschlüsselung erfolgen soll.
     */
    private void startConvertToGrayscaleActivity() {
        Intent intent = new Intent(this, ConvertToGrayscale.class);
        Bundle b = new Bundle();
        b.putString("imagePath", photoPath);
        b.putString("fileName", imageFileName);
        Log.w("fileName", imageFileName);
        Log.w("imagePath", photoPath);
        intent.putExtras(b);

        startActivityForResult(intent, REQUEST_PHOTO_SEND);
    }

    /**
     * Die erste Funktion die bei Start der App aufgerufen wird. Ist es der erste Start der App, werden
     * die benötigten Ordner erstellt und Standardeinstellungen im @{@link SharedPreferences} -Objekt
     * gespeichert. Danach wird die Funktionalität der Buttons, Toolbar und Menü hinzugefügt.
     * @param savedInstanceState Bundle das beim Aufruf der Funktion übergeben wurde, was zusätzliche
     *                           Variablen oder Objekte enthalten kann. Für die Mainactivity ist dieses leer.
     */
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

        // Bei erstem Start der App Standardeinstellungen festlegen
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

                galleryChooserIntent();

            }
        });

        FloatingActionButton newPhoto = (FloatingActionButton) findViewById(R.id.newPhoto);
        newPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takePictureIntent();
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

    /**
     * Funktion zum Start der Kamera des Smartphones. Erst wird eine Datei mit Timestamp im Dateinamen
     * erstellt und bei Erfolg wird die Kamera über einen Intent gestartet. Die URI des erstellten Files
     * wird mit putExtra dem Intent angegeben.
     */
    private void takePictureIntent() {
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
                Uri photoURI = Uri.fromFile(photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Funktion zum Auswählen eines Bildes aus der Galerie des Handys. Die Funktion wird nur durch Auswahl
     * des Menüpunkts "Aus Galerie" oder über den unteren rechten Button im Hauptfenster aufgerufen.
     * Dabei erstellt sie wie beim
     */
    private void galleryChooserIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
    }
/*
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
*/

    /**
     * Diese Funktion wird automatisch aufgerufen, wenn @startActivityForResult beendet wird. Hier
     * wird nun unterschieden, welche Aktion nun getätigt wurde und wie die Daten weiterverarbeitet werden.
     *
     * @param requestCode @REQUEST_TAKE_PHOTO falls das Foto von der Kamera aufgenommen wurde. @REQUEST_PICK_IMAGE
     *                    falls ein Bild aus der Gallerie ausgewählt wurde
     * @param resultCode Wenn die Aktion vorher nicht abgebrochen wurde, wird @RESULT_OK zurückgegeben, @RESULT_CANCELLED sonst
     * @param data übergebene Daten der Aktion. Wenn ein Bild aus der Gallerie gewählt wurde, enthält @data die Uri des Bildes
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        // Nutzer hat selbst ein Foto geschossen
        if(requestCode == REQUEST_TAKE_PHOTO){
            if(resultCode == RESULT_OK) {
                //galleryAddPic();
                //Intent intent = new Intent(this, CropPictureActivity.class);
                startConvertToGrayscaleActivity();
            }
        }
        // Nutzer sucht Foto aus Galerie aus
        else if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String s = selectedImageUri.getPath();

            // Ermittlung des Pfads aus der Uri des Bildes
            String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(selectedImageUri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = { MediaStore.Images.Media.DATA };
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{ id }, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            Log.w("Pfad", filePath);
            cursor.close();

            // Dateinamen ohne Pfad und Endung speichern
            int begin = filePath.lastIndexOf("/"); // ab da fängt der Dateiname an
            String split = filePath.substring(begin+1, filePath.length()-4); // .jpg und .png abgetrennt
            Log.w("Datei", split);

            photoPath = filePath;
            imageFileName = split;
            startConvertToGrayscaleActivity();

        }
        // Bild wurde bearbeitet und an den Server geschickt.
        else if(requestCode == REQUEST_PHOTO_SEND && resultCode == RESULT_OK) {
            Toast toast = Toast.makeText(getApplicationContext(), "Bild wird gesendet", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Handler für die Zurück-Taste des Handys. Schließt das Menü wenn dieses offen ist. Ansonsten
     * schließt es die App, wenn das Hauptfenster offen ist
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Erstellt den Menüreiter.
     * @param menu das Menü
     * @return true wenn das Menü erstellt wird
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Die Funktion wird aufgerufen, wenn der Button @action_settings gedrückt wurde und eine Option
     * gewählt wird. Hier steht die Option "Einstellung" zur Verfügung, die, wenn gedrückt, die @{@link Settings}
     * startet.
     * @param item ID der gewählten Option
     * @return true nachdem das Einstellungsmenü geschlossen wird.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Starte Einstellungsfenster
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Die Funktion verarbeitet, welcher Menüpunkt in der Navigation-View gewählt wurde und startet
     * dementsprechend die passende Aktion. Über die Navigation-View können auf die Aktionen Bild aufnehmen
     * oder aus Gallerie auswählen sowie die Einstellungen zugegriffen werden.
     * @param item Ausgewählter Punkt im Menü
     * @return true sobald die Navigation-View geschlossen wird
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Starte Kamera
            takePictureIntent();
        } else if (id == R.id.nav_gallery) {
            // Suche Bild aus Gallerie aus
            galleryChooserIntent();
        } else if (id == R.id.nav_manage) {
            // Activity für Einstellungen starten
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
