package com.example.git_practica;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.ParseException;
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

    public void agendarCita(String nombre, String fecha, String descripcion,
                            com.android.volley.Response.Listener<JSONObject> listener,
                            com.android.volley.Response.ErrorListener errorListener) {

        String url = "http://10.0.2.2:5001/api/citas";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(fecha);
            String fechaFormateada = sdf.format(date);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nombre", nombre);
            jsonObject.put("fecha", fechaFormateada);
            jsonObject.put("descripcion", descripcion);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    listener,
                    error -> {
                        String errorMessage = "Error en la conexión con el servidor";

                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            errorMessage = "Código de error HTTP: " + statusCode;
                        } else if (error.getCause() != null) {
                            errorMessage = "Error: " + error.getCause().getMessage();
                        }

                        Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();

                        Log.e("VolleyError", "Error al conectar con el servidor", error);
                        error.printStackTrace();
                    }
            );

            getRequestQueue().add(request);

        } catch (Exception e) {
            Toast.makeText(ctx, "Error al crear los datos para la cita", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}