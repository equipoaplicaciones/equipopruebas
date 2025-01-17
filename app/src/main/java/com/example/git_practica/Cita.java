package com.example.git_practica;
// adaptadores
public class Cita {
    private String id;
    private String nombre;
    private String motivoCita; // Cambié "motivo" a "motivoCita" para reflejar tu código original
    private String fecha;
    private String hora;
    private String status;
    private String genero;
    private int edad; // Cambiado de String a int
    private String telefono;
    private String estadoCivil;
    private String domicilio;
    private String email;
    private String comentarios;

    // Constructor completo
    public Cita(String id, String nombre, String motivoCita, String fecha, String hora, String status,
                String genero, int edad, String telefono, String estadoCivil, String domicilio, String email, String comentarios) {
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

    // Constructor simplificado (opcional, útil para otros casos)
    public Cita(String motivoCita, String fecha, String hora) {
        this.motivoCita = motivoCita;
        this.fecha = fecha;
        this.hora = hora;
    }

    // Constructor vacío (útil para frameworks como Firebase o Room)
    public Cita(String motivoCita, String fecha, String hora, String status) {
        this.motivoCita = motivoCita;
        this.fecha = fecha;
        this.hora = hora;
        this.status = status;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMotivoCita() { // Cambié el getter para reflejar el nombre del atributo
        return motivoCita;
    }

    public void setMotivoCita(String motivoCita) {
        this.motivoCita = motivoCita;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}
