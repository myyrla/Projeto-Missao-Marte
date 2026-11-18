package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.domain.ParadaDoRover;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.List;
import org.junit.jupiter.api.Test;

class RotasDoRoverTest {
    @Test
    void produzUmaRotaPorParSemDuplicarOSentidoInverso() {
        List<ParadaDoRover> paradas = List.of(
                parada("A", 1, 0, 4, 0), parada("A", 4, 0, 1, 0),
                parada("B", -2, 3, 2, -3), parada("B", 2, -3, -2, 3));

        List<RotasDoRover.Rota> rotas = RotasDoRover.agrupar(paradas);

        assertEquals(2, rotas.size());
        assertEquals("A", rotas.get(0).par());
        assertEquals(new Posicao(1, 0), rotas.get(0).origem());
        assertEquals(new Posicao(4, 0), rotas.get(0).destino());
        assertEquals("B", rotas.get(1).par());
    }

    private static ParadaDoRover parada(String par, int x, int y, int destinoX, int destinoY) {
        return new ParadaDoRover(par, new Posicao(x, y), new Posicao(destinoX, destinoY));
    }
}
