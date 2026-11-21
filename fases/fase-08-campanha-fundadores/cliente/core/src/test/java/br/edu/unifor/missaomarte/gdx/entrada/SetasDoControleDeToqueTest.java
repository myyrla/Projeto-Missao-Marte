package br.edu.unifor.missaomarte.gdx.entrada;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SetasDoControleDeToqueTest {
    @Test
    void associaCadaDirecaoARotacaoDoIcone() {
        assertEquals(90f, SetasDoControleDeToque.rotacao(ControleDeToque.CIMA).orElseThrow(), 0f);
        assertEquals(180f, SetasDoControleDeToque.rotacao(ControleDeToque.ESQUERDA).orElseThrow(), 0f);
        assertEquals(0f, SetasDoControleDeToque.rotacao(ControleDeToque.DIREITA).orElseThrow(), 0f);
        assertEquals(270f, SetasDoControleDeToque.rotacao(ControleDeToque.BAIXO).orElseThrow(), 0f);
    }

    @Test
    void botoesQueNaoSaoDirecionaisContinuamComTexto() {
        assertTrue(SetasDoControleDeToque.rotacao(ControleDeToque.ATIRAR).isEmpty());
        assertTrue(SetasDoControleDeToque.rotacao(ControleDeToque.PAUSA).isEmpty());
    }
}
