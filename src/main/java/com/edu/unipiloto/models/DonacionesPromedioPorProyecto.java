/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edu.unipiloto.models;

import java.util.List;

/**
 *
 * @author juanc
 */
public class DonacionesPromedioPorProyecto {
    private String idProyecto;

    public String getLogin() {
        return idProyecto;
    }

    public void setLogin(String login) {
        this.idProyecto = login;
    }
    
    public long donacionesPromedio(List<Integer> pro){
        long valorPromedio = 0;
        for (int i = 0; i < pro.size(); i++) {
            valorPromedio += pro.get(i);
        }
        valorPromedio = valorPromedio / pro.size();
        return valorPromedio;
    }
}
