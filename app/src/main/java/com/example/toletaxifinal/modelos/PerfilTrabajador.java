package com.example.toletaxifinal.modelos;

public class PerfilTrabajador {
    private double latitud;
    private double longitud;
    private String nombre;
    private String apellidos;
    private String matricula;
    private String modelo;
    private String correo;
    private String telefono;
    private String id;

    public PerfilTrabajador(double latitud, double longitud, String nombre, String apellidos, String matricula, String modelo, String correo, String telefono, String id) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.matricula = matricula;
        this.modelo = modelo;
        this.correo = correo;
        this.telefono = telefono;
        this.id = id;
    }


    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
