package com.example.git_practica;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitaViewHolder> {

    private List<Cita> citasList;

    public CitasAdapter(List<Cita> citasList) {
        this.citasList = citasList;
    }

    @Override
    public CitaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CitaViewHolder holder, int position) {
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

    public void actualizarCitas(List<Cita> citas) {
        this.citasList = citas;
        notifyDataSetChanged();
    }

    public class CitaViewHolder extends RecyclerView.ViewHolder {

        TextView nombreTextView, fechaTextView, horaTextView, descripcionTextView;

        public CitaViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreCita);
            fechaTextView = itemView.findViewById(R.id.fechaCita);
            horaTextView = itemView.findViewById(R.id.horaCita);
            descripcionTextView = itemView.findViewById(R.id.descripcionCita);
        }
    }
}


