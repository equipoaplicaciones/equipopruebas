package com.example.git_practica;

public class Cita {
    private String id;
    private String nombre;
    private String motivoCita;
    private String fecha;
    private String hora;
    private String genero;
    private int edad;
    private String telefono;
    private String estadoCivil;
    private String domicilio;
    private String email;
    private String comentarios;
    private String status;

    // Constructor con ID y todos los campos
    public Cita(String id, String nombre, String motivoCita, String fecha, String hora, String status, String genero, int edad, String telefono, String estadoCivil, String domicilio, String email, String comentarios) {
        this.id = id;
        this.nombre = nombre;
        this.motivoCita = motivoCita;
        this.fecha = fecha;
        this.hora = hora;
        this.status = status;
        this.genero = genero;
        this.edad = edad;
        this.telefono = telefono;
        this.estadoCivil = estadoCivil;
        this.domicilio = domicilio;
        this.email = email;
        this.comentarios = comentarios;
    }

    public Cita(String nombre, String motivoCita, String fecha, String hora, String status, String genero,
                int edad, String telefono, String estadoCivil, String domicilio, String email, String comentarios) {
        this.nombre = nombre;
        this.motivoCita = motivoCita;
        this.fecha = fecha;
        this.hora = hora;
        this.status = status;
        this.genero = genero;
        this.edad = edad;
        this.telefono = telefono;
        this.estadoCivil = estadoCivil;
        this.domicilio = domicilio;
        this.email = email;
        this.comentarios = comentarios;
    }

    public Cita(String nombre, String motivoCita, String fecha, String hora, String genero, int edad,
                String telefono, String estadoCivil, String domicilio, String email, String comentarios, String status) {
        this.id = id;
        this.nombre = nombre;
        this.motivoCita = motivoCita;
        this.fecha = fecha;
        this.hora = hora;
        this.genero = genero;
        this.edad = edad;
        this.telefono = telefono;
        this.estadoCivil = estadoCivil;
        this.domicilio = domicilio;
        this.email = email;
        this.comentarios = comentarios;
        this.status = status;
    }

    public Cita(String motivoCita, String fecha, String hora) {
        this.motivoCita = motivoCita;
        this.fecha = fecha;
        this.hora = hora;
        this.status = "pendiente";  // Valor por defecto
    }
    public Cita(String motivoCita, String fecha, String hora, String status) {
        this.motivoCita = motivoCita;
        this.fecha = fecha;
        this.hora = hora;
        this.status = status;  // Ahora acepta el status como parámetro
    }


    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMotivoCita() {
        return motivoCita;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getGenero() {
        return genero;
    }

    public int getEdad() {
        return edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public String getEmail() {
        return email;
    }

    public String getComentarios() {
        return comentarios;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
}
