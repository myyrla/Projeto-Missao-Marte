package br.edu.unifor.missaomarte.gdx.entrada;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.Acao;
import org.junit.jupiter.api.Test;

class RepeticaoDoMovimentoTest {

    @Test
    void usaOAtrasoEIntervaloDoTeclado() {
        RepeticaoDoMovimento repeticao = new RepeticaoDoMovimento();
        repeticao.iniciar(Acao.DIREITA);

        assertTrue(repeticao.atualizar(0.279f).isEmpty());
        assertEquals(Acao.DIREITA, repeticao.atualizar(0.001f).orElseThrow());
        assertTrue(repeticao.atualizar(0.139f).isEmpty());
        assertEquals(Acao.DIREITA, repeticao.atualizar(0.001f).orElseThrow());
    }

    @Test
    void mudarDeDirecaoReiniciaOAtrasoInicial() {
        RepeticaoDoMovimento repeticao = new RepeticaoDoMovimento();
        repeticao.iniciar(Acao.DIREITA);
        assertEquals(Acao.DIREITA, repeticao.atualizar(0.28f).orElseThrow());

        repeticao.iniciar(Acao.CIMA);

        assertTrue(repeticao.atualizar(0.279f).isEmpty());
        assertEquals(Acao.CIMA, repeticao.atualizar(0.001f).orElseThrow());
    }

    @Test
    void pararCancelaARepeticaoPendente() {
        RepeticaoDoMovimento repeticao = new RepeticaoDoMovimento();
        repeticao.iniciar(Acao.BAIXO);

        repeticao.parar();

        assertTrue(repeticao.atualizar(1f).isEmpty());
    }
}
