package com.example.git_practica;
import com.android.volley.Response;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
// Asegúrate de que esta importación esté presente
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class AgendarCitaActivity extends AppCompatActivity {

    private EditText editTextNombre, editTextFecha, editTextHora, editTextDescripcion;
    private Button btnGuardarCita;
    private byte[] pdfData; // Variable global para almacenar los datos del PDF descargado

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

            // Crear el objeto JSON con los detalles de la cita
            JSONObject cita = new JSONObject();
            try {
                cita.put("nombre", nombre);
                cita.put("fecha", fecha);
                cita.put("hora", hora);
                cita.put("descripcion", descripcion);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al crear los datos de la cita", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2:5001/api/citas/" + userId; // Incluir userId en la URL
            //String url = "http://192.168.100.110:5001/api/citas/" + userId;

            // Crear una solicitud JSON con el encabezado de autorización
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, cita,
                    response -> {
                        Toast.makeText(AgendarCitaActivity.this, "Cita agendada exitosamente", Toast.LENGTH_SHORT).show();

                        // Imprimir la respuesta para depuración
                        Log.d("AgendarCitaActivity", "Respuesta de la API: " + response.toString());

                        // Acceder al objeto 'cita' y obtener el '_id'
                        try {
                            JSONObject citaObject = response.getJSONObject("cita");
                            String citaId = citaObject.getString("_id"); // Obtener _id de la respuesta

                            // Llamar al método para descargar el PDF
                            downloadPdf(citaId, token);
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

    // Método para descargar el PDF
    private void downloadPdf(String citaId, String token) {
        String pdfUrl = "http://10.0.2.2:5001/api/citas/" + citaId + "/descargar"; // URL para descargar el PDF con citaId

        // Realizar una solicitud para descargar el archivo PDF
        Request<byte[]> downloadRequest = new Request<byte[]>(Request.Method.GET, pdfUrl,
                error -> {
                    Toast.makeText(AgendarCitaActivity.this, "Error al descargar el PDF", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected com.android.volley.Response<byte[]> parseNetworkResponse(NetworkResponse response) {
                // Extraemos los datos binarios de la respuesta
                byte[] data = response.data;

                // Retornamos la respuesta exitosa con los datos binarios y los encabezados de la caché
                return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(byte[] response) {
                // Guardamos los datos PDF en pdfData
                pdfData = response;

                // Llama al método para pedir al usuario dónde guardar el archivo PDF
                pickFileToSavePdf();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // Ejecutar la solicitud de descarga
        Volley.newRequestQueue(this).add(downloadRequest);
    }


    // Método para pedir al usuario dónde guardar el archivo PDF
    private void pickFileToSavePdf() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "cita.pdf"); // Define el nombre del archivo
        startActivityForResult(intent, 1); // Esto abrirá el selector de archivos
    }

    // onActivityResult donde se obtiene la URI y se guarda el archivo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData(); // Obtener la URI donde el usuario quiere guardar el archivo

            if (uri != null) {
                // Verificar que pdfData no sea null ni vacío
                if (pdfData == null || pdfData.length == 0) {
                    Toast.makeText(AgendarCitaActivity.this, "El archivo PDF está vacío o no se pudo descargar.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Escribir el archivo en la ubicación seleccionada
                try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                    if (outputStream != null) {
                        outputStream.write(pdfData); // Escribir el contenido del PDF
                        Toast.makeText(this, "PDF guardado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No se pudo abrir la ubicación para guardar el archivo", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al guardar el PDF", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "URI no válida", Toast.LENGTH_SHORT).show();
            }
        }
    }


}




