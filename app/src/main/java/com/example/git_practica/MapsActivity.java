package com.example.git_practica;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.Polyline;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String API_KEY = "AIzaSyADzVi_xdbhcFjfgKcABzB6iq4qB98UOgs";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Verificar si el permiso de ubicación ha sido concedido
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tiene permiso, solicitarlo
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Si ya tiene permiso, inicializar el mapa
            initializeMap();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // Si el permiso es concedido
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap(); // Proceder con la inicialización del mapa
            } else {
                Toast.makeText(this, "Permiso de ubicación necesario para mostrar el mapa", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);  // Se asegura de que se obtiene la referencia del mapa
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("MapsActivity", "Mapa listo.");

        // Verificar que el permiso de ubicación ha sido otorgado
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            mMap.addMarker(new MarkerOptions().position(userLocation).title("Mi Ubicación"));
                            // Llamar a la API de Directions
                            getDirections(userLocation, new LatLng(19.24274689333512, -98.844869040542));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("MapsActivity", "Error al obtener ubicación", e));
        }
    }

    private void getDirections(LatLng origin, LatLng destination) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionsAPI directionsAPI = retrofit.create(DirectionsAPI.class);

        String originStr = origin.latitude + "," + origin.longitude;
        String destStr = destination.latitude + "," + destination.longitude;

        Call<DirectionsResponse> call = directionsAPI.getDirections(originStr, destStr, API_KEY);
        call.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.isSuccessful()) {
                    DirectionsResponse directionsResponse = response.body();
                    Log.d("MapsActivity", "Respuesta exitosa: " + directionsResponse);
                    if (directionsResponse != null && directionsResponse.routes != null) {
                        List<LatLng> path = decodePolyline(directionsResponse.routes.get(0).legs.get(0).steps.get(0).polyline);
                        mMap.addPolyline(new PolylineOptions().addAll(path).color(0xFF0000FF).width(10));
                    } else {
                        Log.e("MapsActivity", "No se encontraron rutas en la respuesta.");
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Error al obtener la ruta", Toast.LENGTH_SHORT).show();
                    Log.e("MapsActivity", "Error al obtener la respuesta: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<LatLng> decodePolyline(String encoded) {
        List<LatLng> polyline = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            lng += dlng;

            LatLng point = new LatLng(lat / 1E5, lng / 1E5);
            polyline.add(point);
        }
        return polyline;
    }
}
