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

        // Mostrar los datos en los TextViews
        holder.nombreTextView.setText(cita.getNombre());
        holder.motivoTextView.setText(cita.getMotivoCita());
        holder.fechaTextView.setText(cita.getFecha());
        holder.horaTextView.setText(cita.getHora());
        holder.statusTextView.setText(cita.getStatus());  // Agregar el status

        // Configurar el click para ver detalles
        holder.btnVerDetalles.setOnClickListener(v -> {
            // Crear un Intent para iniciar DetallesCitaActivity
            Intent intent = new Intent(v.getContext(), DetallesCitaActivity.class);

            // Pasar los datos de la cita

            intent.putExtra("nombre", cita.getNombre());
            intent.putExtra("motivo", cita.getMotivoCita());
            intent.putExtra("fecha", cita.getFecha());
            intent.putExtra("hora", cita.getHora());
            intent.putExtra("status", cita.getStatus());  // Pasar el status
            intent.putExtra("citaId", cita.getId());
            intent.putExtra("genero", cita.getGenero());
            intent.putExtra("edad", cita.getEdad());
            intent.putExtra("telefono", cita.getTelefono());
            intent.putExtra("estadoCivil", cita.getEstadoCivil());
            intent.putExtra("domicilio", cita.getDomicilio());
            intent.putExtra("email", cita.getEmail());
            intent.putExtra("comentarios", cita.getComentarios());

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

        TextView nombreTextView, motivoTextView, fechaTextView, horaTextView, statusTextView;
        Button btnVerDetalles;

        public CitaViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreCita);
            motivoTextView = itemView.findViewById(R.id.motivoCita);
            fechaTextView = itemView.findViewById(R.id.fechaCita);
            horaTextView = itemView.findViewById(R.id.horaCita);
            statusTextView = itemView.findViewById(R.id.statusCita);  // Agregar referencia a status
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}
