package com.example.git_practica;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendarActivity extends AppCompatActivity {
    private EditText etNombre, etFecha, etHora, etDescripcion;
    private Button btnAgendarCita, btnVerHistorial;
    private String fechaSeleccionada = "";

    private Map<String, List<String>> citasPorFecha = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar);

        // Inicialización de vistas
        etNombre = findViewById(R.id.etNombre);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etDescripcion = findViewById(R.id.etDescripcion);

        btnAgendarCita = findViewById(R.id.btnAgendarCita);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);

        // Listener para el campo de fecha
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
            datePickerDialog.show();
        });

        // Listener para el campo de hora
        etHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AgendarActivity.this,
                    (view, selectedHour, selectedMinute) -> {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

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

            // Crear una nueva cita
            Cita nuevaCita = new Cita(nombre, fecha, hora, descripcion);

            // Usar Retrofit para agendar la cita
            RetrofitHelper.getInstance(this).getApiService().agendarCita(nuevaCita).enqueue(new Callback<Cita>() {
                @Override
                public void onResponse(Call<Cita> call, Response<Cita> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AgendarActivity.this, "Cita agendada con éxito.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AgendarActivity.this, "Error al agendar la cita.", Toast.LENGTH_SHORT).show();
                        Log.e("RetrofitError", "Error al agendar cita: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Cita> call, Throwable t) {
                    Toast.makeText(AgendarActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                    Log.e("RetrofitError", "Fallo la conexión: " + t.getMessage());
                }
            });
        });

        // Listener para ver historial
        btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(AgendarActivity.this, HistorialCitasActivity.class);
            startActivity(intent);
        });

        // Obtener citas previamente agendadas
        obtenerCitasAgendadas();
    }

    private void obtenerCitasAgendadas() {
        RetrofitHelper.getInstance(this).getApiService().obtenerHistorialCitas().enqueue(new Callback<List<Cita>>() {
            @Override
            public void onResponse(Call<List<Cita>> call, Response<List<Cita>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cita> citas = response.body();
                    for (Cita cita : citas) {
                        String fecha = cita.getFecha();
                        String hora = cita.getHora();

                        citasPorFecha.putIfAbsent(fecha, new ArrayList<>());
                        citasPorFecha.get(fecha).add(hora);
                    }
                } else {
                    Toast.makeText(AgendarActivity.this, "Error al obtener el historial de citas.", Toast.LENGTH_SHORT).show();
                    Log.e("RetrofitError", "Error al obtener citas: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Cita>> call, Throwable t) {
                Toast.makeText(AgendarActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                Log.e("RetrofitError", "Fallo la conexión: " + t.getMessage());
            }
        });
    }

    private boolean isHoraOcupada(String fecha, String horaSeleccionada) {
        List<String> horasOcupadas = citasPorFecha.get(fecha);
        return horasOcupadas != null && horasOcupadas.contains(horaSeleccionada);
    }
}
