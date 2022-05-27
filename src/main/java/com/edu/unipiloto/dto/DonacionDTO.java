/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edu.unipiloto.dto;

/**
 *
 * @author juanc
 */
public class DonacionDTO {
    private String idProyecto;
    private String login;
    private String fecha;
    private int valorDonar;

    public DonacionDTO() {
    }

    public String getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(String idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getValorDonar() {
        return valorDonar;
    }

    public void setValorDonar(int valorDonar) {
        this.valorDonar = valorDonar;
    }
    
    
}

