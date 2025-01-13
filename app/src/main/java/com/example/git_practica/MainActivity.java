package com.example.git_practica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        ImageButton imageButtonServicios = findViewById(R.id.imageButton12);
        imageButtonServicios.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServiciosActivity.class);
            startActivity(intent);
        });


        // Configurar el ImageButton para redirigir a AgendarActivity
        /*ImageButton imageButton = findViewById(R.id.imageButton12);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgendarActivity.class);
            startActivity(intent);
        });*/

        // Configurar el ImageButton para redirigir a MapsActivity
        ImageButton imageButton2 = findViewById(R.id.imageButton13);
        imageButton2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        ImageButton imageButtonPrivacidad = findViewById(R.id.imageButton14);
        imageButtonPrivacidad.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacidadActivity.class);
            startActivity(intent);
        });

        ImageButton imageButtonQuienesSomos = findViewById(R.id.imageButton15);
        imageButtonQuienesSomos.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuienesSomosActivity.class);
            startActivity(intent);
        });


        // CheckBox: Listener
        checkBoxRecordar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Acción al marcar/desmarcar el CheckBox
        });
    }

    private void iniciarSesion(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userEmail = user.getEmail();
                            obtenerIdMongoDb(userEmail);

                            if ("admin@dentista.com".equals(userEmail)) {
                                Intent adminIntent = new Intent(MainActivity.this, InterfazAdminActivity.class);
                                startActivity(adminIntent);
                                finish();
                            } else {
                                obtenerIdMongoDb(userEmail);
                            }

                            user.getIdToken(true).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    String idToken = task1.getResult().getToken();
                                    Log.d("MainActivity", "Token recuperado: " + idToken);
                                    guardarTokenEnSharedPreferences(idToken);
                                } else {
                                    Log.e("MainActivity", "Error al recuperar el token: " + task1.getException());
                                }
                            });
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarTokenEnSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FIREBASE_TOKEN", token);
        editor.apply();
        Log.d("MainActivity", "Token guardado en SharedPreferences: " + token);
    }

    private void guardarIdEnSharedPreferences(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MONGO_ID", id);
        editor.apply();
        Log.d("MainActivity", "ID guardado en SharedPreferences: " + id);
    }

    private void obtenerIdMongoDb(String email) {
        try {
            String encodedEmail = URLEncoder.encode(email, "UTF-8");
            //String url = "http://10.0.2.2:5001/api/usuario/mongodb/" + encodedEmail;
            String url = "http://192.168.100.110:5001/api/usuario/mongodb/" + encodedEmail;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            if (response.has("mongodbUserId")) {
                                String mongodbUserId = response.getString("mongodbUserId");
                                guardarIdEnSharedPreferences(mongodbUserId);

                                Intent intent = new Intent(MainActivity.this, interfazusuario.class);
                                intent.putExtra("MONGO_ID", mongodbUserId);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "No se encontró el ID del usuario en MongoDB.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(MainActivity.this, "Error al obtener el ID de MongoDB. Revisa tu conexión.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
            );

            Volley.newRequestQueue(this).add(request);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error al codificar el correo electrónico.", Toast.LENGTH_SHORT).show();
        }
    }
    //1.1
}


