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
 * Created by Angi on 27.03.2017.
 */

public class Settings extends AppCompatActivity{

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int tempHeight, tempWidth, tempPort;
    private String tempIp, tempPhoneId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                //startMainActivity();
            }
        });


    }
    private void startMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
