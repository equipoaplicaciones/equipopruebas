package com.example.git_practica;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CitasAdapter2 extends RecyclerView.Adapter<CitasAdapter2.CitaViewHolder> {
//hola
    private List<Cita> citasList;

    public CitasAdapter2(List<Cita> citasList) {
        this.citasList = citasList;
    }

    @Override
    public CitaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Solo inflar el diseño de admin (item_cita2)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita2, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CitaViewHolder holder, int position) {
        Cita cita = citasList.get(position);

        holder.nombreTextView.setText(cita.getNombre());
        holder.fechaTextView.setText(cita.getFecha());
        holder.horaTextView.setText(cita.getHora());
        holder.descripcionTextView.setText(cita.getDescripcion());

        holder.btnVerDetalles.setOnClickListener(v -> {
            // Crear un Intent para iniciar DetallesCitaActivity
            Intent intent = new Intent(v.getContext(), DetallesCitaActivity.class);

            // Pasando dato de la cita c;
            intent.putExtra("nombre", cita.getNombre());
            intent.putExtra("fecha", cita.getFecha());
            intent.putExtra("hora", cita.getHora());
            intent.putExtra("descripcion", cita.getDescripcion());
            intent.putExtra("citaId", cita.getId());

            // Iniciar la nueva Activity
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    public void actualizarCitas(List<Cita> citas) {
        this.citasList = citas;
        notifyDataSetChanged();
    }

    public class CitaViewHolder extends RecyclerView.ViewHolder {

        TextView nombreTextView, fechaTextView, horaTextView, descripcionTextView;
        Button btnVerDetalles;

        public CitaViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreCita);
            fechaTextView = itemView.findViewById(R.id.fechaCita);
            horaTextView = itemView.findViewById(R.id.horaCita);
            descripcionTextView = itemView.findViewById(R.id.descripcionCita);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}
