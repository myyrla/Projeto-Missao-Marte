package br.edu.unifor.missaomarte.gdx.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class MedidasDoCartaoTest {
    @Test
    void calculaAlturaExataDoConteudoComFatorUmPontoSeis() {
        MedidasDoCartao medidas = new MedidasDoCartao(
                17.6f, 32f, 6.4f, List.of(33.6f), 9.6f, 17.6f);

        assertEquals(107.2f, medidas.alturaTotal(), 0.001f);
    }

    @Test
    void separaOTituloDaPrimeiraLinhaDoConteudo() {
        MedidasDoCartao medidas = new MedidasDoCartao(
                17.6f, 32f, 6.4f, List.of(33.6f, 67.2f), 9.6f, 17.6f);
        float topoDoCartao = 500f;
        float baseDoTitulo = medidas.topoDoTitulo(topoDoCartao) - medidas.alturaDoTitulo();

        assertTrue(medidas.topoDoConteudo(topoDoCartao, 0) < baseDoTitulo);
        assertTrue(medidas.topoDoConteudo(topoDoCartao, 1)
                < medidas.topoDoConteudo(topoDoCartao, 0) - medidas.alturasDoConteudo().get(0));
    }
}
