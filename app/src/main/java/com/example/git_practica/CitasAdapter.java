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
    private List<Cita> citasList;

    // Constructor
    public CitasAdapter(List<Cita> citasList) {
        this.citasList = citasList;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = citasList.get(position);
        holder.nombreTextView.setText(cita.getNombre());
        holder.fechaTextView.setText(cita.getFecha());
        holder.horaTextView.setText(cita.getHora());
        holder.descripcionTextView.setText(cita.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, fechaTextView, horaTextView, descripcionTextView;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.textViewNombre);
            fechaTextView = itemView.findViewById(R.id.textViewFecha);
            horaTextView = itemView.findViewById(R.id.textViewHora);
            descripcionTextView = itemView.findViewById(R.id.textViewDescripcion);
        }
    }
}
