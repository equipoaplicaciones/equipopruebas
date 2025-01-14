package com.example.git_practica;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class InterfazAdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CitasAdapter2 citasAdapter; // Cambiar a CitasAdapter2
    private ArrayList<Cita> citasList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_admin);

        recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        citasList = new ArrayList<>();
        citasAdapter = new CitasAdapter2(citasList); // Usar CitasAdapter2
        recyclerView.setAdapter(citasAdapter);

        fetchCitas();
    }

    private void fetchCitas() {
        String url = "http://192.168.100.110:5001/api/citas";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray citasArray = response.getJSONArray("citas");

                        for (int i = 0; i < citasArray.length(); i++) {
                            JSONObject cita = citasArray.getJSONObject(i);

                            String id = cita.getString("_id"); // Asegúrate de que "_id" coincide con el campo en tu API
                            String nombre = cita.getString("nombre");
                            String fecha = cita.getString("fecha");
                            String hora = cita.getString("hora");
                            String descripcion = cita.getString("descripcion");

                            // Agregar la cita con su ID
                            citasList.add(new Cita(id, nombre, fecha, hora, descripcion));
                        }

                        citasAdapter.notifyDataSetChanged(); // Notificar al adaptador para actualizar la vista

                    } catch (JSONException e) {
                        Toast.makeText(InterfazAdminActivity.this, "Error procesando datos del servidor", Toast.LENGTH_SHORT).show();
                        Log.e("FetchCitas", "Detalles del error: " + e.getMessage(), e);
                    }
                },
                error -> {
                    Toast.makeText(InterfazAdminActivity.this, "Error al obtener datos del servidor", Toast.LENGTH_SHORT).show();
                    Log.e("FetchCitas", "Error al obtener datos: " + error.getMessage(), error);
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) { // Verifica el código de solicitud y el resultado
            // Recarga las citas desde el servidor al regresar
            citasList.clear(); // Limpia la lista actual
            fetchCitas();      // Vuelve a cargar las citas desde el servidor
        }
    }
}


