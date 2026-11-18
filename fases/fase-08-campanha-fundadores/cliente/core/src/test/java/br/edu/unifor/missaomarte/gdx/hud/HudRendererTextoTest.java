package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.gdx.ui.IconeDaInterface;
import org.junit.jupiter.api.Test;

class HudRendererTextoTest {
    @Test
    void preservaDificuldadeMunicaoEMiraEmFormatosCurtos() {
        assertEquals("Dif.: DIFÍCIL", HudRenderer.formatarDificuldade(Dificuldade.DIFICIL));
        assertEquals("Mun. 5   Mira >", HudRenderer.formatarMunicaoEMira(5, Acao.DIREITA));
    }

    @Test
    void painelIdentificaConvidadoENomesDeDificuldadeComAcento() {
        assertEquals("CONVIDADO · FÁCIL", HudRenderer.tituloDoPiloto("Piloto", Dificuldade.FACIL));
        assertEquals("PILOTO MYRLA · MÉDIO", HudRenderer.tituloDoPiloto("Myrla", Dificuldade.MEDIO));
        assertEquals("PILOTO CRIS · DIFÍCIL", HudRenderer.tituloDoPiloto("Cris", Dificuldade.DIFICIL));
    }

    @Test
    void partidaRapidaUsaIconesPrevistosNasTresFichas() {
        assertEquals(IconeDaInterface.ASTRONAUTA, HudRenderer.iconeDaPartidaRapida(0));
        assertEquals(IconeDaInterface.CHECK, HudRenderer.iconeDaPartidaRapida(1));
        assertEquals(IconeDaInterface.SETA, HudRenderer.iconeDaPartidaRapida(2));
    }

    @Test
    void seloDaTempestadeTemTextoFixoSemUsarOsAvisosTransitorios() {
        assertEquals("TEMPESTADE DE AREIA", HudRenderer.textoDaTempestade());
    }
}
