package com.example.git_practica;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistorialCitasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CitasAdapter citasAdapter;
    private List<Map<String, String>> citasList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_citas);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        citasAdapter = new CitasAdapter(citasList);
        recyclerView.setAdapter(citasAdapter);

        obtenerHistorialCitas();
    }

    private void obtenerHistorialCitas() {

        VolleyHelper volleyHelper = VolleyHelper.getInstance(this);

        volleyHelper.obtenerHistorialCitas(new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    citasList.clear();  // Limpiar la lista antes de agregar los nuevos datos

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject cita = response.getJSONObject(i);

                        // Crear un mapa para cada cita
                        Map<String, String> citaData = new HashMap<>();
                        citaData.put("nombre", cita.getString("nombre"));
                        citaData.put("fecha", cita.getString("fecha"));
                        citaData.put("hora", cita.getString("hora"));
                        citaData.put("descripcion", cita.getString("descripcion"));

                        citasList.add(citaData);
                    }

                    citasAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }
}
