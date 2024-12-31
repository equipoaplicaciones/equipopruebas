package com.example.git_practica;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng userLocation; // Usaremos esta variable para la ubicación del usuario
    private FusedLocationProviderClient fusedLocationClient; // Cliente de ubicación
    private final LatLng CONSULTORIO_LOCATION = new LatLng(19.24274689333512, -98.844869040542); // Ubicación del consultorio
    private final String API_KEY = "AIzaSyADzVi_xdbhcFjfgKcABzB6iq4qB98UOgs"; // Reemplaza con tu clave de API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // Inicializa el cliente

        // Configurar el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Habilitar los controles de zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Intentar obtener la ubicación actual del usuario
        getUserLocation();
    }

    // Método para obtener la ubicación actual del usuario
    private void getUserLocation() {
        // Verificar si tenemos permisos para acceder a la ubicación
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Obtener la latitud y longitud de la ubicación actual
                                userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                                // Mover la cámara del mapa a la ubicación actual
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

                                // Agregar un marcador en la ubicación del usuario con un ícono personalizado
                                mMap.addMarker(new MarkerOptions()
                                        .position(userLocation)
                                        .title("Mi ubicación")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_location))); // Icono del usuario

                                // Obtener y trazar la ruta hacia el consultorio
                                getRoute();
                            }
                        }
                    });
        } else {
            // Si no tenemos permisos, pedirlos
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Método para obtener y trazar la ruta (igual que antes)
    private void getRoute() {
        // Construir la URL para la API Directions
        String origin = userLocation.latitude + "," + userLocation.longitude;
        String destination = CONSULTORIO_LOCATION.latitude + "," + CONSULTORIO_LOCATION.longitude;

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin +
                "&destination=" + destination +
                "&mode=driving" +
                "&key=" + API_KEY;

        // Crear una solicitud con Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            parseRouteAndDraw(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        // Agregar la solicitud a la cola
        requestQueue.add(stringRequest);
    }

    // Método para trazar la ruta (igual que antes)
    private void parseRouteAndDraw(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray routes = jsonObject.getJSONArray("routes");

        if (routes.length() > 0) {
            JSONObject route = routes.getJSONObject(0);
            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
            String points = overviewPolyline.getString("points");

            // Decodificar la polilínea
            List<LatLng> decodedPath = PolyUtil.decode(points);

            // Dibujar la ruta en el mapa
            mMap.addPolyline(new PolylineOptions()
                    .addAll(decodedPath)
                    .color(ContextCompat.getColor(this, R.color.black))
                    .width(10f));
        }

        // Agregar marcador para el consultorio con un ícono personalizado
        mMap.addMarker(new MarkerOptions()
                .position(CONSULTORIO_LOCATION)
                .title("Consultorio Dental")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dental_office))); // Icono del consultorio
    }
}
