package com.example.git_practica;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VolleyHelper {
    private static VolleyHelper instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private VolleyHelper(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyHelper getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyHelper(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public void agendarCita(String nombre, Date fecha, String hora, String descripcion,
                            com.android.volley.Response.Listener<JSONObject> listener,
                            com.android.volley.Response.ErrorListener errorListener) {

        String url = "http://10.0.2.2:5001/api/citas";
        //String url = "http://192.168.100.110:5001/api/citas";

        try {
            // Convertir la fecha a un formato ISO 8601
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            String fechaFormateada = sdf.format(fecha);

            // Crear el JSON con los datos
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nombre", nombre);
            jsonObject.put("fecha", fechaFormateada); // Usar la fecha formateada
            jsonObject.put("hora", hora); // Hora adicional
            jsonObject.put("descripcion", descripcion);

            Log.d("VolleyHelper", "JSON enviado: " + jsonObject.toString());

            // Crear la solicitud de tipo POST
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    listener,
                    errorListener
            );

            // Añadir la solicitud a la cola
            getRequestQueue().add(request);

        } catch (Exception e) {
            Toast.makeText(ctx, "Error al crear los datos para la cita", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void obtenerHistorialCitas(com.android.volley.Response.Listener<JSONObject> listener,
                                      com.android.volley.Response.ErrorListener errorListener) {

        String url = "http://10.0.2.2:5001/api/citas";
        //String url = "http://192.168.100.110:5001/api/citas";

        // Crear la solicitud de tipo GET
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,  // No se necesita enviar un cuerpo en la solicitud GET
                listener,
                errorListener
        );

        // Añadir la solicitud a la cola
        getRequestQueue().add(request);
    }


}
