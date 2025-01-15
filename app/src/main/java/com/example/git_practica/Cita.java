package com.example.git_practica;
public class Cita {
    private String id;
    private String nombre;
    private String fecha;
    private String hora;
    private String descripcion;
//k
    public Cita(String nombre, String fecha, String hora, String descripcion) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.descripcion = descripcion;
    }

    public Cita(String id, String nombre, String fecha, String hora, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.descripcion = descripcion;
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
}

