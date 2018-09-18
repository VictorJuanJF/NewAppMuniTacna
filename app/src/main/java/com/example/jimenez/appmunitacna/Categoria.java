package com.example.jimenez.appmunitacna;

public class Categoria {
    private String nombre;
    private String descripcion;
    private int imagenId;


    public Categoria() {
        //Needed for firebase
    }

    public Categoria(String nombre, String descripcion, int imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenId = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getImagen() {
        return imagenId;
    }

    public void setImagen(int    imagen) {
        this.imagenId = imagen;
    }
}
