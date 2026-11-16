package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import org.junit.jupiter.api.Test;

class HudRendererTextoTest {
    @Test
    void preservaDificuldadeMunicaoEMiraEmFormatosCurtos() {
        assertEquals("Dif.: DIFICIL", HudRenderer.formatarDificuldade(Dificuldade.DIFICIL));
        assertEquals("Mun. 5   Mira >", HudRenderer.formatarMunicaoEMira(5, Acao.DIREITA));
    }
}
