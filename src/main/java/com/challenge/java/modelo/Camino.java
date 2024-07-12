package com.challenge.java.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Camino {
    private long id;
    private long idOrigen;
    private long idDestino;
    private double costo;
}
