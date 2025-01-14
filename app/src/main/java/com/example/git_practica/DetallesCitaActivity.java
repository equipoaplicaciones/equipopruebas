package com.example.git_practica;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class DetallesCitaActivity extends AppCompatActivity {

    private TextView detalleNombre, detalleFecha, detalleHora, detalleDescripcion;
    private Button btnCancelarCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_cita);

        detalleNombre = findViewById(R.id.detalleNombre);
        detalleFecha = findViewById(R.id.detalleFecha);
        detalleHora = findViewById(R.id.detalleHora);
        detalleDescripcion = findViewById(R.id.detalleDescripcion);
        btnCancelarCita = findViewById(R.id.btnCancelarCita);

        // Recibir datos desde el Intent
        if (getIntent() != null) {
            String citaId = getIntent().getStringExtra("citaId");
            String nombre = getIntent().getStringExtra("nombre");
            String fecha = getIntent().getStringExtra("fecha");
            String hora = getIntent().getStringExtra("hora");
            String descripcion = getIntent().getStringExtra("descripcion");

            if (citaId != null) {
                Toast.makeText(this, "Cita ID: " + citaId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cita ID no recibido", Toast.LENGTH_SHORT).show();
            }

            // Mostrar los datos
            detalleNombre.setText("Nombre: " + nombre);
            detalleFecha.setText("Fecha: " + fecha);
            detalleHora.setText("Hora: " + hora);
            detalleDescripcion.setText("Descripción: " + descripcion);
        }

        // Configurar el botón para cancelar la cita
        btnCancelarCita.setOnClickListener(v -> cancelarCita());
    }

    private void cancelarCita() {
        // Obtener el ID de la cita
        String citaId = getIntent().getStringExtra("citaId");

        if (citaId == null || citaId.isEmpty()) {
            Toast.makeText(this, "ID de la cita no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.100.110:5001/api/citas/" + citaId;  // Reemplaza con la IP adecuada

        // Hacer la solicitud DELETE
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(DetallesCitaActivity.this, "Cita cancelada exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    Toast.makeText(DetallesCitaActivity.this, "Error al cancelar la cita", Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
