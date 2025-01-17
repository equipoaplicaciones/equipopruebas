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

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123; // Código de solicitud para Google Sign-In
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auths
        auth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Obtén esto desde strings.xml
                .requestEmail()
                .build();





        // Botón para iniciar sesión con correo y contraseña
        Button btnLogin = findViewById(R.id.button7);
        EditText emailField = findViewById(R.id.editTextTextEmailAddress);  // Corregido: falta cerrar el paréntesis
        EditText passwordField = findViewById(R.id.editTextTextPassword);
        Button Registro = findViewById(R.id.btnRegistro);
        ImageButton mapa = findViewById(R.id.imageButton13);



        Button registro = findViewById(R.id.btnRegistro); // Declaración única
        registro.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
        });


        btnLogin.setOnClickListener(view -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            } else {
                iniciarSesion(email, password);
            }
        });



        // Otros botones de navegación
        configurarBotonesDeNavegacion();
    }







    // Método para iniciar sesión con correo y contraseña
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
                            }

                            user.getIdToken(true).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    String idToken = task1.getResult().getToken();
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

    // Guardar token en SharedPreferences
    private void guardarTokenEnSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FIREBASE_TOKEN", token);
        editor.apply();
        Log.d("MainActivity", "Token guardado en SharedPreferences: " + token);
    }

    private void guardarUsuarioEnMongoDB(FirebaseUser user) {
        String email = user.getEmail();
        String name = user.getDisplayName();
        String photoUrl = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;

        Log.d("MainActivity", "Guardando usuario: " + email + ", Nombre: " + name + ", Foto: " + photoUrl);
        obtenerIdMongoDb(email);
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
                        }
                    },
                    error -> {
                        Toast.makeText(MainActivity.this, "Error al obtener el ID de MongoDB.", Toast.LENGTH_SHORT).show();
                    }
            );

            Volley.newRequestQueue(this).add(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void guardarIdEnSharedPreferences(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MONGO_ID", id);
        editor.apply();
    }

    // Configuración de botones de navegación
    private void configurarBotonesDeNavegacion() {
        ImageButton imageButtonServicios = findViewById(R.id.imageButton12);
        imageButtonServicios.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServiciosActivity.class);
            startActivity(intent);
        });

        ImageButton imageButtonQuienesSomos = findViewById(R.id.imageButton15);
        imageButtonQuienesSomos.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuienesSomosActivity.class);
            startActivity(intent);
        });

        ImageButton imageButtonPrivacidad = findViewById(R.id.imageButton14);
        imageButtonPrivacidad.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacidadActivity.class);
            startActivity(intent);
        });

        ImageButton mapa = findViewById(R.id.imageButton13);
        mapa.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        });

    }
}

