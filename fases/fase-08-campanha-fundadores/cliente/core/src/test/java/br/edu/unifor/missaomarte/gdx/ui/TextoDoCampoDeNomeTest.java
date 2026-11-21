package br.edu.unifor.missaomarte.gdx.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TextoDoCampoDeNomeTest {
    @Test
    void convidadoSemNomeRecebeExemploApagado() {
        TextoDoCampoDeNome texto = TextoDoCampoDeNome.de(false, "", "", "_");

        assertEquals("Toque para digitar", texto.conteudo());
        assertTrue(texto.exemplo());
    }

    @Test
    void nomeDigitadoEContaLogadaNaoUsamExemplo() {
        TextoDoCampoDeNome digitado = TextoDoCampoDeNome.de(false, "", "Myrla", "_");
        TextoDoCampoDeNome logado = TextoDoCampoDeNome.de(true, "Cristiano", "", "_");

        assertEquals("Myrla_", digitado.conteudo());
        assertEquals("Cristiano", logado.conteudo());
        assertFalse(digitado.exemplo());
        assertFalse(logado.exemplo());
    }
}
