/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edu.unipiloto.services;

import com.edu.unipiloto.Main.PersistenceManager;
import com.edu.unipiloto.models.Proyecto;
import com.edu.unipiloto.dto.ProyectoDTO;
import com.edu.unipiloto.models.estadoProyecto;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author juanc
 */
@Path("/proyect")
@Produces(MediaType.APPLICATION_JSON)
public class ProyectoService {

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

    public String getLogin(String login) {
        Query q = entityManager.createQuery("select u.tipoUsuario from Usuario u WHERE u.login = '" + login + "'");
        String tipoUsuario = q.getSingleResult().toString();
        return tipoUsuario;
    }

    public Proyecto getProyect(String idProyecto) {
        Query q = entityManager.createQuery("select u from Proyecto u WHERE u.id = '" + idProyecto +"'");
        Proyecto pro = (Proyecto) q.getSingleResult();
        return pro;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Proyecto u");
        List<Proyecto> pro = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(pro).build();
    }

    @POST
    @Path("/cambiarEstado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoProyecto(estadoProyecto estado) {
        estadoProyecto nuevoEstado = estado;
        Proyecto pro = getProyect(nuevoEstado.getIdProyecto());
        pro.setEstado(nuevoEstado.getEstado());
        JSONObject rta = new JSONObject();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(pro);
            entityManager.getTransaction().commit();
            entityManager.refresh(pro);
            rta.put("mensaje", "Se actualizó de manera exitosa el proyecto");
            rta.put("estado", nuevoEstado.getEstado());
            rta.put("idProyecto", nuevoEstado.getIdProyecto());

        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            nuevoEstado = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

    @POST
    @Path("/addProyect")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProyect(ProyectoDTO proyecto) {

        Proyecto pro = new Proyecto();
        JSONObject rta = new JSONObject();
        pro.setCantidadRecaudada(proyecto.getCantidadRecaudada());
        pro.setCantidadRecaudar(proyecto.getCantidadRecaudar());
        pro.setDescripcion(proyecto.getDescripcion());
        pro.setEstado(proyecto.getEstado());
        pro.setFechaInicio(proyecto.getFechaInicio());
        pro.setFechaLimite(proyecto.getFechaLimite());
        pro.setNombre(proyecto.getNombre());
        pro.setResponsable(proyecto.getResponsable());
        pro.setTipoProyecto(proyecto.getTipoProyecto());

        String tipoUsuario = getLogin(pro.getResponsable());
        try {

            if (tipoUsuario.equals("Emprendedor")) {
                entityManager.getTransaction().begin();
                entityManager.persist(pro);
                entityManager.getTransaction().commit();
                entityManager.refresh(pro);
                rta.put("mensaje", "Se creo exitosamente el proyecto, sus datos son");
                rta.put("nombre", pro.getNombre());
                rta.put("responsable", pro.getResponsable());
                rta.put("CantidadRecaudada", pro.getCantidadRecaudada());
            } else {
                rta.put("mensaje", "Siendo financiador no se puede crear un proyecto");
            }

        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            pro = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

    @OPTIONS
    public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
        return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }

}
