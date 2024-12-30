package com.example.git_practica;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        EditText emailField = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordField = findViewById(R.id.editTextTextPassword);
        Button loginButton = findViewById(R.id.button7);
        CheckBox rememberMeCheckBox = findViewById(R.id.checkBox);

        // Configurar el listener para el botón de inicio de sesión
        loginButton.setOnClickListener(view -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                signIn(email, password);
            } else {
                Toast.makeText(MainActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el ImageButton para abrir MapsActivity
        ImageButton imageButton = findViewById(R.id.imageButton13);
        imageButton.setOnClickListener(view -> {
            // Crear un Intent para abrir MapsActivity
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });
    }

    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                        // Redirigir al usuario a la pantalla principal
                    } else {
                        // Intentar registrar al usuario si no está registrado
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, registrationTask -> {
                                    if (registrationTask.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Cuenta creada exitosamente.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Error: " + registrationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }
}
