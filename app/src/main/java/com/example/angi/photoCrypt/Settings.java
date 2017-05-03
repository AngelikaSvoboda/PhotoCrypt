package com.example.angi.photoCrypt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Diese Klasse stellt das Einstellungsmenü dar, wo der Nutzer die Größe der zu verschlüsselten Bilder
 * und die IP-Adresse und Port des Servers einstellen kann.
 */

public class Settings extends AppCompatActivity{

    /** Objekt zum Speichern von App-Einstellungen wie Bildgröße und Serverdaten **/
    private SharedPreferences sp;
    /** Objekt mit dem @sp editiert und überschrieben werden kann **/
    private SharedPreferences.Editor editor;
    /** Temporäre Variable für die Höhe. Sie wird am Ende vom @editor übernommen, wenn
     * der Nutzer die Änderungen übernimmt**/
    private int tempHeight,
    /** Temporäre Variable für die Breite**/
            tempWidth,
    /** Temporäre Variable für den Port der Servers**/
            tempPort;
    /** Temporäre Variable für die IP-Adresse des Servers**/
    private String tempIp,
    /** Temporäre Variable für die ID des Telefons**/
            tempPhoneId;

    /**
     * Die Funktion setzt die Werte in die EditText-Views ein, die in dem @sp-Objekt gespeichert sind.
     * Dabei setzt er auch die temporären Variablen auf den gleichen Wert. Jede EditText-View
     * erhält nun einen TextWatcher, wo auf die Eingabe des Nutzers reagiert wird und in den temporären
     * Variablen abgespeichert wird. Ist die EditText-View leer, wird für int-Variablen 0 als Wert
     * gesetzt, da "" als String interpretiert wird. Drückt der Nutzer nun Übernehmen, wird zunächst
     * gesprüft, ob eins der Felder leer gelassen oder 0 ist. Dann erscheint eine Meldung als Toast-
     * Objekt und weißt darauf hin, das die Eingaben unvollständig sind. Nur bei korrekten Eingaben
     * wird im @editor-Objekt dann die Änderungen übernommen. Drückt der Nutzer abbrechen, wird die
     * Activity einfach beendet ohne die temporären Variablen zu übernehmen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        sp = getSharedPreferences("settings", 0);
        editor = sp.edit();

        final EditText breite = (EditText) findViewById(R.id.editTextBreite);
        tempWidth = sp.getInt("Breite", 0);
        breite.setText(String.valueOf(tempWidth));

        final EditText hoehe = (EditText) findViewById(R.id.editTextHoehe);
        tempHeight = sp.getInt("Höhe", 0);
        hoehe.setText(String.valueOf(tempHeight));

        final EditText ip = (EditText) findViewById(R.id.editTextIp);
        tempIp = sp.getString("IP", "0");
        ip.setText(tempIp);

        final EditText port = (EditText) findViewById(R.id.editTextPort);
        tempPort = sp.getInt("Port", 0);
        port.setText(String.valueOf(tempPort));

        final EditText info = (EditText) findViewById(R.id.editTextId);
        tempPhoneId = sp.getString("TelefonId", "0");
        info.setText(tempPhoneId);



        Button buttonDismiss = (Button) findViewById(R.id.buttonDismiss);
        Button buttonOk = (Button) findViewById(R.id.buttonOk);

        breite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String s = breite.getText().toString();
                if(!s.isEmpty())
                {
                    int width = Integer.parseInt(s);
                    tempWidth = width;
                    //editor.putInt("Breite", width);
                }
                else
                {
                    tempWidth = 0;
                    //editor.putInt("Breite", 0);
                }

                //editor.commit();
            }
        });

        hoehe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String s = hoehe.getText().toString();
                if(!s.isEmpty())
                {
                    int height = Integer.parseInt(s);
                    tempHeight = height;
                    //editor.putInt("Höhe", height);
                    //editor.commit();
                }
                else
                {
                    tempHeight = 0;
                    //editor.putInt("Höhe", 0);
                }

            }
        });

        ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                tempIp = ip.getText().toString();
            }
        });

        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String s = port.getText().toString();
                if(!s.isEmpty())
                {
                    tempPort = Integer.parseInt(s);
                }
                else
                {
                    tempPort = 0;
                }
            }
        });

        info.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                tempPhoneId = info.getText().toString();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tempHeight <= 0 || tempWidth <= 0 || tempPort <= 0) {
                    Toast.makeText(getApplicationContext(), "Feld darf nicht 0 sein", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Änderungen übernehmen, abspeichern
                    editor.putInt("Höhe", tempHeight);
                    editor.putInt("Breite", tempWidth);
                    editor.putInt("Port", tempPort);
                    editor.putString("IP", tempIp);
                    editor.putString("TelefonId", tempPhoneId);

                    editor.commit();
                    //startMainActivity();
                    finish();
                }
            }
        });

        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
