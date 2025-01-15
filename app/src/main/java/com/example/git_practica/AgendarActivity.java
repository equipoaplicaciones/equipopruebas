package com.example.git_practica;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class AgendarActivity extends AppCompatActivity {
    private EditText etNombre, etFecha, etHora, etDescripcion;
    private Button btnAgendarCita;
    private String fechaSeleccionada = "";

    private Map<String, List<String>> citasPorFecha = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar);

        etNombre = findViewById(R.id.etNombre);
        etFecha = findViewById(R.id.etFecha);
        etDescripcion = findViewById(R.id.etDescripcion);
        etHora = findViewById(R.id.etHora);
        btnAgendarCita = findViewById(R.id.btnAgendarCita);

        etFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AgendarActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        etFecha.setText(formattedDate);
                        fechaSeleccionada = formattedDate;
                    },
                    year, month, day
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            bloquearFechas(datePickerDialog);
            datePickerDialog.show();
        });

        etHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AgendarActivity.this,
                    (view, selectedHour, selectedMinute) -> {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

                        // Validar que la hora esté dentro del rango permitido
                        if (selectedHour < 8 || selectedHour > 20) {
                            Toast.makeText(AgendarActivity.this, "Por favor selecciona una hora entre 8:00 AM y 8:00 PM.", Toast.LENGTH_SHORT).show();
                        } else if (isHoraOcupada(fechaSeleccionada, formattedTime)) {
                            Toast.makeText(AgendarActivity.this, "Esta hora ya está ocupada.", Toast.LENGTH_SHORT).show();
                        } else {
                            etHora.setText(formattedTime);
                        }
                    },
                    hour, minute, true
            );

            timePickerDialog.show();
        });

        btnAgendarCita.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString();
            String fecha = etFecha.getText().toString();
            String hora = etHora.getText().toString();
            String descripcion = etDescripcion.getText().toString();

            if (nombre.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                Toast.makeText(AgendarActivity.this, "Nombre, fecha y hora son obligatorios.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date parsedDate = sdf.parse(fecha);
                if (parsedDate != null) {
                    fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate); // Formato ISO (YYYY-MM-DD)
                }
            } catch (ParseException e) {
                Toast.makeText(AgendarActivity.this, "Error al convertir la fecha. Asegúrate de usar el formato correcto (DD/MM/YYYY).", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            JSONObject nuevaCita = new JSONObject();
            try {
                nuevaCita.put("nombre", nombre);
                nuevaCita.put("fecha", fecha);
                nuevaCita.put("hora", hora);
                nuevaCita.put("descripcion", descripcion);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(AgendarActivity.this, "Error al crear la cita.", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2:5001/api/citas";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    nuevaCita,
                    response -> Toast.makeText(AgendarActivity.this, "Cita agendada con éxito.", Toast.LENGTH_SHORT).show(),
                    error -> {
                        Toast.makeText(AgendarActivity.this, "Error al agendar la cita.", Toast.LENGTH_SHORT).show();
                        Log.e("VolleyError", error.toString());
                    }
            );

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);
        });

        Button btnHistorial = findViewById(R.id.btnVerHistorial);
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgendarActivity.this, HistorialCitasActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isHoraOcupada(String fecha, String horaSeleccionada) {
        List<String> horasOcupadas = citasPorFecha.get(fecha);
        if (horasOcupadas != null) {
            return horasOcupadas.contains(horaSeleccionada);  // Devuelve true si la hora está ocupada
        }
        return false;
    }

    private void bloquearFechas(DatePickerDialog datePickerDialog) {
        datePickerDialog.getDatePicker().setOnDateChangedListener((view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);

            // Verificar si esa fecha tiene horas ocupadas
            List<String> horasOcupadas = citasPorFecha.get(fechaSeleccionada);

            if (horasOcupadas != null) {
                // Deshabilitar las horas ocupadas
                bloquearHorasOcupadas(horasOcupadas);
            }
        });
    }

    private void bloquearHorasOcupadas(List<String> horasOcupadas) {
        etHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Crear el TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AgendarActivity.this,
                    (view, selectedHour, selectedMinute) -> {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

                        // Verificar si la hora seleccionada está ocupada
                        if (horasOcupadas.contains(formattedTime)) {
                            Toast.makeText(AgendarActivity.this, "Esta hora ya está ocupada.", Toast.LENGTH_SHORT).show();
                        } else {
                            etHora.setText(formattedTime);
                        }
                    },
                    hour, minute, true
            );

            for (String horaOcupada : horasOcupadas) {
                int horaOcupadaInt = Integer.parseInt(horaOcupada.split(":")[0]);
                timePickerDialog.updateTime(horaOcupadaInt + 1, 0); // Bloqueando la hora ocupada
            }

            timePickerDialog.show();
        });
    }
}
