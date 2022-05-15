/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.models;

/**
 *
 * @author juanc
 */
public class ProyectoDTO {
    
    private String responsable;

    private String nombre;
    
    private String fechaInicio;

    private String fechaLimite;

    private Integer cantidadRecaudar;
    
    private Integer cantidadRecaudada;

    private String descripcion;
    
    private String estado;
    
    private String tipoProyecto;

    public ProyectoDTO() {
    }
    
    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Integer getCantidadRecaudar() {
        return cantidadRecaudar;
    }

    public void setCantidadRecaudar(Integer cantidadRecaudar) {
        this.cantidadRecaudar = cantidadRecaudar;
    }

    public Integer getCantidadRecaudada() {
        return cantidadRecaudada;
    }

    public void setCantidadRecaudada(Integer cantidadRecaudada) {
        this.cantidadRecaudada = cantidadRecaudada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoProyecto() {
        return tipoProyecto;
    }

    public void setTipoProyecto(String tipoProyecto) {
        this.tipoProyecto = tipoProyecto;
    }
    
    
    
}
