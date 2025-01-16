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
import java.util.List;

public class HistorialCitasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CitasAdapter2 citasAdapter2;  // Usar CitasAdapter2
    private List<Cita> citasList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_citas);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Usar CitasAdapter2 en lugar de CitasAdapter
        citasAdapter2 = new CitasAdapter2(citasList);
        recyclerView.setAdapter(citasAdapter2);

        obtenerHistorialCitas();
    }

    private void obtenerHistorialCitas() {
        VolleyHelper volleyHelper = VolleyHelper.getInstance(this);

        volleyHelper.obtenerHistorialCitas(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    citasList.clear();  // Limpiar la lista antes de agregar los nuevos datos

                    // Obtener el JSONArray dentro de la clave "citas"
                    JSONArray citasArray = response.getJSONArray("citas");

                    // Convertir cada objeto JSON a una instancia de Cita
                    for (int i = 0; i < citasArray.length(); i++) {
                        JSONObject cita = citasArray.getJSONObject(i);

                        // Crear una nueva instancia de Cita con los datos del JSON
                        Cita nuevaCita = new Cita(
                                cita.getString("motivoCita"),
                                cita.getString("fecha"),
                                cita.getString("hora")
                        );

                        citasList.add(nuevaCita);  // Agregar la cita a la lista
                    }

                    citasAdapter2.notifyDataSetChanged();  // Notificar al adaptador que los datos han cambiado
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
