package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.Input.Buttons;
import org.junit.jupiter.api.Test;

class TelaBasePonteiroTest {
    @Test
    void cliqueEsquerdoFicaDesligadoPorPadrao() {
        assertFalse(new TelaSemClique().aceitaClique());
    }

    @Test
    void desktopExigeOptInEToqueContinuaAceitandoPonteiros() {
        assertTrue(TelaBase.aceitaPonteiro(true, false, Buttons.RIGHT));
        assertFalse(TelaBase.aceitaPonteiro(false, false, Buttons.LEFT));
        assertTrue(TelaBase.aceitaPonteiro(false, true, Buttons.LEFT));
        assertFalse(TelaBase.aceitaPonteiro(false, true, Buttons.RIGHT));
    }

    private static final class TelaSemClique extends TelaBase {
        private TelaSemClique() {
            super(null);
        }

        @Override
        protected void desenhar(float delta) {
        }

        private boolean aceitaClique() {
            return aceitaCliqueEsquerdo();
        }
    }
}
