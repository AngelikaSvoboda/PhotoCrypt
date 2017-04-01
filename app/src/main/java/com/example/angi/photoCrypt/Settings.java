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

/**
 * Created by Angi on 27.03.2017.
 */

public class Settings extends AppCompatActivity{

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        sp = getSharedPreferences("settings", 0);
        editor = sp.edit();

        final EditText breite = (EditText) findViewById(R.id.editTextBreite);
        breite.setText(sp.getString("Breite", "0"));
        final EditText hoehe = (EditText) findViewById(R.id.editTextHoehe);
        hoehe.setText(sp.getString("Höhe", "0"));
        final EditText ip = (EditText) findViewById(R.id.editTextIp);
        ip.setText(sp.getString("IP", "0"));
        final EditText port = (EditText) findViewById(R.id.editTextPort);
        final EditText info = (EditText) findViewById(R.id.editTextId);
        info.setText(sp.getString("TelefonId", "0"));

        Button buttonDismiss = (Button) findViewById(R.id.buttonDismiss);
        Button buttonOk = (Button) findViewById(R.id.buttonOk);

        breite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int width = Integer.parseInt(breite.getText().toString());
                editor.putInt("Breite", width);
                //editor.commit();
            }
        });

        hoehe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int height = Integer.parseInt(hoehe.getText().toString());
                editor.putInt("Höhe", height);
                editor.commit();

            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.commit();
                startMainActivity();

            }
        });

        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity();
            }
        });


    }
    private void startMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
