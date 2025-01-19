package com.example.git_practica;

import com.android.volley.Response;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AgendarCitaActivity extends AppCompatActivity {

    private EditText editTextNombre, editTextMotivo, editTextFecha, editTextHora, editTextGenero,
            editTextEdad, editTextTelefono, editTextEstadoCivil, editTextDomicilio,
            editTextEmail, editTextComentarios;
    private Button btnGuardarCita;
    private byte[] pdfData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        // Inicialización de campos EditText
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextMotivo = findViewById(R.id.editTextMotivoCita);
        editTextFecha = findViewById(R.id.editTextFecha);
        editTextHora = findViewById(R.id.editTextHora);
        editTextGenero = findViewById(R.id.editTextGenero);
        editTextEdad = findViewById(R.id.editTextEdad);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextEstadoCivil = findViewById(R.id.editTextEstadoCivil);
        editTextDomicilio = findViewById(R.id.editTextDomicilio);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextComentarios = findViewById(R.id.editTextComentarios);
        btnGuardarCita = findViewById(R.id.btnGuardarCita);

        // Agregar DatePicker al campo de fecha
        editTextFecha.setOnClickListener(v -> showDatePickerDialog());

        // Agregar TimePicker al campo de hora
        editTextHora.setOnClickListener(v -> showTimePickerDialog());

        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("MONGO_ID", null);
        String token = sharedPreferences.getString("FIREBASE_TOKEN", null);

        if (userId == null || token == null) {
            Toast.makeText(this, "No se pudo obtener el ID del usuario o el token", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardarCita.setOnClickListener(v -> {
            String nombre = editTextNombre.getText().toString().trim();
            String motivo = editTextMotivo.getText().toString().trim();
            String fecha = editTextFecha.getText().toString().trim();
            String hora = editTextHora.getText().toString().trim();
            String genero = editTextGenero.getText().toString().trim();
            String edad = editTextEdad.getText().toString().trim();
            String telefono = editTextTelefono.getText().toString().trim();
            String estadoCivil = editTextEstadoCivil.getText().toString().trim();
            String domicilio = editTextDomicilio.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String comentarios = editTextComentarios.getText().toString().trim();

            if (nombre.isEmpty() || motivo.isEmpty() || fecha.isEmpty() || hora.isEmpty() || genero.isEmpty() ||
                    edad.isEmpty() || telefono.isEmpty() || estadoCivil.isEmpty() || domicilio.isEmpty() ||
                    email.isEmpty() || comentarios.isEmpty()) {
                Toast.makeText(AgendarCitaActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el objeto JSON con los detalles de la cita
            JSONObject cita = new JSONObject();
            try {
                cita.put("nombre", nombre);
                cita.put("motivo", motivo);
                cita.put("fecha", fecha);
                cita.put("hora", hora);
                cita.put("genero", genero);
                cita.put("edad", edad);
                cita.put("telefono", telefono);
                cita.put("estadoCivil", estadoCivil);
                cita.put("domicilio", domicilio);
                cita.put("email", email);
                cita.put("comentarios", comentarios);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al crear los datos de la cita", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://192.168.100.110:5001/api/citas/" + userId;

            // Crear una solicitud JSON con el encabezado de autorización
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, cita,
                    response -> {
                        Toast.makeText(AgendarCitaActivity.this, "Cita agendada exitosamente", Toast.LENGTH_SHORT).show();
                        Log.d("AgendarCitaActivity", "Respuesta de la API: " + response.toString());
                        try {
                            JSONObject citaObject = response.getJSONObject("cita");
                            String citaId = citaObject.getString("_id");
                            downloadPdf(citaId, token);

                            // Regresar a la actividad InterfazUsuario
                            Intent intent = new Intent(AgendarCitaActivity.this, interfazusuario.class);
                            startActivity(intent);
                            finish(); // Finalizar la actividad actual para que no esté en la pila de actividades
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AgendarCitaActivity.this, "Error al obtener el ID de la cita", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(AgendarCitaActivity.this, "Error al agendar la cita", Toast.LENGTH_SHORT).show();
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });

    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    editTextFecha.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                    editTextHora.setText(selectedTime);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void downloadPdf(String citaId, String token) {
        //String pdfUrl = "http://10.0.2.2:5001/api/citas/" + citaId + "/descargar";
        String pdfUrl = "http://192.168.100.110:5001/api/citas/" + citaId + "/descargar";

        Request<byte[]> downloadRequest = new Request<byte[]>(Request.Method.GET, pdfUrl,
                error -> {
                    Toast.makeText(AgendarCitaActivity.this, "Error al descargar el PDF", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected com.android.volley.Response<byte[]> parseNetworkResponse(NetworkResponse response) {
                byte[] data = response.data;
                return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(byte[] response) {
                pdfData = response;
                pickFileToSavePdf();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(downloadRequest);
    }

    private void pickFileToSavePdf() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "cita.pdf");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            if (uri != null && pdfData != null && pdfData.length > 0) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                    if (outputStream != null) {
                        outputStream.write(pdfData);
                        Toast.makeText(this, "PDF guardado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No se pudo abrir la ubicación para guardar el archivo", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al guardar el PDF", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "El archivo PDF está vacío o no se pudo descargar.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
