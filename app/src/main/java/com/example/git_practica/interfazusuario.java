package com.example.git_practica;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class interfazusuario extends AppCompatActivity {

    private RecyclerView recyclerViewCitas;
    private CitasAdapter citasAdapter;
    private List<Cita> citasList = new ArrayList<>();
    private ImageButton btnAgendarCita; // Cambiado de Button a ImageButton
    private BroadcastReceiver citaReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfazusuario);

        recyclerViewCitas = findViewById(R.id.recyclerViewCitas);
        btnAgendarCita = findViewById(R.id.btnAgendarCita); // btnAgendarCita ahora es un ImageButton

        recyclerViewCitas.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar el adaptador con una lista vacía
        citasAdapter = new CitasAdapter(this, citasList);
        recyclerViewCitas.setAdapter(citasAdapter);

        String mongodbUserId = getMongodbUserIdFromPreferences();
        if (mongodbUserId != null) {
            obtenerCitasUsuario(mongodbUserId);
        } else {
            Toast.makeText(this, "No se pudo obtener el ID de MongoDB", Toast.LENGTH_SHORT).show();
        }

        // Configurar el clic del botón para agendar cita1
        btnAgendarCita.setOnClickListener(v -> {
            Intent intent = new Intent(interfazusuario.this, AgendarCitaActivity.class);
            startActivity(intent);
        });

        // Registrar el BroadcastReceiver para actualizar las citas cuando se agrega una nueva
        citaReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String userId = getMongodbUserIdFromPreferences();
                if (userId != null) {
                    obtenerCitasUsuario(userId);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(citaReceiver, new IntentFilter("CITA_AGREGADA"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar el BroadcastReceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(citaReceiver);
    }

    private String getMongodbUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        String id = sharedPreferences.getString("MONGO_ID", null);
        Log.d("interfazusuario", "ID obtenido de SharedPreferences: " + id);
        return id;
    }

    private void obtenerCitasUsuario(String userId) {
        //String url = "http://10.0.2.2:5001/api/usuario/" + userId + "/citas";
        String url = "http://192.168.100.110:5001/api/usuario/" + userId + "/citas";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("obtenerCitasUsuario", "Respuesta del servidor: " + response.toString());

                        if (response.has("citas")) {
                            JSONArray citasArray = response.getJSONArray("citas");
                            citasList.clear();
                            for (int i = 0; i < citasArray.length(); i++) {
                                JSONObject citaObject = citasArray.getJSONObject(i);
                                String motivo = citaObject.getString("motivo"); // Cambiado de 'nombre' a 'motivo'
                                String fecha = citaObject.getString("fecha");
                                String hora = citaObject.getString("hora");
                                String status = citaObject.optString("status", "Pendiente"); // Valor predeterminado

                                // Convertir fecha a formato adecuado
                                String fechaFormateada = formatDate(fecha);

                                // Crear la cita y agregarla a la lista (eliminada la descripción)
                                Cita cita = new Cita(motivo, fechaFormateada, hora, status); // Sin 'descripcion'
                                citasList.add(cita);
                            }

                            // Actualizar el adaptador con las nuevas citas
                            citasAdapter.actualizarCitas(citasList);
                        } else {
                            Log.d("obtenerCitasUsuario", "No se encontraron citas.");
                            Toast.makeText(this, "No tienes citas agendadas", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("obtenerCitasUsuario", "Error al procesar las citas: ", e);
                    }
                },
                error -> {
                    Log.e("obtenerCitasUsuario", "Error al obtener las citas: ", error);
                    Toast.makeText(this, "Error al obtener las citas, verifica tu conexión", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }


    // Método para formatear la fecha recibida
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = iso8601Format.parse(dateStr);
            SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return simpleFormat.format(date);
        } catch (ParseException e) {
            Log.e("formatDate", "Error al formatear la fecha", e);
            return dateStr; // Devolver la fecha original si ocurre un error
        }
    }
}



