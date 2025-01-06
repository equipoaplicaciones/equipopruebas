package com.example.git_practica;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Asegúrate de que este es el layout correcto

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Vistas de los campos de entrada y botón de inicio de sesión
        EditText editEmail = findViewById(R.id.editTextTextEmailAddress);
        EditText editPassword = findViewById(R.id.editTextTextPassword);
        Button btnIniciarSesion = findViewById(R.id.button7);
        CheckBox checkBoxRecordar = findViewById(R.id.checkBox);

        // Configurar el botón de inicio de sesión
        btnIniciarSesion.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            // Validar los campos
            if (!email.isEmpty() && !password.isEmpty()) {
                iniciarSesion(email, password);
            } else {
                Toast.makeText(MainActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para redirigir a la actividad de registro
        Button btnRegistro = findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(view -> {
            // Crear un Intent para abrir la actividad de registro
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Agregar la funcionalidad para recordar la contraseña si es necesario
        checkBoxRecordar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Aquí podrías agregar lógica para guardar el estado de "Recordar Contraseña" en SharedPreferences si es necesario
        });
    }

    private void iniciarSesion(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso
                        Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                        // Obtener el usuario de Firebase
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Obtener el email del usuario de Firebase
                            String userEmail = user.getEmail();
                            // Llamar al método para obtener el ID de MongoDB
                            obtenerIdMongoDb(userEmail);
                        }
                    } else {
                        // Error al iniciar sesión
                        Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void obtenerIdMongoDb(String email) {
        // URL del endpoint de tu backend que devuelve el ID de MongoDB usando el correo electrónico
        String url = "http://tu-backend.com/api/usuario/mongodb/" + email;

        // Crear la solicitud GET usando Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener el ID de MongoDB desde la respuesta
                            String mongodbUserId = response.getString("mongodbUserId");

                            // Guardar el ID de MongoDB en algún lugar (puedes usar SharedPreferences)
                            // Aquí redirigimos al usuario a la actividad de interfazusuario pasando el ID
                            Intent intent = new Intent(MainActivity.this, interfazusuario.class);
                            intent.putExtra("MONGO_ID", mongodbUserId);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error al obtener el ID de MongoDB", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    // En caso de error
                    Toast.makeText(MainActivity.this, "Error al obtener el ID de MongoDB", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(request);
    }
}
