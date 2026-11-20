package br.edu.unifor.missaomarte.gdx.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import br.edu.unifor.missaomarte.gdx.ui.IconeDaInterface;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.visual.PoliticaDeDestaque;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HudRendererTextoTest {
    @Test
    void areaDoTextoDoAvisoMantemOMesmoCentroDaCaixa() {
        RetanguloDeTela caixa = new RetanguloDeTela(100f, 40f, 300f, 50f);

        RetanguloDeTela texto = HudRenderer.areaDoTextoDoAviso(caixa, 1f);

        assertEquals(112f, texto.x(), 0.001f);
        assertEquals(276f, texto.largura(), 0.001f);
        assertEquals(caixa.centroX(), texto.centroX(), 0.001f);
    }

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

    @Test
    void sinalDoHudUsaADistanciaCalculadaParaOAlvoDoObjetivo() {
        Professor maisProximo = new Professor("Ada", 6, 0);
        Professor fundador = new Professor("Fundador Professor", 4, 3);
        PoliticaDeDestaque.Resultado resultado = PoliticaDeDestaque.decidir(
                new Posicao(0, 0),
                List.of(maisProximo, fundador), PoliticaDeDestaque.Tipo.PROFESSOR, Set.of(), List.of());

        assertEquals("Sinal a 7 casas",
                HudRenderer.textoDoSinal(resultado.alvoDoObjetivo().orElseThrow()));
    }
}
