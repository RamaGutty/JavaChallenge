package com.challenge.java;


import com.challenge.java.modelo.Camino;
import com.challenge.java.servicio.ServicioViajes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ControladorCaminosTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServicioViajes servicioViajes;

    @AfterEach
    public void limpiarDatos() {
        servicioViajes.limpiarDatos();
    }

    @Test
    public void agregarEstacionTest() throws Exception {
        mockMvc.perform(put("/api/estaciones/1")
                .param("nombre", "Madrid"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estación Madrid creada con éxito"));
    
        mockMvc.perform(put("/api/estaciones/2")
                .param("nombre", "París"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estación París creada con éxito"));
    
        mockMvc.perform(put("/api/estaciones/3")
                .param("nombre", "Londres"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estación Londres creada con éxito"));
    
        mockMvc.perform(put("/api/estaciones/4")
                .param("nombre", "Barcelona"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estación Barcelona creada con éxito"));
    }

    @Test
    public void agregarEstacionDuplicadaTest() throws Exception {
        mockMvc.perform(put("/api/estaciones/1")
                .param("nombre", "Madrid"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estación Madrid creada con éxito"));
    
                mockMvc.perform(put("/api/estaciones/1")
                .param("nombre", "Madrid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La estacion con ID 1 ya existe."));
    }

    @Test
    public void agregarCaminoTest() throws Exception {

    servicioViajes.agregarEstacion(1, "Madrid");
    servicioViajes.agregarEstacion(2, "París");
    servicioViajes.agregarEstacion(3, "Londres");
    servicioViajes.agregarEstacion(4, "Barcelona");

    mockMvc.perform(put("/api/caminos/1")
            .param("idOrigen", "1")
            .param("idDestino", "2")
            .param("costo", "50"))
            .andExpect(status().isOk())
            .andExpect(content().string("Camino agregado correctamente."));

    mockMvc.perform(put("/api/caminos/2")
            .param("idOrigen", "2")
            .param("idDestino", "3")
            .param("costo", "40"))
            .andExpect(status().isOk())
            .andExpect(content().string("Camino agregado correctamente."));

    mockMvc.perform(put("/api/caminos/3")
            .param("idOrigen", "3")
            .param("idDestino", "4")
            .param("costo", "60"))
            .andExpect(status().isOk())
            .andExpect(content().string("Camino agregado correctamente."));

    mockMvc.perform(put("/api/caminos/4")
            .param("idOrigen", "1")
            .param("idDestino", "4")
            .param("costo", "30"))
            .andExpect(status().isOk())
            .andExpect(content().string("Camino agregado correctamente."));
}
    @Test
    public void agregarCaminoDuplicadoTest() throws Exception {

        servicioViajes.agregarEstacion(1, "Madrid");
        servicioViajes.agregarEstacion(2, "París");

        mockMvc.perform(put("/api/caminos/1")
                .param("idOrigen", "1")
                .param("idDestino", "2")
                .param("costo", "50"))
                .andExpect(status().isOk())
                .andExpect(content().string("Camino agregado correctamente."));

        mockMvc.perform(put("/api/caminos/2")
                .param("idOrigen", "1")
                .param("idDestino", "2")
                .param("costo", "50"))
                .andExpect(status().isConflict())
                .andExpect(content().string("El camino entre la estación de origen y destino ya existe."));
}

    @Test
    public void encontrarCaminoOptimoTest() throws Exception {

        servicioViajes.agregarEstacion(1, "Madrid");
        servicioViajes.agregarEstacion(2, "París");
        servicioViajes.agregarEstacion(3, "Londres");
        servicioViajes.agregarEstacion(4, "Barcelona");

        servicioViajes.agregarCamino(1, 1, 2, 50.0);
        servicioViajes.agregarCamino(2, 2, 3, 40.0);
        servicioViajes.agregarCamino(3, 3, 4, 60.0);
        servicioViajes.agregarCamino(4, 1, 4, 30.0);

        mockMvc.perform(get("/api/caminos/1/4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.estaciones[0].id").value(1))
                .andExpect(jsonPath("$.estaciones[0].nombre").value("Madrid"))
                .andExpect(jsonPath("$.estaciones[1].id").value(4))
                .andExpect(jsonPath("$.estaciones[1].nombre").value("Barcelona"))
                .andExpect(jsonPath("$.costoTotal").value(30.0));

        mockMvc.perform(get("/api/caminos/2/4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.estaciones[0].id").value(2))
                .andExpect(jsonPath("$.estaciones[0].nombre").value("París"))
                .andExpect(jsonPath("$.estaciones[1].id").value(1))
                .andExpect(jsonPath("$.estaciones[1].nombre").value("Madrid"))
                .andExpect(jsonPath("$.estaciones[2].id").value(4))
                .andExpect(jsonPath("$.estaciones[2].nombre").value("Barcelona"))
                .andExpect(jsonPath("$.costoTotal").value(80));
    }

    @Test
    public void encontrarCaminoOptimoNoConectadoTest() throws Exception {

        servicioViajes.agregarEstacion(1, "Madrid");
        servicioViajes.agregarEstacion(2, "París");
        servicioViajes.agregarEstacion(3, "Londres");
        servicioViajes.agregarEstacion(4, "Barcelona");

        servicioViajes.agregarCamino(1, 1, 2, 50.0);
        servicioViajes.agregarCamino(2, 2, 3, 40.0);

        mockMvc.perform(get("/api/caminos/1/4"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").value("No existe camino para llegar a la estacion de destino"));
    }
    
}
