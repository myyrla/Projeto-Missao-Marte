package br.edu.unifor.missaomarte.application.campanha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.Random;
import org.junit.jupiter.api.Test;

class PreparadorDaPartidaDaCampanhaTest {
    private static final int MIN = -5;
    private static final int MAX = 5;

    private final PreparadorDaPartidaDaCampanha preparador =
            new PreparadorDaPartidaDaCampanha(new MissaoFactory());

    @Test
    void preparaSessaoComPilotoConfiguracaoEPontuacaoDaEtapa() {
        GameSession sessao = preparador.preparar(
                CatalogoDaCampanha.etapa(1), "Comandante", MIN, MAX, new Random(10));

        assertEquals("Comandante", sessao.getPiloto());
        assertEquals(Dificuldade.FACIL, sessao.getDificuldade());
        assertEquals(ModoDeJogo.DINAMICO, sessao.getModo());
        assertEquals(1_000, sessao.getPontuacao());

        avancar(sessao, 4f);
        assertEquals(990, sessao.getPontuacao());
    }

    @Test
    void incluiNamiSomenteNasEtapasConfiguradas() {
        GameSession etapa1 = preparador.preparar(
                CatalogoDaCampanha.etapa(1), "Piloto", MIN, MAX, new Random(20));
        GameSession etapa2 = preparador.preparar(
                CatalogoDaCampanha.etapa(2), "Piloto", MIN, MAX, new Random(20));

        assertTrue(etapa1.getMissao().getEstacoesNami().isEmpty());
        assertEquals(1, etapa2.getMissao().getEstacoesNami().size());
    }

    @Test
    void entregaMecanicaDaTempestadeParaASessaoDaEtapaTres() {
        GameSession etapa2 = preparador.preparar(
                CatalogoDaCampanha.etapa(2), "Piloto", MIN, MAX, new Random(30));
        GameSession etapa3 = preparador.preparar(
                CatalogoDaCampanha.etapa(3), "Piloto", MIN, MAX, new Random(30));

        avancar(etapa2, 13.1f);
        avancar(etapa3, 13.1f);

        Posicao distante = new Posicao(MAX, MAX);
        assertTrue(etapa2.estaVisivel(distante));
        assertFalse(etapa3.estaVisivel(distante));
    }

    private static void avancar(GameSession sessao, float segundos) {
        int passos = Math.round(segundos / 0.1f);
        for (int i = 0; i < passos; i++) {
            sessao.avancar(0.1f);
        }
    }
}
