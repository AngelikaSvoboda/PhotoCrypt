package com.example.angi.photoCrypt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.widget.ImageView;

/**
 * Klasse zum Skalieren von Bitmaps auf die Größe der Imageview. Die enthaltende Funktion @setPic()
 * wird von den Activities genutzt, die ein Bild in ihrer ImageView darstellen wollen. Da meist die
 * Pixelanzahl der Bitmaps höer ist als die ImageView darstellen kann
 */
public class scaleImage {
    /**
     * Methode zur Skalierung eines Bildes an die Größe einer ImageView. Zuerst werden die Wunschhöhe
     * und -breite der View entnommen und ein @{@link BitmapFactory}-Objekt erstellt. Dem werden der
     * Pfad zum Bild und der @scaleFactor, also die Division von Bildmaß und Wunschmaß, übergeben und
     * zu einem skalierten Bitmap decodiert. Das wird in die View gesetzt und am Ende zurückgegeben.
     * @param mImageView ImageView. in der das Bild angezeigt werden soll
     * @param mCurrentPhotoPath Pfad zum Bild
     * @return erstelltes Bitmap, welches in die View eingefügt wurde
     */
    static Bitmap setPic(ImageView mImageView, String mCurrentPhotoPath) {

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
        bmOptions.inMutable = true;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);

        return bitmap;
    }
}
