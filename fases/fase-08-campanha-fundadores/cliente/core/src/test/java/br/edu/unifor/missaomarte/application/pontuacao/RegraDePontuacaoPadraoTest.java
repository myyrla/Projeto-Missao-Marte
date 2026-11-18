package br.edu.unifor.missaomarte.application.pontuacao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Professor;
import org.junit.jupiter.api.Test;

class RegraDePontuacaoPadraoTest {
    private final RegraDePontuacao regra = new RegraDePontuacaoPadrao(100);

    @Test
    void preservaTodosOsValoresDaPartidaRapida() {
        assertEquals(100, regra.pontuacaoInicial());
        assertEquals(10, regra.pontosDoResgate(new Passageiro("Passageiro", 0, 0)));
        assertEquals(20, regra.pontosDoResgate(new Professor("Professor", 0, 0)));
        assertEquals(15, regra.pontosDoResgate(new Engenheiro("Engenheiro", 0, 0)));
        assertEquals(25, regra.pontosDoResgate(new Astronauta("Astronauta", 0, 0)));
        assertEquals(0, regra.pontosDaEntrega(3));
        assertEquals(1, regra.custoDoMovimento());
        assertEquals(1, regra.custoPorSegundo());
        assertEquals(2, regra.custoDoTiro());
        assertEquals(5, regra.custoDaColisao(0));
        assertEquals(10, regra.custoDaColisao(2));
        assertEquals(0, regra.bonusDaVitoria(3));
    }
}
