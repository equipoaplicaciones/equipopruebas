package com.example.git_practica;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton; // Importar MaterialButton
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

        // Configurar el botón de cerrar sesión
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(InterfazAdminActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Termina la actividad actual
        });
    }

    private void fetchCitas() {
        String url = "http://192.168.100.110:5001/api/citas";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Verificar la respuesta completa
                        Log.d("FetchCitas", "Respuesta JSON: " + response.toString());

                        // Asegurarse de que la respuesta contenga el array "citas"
                        if (response.has("citas")) {
                            JSONArray citasArray = response.getJSONArray("citas");

                            for (int i = 0; i < citasArray.length(); i++) {
                                JSONObject cita = citasArray.getJSONObject(i);
                                String id = cita.getString("_id");
                                // Obtener los campos del JSON
                                String nombre = cita.getString("nombre");
                                String motivoCita = cita.getString("motivo");
                                String fecha = cita.getString("fecha");
                                String hora = cita.getString("hora");
                                String status = cita.getString("status");
                                String genero = cita.getString("genero");
                                String edadStr = cita.getString("edad");  // Edad como String
                                String telefono = cita.getString("telefono");
                                String estadoCivil = cita.getString("estadoCivil");
                                String domicilio = cita.getString("domicilio");
                                String email = cita.getString("email");
                                String comentarios = cita.getString("comentarios");

                                // Conversión de edad a int
                                int edad = Integer.parseInt(edadStr);

                                // Agregar logs para verificar los valores
                                Log.d("FetchCitas", "Cita recibida: " +
                                        "Nombre: " + nombre + ", Motivo: " + motivoCita + ", Fecha: " + fecha +
                                        ", Hora: " + hora + ", Status: " + status +
                                        ", Género: " + genero + ", Edad: " + edad +
                                        ", Teléfono: " + telefono + ", Estado Civil: " + estadoCivil +
                                        ", Domicilio: " + domicilio + ", Email: " + email +
                                        ", Comentarios: " + comentarios);

                                // Crear una nueva instancia de Cita con los datos del JSON
                                Cita nuevaCita = new Cita(id,
                                        nombre, motivoCita, fecha, hora, status, genero,
                                        edad, telefono, estadoCivil, domicilio, email, comentarios
                                );

                                // Agregar la cita a la lista
                                citasList.add(nuevaCita);
                            }

                            citasAdapter.notifyDataSetChanged(); // Notificar al adaptador para actualizar la vista
                        } else {
                            Log.e("FetchCitas", "No se encontró el campo 'citas' en la respuesta.");
                        }

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
