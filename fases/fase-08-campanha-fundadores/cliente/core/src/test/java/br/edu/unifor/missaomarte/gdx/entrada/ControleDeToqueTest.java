package br.edu.unifor.missaomarte.gdx.entrada;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.gdx.layout.LayoutDaPartida;
import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ControleDeToqueTest {
    private static final float[][] MUNDOS = {
        {800f, 600f}, {1067f, 600f}, {1333f, 600f}, {800f, 900f}, {800f, 1067f}, {800f, 1422f}, {800f, 1778f}
    };

    private static ControleDeToque controles(float[] mundo, boolean dinamico, float alvoMinimo) {
        LayoutDaPartida layout = LayoutDaPartida.calcular(mundo[0], mundo[1], 11, true, MargensSeguras.NENHUMA);
        return new ControleDeToque(layout.controles(), layout.emRetrato(), dinamico, alvoMinimo);
    }

    private static ControleDeToque controlesVerticais(float altura, boolean dinamico,
            ControleDeToque.MedidasDosRotulos medidas) {
        LayoutDaPartida layout = LayoutDaPartida.calcular(
                800f, altura, 11, true, MargensSeguras.NENHUMA);
        return new ControleDeToque(layout.controles(), true, dinamico, 72f, medidas);
    }

    private static ControleDeToque.MedidasDosRotulos medidasDosRotulos(float fator) {
        float altura = 16f * fator;
        return new ControleDeToque.MedidasDosRotulos(Map.of(
                ControleDeToque.ATIRAR, new ControleDeToque.MedidaDoRotulo(42f * fator, altura),
                ControleDeToque.EMBARCAR, new ControleDeToque.MedidaDoRotulo(66f * fator, altura),
                ControleDeToque.PAUSA, new ControleDeToque.MedidaDoRotulo(40f * fator, altura),
                ControleDeToque.SOM, new ControleDeToque.MedidaDoRotulo(28f * fator, altura),
                ControleDeToque.ABORTAR, new ControleDeToque.MedidaDoRotulo(57f * fator, altura)));
    }

    private static BotaoVirtual botao(ControleDeToque c, String id) {
        for (BotaoVirtual b : c.getBotoes()) {
            if (b.getId().equals(id)) {
                return b;
            }
        }
        return null;
    }

    private static RetanguloDeTela retangulo(BotaoVirtual b) {
        return new RetanguloDeTela(b.getX(), b.getY(), b.getLargura(), b.getAltura());
    }

    private static float centroX(BotaoVirtual botao) {
        return botao.getX() + botao.getLargura() / 2f;
    }

    private static float centroY(BotaoVirtual botao) {
        return botao.getY() + botao.getAltura() / 2f;
    }

    private static void assertFolgaEntreCirculos(float x1, float y1, float raio1,
            float x2, float y2, float raio2, float folgaMinima) {
        float distancia = (float) Math.hypot(x2 - x1, y2 - y1);
        assertTrue(distancia - raio1 - raio2 >= folgaMinima - 0.001f,
                "folga de " + (distancia - raio1 - raio2));
    }

    @Test
    void botaoContemPontosDentroDoRetanguloIncluindoAsBordas() {
        BotaoVirtual b = new BotaoVirtual("x", "X", 10f, 20f, 30f, 40f);

        assertTrue(b.contem(10f, 20f));
        assertTrue(b.contem(40f, 60f));
        assertTrue(b.contem(25f, 40f));
        assertFalse(b.contem(9.9f, 40f));
        assertFalse(b.contem(25f, 60.1f));
    }

    @Test
    void modoDinamicoTemSetasTiroPausaSomEAbortarMasNaoEmbarcar() {
        ControleDeToque c = controles(MUNDOS[2], true, 53f);

        assertEquals(8, c.getBotoes().size());
        for (String id : new String[] {"cima", "baixo", "esquerda", "direita", "atirar", "pausa", "som", "abortar"}) {
            assertNotNull(botao(c, id), id);
        }
        assertNull(botao(c, "embarcar"));
    }

    @Test
    void modoClassicoTemEmbarcarEMantemAsFuncoesDaFase6() {
        ControleDeToque c = controles(MUNDOS[2], false, 53f);

        assertEquals(7, c.getBotoes().size());
        assertNotNull(botao(c, "embarcar"));
        assertNull(botao(c, "atirar"));
        assertNull(botao(c, "pausa"));
    }

    @Test
    void botoesFicamDentroDaAreaDeControles() {
        for (float[] mundo : MUNDOS) {
            for (boolean dinamico : new boolean[] {false, true}) {
                LayoutDaPartida layout = LayoutDaPartida.calcular(mundo[0], mundo[1], 11, true, MargensSeguras.NENHUMA);
                ControleDeToque c = new ControleDeToque(layout.controles(), layout.emRetrato(), dinamico, 53f);
                for (BotaoVirtual b : c.getBotoes()) {
                    assertTrue(retangulo(b).estaDentroDe(layout.controles()), b.getId() + " fora em " + mundo[0] + "x" + mundo[1]);
                }
            }
        }
    }

    @Test
    void botoesNaoSeSobrepoemEmNenhumaProporcao() {
        for (float[] mundo : MUNDOS) {
            for (boolean dinamico : new boolean[] {false, true}) {
                List<BotaoVirtual> lista = new ArrayList<>(controles(mundo, dinamico, 53f).getBotoes());
                for (BotaoVirtual a : lista) {
                    for (BotaoVirtual b : lista) {
                        if (a != b) {
                            assertFalse(retangulo(a).sobrepoe(retangulo(b)), a.getId() + " x " + b.getId() + " em " + mundo[0] + "x" + mundo[1]);
                        }
                    }
                }
            }
        }
    }

    @Test
    void cadaBotaoRespeitaOAlvoMinimoDeToque() {
        for (float[] mundo : MUNDOS) {
            for (float alvo : new float[] {32f, 53f, 70f}) {
                for (BotaoVirtual b : controles(mundo, true, alvo).getBotoes()) {
                    assertTrue(b.getLargura() >= alvo - 0.001f && b.getAltura() >= alvo - 0.001f,
                            b.getId() + " menor que " + alvo + " em " + mundo[0] + "x" + mundo[1]);
                }
            }
        }
    }

    @Test
    void controlesRedondosDensosRespeitamOAlvoMinimoSemSobreposicao() {
        float alvoMinimo = 107f;
        RetanguloDeTela area = new RetanguloDeTela(0f, 0f, 800f, 400f);
        ControleDeToque controles = new ControleDeToque(area, true, true, alvoMinimo);
        List<BotaoVirtual> lista = new ArrayList<>(controles.getBotoes());

        for (BotaoVirtual botao : lista) {
            assertTrue(botao.getLargura() >= alvoMinimo && botao.getAltura() >= alvoMinimo,
                    botao.getId());
        }
        for (BotaoVirtual a : lista) {
            for (BotaoVirtual b : lista) {
                if (a != b) {
                    assertFalse(retangulo(a).sobrepoe(retangulo(b)), a.getId() + " x " + b.getId());
                }
            }
        }
        assertEquals(botao(controles, ControleDeToque.ABORTAR).getLargura(),
                botao(controles, ControleDeToque.ABORTAR).getAltura(), 0.001f);
    }

    @Test
    void asSetasContinuamEmCruzNaPaisagemAteASegundaEtapa() {
        ControleDeToque c = controles(MUNDOS[2], true, 53f);
        BotaoVirtual cima = botao(c, "cima");
        BotaoVirtual baixo = botao(c, "baixo");
        BotaoVirtual esquerda = botao(c, "esquerda");
        BotaoVirtual direita = botao(c, "direita");

        assertTrue(cima.getY() > esquerda.getY() && esquerda.getY() > baixo.getY());
        assertEquals(cima.getX(), baixo.getX(), 0.001f);
        assertEquals(esquerda.getY(), direita.getY(), 0.001f);
        assertTrue(esquerda.getX() < cima.getX() && cima.getX() < direita.getX());
        assertTrue(c.joystick().isEmpty());
    }

    @Test
    void emRetratoOJoystickFicaAEsquerdaEAsAcoesRedondasADireita() {
        ControleDeToque c = controles(MUNDOS[5], true, 53f);
        JoystickVirtual joystick = c.joystick().orElseThrow();

        assertEquals(4, c.getBotoes().size());
        assertNull(botao(c, "cima"));
        assertNull(botao(c, "baixo"));
        assertNull(botao(c, "esquerda"));
        assertNull(botao(c, "direita"));
        assertTrue(joystick.centroX() + joystick.raio() <= botao(c, "atirar").getX());
        assertTrue(botao(c, "atirar").getX() > 400f);
        for (BotaoVirtual b : c.getBotoes()) {
            assertEquals(b.getLargura(), b.getAltura(), 0.001f, b.getId());
        }
        assertTrue(botao(c, "atirar").getLargura() > botao(c, "pausa").getLargura());
    }

    @Test
    void emRetratoClassicoTemJoystickEmbarcarSomEAbortar() {
        ControleDeToque c = controles(MUNDOS[5], false, 53f);

        assertTrue(c.joystick().isPresent());
        assertEquals(3, c.getBotoes().size());
        assertNotNull(botao(c, "embarcar"));
        assertNotNull(botao(c, "som"));
        assertNotNull(botao(c, "abortar"));
        assertNull(botao(c, "pausa"));
        assertNull(botao(c, "atirar"));
    }

    @Test
    void permiteManterJoystickEAtirarComOutroPonteiro() {
        ControleDeToque c = controles(MUNDOS[5], true, 53f);
        JoystickVirtual joystick = c.joystick().orElseThrow();
        BotaoVirtual atirar = botao(c, "atirar");

        ControleDeToque.ResultadoDaPressao movimento = c.pressionar(
                0, joystick.centroX() + joystick.raio() * 0.8f, joystick.centroY());
        ControleDeToque.ResultadoDaPressao tiro = c.pressionar(
                1, atirar.getX() + atirar.getLargura() / 2f,
                atirar.getY() + atirar.getAltura() / 2f);
        JoystickVirtual.Resultado solturaDoTiro = c.soltar(1);

        assertTrue(movimento.tratado());
        assertEquals(Optional.empty(), movimento.botao());
        assertEquals(Acao.DIREITA, joystick.direcao());
        assertEquals(Optional.of(ControleDeToque.ATIRAR), tiro.botao());
        assertTrue(solturaDoTiro.comandos().isEmpty());
        assertEquals(0, joystick.ponteiroAtivo());
        assertEquals(Acao.DIREITA, joystick.direcao());
    }

    @Test
    void joystickEBotoesFicamDentroDaFaixaVerticalSemSeSobrepor() {
        for (float[] mundo : new float[][] {MUNDOS[3], MUNDOS[4], MUNDOS[5], MUNDOS[6]}) {
            for (float alvo : new float[] {48f, 72f, 107f}) {
                LayoutDaPartida layout = LayoutDaPartida.calcular(
                        mundo[0], mundo[1], 11, true, MargensSeguras.NENHUMA);
                ControleDeToque controle = new ControleDeToque(
                        layout.controles(), true, true, alvo);
                JoystickVirtual joystick = controle.joystick().orElseThrow();
                RetanguloDeTela base = new RetanguloDeTela(
                        joystick.centroX() - joystick.raio(),
                        joystick.centroY() - joystick.raio(),
                        joystick.raio() * 2f, joystick.raio() * 2f);

                assertTrue(base.estaDentroDe(layout.controles()), "base fora em " + mundo[1]);
                for (BotaoVirtual botao : controle.getBotoes()) {
                    RetanguloDeTela areaDoBotao = retangulo(botao);
                    assertTrue(areaDoBotao.estaDentroDe(layout.controles()), botao.getId());
                    assertFalse(base.sobrepoe(areaDoBotao), "joystick x " + botao.getId());
                }
            }
        }
    }

    @Test
    void rotulosAmpliadosCabemNosCirculosEmAmbasAsAlturasDoRetrato() {
        ControleDeToque.MedidasDosRotulos medidas = medidasDosRotulos(1.6f);
        for (float altura : new float[] {1778f, 1326f}) {
            for (boolean dinamico : new boolean[] {false, true}) {
                ControleDeToque controle = controlesVerticais(altura, dinamico, medidas);
                for (BotaoVirtual botao : controle.getBotoes()) {
                    ControleDeToque.MedidaDoRotulo medida = medidas.de(botao.getId());
                    float diagonal = (float) Math.hypot(medida.largura(), medida.altura());
                    assertTrue(diagonal + 20f <= botao.getLargura() + 0.001f,
                            botao.getId() + " vaza em 800x" + altura);
                }
            }
        }
    }

    @Test
    void circulosAmpliadosMantemEspacoMinimoEmAmbasAsAlturasDoRetrato() {
        ControleDeToque.MedidasDosRotulos medidas = medidasDosRotulos(1.6f);
        for (float altura : new float[] {1778f, 1326f}) {
            for (boolean dinamico : new boolean[] {false, true}) {
                ControleDeToque controle = controlesVerticais(altura, dinamico, medidas);
                List<BotaoVirtual> botoes = controle.getBotoes();
                JoystickVirtual joystick = controle.joystick().orElseThrow();
                for (int i = 0; i < botoes.size(); i++) {
                    BotaoVirtual atual = botoes.get(i);
                    assertFolgaEntreCirculos(joystick.centroX(), joystick.centroY(), joystick.raio(),
                            centroX(atual), centroY(atual), atual.getLargura() / 2f, 16f);
                    for (int j = i + 1; j < botoes.size(); j++) {
                        BotaoVirtual outro = botoes.get(j);
                        assertFolgaEntreCirculos(centroX(atual), centroY(atual), atual.getLargura() / 2f,
                                centroX(outro), centroY(outro), outro.getLargura() / 2f, 16f);
                    }
                }
            }
        }
    }

    @Test
    void grupoDeAcoesFicaADireitaComPrincipalAcimaDosSecundarios() {
        ControleDeToque.MedidasDosRotulos medidas = medidasDosRotulos(1.6f);
        for (boolean dinamico : new boolean[] {false, true}) {
            ControleDeToque controle = controlesVerticais(1326f, dinamico, medidas);
            BotaoVirtual principal = botao(controle,
                    dinamico ? ControleDeToque.ATIRAR : ControleDeToque.EMBARCAR);
            float maiorYSecundario = controle.getBotoes().stream()
                    .filter(candidato -> candidato != principal)
                    .map(BotaoVirtual::getY)
                    .max(Float::compare)
                    .orElseThrow();

            assertTrue(principal.getX() > 400f);
            assertTrue(principal.getY() > maiorYSecundario);
        }
    }

    @Test
    void emDevolveOBotaoSobOPontoOuNull() {
        ControleDeToque c = controles(MUNDOS[2], true, 53f);
        BotaoVirtual atirar = botao(c, "atirar");

        assertEquals("atirar", c.em(atirar.getX() + 5f, atirar.getY() + 5f).getId());
        assertNull(c.em(0f, 0f));
    }

    @Test
    void traduzBotaoEmAcao() {
        assertEquals(Acao.CIMA, ControleDeToque.acaoDe("cima"));
        assertEquals(Acao.BAIXO, ControleDeToque.acaoDe("baixo"));
        assertEquals(Acao.ESQUERDA, ControleDeToque.acaoDe("esquerda"));
        assertEquals(Acao.DIREITA, ControleDeToque.acaoDe("direita"));
        assertEquals(Acao.EMBARCAR, ControleDeToque.acaoDe("embarcar"));
        assertEquals(Acao.ATIRAR, ControleDeToque.acaoDe("atirar"));
        assertEquals(Acao.SAIR, ControleDeToque.acaoDe("abortar"));
        assertEquals(Acao.INVALIDA, ControleDeToque.acaoDe("som"));
        assertEquals(Acao.INVALIDA, ControleDeToque.acaoDe("pausa"));
        assertEquals(Acao.INVALIDA, ControleDeToque.acaoDe("qualquer"));
    }

    @Test
    void soAsSetasRepetem() {
        assertTrue(ControleDeToque.repete("cima"));
        assertTrue(ControleDeToque.repete("direita"));
        assertTrue(ControleDeToque.ehSeta("baixo"));
        assertFalse(ControleDeToque.repete("embarcar"));
        assertFalse(ControleDeToque.repete("atirar"));
        assertFalse(ControleDeToque.repete("pausa"));
        assertFalse(ControleDeToque.repete("som"));
    }
}
