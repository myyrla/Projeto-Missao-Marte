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
import br.edu.unifor.missaomarte.gdx.hud.LayoutDoHud;
import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
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
                List.of(professor, astronauta), null, Set.of(), List.of());

        assertSame(astronauta, resultado.alvo().orElseThrow());
        assertEquals("ASTRONAUTA · 1 CASA", resultado.textoDaEtiqueta());
    }

    @Test
    void campanhaPrefereOFundadorMesmoComPassageiroDoMesmoTipoMaisProximo() {
        Astronauta astronauta = new Astronauta("Bia", 0, 1);
        Professor professorMaisProximo = new Professor("Ada", 6, 0);
        Professor fundador = new Professor("Fundador Professor", 4, 3);

        PoliticaDeDestaque.Resultado comFundador = decidir(
                List.of(astronauta, professorMaisProximo, fundador), PoliticaDeDestaque.Tipo.PROFESSOR,
                Set.of(), List.of());
        PoliticaDeDestaque.Resultado semFundador = decidir(
                List.of(astronauta), PoliticaDeDestaque.Tipo.PROFESSOR,
                Set.of(), List.of());

        assertSame(fundador, comFundador.alvo().orElseThrow());
        assertEquals("PROFESSOR · 7 CASAS", comFundador.textoDaEtiqueta());
        assertSame(fundador, comFundador.alvoDoObjetivo().orElseThrow().passageiro());
        assertEquals(7, comFundador.alvoDoObjetivo().orElseThrow().distancia());
        assertSame(astronauta, semFundador.alvo().orElseThrow());
    }

    @Test
    void tipoResgatadoFicaTransparenteENaoRecebeEtiqueta() {
        Astronauta resgatado = new Astronauta("Bia", 0, 1);
        Engenheiro pendente = new Engenheiro("Caio", 3, 0);

        PoliticaDeDestaque.Resultado resultado = decidir(
                List.of(resgatado, pendente), null, Set.of(PoliticaDeDestaque.Tipo.ASTRONAUTA),
                List.of());

        assertSame(pendente, resultado.alvo().orElseThrow());
        assertEquals(0.60f, resultado.opacidade(resgatado), 0.001f);
        assertEquals(1f, resultado.opacidade(pendente), 0.001f);

        PoliticaDeDestaque.Resultado somenteResgatado = decidir(
                List.of(resgatado), null, Set.of(PoliticaDeDestaque.Tipo.ASTRONAUTA),
                List.of());
        assertTrue(somenteResgatado.alvo().isEmpty());
        assertTrue(somenteResgatado.textoDaEtiqueta().isEmpty());
    }

    @Test
    void alvoInvisivelPodePerderEtiquetaEPulsoSemApagarOsOutrosDestaques() {
        Astronauta astronauta = new Astronauta("Bia", 0, 1);
        Inimigo inimigo = new Inimigo(2, 0);
        PoliticaDeDestaque.Resultado resultado = decidir(
                List.of(astronauta), null, Set.of(), List.of(inimigo));

        PoliticaDeDestaque.Resultado semAlvo = resultado.semAlvoDestacado();

        assertTrue(semAlvo.alvo().isEmpty());
        assertTrue(semAlvo.textoDaEtiqueta().isEmpty());
        assertSame(astronauta, semAlvo.alvoDoObjetivo().orElseThrow().passageiro());
        assertEquals(1, semAlvo.alvoDoObjetivo().orElseThrow().distancia());
        assertEquals(0.5f, semAlvo.opacidadeDaZona(inimigo), 0.001f);
    }

    @Test
    void zonaDePerigoSoApareceAteTresCasasComOpacidadeProgressiva() {
        Inimigo naMesmaCasa = new Inimigo(0, 0);
        Inimigo aTresCasas = new Inimigo(3, 0);
        Inimigo aQuatroCasas = new Inimigo(4, 0);

        PoliticaDeDestaque.Resultado resultado = decidir(List.of(), null, Set.of(),
                List.of(naMesmaCasa, aTresCasas, aQuatroCasas));

        assertEquals(1f, resultado.opacidadeDaZona(naMesmaCasa), 0.001f);
        assertEquals(0.25f, resultado.opacidadeDaZona(aTresCasas), 0.001f);
        assertEquals(0f, resultado.opacidadeDaZona(aQuatroCasas), 0.001f);
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
    void etiquetaEvitaAsNovasColunasDoHud() {
        RetanguloDeTela mapa = new RetanguloDeTela(280f, 0f, 720f, 720f);
        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, 1280f, 720f);
        LayoutDoHud layout = LayoutDoHud.calcular(1280f, 720f, 11, false,
                MargensSeguras.NENHUMA, mapa);
        RetanguloDeTela alvo = new RetanguloDeTela(282f, 330f, 40f, 40f);

        RetanguloDeTela etiqueta = PoliticaDeDestaque.posicionarEtiqueta(
                alvo, 100f, 30f, 8f, tela, layout.areasDoHud(true, true)).orElseThrow();

        assertRetangulo(new RetanguloDeTela(330f, 335f, 100f, 30f), etiqueta);
        assertTrue(layout.areasDoHud(true, true).stream().noneMatch(etiqueta::sobrepoe));
    }

    @Test
    void etiquetaTambemEvitaORetanguloDaNave() {
        RetanguloDeTela alvo = new RetanguloDeTela(130f, 130f, 40f, 40f);
        RetanguloDeTela tela = new RetanguloDeTela(0f, 0f, 300f, 300f);
        RetanguloDeTela nave = new RetanguloDeTela(95f, 175f, 110f, 40f);

        List<RetanguloDeTela> proibidas = PoliticaDeDestaque.areasEvitadas(
                List.of(), nave);
        RetanguloDeTela etiqueta = PoliticaDeDestaque.posicionarEtiqueta(
                alvo, 100f, 30f, 8f, tela, proibidas).orElseThrow();

        assertRetangulo(new RetanguloDeTela(100f, 92f, 100f, 30f), etiqueta);
        assertFalse(etiqueta.sobrepoe(nave));
    }

    @Test
    void textoUsaDistanciaManhattanNoPlural() {
        Professor professor = new Professor("Ada", 2, 3);

        PoliticaDeDestaque.Resultado resultado = decidir(
                List.of(professor), null, Set.of(), List.of());

        assertEquals("PROFESSOR · 5 CASAS", resultado.textoDaEtiqueta());
    }

    private static PoliticaDeDestaque.Resultado decidir(List<? extends Passageiro> passageiros,
            PoliticaDeDestaque.Tipo tipoDoFundador, Set<PoliticaDeDestaque.Tipo> tiposResgatados,
            List<Inimigo> inimigos) {
        return PoliticaDeDestaque.decidir(NAVE, passageiros, tipoDoFundador, tiposResgatados, inimigos);
    }

    private static void assertRetangulo(RetanguloDeTela esperado, RetanguloDeTela atual) {
        assertEquals(esperado.x(), atual.x(), 0.001f);
        assertEquals(esperado.y(), atual.y(), 0.001f);
        assertEquals(esperado.largura(), atual.largura(), 0.001f);
        assertEquals(esperado.altura(), atual.altura(), 0.001f);
    }
}
