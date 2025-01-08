package com.example.git_practica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AgendarCitaActivity extends AppCompatActivity {

    private EditText editTextNombre, editTextFecha, editTextHora, editTextDescripcion;
    private Button btnGuardarCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextFecha = findViewById(R.id.editTextFecha);
        editTextHora = findViewById(R.id.editTextHora);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        btnGuardarCita = findViewById(R.id.btnGuardarCita);

        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("MONGO_ID", null);
        String token = sharedPreferences.getString("FIREBASE_TOKEN", null);

        if (userId == null || token == null) {
            Toast.makeText(this, "No se pudo obtener el ID del usuario o el token", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardarCita.setOnClickListener(v -> {
            String nombre = editTextNombre.getText().toString().trim();
            String fecha = editTextFecha.getText().toString().trim();
            String hora = editTextHora.getText().toString().trim();
            String descripcion = editTextDescripcion.getText().toString().trim();

            if (nombre.isEmpty() || fecha.isEmpty() || hora.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(AgendarCitaActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject cita = new JSONObject();
            try {
                cita.put("usuarioId", userId);
                cita.put("nombre", nombre);
                cita.put("fecha", fecha);
                cita.put("hora", hora);
                cita.put("descripcion", descripcion);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al crear los datos de la cita", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2:5001/api/citas";

            // Crear una solicitud JSON con el encabezado de autorización
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, cita,
                    response -> {
                        Toast.makeText(AgendarCitaActivity.this, "Cita agendada exitosamente", Toast.LENGTH_SHORT).show();
                        // Enviar el broadcast para notificar que la cita se ha agregado
                        Intent intent = new Intent("CITA_AGREGADA");
                        LocalBroadcastManager.getInstance(AgendarCitaActivity.this).sendBroadcast(intent);
                        finish();
                    },
                    error -> {
                        Toast.makeText(AgendarCitaActivity.this, "Error al agendar la cita", Toast.LENGTH_SHORT).show();
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    // Agregar el token en los encabezados de la solicitud
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            // Realizar la solicitud
            Volley.newRequestQueue(this).add(request);
        });
    }
}


