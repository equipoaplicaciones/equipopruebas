package com.example.git_practica;
public class Cita {
    private String id;
    private String nombre;
    private String fecha;
    private String hora;
    private String descripcion;
    private String status;

    public Cita(String nombre, String fecha, String hora, String descripcion) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.descripcion = descripcion;
        this.status = "pendiente";  // Valor por defecto
    }

    public Cita(String id, String nombre, String fecha, String hora, String descripcion, String status) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.descripcion = descripcion;
        this.status = status;
    }

    public Cita(String nombre, String fecha, String hora, String descripcion, String status) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.descripcion = descripcion;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
