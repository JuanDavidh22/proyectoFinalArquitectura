/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Donacion;
import com.example.models.DonacionDTO;
import com.example.models.Proyecto;
import com.example.models.ProyectoDTO;
import com.example.models.Usuario;
import com.example.models.UsuarioDTO;
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

    public List<Proyecto> getProyecto(long idProyecto) {
        Query q = entityManager.createQuery("select p from Proyecto p WHERE p.id = " + idProyecto);
        List<Proyecto> pro = q.getResultList();
        return pro;
    }

    public List<Usuario> getUsuario(String login) {
        Query q = entityManager.createQuery("select u from Usuario u WHERE u.login = '" + login + "'");
        List<Usuario> user = q.getResultList();
        return user;
    }

    public void actualizarProyecto(long idProyecto) {
        Query q = entityManager.createQuery("UPDATE Proyecto SET WHERE p.id = " + idProyecto);
        List<Proyecto> pro = q.getResultList();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Proyecto u");
        List<Proyecto> pro = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(pro).build();
    }

    @GET
    @Path("/getValorPromedioFinanciado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValorFinanciadoPromedio() {
        Query q = entityManager.createQuery("select d.valorDonar from Donacion d");
        long valorPromedio = 0;
        JSONObject rta = new JSONObject();
        List<Integer> pro = q.getResultList();
        for (int i = 0; i < pro.size(); i++) {
            valorPromedio += pro.get(i);
        }
        rta.put("valorPromedioFinanciado", valorPromedio);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }
    
    @GET
    @Path("/getValorAcumuladoDonacionesAUnProyecto")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValorAcumuladoDonacionesAUnProyecto(String JSON) {
        
        String[] login = JSON.split("\"");
        String idProyecto = login[2];
        Query q = entityManager.createQuery("select d.valorDonar from Donacion d WHERE d.idProyecto = '" + idProyecto + "'");
        long valorPromedio = 0;
        JSONObject rta = new JSONObject();
        List<Integer> pro = q.getResultList();
        for (int i = 0; i < pro.size(); i++) {
            valorPromedio += pro.get(i);
        }
        rta.put("valorPromedioFinanciado", valorPromedio);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
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
        rta.put("ValorEsperadoPromedio", valorPromedio);
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

        List<Proyecto> pro = getProyecto(donar.getIdProyecto());
        List<Usuario> user = getUsuario(donar.getLogin());
        int cantidadDinero = 0;

        if (user.get(0).getTipoUsuario().equals("Financiador")) {
            if (pro.get(0).getEstado().equals("publicado") || pro.get(0).getEstado().equals("monitoreo")) {
                cantidadDinero = donar.getValorDonar() + pro.get(0).getCantidadRecaudada();

                if (cantidadDinero < pro.get(0).getCantidadRecaudar()) {

//                    actualizarProyecto(pro.get(0).getId();
//cambiar la cantidad recaudada
                } else if (pro.get(0).getCantidadRecaudar() == pro.get(0).getCantidadRecaudada()) {
                    rta.put("mensaje", "La cantidad de dinero solicitada ya se ha recolectado");
                } else if (cantidadDinero > pro.get(0).getCantidadRecaudar()) {
                    cantidadDinero = cantidadDinero - pro.get(0).getCantidadRecaudar();
                    //cambiar la cantidad recaudada
                    //cambiar estado
                    rta.put("mensaje", "El proyecto completó la inversión necesaria de " + pro.get(0).getCantidadRecaudar() + ", le sobran " + cantidadDinero);
                }
            } else {
                rta.put("mensaje", "el proyecto está cerrado");
            }
        } else {
            rta.put("mensaje", "Usted es un emprendedor y no puede realizar donaciones a proyectos");
        }

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(donar);
            entityManager.getTransaction().commit();
            entityManager.refresh(donar);

            rta.put("mensaje", "Se consignó correctamente al proyecto");
            rta.put("valorDonado", donar.getValorDonar());
            rta.put("idProyecto", donar.getIdProyecto());
            rta.put("fecha", donar.getFecha());

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
