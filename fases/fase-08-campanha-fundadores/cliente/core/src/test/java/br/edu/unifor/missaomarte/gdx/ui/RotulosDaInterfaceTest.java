package br.edu.unifor.missaomarte.gdx.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import org.junit.jupiter.api.Test;

class RotulosDaInterfaceTest {
    @Test
    void formataEnumsDeDificuldadeSemAlterarSeusNomesTecnicos() {
        assertEquals("FÁCIL", RotulosDaInterface.dificuldade(Dificuldade.FACIL));
        assertEquals("MÉDIO", RotulosDaInterface.dificuldade(Dificuldade.MEDIO));
        assertEquals("DIFÍCIL", RotulosDaInterface.dificuldade(Dificuldade.DIFICIL));

        assertEquals("FACIL", Dificuldade.FACIL.name());
        assertEquals("MEDIO", Dificuldade.MEDIO.name());
        assertEquals("DIFICIL", Dificuldade.DIFICIL.name());
    }

    @Test
    void formataValoresTecnicosRecebidosDoServidorSomenteParaExibicao() {
        assertEquals("FÁCIL", RotulosDaInterface.dificuldade("FACIL"));
        assertEquals("MÉDIO", RotulosDaInterface.dificuldade("MEDIO"));
        assertEquals("DIFÍCIL", RotulosDaInterface.dificuldade("DIFICIL"));
    }
}
