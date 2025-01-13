package com.example.git_practica;

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
    private CitasAdapter citasAdapter;
    private ArrayList<Cita> citasList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_admin);

        recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        citasList = new ArrayList<>();
        citasAdapter = new CitasAdapter(citasList);
        recyclerView.setAdapter(citasAdapter);

        fetchCitas();
    }

    private void fetchCitas() {
        String url = "http://192.168.100.110:5001/api/citas";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtén el arreglo de citas del JSON
                            JSONArray citasArray = response.getJSONArray("citas");

                            // Itera sobre las citas
                            for (int i = 0; i < citasArray.length(); i++) {
                                JSONObject cita = citasArray.getJSONObject(i);
                                String nombre = cita.getString("nombre");
                                String fecha = cita.getString("fecha");
                                String hora = cita.getString("hora");
                                String descripcion = cita.getString("descripcion");

                                citasList.add(new Cita(nombre, fecha, hora, descripcion));
                            }

                            citasAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            // Error procesando datos del JSON
                            Toast.makeText(InterfazAdminActivity.this, "Error procesando datos del servidor", Toast.LENGTH_SHORT).show();
                            Log.e("FetchCitas", "Detalles del error: " + e.getMessage(), e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error en la solicitud
                        Toast.makeText(InterfazAdminActivity.this, "Error al obtener datos del servidor", Toast.LENGTH_SHORT).show();
                        Log.e("FetchCitas", "Error al obtener datos: " + error.getMessage(), error);
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
}


