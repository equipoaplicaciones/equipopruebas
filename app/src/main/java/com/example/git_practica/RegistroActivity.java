package com.example.git_practica;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistroActivity extends AppCompatActivity {
    private EditText editNombre, editEmail, editContrasena;
    private Button btnRegistrar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar vistas
        editNombre = findViewById(R.id.editNombre);
        editEmail = findViewById(R.id.editEmail);
        editContrasena = findViewById(R.id.editContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar el botón de registro
        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = editNombre.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String contrasena = editContrasena.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                        // Enviar los datos al backend usando Volley
                        enviarDatosBackend(nombre, email, contrasena);
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void enviarDatosBackend(String nombre, String email, String contrasena) {
        // URL de tu backend
        String url = "http://10.0.2.2:5001/api/registro";  // URL correcta para el emulador
        //String url = "http://192.168.100.110:5001/api/registro";

        // Crear el objeto JSON con los datos del usuario
        JSONObject userObject = new JSONObject();
        try {
            userObject.put("nombre", nombre);
            userObject.put("email", email);
            userObject.put("contrasena", contrasena);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Crear la solicitud POST con Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, userObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta del servidor
                        try {
                            String mensaje = response.getString("mensaje");
                            Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error de la solicitud
                        Toast.makeText(RegistroActivity.this, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show();
                    }
                });

        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}

