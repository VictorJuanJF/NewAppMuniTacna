package com.example.jimenez.appmunitacna.Clases;

import android.net.Uri;

public class Reporte {
    private String reporteId;
    private String userId;
    private String categoria;
    private String titulo;
    private String ubicacion;
    private String descripcion;
    private String imgURL;
    private long fecha;
    private boolean estado;

    public Reporte() {
    }

    public Reporte(String reporteId, String userId, String categoria, String titulo, String ubicacion, String descripcion, String imgURL, long fecha, boolean estado) {
        this.reporteId = reporteId;
        this.userId = userId;
        this.categoria = categoria;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.imgURL = imgURL;
        this.fecha = fecha;
        this.estado = estado;
    }

    public String getReporteId() {
        return reporteId;
    }

    public void setReporteId(String reporteId) {
        this.reporteId = reporteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
