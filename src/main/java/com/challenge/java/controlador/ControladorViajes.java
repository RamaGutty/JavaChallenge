package com.challenge.java.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.java.modelo.Camino;
import com.challenge.java.modelo.Estacion;
import com.challenge.java.servicio.ServicioViajes;

@RestController
@RequestMapping("/api")
public class ControladorViajes {

    @Autowired
    private ServicioViajes servicioViajes;

    @PutMapping("/estaciones/{id_estacion}")
    public void agregarEstacion(@PathVariable("id_estacion") long idEstacion, @RequestParam String nombre) {
        servicioViajes.agregarEstacion(idEstacion, nombre);
    }

    @PutMapping("/caminos/{id_camino}")
    public void agregarCamino(@PathVariable("id_camino") long idCamino, @RequestParam long idOrigen, @RequestParam long idDestino, @RequestParam double costo) {
        servicioViajes.agregarCamino(idCamino, idOrigen, idDestino, costo);
    }

    @GetMapping("/caminos/{id_origen}/{id_destino}")
    public List<Estacion> obtenerCaminoOptimo(@PathVariable("id_origen") long idOrigen, @PathVariable("id_destino") long idDestino) {
        return servicioViajes.encontrarCaminoOptimo(idOrigen, idDestino);
    }

    @GetMapping("/estaciones")
    public List<Estacion> obtenerEstaciones() { 
        return servicioViajes.obtenerEstaciones();
    }

    @GetMapping("/caminos")
    public List<Camino> obtenerCaminos() {
        return servicioViajes.obtenerCaminos();
    }
}
