package br.edu.unifor.missaomarte.application.pontuacao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Professor;
import org.junit.jupiter.api.Test;

class RegraDePontuacaoDaCampanhaTest {
    @Test
    void multiplicaARegraPadraoESomaOsBonusDaCampanha() {
        RegraDePontuacao regra = new RegraDePontuacaoDaCampanha(new RegraDePontuacaoPadrao(100));

        assertEquals(1000, regra.pontuacaoInicial());
        assertEquals(100, regra.pontosDoResgate(new Passageiro("Passageiro", 0, 0)));
        assertEquals(200, regra.pontosDoResgate(new Professor("Professor", 0, 0)));
        assertEquals(150, regra.pontosDoResgate(new Engenheiro("Engenheiro", 0, 0)));
        assertEquals(250, regra.pontosDoResgate(new Astronauta("Astronauta", 0, 0)));
        assertEquals(150, regra.pontosDaEntrega(3));
        assertEquals(10, regra.custoDoMovimento());
        assertEquals(10, regra.custoPorSegundo());
        assertEquals(20, regra.custoDoTiro());
        assertEquals(50, regra.custoDaColisao(1));
        assertEquals(300, regra.bonusDaVitoria(3));
    }

    @Test
    void derivaOsValoresDaRegraRecebidaSemRepetirOsNumerosPadrao() {
        RegraDePontuacao base = new RegraDePontuacao() {
            @Override public int pontuacaoInicial() { return 7; }
            @Override public int pontosDoResgate(Passageiro passageiro) { return 11; }
            @Override public int pontosDaEntrega(int passageiros) { return 13 * passageiros; }
            @Override public int custoDoMovimento() { return 17; }
            @Override public int custoPorSegundo() { return 19; }
            @Override public int custoDoTiro() { return 23; }
            @Override public int custoDaColisao(int perigos) { return 29 * perigos; }
            @Override public int bonusDaVitoria(int vidasRestantes) { return 31 * vidasRestantes; }
        };
        RegraDePontuacao regra = new RegraDePontuacaoDaCampanha(base);

        assertEquals(70, regra.pontuacaoInicial());
        assertEquals(110, regra.pontosDoResgate(new Passageiro("Teste", 0, 0)));
        assertEquals(360, regra.pontosDaEntrega(2));
        assertEquals(170, regra.custoDoMovimento());
        assertEquals(190, regra.custoPorSegundo());
        assertEquals(230, regra.custoDoTiro());
        assertEquals(580, regra.custoDaColisao(2));
        assertEquals(820, regra.bonusDaVitoria(2));
    }
}
