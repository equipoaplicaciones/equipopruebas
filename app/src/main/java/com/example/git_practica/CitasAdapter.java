package com.example.git_practica;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitaViewHolder> {
    private List<Map<String, String>> citasList;

    public CitasAdapter(List<Map<String, String>> citasList) {
        this.citasList = citasList;
    }

    @Override
    public CitaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Crear el LinearLayout programáticamente
        LinearLayout layout = new LinearLayout(parent.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Crear los TextViews para cada dato
        TextView tvNombre = new TextView(parent.getContext());
        TextView tvFecha = new TextView(parent.getContext());
        TextView tvHora = new TextView(parent.getContext());
        TextView tvDescripcion = new TextView(parent.getContext());

        // Asignar un ID a cada TextView para poder acceder a ellos más tarde
        tvNombre.setId(View.generateViewId());
        tvFecha.setId(View.generateViewId());
        tvHora.setId(View.generateViewId());
        tvDescripcion.setId(View.generateViewId());

        // Añadir los TextViews al LinearLayout
        layout.addView(tvNombre);
        layout.addView(tvFecha);
        layout.addView(tvHora);
        layout.addView(tvDescripcion);

        return new CitaViewHolder(layout, tvNombre, tvFecha, tvHora, tvDescripcion);
    }

    @Override
    public void onBindViewHolder(CitaViewHolder holder, int position) {
        Map<String, String> cita = citasList.get(position);
        holder.tvNombre.setText(cita.get("nombre"));
        holder.tvFecha.setText(cita.get("fecha"));
        holder.tvHora.setText(cita.get("hora"));
        holder.tvDescripcion.setText(cita.get("descripcion"));
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFecha, tvHora, tvDescripcion;

        public CitaViewHolder(View itemView, TextView tvNombre, TextView tvFecha, TextView tvHora, TextView tvDescripcion) {
            super(itemView);
            this.tvNombre = tvNombre;
            this.tvFecha = tvFecha;
            this.tvHora = tvHora;
            this.tvDescripcion = tvDescripcion;
        }
    }
}
