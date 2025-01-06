package com.example.git_practica;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class interfazusuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfazusuario);

        // Obtener el ID de MongoDB pasado en el Intent
        String mongodbUserId = getIntent().getStringExtra("MONGO_ID");

        if (mongodbUserId != null) {
            // Llamar al método para obtener las citas del usuario
            obtenerCitasDeUsuario(mongodbUserId);
        }
    }

    private void obtenerCitasDeUsuario(String mongodbUserId) {
        // URL del endpoint que devuelve las citas del usuario usando su MongoDB ID
        String url = "http://10.0.2.2:5001/api/usuario/" + mongodbUserId + "/citas";  // Corregido

        // Crear la solicitud GET usando Volley
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Procesar las citas recibidas
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cita = response.getJSONObject(i);
                                String nombre = cita.getString("nombre");
                                String fecha = cita.getString("fecha");
                                String descripcion = cita.getString("descripcion");

                                // Aquí puedes agregar las citas a un adaptador y luego mostrarlas en un RecyclerView
                                // Ejemplo de cómo agregar las citas a una lista:
                                // citasList.add(new Cita(nombre, fecha, descripcion));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Mostrar mensaje de error si no se obtienen las citas
                        Toast.makeText(interfazusuario.this, "Error al obtener las citas", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        );

        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(request);
    }
}
