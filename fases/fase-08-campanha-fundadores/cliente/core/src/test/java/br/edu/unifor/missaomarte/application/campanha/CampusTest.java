package br.edu.unifor.missaomarte.application.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import org.junit.jupiter.api.Test;

class CampusTest {
    @Test
    void todasAsConstrucoesComecamEmPlanta() {
        Campus campus = Campanha.nova().campus();

        for (Construcao construcao : Construcao.values()) {
            assertEquals(EstadoDaConstrucao.PLANTA, campus.estado(construcao));
        }
    }

    @Test
    void vitoriaDoAstronautaConstroiSomenteAsConstrucoesDele() {
        Campanha campanha = Campanha.nova();
        campanha.registrarResultado(1, EstadoPartida.VITORIA, 30);

        Campus campus = campanha.campus();
        assertEquals(EstadoDaConstrucao.CONSTRUIDA, campus.estado(Construcao.TORRE_DE_COMUNICACAO));
        assertEquals(EstadoDaConstrucao.CONSTRUIDA, campus.estado(Construcao.ZONAS_DE_POUSO));
        assertEquals(EstadoDaConstrucao.PLANTA, campus.estado(Construcao.BIBLIOTECA));
        assertEquals(EstadoDaConstrucao.PLANTA, campus.estado(Construcao.REITORIA));
    }

    @Test
    void conclusaoInauguraAReitoria() {
        Campanha campanha = Campanha.nova();
        campanha.registrarResultado(1, EstadoPartida.VITORIA, 30);
        campanha.registrarResultado(2, EstadoPartida.VITORIA, 40);
        campanha.registrarResultado(3, EstadoPartida.VITORIA, 50);

        assertEquals(EstadoDaConstrucao.INAUGURADA, campanha.campus().estado(Construcao.REITORIA));
    }
}
