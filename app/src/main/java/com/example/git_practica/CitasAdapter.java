package com.example.git_practica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitaViewHolder> {

    private List<Cita> citasList;
    private Context context;

    public CitasAdapter(Context context, List<Cita> citasList) {
        this.context = context;
        this.citasList = citasList;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del item_cita
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        // Obtener la cita actual
        Cita cita = citasList.get(position);

        // Configurar los datos de la cita en las vistas
        holder.nombreTextView.setText(cita.getNombre());
        holder.fechaTextView.setText(cita.getFecha());
        holder.horaTextView.setText(cita.getHora());
        holder.descripcionTextView.setText(cita.getDescripcion());

        // Cambiar el color de la tarjeta según el estado
        if (cita.getStatus() != null) {
            switch (cita.getStatus().toLowerCase()) {
                case "pendiente":
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                    break;
                case "aceptada":
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
                    break;
                default:
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    break;
            }
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return citasList != null ? citasList.size() : 0;
    }

    public void actualizarCitas(List<Cita> nuevasCitas) {
        this.citasList = nuevasCitas;
        notifyDataSetChanged();
    }

    public static class CitaViewHolder extends RecyclerView.ViewHolder {

        TextView nombreTextView, fechaTextView, horaTextView, descripcionTextView;
        CardView cardView;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreCita);
            fechaTextView = itemView.findViewById(R.id.fechaCita);
            horaTextView = itemView.findViewById(R.id.horaCita);
            descripcionTextView = itemView.findViewById(R.id.descripcionCita);
            cardView = itemView.findViewById(R.id.cardView); // Obtener referencia a la CardView
        }
    }
}
