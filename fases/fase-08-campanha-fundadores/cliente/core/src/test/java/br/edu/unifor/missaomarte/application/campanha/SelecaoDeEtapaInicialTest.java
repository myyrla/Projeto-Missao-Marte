package br.edu.unifor.missaomarte.application.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SelecaoDeEtapaInicialTest {
    @Test
    void aceitaValoresDeUmATresComEspacos() {
        assertEquals(1, SelecaoDeEtapaInicial.interpretar("1").orElseThrow());
        assertEquals(2, SelecaoDeEtapaInicial.interpretar(" 2 ").orElseThrow());
        assertEquals(3, SelecaoDeEtapaInicial.interpretar("3").orElseThrow());
    }

    @Test
    void ignoraValorAusenteTextoEValorForaDoIntervalo() {
        assertTrue(SelecaoDeEtapaInicial.interpretar(null).isEmpty());
        assertTrue(SelecaoDeEtapaInicial.interpretar("").isEmpty());
        assertTrue(SelecaoDeEtapaInicial.interpretar("fase2").isEmpty());
        assertTrue(SelecaoDeEtapaInicial.interpretar("0").isEmpty());
        assertTrue(SelecaoDeEtapaInicial.interpretar("4").isEmpty());
    }
}
