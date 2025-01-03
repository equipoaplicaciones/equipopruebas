package com.example.git_practica;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AgendarActivity extends AppCompatActivity {
    private EditText etNombre, etFecha, etDescripcion;
    private Button btnAgendarCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar);

        etNombre = findViewById(R.id.etNombre);
        etFecha = findViewById(R.id.etFecha);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnAgendarCita = findViewById(R.id.btnAgendarCita);

        // Configuración del DatePickerDialog
        etFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AgendarActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Formato de la fecha seleccionada
                        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        etFecha.setText(formattedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        btnAgendarCita.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString();
            String fecha = etFecha.getText().toString();
            String descripcion = etDescripcion.getText().toString();

            // Validar que la fecha no esté vacía
            if (nombre.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(AgendarActivity.this, "Nombre y fecha son obligatorios.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convertir la fecha al formato ISO
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date parsedDate = sdf.parse(fecha);
                if (parsedDate != null) {
                    // Usamos el formato ISO para la fecha
                    fecha = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(parsedDate);
                }
            } catch (ParseException e) {
                Toast.makeText(AgendarActivity.this, "Error al convertir la fecha. Asegúrate de usar el formato correcto (DD/MM/YYYY).", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            // Enviar la cita al servidor
            VolleyHelper.getInstance(getApplicationContext()).agendarCita(nombre, fecha, descripcion,
                    response -> {
                        Log.d("Volley", "Respuesta del servidor: " + response.toString());

                        try {
                            String mensaje = response.getString("mensaje");
                            Toast.makeText(AgendarActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("Volley", "Error al procesar la respuesta", e);
                        }
                    },
                    error -> {
                        String errorMessage = "Error en la conexión con el servidor";

                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            errorMessage = "Código de error HTTP: " + statusCode;
                        } else if (error.getCause() != null) {
                            errorMessage = "Error: " + error.getCause().getMessage();
                        }

                        Toast.makeText(AgendarActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                        Log.e("VolleyError", "Error al conectar con el servidor", error);
                        error.printStackTrace();
                    });
        });
    }
}
