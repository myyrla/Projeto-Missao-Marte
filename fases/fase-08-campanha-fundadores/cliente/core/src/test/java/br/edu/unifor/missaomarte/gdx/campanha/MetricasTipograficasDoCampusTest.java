package br.edu.unifor.missaomarte.gdx.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class MetricasTipograficasDoCampusTest {
    @Test
    void umaLinhaUsaGetLineHeightMesmoQuandoOsGlifosSaoMenores() {
        assertEquals(33.6f, MetricasTipograficasDoCampus.alturaEmLinhas(
                25f, 25f, 33.6f), 0.001f);
    }

    @Test
    void textoQuebradoUsaUmaAlturaDeLinhaParaCadaLinha() {
        assertEquals(67.2f, MetricasTipograficasDoCampus.alturaEmLinhas(
                58.6f, 25f, 33.6f), 0.001f);
    }

    @Test
    void espacamentoAcompanhaOFatorTipografico() {
        assertEquals(6.4f, MetricasTipograficasDoCampus.escalar(
                4f, 33.6f, 21f), 0.001f);
    }

    @Test
    void cartaoComUmaLinhaUsaAlturaExataNaEscalaUmPontoSeis() {
        MedidasDoCartao medidas = MetricasTipograficasDoCampus.medidasDoCartao(
                24f, 24f, 32f,
                List.of(25f), 25f, 33.6f);

        assertEquals(107.2f, medidas.alturaTotal(), 0.001f);
        assertEquals(32f, medidas.alturaDoTitulo(), 0.001f);
        assertEquals(33.6f, medidas.alturasDoConteudo().get(0), 0.001f);
    }
}
