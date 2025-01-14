package com.example.git_practica;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.widget.DatePicker;
import android.widget.TimePicker;

public class DetallesCitaActivity extends AppCompatActivity {

    private TextView detalleNombre, detalleFecha, detalleHora, detalleDescripcion;
    private Button btnCancelarCita, btnPosponerCita, btnConfirmarPosponer;
    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_cita);

        detalleNombre = findViewById(R.id.detalleNombre);
        detalleFecha = findViewById(R.id.detalleFecha);
        detalleHora = findViewById(R.id.detalleHora);
        detalleDescripcion = findViewById(R.id.detalleDescripcion);
        btnCancelarCita = findViewById(R.id.btnCancelarCita);
        btnPosponerCita = findViewById(R.id.btnPosponerCita);
        btnConfirmarPosponer = findViewById(R.id.btnConfirmarPosponer);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);

        LinearLayout dateTimeLayout = findViewById(R.id.dateTimeLayout);

        // Recibir datos desde el Intent
        if (getIntent() != null) {
            String citaId = getIntent().getStringExtra("citaId");
            String nombre = getIntent().getStringExtra("nombre");
            String fecha = getIntent().getStringExtra("fecha");
            String hora = getIntent().getStringExtra("hora");
            String descripcion = getIntent().getStringExtra("descripcion");

            detalleNombre.setText("Nombre: " + nombre);
            detalleFecha.setText("Fecha: " + fecha);
            detalleHora.setText("Hora: " + hora);
            detalleDescripcion.setText("Descripción: " + descripcion);
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
                    response -> Toast.makeText(this, "Cita pospuesta exitosamente", Toast.LENGTH_SHORT).show(),
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
}