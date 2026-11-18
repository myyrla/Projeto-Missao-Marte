package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PoliticaDeDestaqueTest {
    private static final Posicao NAVE = new Posicao(0, 0);

    @Test
    void partidaRapidaEscolheSomenteOPassageiroMaisProximo() {
        Professor professor = new Professor("Ada", 3, 0);
        Astronauta astronauta = new Astronauta("Bia", 0, 1);

        PoliticaDeDestaque.Resultado resultado = decidir(
                List.of(professor, astronauta), null, Set.of(), List.of(), List.of());

        assertSame(astronauta, resultado.alvo().orElseThrow());
        assertEquals("ASTRONAUTA · 1 CASA", resultado.textoDaEtiqueta());
    }

    @Test
    void campanhaPrefereTipoDoFundadorEMudaParaOMaisProximoSeEleNaoExistir() {
        Astronauta astronauta = new Astronauta("Bia", 0, 1);
        Professor professorDistante = new Professor("Ada", 5, 5);
        Professor professor = new Professor("Fundador Professor", 4, 0);

        PoliticaDeDestaque.Resultado comFundador = decidir(
                List.of(astronauta, professorDistante, professor), PoliticaDeDestaque.Tipo.PROFESSOR,
                Set.of(), List.of(), List.of());
        PoliticaDeDestaque.Resultado semFundador = decidir(
                List.of(astronauta), PoliticaDeDestaque.Tipo.PROFESSOR,
                Set.of(), List.of(), List.of());

        assertSame(professor, comFundador.alvo().orElseThrow());
        assertSame(astronauta, semFundador.alvo().orElseThrow());
    }

    @Test
    void tipoResgatadoFicaTransparenteENaoRecebeEtiqueta() {
        Astronauta resgatado = new Astronauta("Bia", 0, 1);
        Engenheiro pendente = new Engenheiro("Caio", 3, 0);

        PoliticaDeDestaque.Resultado resultado = decidir(
                List.of(resgatado, pendente), null, Set.of(PoliticaDeDestaque.Tipo.ASTRONAUTA),
                List.of(), List.of());

        assertSame(pendente, resultado.alvo().orElseThrow());
        assertEquals(0.60f, resultado.opacidade(resgatado), 0.001f);
        assertEquals(1f, resultado.opacidade(pendente), 0.001f);

        PoliticaDeDestaque.Resultado somenteResgatado = decidir(
                List.of(resgatado), null, Set.of(PoliticaDeDestaque.Tipo.ASTRONAUTA),
                List.of(), List.of());
        assertTrue(somenteResgatado.alvo().isEmpty());
        assertTrue(somenteResgatado.textoDaEtiqueta().isEmpty());
    }

    @Test
    void alvoInvisivelPodePerderEtiquetaEPulsoSemApagarOsOutrosDestaques() {
        Astronauta astronauta = new Astronauta("Bia", 0, 1);
        Inimigo inimigo = new Inimigo(2, 0);
        RotasDoRover.Rota rota = new RotasDoRover.Rota("A", new Posicao(1, 0), new Posicao(4, 0));
        PoliticaDeDestaque.Resultado resultado = decidir(
                List.of(astronauta), null, Set.of(), List.of(inimigo), List.of(rota));

        PoliticaDeDestaque.Resultado semAlvo = resultado.semAlvoDestacado();

        assertTrue(semAlvo.alvo().isEmpty());
        assertTrue(semAlvo.textoDaEtiqueta().isEmpty());
        assertEquals(0.5f, semAlvo.opacidadeDaZona(inimigo), 0.001f);
        assertEquals("A", semAlvo.rota().orElseThrow().par());
    }

    @Test
    void zonaDePerigoSoApareceAteTresCasasComOpacidadeProgressiva() {
        Inimigo naMesmaCasa = new Inimigo(0, 0);
        Inimigo aTresCasas = new Inimigo(3, 0);
        Inimigo aQuatroCasas = new Inimigo(4, 0);

        PoliticaDeDestaque.Resultado resultado = decidir(List.of(), null, Set.of(),
                List.of(naMesmaCasa, aTresCasas, aQuatroCasas), List.of());

        assertEquals(1f, resultado.opacidadeDaZona(naMesmaCasa), 0.001f);
        assertEquals(0.25f, resultado.opacidadeDaZona(aTresCasas), 0.001f);
        assertEquals(0f, resultado.opacidadeDaZona(aQuatroCasas), 0.001f);
    }

    @Test
    void mostraSomenteRotaDaParadaEmQueANaveEstaOuDaVizinha() {
        RotasDoRover.Rota rotaA = new RotasDoRover.Rota("A", new Posicao(1, 0), new Posicao(4, 0));
        RotasDoRover.Rota rotaB = new RotasDoRover.Rota("B", new Posicao(-4, -4), new Posicao(-3, -4));

        PoliticaDeDestaque.Resultado vizinha = PoliticaDeDestaque.decidir(
                NAVE, List.of(), null, Set.of(), List.of(), List.of(rotaA, rotaB));
        PoliticaDeDestaque.Resultado sobre = PoliticaDeDestaque.decidir(
                new Posicao(4, 0), List.of(), null, Set.of(), List.of(), List.of(rotaA, rotaB));
        PoliticaDeDestaque.Resultado longe = PoliticaDeDestaque.decidir(
                new Posicao(0, 3), List.of(), null, Set.of(), List.of(), List.of(rotaA, rotaB));

        assertEquals("A", vizinha.rota().orElseThrow().par());
        assertEquals("A", sobre.rota().orElseThrow().par());
        assertTrue(longe.rota().isEmpty());
    }

    @Test
    void etiquetaTentaAcimaAbaixoDireitaEEsquerdaEIgnoraObjetosDoMapa() {
        RetanguloDeTela alvo = new RetanguloDeTela(130f, 130f, 40f, 40f);
        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, 300f, 300f);
        RetanguloDeTela acima = new RetanguloDeTela(100f, 178f, 100f, 30f);
        RetanguloDeTela abaixo = new RetanguloDeTela(100f, 92f, 100f, 30f);
        RetanguloDeTela direita = new RetanguloDeTela(178f, 135f, 100f, 30f);
        RetanguloDeTela esquerda = new RetanguloDeTela(22f, 135f, 100f, 30f);

        assertRetangulo(acima, PoliticaDeDestaque.posicionarEtiqueta(
                alvo, 100f, 30f, 8f, tela, List.of()).orElseThrow());
        assertRetangulo(abaixo, PoliticaDeDestaque.posicionarEtiqueta(
                alvo, 100f, 30f, 8f, tela, List.of(acima)).orElseThrow());
        assertRetangulo(direita, PoliticaDeDestaque.posicionarEtiqueta(
                alvo, 100f, 30f, 8f, tela, List.of(acima, abaixo)).orElseThrow());
        assertRetangulo(esquerda, PoliticaDeDestaque.posicionarEtiqueta(
                alvo, 100f, 30f, 8f, tela, List.of(acima, abaixo, direita)).orElseThrow());
    }

    @Test
    void omiteEtiquetaSeTodasAsPosicoesColidiremComHudOuSairemDaTela() {
        RetanguloDeTela alvo = new RetanguloDeTela(5f, 5f, 40f, 40f);
        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, 120f, 120f);
        RetanguloDeTela hud = new RetanguloDeTela(0f, 0f, 120f, 120f);

        assertTrue(PoliticaDeDestaque.posicionarEtiqueta(
                alvo, 100f, 30f, 8f, tela, List.of(hud)).isEmpty());
    }

    @Test
    void textoUsaDistanciaManhattanNoPlural() {
        Professor professor = new Professor("Ada", 2, 3);

        PoliticaDeDestaque.Resultado resultado = decidir(
                List.of(professor), null, Set.of(), List.of(), List.of());

        assertEquals("PROFESSOR · 5 CASAS", resultado.textoDaEtiqueta());
    }

    private static PoliticaDeDestaque.Resultado decidir(List<? extends Passageiro> passageiros,
            PoliticaDeDestaque.Tipo tipoDoFundador, Set<PoliticaDeDestaque.Tipo> tiposResgatados,
            List<Inimigo> inimigos, List<RotasDoRover.Rota> rotas) {
        return PoliticaDeDestaque.decidir(NAVE, passageiros, tipoDoFundador, tiposResgatados, inimigos, rotas);
    }

    private static void assertRetangulo(RetanguloDeTela esperado, RetanguloDeTela atual) {
        assertEquals(esperado.x(), atual.x(), 0.001f);
        assertEquals(esperado.y(), atual.y(), 0.001f);
        assertEquals(esperado.largura(), atual.largura(), 0.001f);
        assertEquals(esperado.altura(), atual.altura(), 0.001f);
    }
}
