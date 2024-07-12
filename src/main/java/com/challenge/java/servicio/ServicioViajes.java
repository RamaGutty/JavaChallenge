package com.challenge.java.servicio;


import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.challenge.java.modelo.Camino;
import com.challenge.java.modelo.Estacion;

@Service
public class ServicioViajes {
    private final Map<Long, Estacion> estaciones = new HashMap<>();
    private final List<Camino> caminos = new ArrayList<>();

    public ResponseEntity<String> agregarEstacion(long id, String nombre) {
        if (existeEstacion(id)) {
            return new ResponseEntity<>("La estacion con ID " + id + " ya existe.", HttpStatus.OK);
        }else {
            estaciones.put(id, new Estacion(id, nombre));
            return new ResponseEntity<>("Estación " + nombre + "creada con éxito", HttpStatus.OK);
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

    public List<Estacion> encontrarCaminoOptimo(long idInicio, long idFin) {
        Map<Long, Double> distancias = new HashMap<>();
        Map<Long, Long> previo = new HashMap<>();
        PriorityQueue<Long> nodos = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));

        estaciones.keySet().forEach(id -> {
            if (id == idInicio) {
                distancias.put(id, 0.0);
            } else {
                distancias.put(id, Double.MAX_VALUE);
            }
            nodos.add(id);
        });

        while (!nodos.isEmpty()) {
            long menor = nodos.poll();

            if (menor == idFin) {
                List<Estacion> camino = new ArrayList<>();
                while (previo.containsKey(menor)) {
                    camino.add(estaciones.get(menor));
                    menor = previo.get(menor);
                }
                camino.add(estaciones.get(idInicio));
                Collections.reverse(camino);
                return camino;
            }

            if (distancias.get(menor) == Double.MAX_VALUE) {
                break;
            }

            for (Camino vecino : caminos) {
                if (vecino.getIdOrigen() == menor) {
                    long alt = vecino.getIdDestino();
                    double nuevaDist = distancias.get(menor) + vecino.getCosto();

                    if (nuevaDist < distancias.get(alt)) {
                        distancias.put(alt, nuevaDist);
                        previo.put(alt, menor);
                        nodos.add(alt);
                    }
                }
            }
        }

        return Collections.emptyList();
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
}
