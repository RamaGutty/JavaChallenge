package com.challenge.java.servicio;


import java.util.*;

import org.springframework.stereotype.Service;

import com.challenge.java.modelo.Camino;
import com.challenge.java.modelo.Estacion;

@Service
public class ServicioViajes {
    private final Map<Long, Estacion> estaciones = new HashMap<>();
    private final List<Camino> caminos = new ArrayList<>();

    public void agregarEstacion(long id, String nombre) {
        estaciones.putIfAbsent(id, new Estacion(id, nombre));
    }

    public void agregarCamino(long id, long idOrigen, long idDestino, double costo) {
        Estacion origen = estaciones.get(idOrigen);
        Estacion destino = estaciones.get(idDestino);
        if (origen != null && destino != null) {
            caminos.add(new Camino(id, idOrigen, idDestino, costo));
            caminos.add(new Camino(id, idDestino, idOrigen, costo));
        }
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
