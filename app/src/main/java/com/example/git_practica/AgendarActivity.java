package com.example.git_practica;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
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
        etHora = findViewById(R.id.etHora);
        etDescripcion = findViewById(R.id.etDescripcion);
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

                        if (isHoraOcupada(fechaSeleccionada, formattedTime)) {
                            Toast.makeText(AgendarActivity.this, "Esta hora ya está ocupada.", Toast.LENGTH_SHORT).show();
                        } else {
                            etHora.setText(formattedTime);
                        }
                    },
                    hour, minute, true
            );

            // Mostrar el TimePickerDialog
            timePickerDialog.show();
        });

        // Manejo de clic en botón para agendar
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

            String url = "http://192.168.100.110:5000/api/citas"; // Reemplaza con tu URL correcta

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

            // Añadir la solicitud a la cola de solicitudes
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);
        });

        // Obtener citas ya agendadas
        obtenerCitasAgendadas();
    }

    private void obtenerCitasAgendadas() {
        String url = "http://192.168.100.110:5000/api/citas";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray citasArray = response.getJSONArray("citas");
                        // Procesar citas y obtener las horas ocupadas
                        obtenerHorasOcupadas(citasArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error procesando las citas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error al obtener las citas", Toast.LENGTH_SHORT).show();
                    Log.e("VolleyError", error.toString());
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void obtenerHorasOcupadas(JSONArray citasArray) {
        try {
            // Recorrer las citas obtenidas del servidor
            for (int i = 0; i < citasArray.length(); i++) {
                JSONObject cita = citasArray.getJSONObject(i);
                String fecha = cita.getString("fecha");
                String hora = cita.getString("hora");

                // Convertir la fecha y hora en un objeto Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date fechaHoraCita = sdf.parse(fecha + " " + hora);

                if (fechaHoraCita != null) {
                    // Almacenar la hora ocupada en la fecha correspondiente
                    if (!citasPorFecha.containsKey(fecha)) {
                        citasPorFecha.put(fecha, new ArrayList<>());
                    }
                    citasPorFecha.get(fecha).add(hora); // Agregar la hora ocupada a la lista
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
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

    private boolean isHoraOcupada(String fecha, String horaSeleccionada) {
        List<String> horasOcupadas = citasPorFecha.get(fecha);
        if (horasOcupadas != null) {
            return horasOcupadas.contains(horaSeleccionada);  // Devuelve true si la hora está ocupada
        }
        return false;
    }
}
