package com.challenge.java.servicio;


import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.challenge.java.modelo.Camino;
import com.challenge.java.modelo.CaminoOptimo;
import com.challenge.java.modelo.Estacion;

@Service
public class ServicioViajes {
    private final Map<Long, Estacion> estaciones = new HashMap<>();
    private final List<Camino> caminos = new ArrayList<>();

    public ResponseEntity<String> agregarEstacion(long id, String nombre) {
        if (existeEstacion(id)) {
            return new ResponseEntity<>("La estacion con ID " + id + " ya existe.", HttpStatus.BAD_REQUEST);
        }else {
            estaciones.put(id, new Estacion(id, nombre));
            return new ResponseEntity<>("Estación " + nombre + " creada con éxito", HttpStatus.OK);
        }
    }

    public boolean existeEstacion(long id){
        return estaciones.containsKey(id);
    }
    

    public ResponseEntity<String> agregarCamino(long id, long idOrigen, long idDestino, double costo) {
        Estacion origen = estaciones.get(idOrigen);
        Estacion destino = estaciones.get(idDestino);
        
        if (origen == null) {
            return new ResponseEntity<>("La estación de origen con ID " + idOrigen + " no existe.", HttpStatus.BAD_REQUEST);
        }
        
        if (destino == null) {
            return new ResponseEntity<>("La estación de destino con ID " + idDestino + " no existe.", HttpStatus.BAD_REQUEST);
        }
        
        if (existeCamino(idOrigen, idDestino)) {
            return new ResponseEntity<>("El camino entre la estación de origen y destino ya existe.", HttpStatus.CONFLICT);
        }
        
        caminos.add(new Camino(id, idOrigen, idDestino, costo));
        caminos.add(new Camino(id, idDestino, idOrigen, costo));
        return new ResponseEntity<>("Camino agregado exitosamente.", HttpStatus.OK);
    }
    

    private boolean existeCamino(long idOrigen, long idDestino) {
        return caminos.stream().anyMatch(camino -> 
            (camino.getIdOrigen() == idOrigen && camino.getIdDestino() == idDestino) ||
            (camino.getIdOrigen() == idDestino && camino.getIdDestino() == idOrigen));
    }

    public List<Estacion> obtenerEstaciones() {
        return new ArrayList<>(estaciones.values());
    }

    public List<Camino> obtenerCaminos() {
        return new ArrayList<>(caminos);
    }

    public ResponseEntity<CaminoOptimo> encontrarCaminoOptimo(long idInicio, long idFin) {
        Estacion origen = estaciones.get(idInicio);
        Estacion destino = estaciones.get(idFin);
        
        if (origen == null) {
            return new ResponseEntity<>(new CaminoOptimo(Collections.emptyList(), 0, "La estación de origen con ID " + idInicio + " no existe."), HttpStatus.BAD_REQUEST);
        }
        
        if (destino == null) {
            return new ResponseEntity<>(new CaminoOptimo(Collections.emptyList(), 0, "La estación de destino con ID " + idFin + " no existe."), HttpStatus.BAD_REQUEST);
        }

        Map<Long, Double> distancias = new HashMap<>();
        Map<Long, Long> previo = new HashMap<>();
        PriorityQueue<Nodo> colaPrioridad = new PriorityQueue<>(Comparator.comparingDouble(nodo -> nodo.distancia));

        for (Long id : estaciones.keySet()) {
            if (id == idInicio) {
                distancias.put(id, 0.0);
                colaPrioridad.add(new Nodo(id, 0.0));
            } else {
                distancias.put(id, Double.MAX_VALUE);
                colaPrioridad.add(new Nodo(id, Double.MAX_VALUE));
            }
        }

        while (!colaPrioridad.isEmpty()) {
            Nodo nodoActual = colaPrioridad.poll();
            long idActual = nodoActual.id;

            if (idActual == idFin) {
                List<Estacion> camino = new ArrayList<>();
                for (Long at = idFin; at != null; at = previo.get(at)) {
                    camino.add(estaciones.get(at));
                }
                Collections.reverse(camino);
                CaminoOptimo caminoOptimo = new CaminoOptimo(camino, obtenerCostoCamino(listaIdEstaciones(camino)));
                return new ResponseEntity<>(caminoOptimo, HttpStatus.OK);
            }

            for (Camino camino : caminos) {
                if (camino.getIdOrigen() == idActual) {
                    long vecino = camino.getIdDestino();
                    double nuevaDistancia = distancias.get(idActual) + camino.getCosto();
                    if (nuevaDistancia < distancias.get(vecino)) {
                        distancias.put(vecino, nuevaDistancia);
                        previo.put(vecino, idActual);
                        colaPrioridad.add(new Nodo(vecino, nuevaDistancia));
                    }
                }
            }
        }

        return new ResponseEntity<>(new CaminoOptimo(Collections.emptyList(), 0, "No existe camino para llegar a la estacion de destino"), HttpStatus.BAD_REQUEST);
    }

    public List<Long> listaIdEstaciones(List<Estacion> estaciones) {
        return estaciones.stream()
                .map(Estacion::getId)
                .collect(Collectors.toList());
    }
    

    public double obtenerCostoCamino(List<Long> idEstaciones) {
        double costo = 0;
        for (int i = 0; i < idEstaciones.size() - 1; i++) {
            long idOrigen = idEstaciones.get(i);
            long idDestino = idEstaciones.get(i + 1);
            Optional<Camino> camino = caminos.stream()
                    .filter(p -> p.getIdOrigen() == idOrigen && p.getIdDestino() == idDestino)
                    .findFirst();
            if (camino.isPresent()) {
                costo += camino.get().getCosto();
            } else {
                return Double.MAX_VALUE;
            }
        }
        return costo;
    }

    private static class Nodo {
        long id;
        double distancia;

        Nodo(long id, double distancia) {
            this.id = id;
            this.distancia = distancia;
        }
    }
}
