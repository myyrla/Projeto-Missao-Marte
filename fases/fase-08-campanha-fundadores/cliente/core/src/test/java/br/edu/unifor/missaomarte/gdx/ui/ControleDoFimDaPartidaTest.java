package br.edu.unifor.missaomarte.gdx.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ControleDoFimDaPartidaTest {
    @Test
    void iniciaEmTentarDeNovoETrocaOFocoHorizontalmente() {
        ControleDoFimDaPartida controle = new ControleDoFimDaPartida();

        assertEquals(ControleDoFimDaPartida.Acao.TENTAR_DE_NOVO, controle.acaoEmFoco());
        controle.direita();
        assertEquals(ControleDoFimDaPartida.Acao.SAIR, controle.acaoEmFoco());
        controle.esquerda();
        assertEquals(ControleDoFimDaPartida.Acao.TENTAR_DE_NOVO, controle.acaoEmFoco());
    }

    @Test
    void permiteFocarDiretamenteParaToque() {
        ControleDoFimDaPartida controle = new ControleDoFimDaPartida();

        controle.focar(ControleDoFimDaPartida.Acao.SAIR);

        assertEquals(ControleDoFimDaPartida.Acao.SAIR, controle.acaoEmFoco());
        assertEquals(1, controle.foco());
    }
}
