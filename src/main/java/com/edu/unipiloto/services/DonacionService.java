/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edu.unipiloto.services;

import com.edu.unipiloto.Main.PersistenceManager;
import com.edu.unipiloto.dto.DonacionDTO;
import com.edu.unipiloto.models.Donacion;
import com.edu.unipiloto.models.DonacionesPromedioPorProyecto;
import com.edu.unipiloto.models.ObtenerDonacionesPorUsuario;
import com.edu.unipiloto.models.Proyecto;
import com.edu.unipiloto.models.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author juanc
 */
@Path("/donacion")
@Produces(MediaType.APPLICATION_JSON)
public class DonacionService {

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

    public Proyecto getProyecto(String idProyecto) {
        Query q = entityManager.createQuery("select p from Proyecto p WHERE p.id = '" + idProyecto + "'");
        Proyecto pro;
        try {
            pro = (Proyecto) q.getSingleResult();
        } catch (Exception e) {
            pro = null;
        }
        return pro;
    }

    public Usuario getUsuario(String login) {
        Query q = entityManager.createQuery("select u from Usuario u WHERE u.login = '" + login + "'");
        Usuario user;
        try {
            user = (Usuario) q.getSingleResult();
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    @GET
    @Path("/getDonaciones/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDonaciones(@PathParam("login") String login) {
        Query q = entityManager.createQuery("select d from Donacion d");
        List<Donacion> donaciones = q.getResultList();
        List<Donacion> donacionesName = new ArrayList<Donacion>();
        for (int i = 0; i < donaciones.size(); i++) {
            if (donaciones.get(i).getLogin().equals(login)) {
                donacionesName.add(donaciones.get(i));
            }
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(donacionesName).build();
    }

    @GET
    @Path("/getValorAcumuladoDonacionesAUnProyecto/{idProyecto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValorAcumuladoDonacionesAUnProyecto(@PathParam("idProyecto") String idProyecto) {

        JSONObject rta = new JSONObject();
        Query q = entityManager.createQuery("select d.valorDonar from Donacion d WHERE d.idProyecto = '" + idProyecto + "'");
        List<Integer> pro = q.getResultList();
        
        long valorPromedio = 0;
        for (int i = 0; i < pro.size(); i++) {
            valorPromedio += pro.get(i);
        }
        valorPromedio = valorPromedio / pro.size();
        rta.put("valorPromedioFinanciado", valorPromedio);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

    @POST
    @Path("/addDonacion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDonacion(DonacionDTO donacion) {

        Donacion donar = new Donacion();
        JSONObject rta = new JSONObject();

        donar.setFecha(donacion.getFecha());
        donar.setIdProyecto(donacion.getIdProyecto());
        donar.setLogin(donacion.getLogin());
        donar.setValorDonar(donacion.getValorDonar());

        Proyecto pro = getProyecto(donar.getIdProyecto());
        Usuario user = getUsuario(donar.getLogin());
        int cantidadDinero = 0;
        try {
            if (pro != null || user != null) {
                if (user.getTipoUsuario().equals("Financiador")) {
                    if (pro.getEstado().equals("Activo")) {
                        cantidadDinero = donar.getValorDonar() + pro.getCantidadRecaudada();
                        if (cantidadDinero < pro.getCantidadRecaudar()) {
                            pro.setCantidadRecaudada(cantidadDinero);
                            entityManager.getTransaction().begin();
                            entityManager.merge(pro);
                            entityManager.persist(donar);
                            entityManager.getTransaction().commit();
                            entityManager.refresh(pro);
                            entityManager.refresh(donar);
                            
                            rta.put("mensaje", "Se consignó correctamente al proyecto");
                            rta.put("valorDonado", donacion.getValorDonar());
                            rta.put("idProyecto", donacion.getIdProyecto());
                            rta.put("fecha", donacion.getFecha());

                        } else if (pro.getCantidadRecaudar() == pro.getCantidadRecaudada()) {
                            rta.put("mensaje", "La cantidad de dinero solicitada ya se ha recolectado");
                        } else if (cantidadDinero > pro.getCantidadRecaudar()) {
                            
                            cantidadDinero = cantidadDinero - pro.getCantidadRecaudar();

                            pro.setCantidadRecaudar(pro.getCantidadRecaudada());
                            pro.setEstado("Cierre");
                            entityManager.getTransaction().begin();
                            entityManager.merge(pro);
                            entityManager.persist(donar);
                            entityManager.getTransaction().commit();
                            entityManager.refresh(pro);
                            entityManager.refresh(donar);
                            rta.put("mensaje", "El proyecto completó la inversión necesaria de " + pro.getCantidadRecaudar() + ", le sobran " + cantidadDinero);
                        }
                    } else {
                        rta.put("mensaje", "el proyecto está cerrado");
                    }
                } else {
                    rta.put("mensaje", "Usted es un emprendedor y no puede realizar donaciones a proyectos");
                }
            }else{
                rta.put("mensaje", "No se encontró el usuario o el proyecto especificado");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            donar = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }
}
