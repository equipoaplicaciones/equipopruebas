package com.example.git_practica;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetallesCitaActivity extends AppCompatActivity {

    private TextView detalleNombre, detalleMotivo, detalleFecha, detalleHora, detalleGenero,
            detalleEdad, detalleTelefono, detalleEstadoCivil, detalleDomicilio, detalleEmail, detalleComentarios;
    private Button btnCancelarCita, btnPosponerCita, btnConfirmarPosponer, btnAceptarCita;
    private View dateTimeLayout; // Este layout debe existir en tu archivo XML
    private DatePicker datePicker; // Este debe estar en tu archivo XML
    private TimePicker timePicker; // Este también debe estar en tu archivo XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_cita);

        detalleNombre = findViewById(R.id.detalleNombre);
        detalleMotivo = findViewById(R.id.detalleMotivo);
        detalleFecha = findViewById(R.id.detalleFecha);
        detalleHora = findViewById(R.id.detalleHora);
        detalleGenero = findViewById(R.id.detalleGenero);
        detalleEdad = findViewById(R.id.detalleEdad);
        detalleTelefono = findViewById(R.id.detalleTelefono);
        detalleEstadoCivil = findViewById(R.id.detalleEstadoCivil);
        detalleDomicilio = findViewById(R.id.detalleDomicilio);
        detalleEmail = findViewById(R.id.detalleEmail);
        detalleComentarios = findViewById(R.id.detalleComentarios);

        btnCancelarCita = findViewById(R.id.btnCancelarCita);
        btnPosponerCita = findViewById(R.id.btnPosponerCita);
        btnConfirmarPosponer = findViewById(R.id.btnConfirmarPosponer);
        btnAceptarCita = findViewById(R.id.btnAceptarCita);

        // Referencias a los componentes para la selección de fecha y hora
        dateTimeLayout = findViewById(R.id.dateTimeLayout); // Asegúrate de que este layout exista en tu XML
        datePicker = findViewById(R.id.datePicker); // Asegúrate de que este DatePicker exista en tu XML
        timePicker = findViewById(R.id.timePicker); // Asegúrate de que este TimePicker exista en tu XML

        if (getIntent() != null) {
            String citaId = getIntent().getStringExtra("citaId");
            String nombre = getIntent().getStringExtra("nombre");
            String motivo = getIntent().getStringExtra("motivo");
            String fecha = getIntent().getStringExtra("fecha");
            String hora = getIntent().getStringExtra("hora");
            String genero = getIntent().getStringExtra("genero");
            String edad = getIntent().getStringExtra("edad");
            String telefono = getIntent().getStringExtra("telefono");
            String estadoCivil = getIntent().getStringExtra("estadoCivil");
            String domicilio = getIntent().getStringExtra("domicilio");
            String email = getIntent().getStringExtra("email");
            String comentarios = getIntent().getStringExtra("comentarios");

            Log.d("DetallesCitaActivity", "citaId: " + citaId);
            Log.d("DetallesCitaActivity", "nombre: " + nombre);
            Log.d("DetallesCitaActivity", "motivo: " + motivo);
            Log.d("DetallesCitaActivity", "fecha: " + fecha);
            Log.d("DetallesCitaActivity", "hora: " + hora);
            Log.d("DetallesCitaActivity", "genero: " + genero);
            Log.d("DetallesCitaActivity", "edad: " + edad);
            Log.d("DetallesCitaActivity", "telefono: " + telefono);
            Log.d("DetallesCitaActivity", "estadoCivil: " + estadoCivil);
            Log.d("DetallesCitaActivity", "domicilio: " + domicilio);
            Log.d("DetallesCitaActivity", "email: " + email);
            Log.d("DetallesCitaActivity", "comentarios: " + comentarios);

            detalleNombre.setText("Nombre: " + nombre);
            detalleMotivo.setText("Motivo: " + motivo);
            detalleFecha.setText("Fecha: " + fecha);
            detalleHora.setText("Hora: " + hora);
            detalleGenero.setText("Género: " + genero);
            detalleEdad.setText("Edad: " + edad);
            detalleTelefono.setText("Teléfono: " + telefono);
            detalleEstadoCivil.setText("Estado Civil: " + estadoCivil);
            detalleDomicilio.setText("Domicilio: " + domicilio);
            detalleEmail.setText("Email: " + email);
            detalleComentarios.setText("Comentarios: " + comentarios);
        }

        btnCancelarCita.setOnClickListener(v -> cancelarCita());
        btnPosponerCita.setOnClickListener(v -> {
            dateTimeLayout.setVisibility(View.VISIBLE);
            btnPosponerCita.setVisibility(View.GONE);
        });

        btnConfirmarPosponer.setOnClickListener(v -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);

            Date nuevaFechaHora = calendar.getTime();
            posponerCita(nuevaFechaHora);

            dateTimeLayout.setVisibility(View.GONE);
            btnConfirmarPosponer.setVisibility(View.GONE);
            btnPosponerCita.setVisibility(View.VISIBLE);
        });

        btnAceptarCita.setOnClickListener(v -> aceptarCita());
    }

    private void cancelarCita() {
        String citaId = getIntent().getStringExtra("citaId");

        if (citaId == null || citaId.isEmpty()) {
            Toast.makeText(this, "ID de la cita no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.100.110:5001/api/citas/" + citaId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(this, "Cita cancelada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetallesCitaActivity.this, InterfazAdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(this, "Error al cancelar la cita", Toast.LENGTH_SHORT).show());

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void posponerCita(Date nuevaFechaHora) {
        String citaId = getIntent().getStringExtra("citaId");

        if (citaId == null || citaId.isEmpty()) {
            Toast.makeText(this, "ID de la cita no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String nuevaFechaString = sdf.format(nuevaFechaHora);

        String nuevaFechaCita = nuevaFechaString.split(" ")[0];
        String nuevaHoraCita = nuevaFechaString.split(" ")[1];

        detalleFecha.setText("Fecha: " + nuevaFechaCita);
        detalleHora.setText("Hora: " + nuevaHoraCita);

        try {
            JSONObject citaActualizada = new JSONObject();
            citaActualizada.put("fecha", nuevaFechaCita);
            citaActualizada.put("hora", nuevaHoraCita);

            String url = "http://192.168.100.110:5001/api/citas/" + citaId;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, citaActualizada,
                    response -> {
                        Toast.makeText(this, "Cita pospuesta exitosamente", Toast.LENGTH_SHORT).show();

                        // Redirigir a InterfazAdminActivity
                        Intent intent = new Intent(DetallesCitaActivity.this, InterfazAdminActivity.class);
                        startActivity(intent);
                        finish();  // Terminar la actividad actual
                    },
                    error -> Toast.makeText(this, "Error al posponer la cita", Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear el JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void aceptarCita() {
        String citaId = getIntent().getStringExtra("citaId");

        if (citaId == null || citaId.isEmpty()) {
            Toast.makeText(this, "ID de la cita no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject citaAceptada = new JSONObject();
        try {
            citaAceptada.put("nuevoEstado", "Aceptada");
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al preparar los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.100.110:5001/api/citas/" + citaId + "/estado";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, citaAceptada,
                response -> {
                    Toast.makeText(this, "Cita aceptada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetallesCitaActivity.this, InterfazAdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(this, "Error al aceptar la cita", Toast.LENGTH_SHORT).show());

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
