package com.example.git_practica;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacidadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacidad); // Aquí vinculamos el layout de la vista

        Button buttonCerrar = findViewById(R.id.buttonCerrar);
        buttonCerrar.setOnClickListener(v -> finish());
    }
}
