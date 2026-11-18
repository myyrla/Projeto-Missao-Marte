package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.edu.unifor.missaomarte.application.EventoJogo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.ParadaDoRover;
import br.edu.unifor.missaomarte.domain.Posicao;
import org.junit.jupiter.api.Test;

class AvisosDaCampanhaTest {
    @Test
    void informaRecuperacaoDeCascoEParadaDeDestino() {
        Missao missao = new Missao(new Nave(4, 0, 3, 1));
        missao.adicionarParadaDoRover(new ParadaDoRover("A", new Posicao(1, 0), new Posicao(4, 0)));
        missao.adicionarParadaDoRover(new ParadaDoRover("A", new Posicao(4, 0), new Posicao(1, 0)));

        assertEquals("+1 casco", AvisosDaCampanha.texto(EventoJogo.NAMI_ATIVADA, missao));
        assertEquals("Rover: parada A", AvisosDaCampanha.texto(EventoJogo.ROVER_UTILIZADO, missao));
        assertNull(AvisosDaCampanha.texto(EventoJogo.MOVIMENTO, missao));
    }
}
