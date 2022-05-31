/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edu.unipiloto.services;

import com.edu.unipiloto.Main.PersistenceManager;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author juanc
 */
@Path("/estadisticas")
@Produces(MediaType.APPLICATION_JSON)
public class EstadisticasService {

    @PersistenceContext(unitName = "mongoPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/getValorEsperadoPromedio")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValorEsperadoPromedio() {
        Query q = entityManager.createQuery("select p.cantidadRecaudar from Proyecto p");
        long valorPromedio = 0;
        JSONObject rta = new JSONObject();
        List<Integer> pro = q.getResultList();
        for (int i = 0; i < pro.size(); i++) {
            valorPromedio += pro.get(i);
        }
        valorPromedio /= pro.size(); 
        rta.put("ValorEsperadoPromedio", valorPromedio);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

    @GET
    @Path("/getValorPromedioFinanciado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValorFinanciadoPromedio() {
       
        long valorPromedio = 0;
        JSONObject rta = new JSONObject();
         Query q = entityManager.createQuery("select d.valorDonar from Donacion d");
        List<Integer> pro = q.getResultList();
        
        for (int i = 0; i < pro.size(); i++) {
            valorPromedio += pro.get(i);
        }
        valorPromedio /= pro.size();
        rta.put("valorPromedioFinanciado", valorPromedio);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }
}
