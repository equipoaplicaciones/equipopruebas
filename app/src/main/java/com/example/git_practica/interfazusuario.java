package com.example.git_practica;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;
public class interfazusuario extends AppCompatActivity {

    private RecyclerView recyclerViewCitas;
    private CitasAdapter citasAdapter;
    private List<Cita> citasList = new ArrayList<>();
    private Button btnAgendarCita;
    private BroadcastReceiver citaReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfazusuario);

        recyclerViewCitas = findViewById(R.id.recyclerViewCitas);
        btnAgendarCita = findViewById(R.id.btnAgendarCita);

        recyclerViewCitas.setLayoutManager(new LinearLayoutManager(this));

        String mongodbUserId = getMongodbUserIdFromPreferences();
        if (mongodbUserId != null) {
            obtenerCitasUsuario(mongodbUserId);
        } else {
            Toast.makeText(this, "No se pudo obtener el ID de MongoDB", Toast.LENGTH_SHORT).show();
        }

        btnAgendarCita.setOnClickListener(v -> {
            Intent intent = new Intent(interfazusuario.this, AgendarCitaActivity.class);
            startActivity(intent);
        });

        // Registrar el BroadcastReceiver para actualizar la lista de citas
        citaReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String userId = getMongodbUserIdFromPreferences();
                if (userId != null) {
                    obtenerCitasUsuario(userId);  // Recargar las citas
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
        return sharedPreferences.getString("MONGO_ID", null);
    }

    private void obtenerCitasUsuario(String userId) {
        String url = "http://10.0.2.2:5001/api/usuario/" + userId + "/citas";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("citas")) {
                            JSONArray citasArray = response.getJSONArray("citas");
                            citasList.clear();
                            for (int i = 0; i < citasArray.length(); i++) {
                                JSONObject citaObject = citasArray.getJSONObject(i);
                                String nombre = citaObject.getString("nombre");
                                String fecha = citaObject.getString("fecha");
                                String hora = citaObject.getString("hora");
                                String descripcion = citaObject.getString("descripcion");
                                Cita cita = new Cita(nombre, fecha, hora, descripcion);
                                citasList.add(cita);
                            }
                            citasAdapter = new CitasAdapter(citasList);
                            recyclerViewCitas.setAdapter(citasAdapter);
                        } else {
                            Toast.makeText(this, "No se encontraron citas", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error al obtener las citas", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}


