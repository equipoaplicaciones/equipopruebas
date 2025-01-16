package com.example.git_practica;

import android.content.Intent;
import android.database.Cursor;
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
    private CitasAdapter2 citasAdapter;
    private ArrayList<Cita> citasList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_admin);

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        citasList = new ArrayList<>();
        citasAdapter = new CitasAdapter2(citasList); // Usar CitasAdapter2
        recyclerView.setAdapter(citasAdapter);

        // Cargar citas desde el servidor o desde SQLite
        fetchCitas();  // Cargar desde el servidor
        // fetchCitasDesdeSQLite(); // Descomenta esta línea si prefieres cargar desde SQLite

        // Configurar el botón de cerrar sesión
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(InterfazAdminActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Termina la actividad actual
        });
    }

    /**
     * Método para obtener las citas desde el servidor usando Volley.
     */
    private void fetchCitas() {
        String url = "http://192.168.100.110:5001/api/citas";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("citas")) {
                            JSONArray citasArray = response.getJSONArray("citas");

                            // Limpiar la lista antes de agregar nuevas citas
                            citasList.clear();

                            // Iterar sobre el array de citas y agregar a la lista
                            for (int i = 0; i < citasArray.length(); i++) {
                                JSONObject cita = citasArray.getJSONObject(i);

                                String id = cita.getString("_id");
                                String nombre = cita.getString("nombre");
                                String motivoCita = cita.getString("motivo");
                                String fecha = cita.getString("fecha");
                                String hora = cita.getString("hora");
                                String status = cita.getString("status");
                                String genero = cita.getString("genero");
                                int edad = cita.getInt("edad"); // Cambié a int directamente
                                String telefono = cita.getString("telefono");
                                String estadoCivil = cita.getString("estadoCivil");
                                String domicilio = cita.getString("domicilio");
                                String email = cita.getString("email");
                                String comentarios = cita.getString("comentarios");

                                // Crear el objeto Cita
                                Cita nuevaCita = new Cita(
                                        id, nombre, motivoCita, fecha, hora, status,
                                        genero, edad, telefono, estadoCivil, domicilio, email, comentarios
                                );

                                // Agregar la cita a la lista
                                citasList.add(nuevaCita);
                            }

                            // Notificar al adaptador que los datos han cambiado
                            citasAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("FetchCitas", "No se encontró el campo 'citas' en la respuesta.");
                        }

                    } catch (JSONException e) {
                        // Mostrar un error en caso de problemas con la respuesta JSON
                        Toast.makeText(InterfazAdminActivity.this, "Error procesando datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Manejar el error en la solicitud de la red
                    Toast.makeText(InterfazAdminActivity.this, "Error al obtener datos del servidor", Toast.LENGTH_SHORT).show();
                });

        // Crear la cola de solicitudes de Volley y añadir la solicitud
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    /**
     * Método para obtener las citas desde la base de datos SQLite.
     */
    private void fetchCitasDesdeSQLite() {
        SQLiteHelper dbHelper = new SQLiteHelper(this);
        Cursor cursor = dbHelper.obtenerCitas(); // Obtiene todas las citas

        citasList.clear(); // Limpiar la lista antes de agregar nuevos datos

        if (cursor.moveToFirst()) {
            do {
                // Obtener los valores de cada columna en la tabla SQLite
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String motivoCita = cursor.getString(cursor.getColumnIndexOrThrow("motivo"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String genero = cursor.getString(cursor.getColumnIndexOrThrow("genero"));
                int edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"));
                String telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"));
                String estadoCivil = cursor.getString(cursor.getColumnIndexOrThrow("estadoCivil"));
                String domicilio = cursor.getString(cursor.getColumnIndexOrThrow("domicilio"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String comentarios = cursor.getString(cursor.getColumnIndexOrThrow("comentarios"));

                // Crear una nueva instancia de Cita y agregarla a la lista
                Cita cita = new Cita(id, nombre, motivoCita, fecha, hora, status,
                        genero, edad, telefono, estadoCivil, domicilio, email, comentarios);
                citasList.add(cita);

                // Opcional: Log para ver los datos que se están cargando
                Log.d("SQLite", "Cita cargada: " + cita.getNombre());
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No hay citas guardadas.", Toast.LENGTH_SHORT).show();
        }

        cursor.close(); // Cerrar el cursor
        citasAdapter.notifyDataSetChanged(); // Notificar al adaptador para que actualice el RecyclerView
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


