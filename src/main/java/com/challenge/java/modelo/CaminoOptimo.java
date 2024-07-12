package com.challenge.java.modelo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaminoOptimo {
    private List<Estacion> estaciones;
    private double costoTotal;
    private String mensaje;



    public CaminoOptimo(List<Estacion> estaciones, double costoTotal){
        this.estaciones = estaciones;
        this.costoTotal = costoTotal;
    }

}