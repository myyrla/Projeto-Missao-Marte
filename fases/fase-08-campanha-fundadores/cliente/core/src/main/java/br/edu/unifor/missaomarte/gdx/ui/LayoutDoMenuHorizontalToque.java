package br.edu.unifor.missaomarte.gdx.ui;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Geometria pura das opções e do rodapé do menu horizontal de toque. */
public final class LayoutDoMenuHorizontalToque {
    private static final float LARGURA_DE_REFERENCIA = 1280f;
    private static final float ALTURA_DE_REFERENCIA = 720f;
    private static final float MARGEM_INTERNA_HORIZONTAL = 20f;
    private static final float MARGEM_INTERNA_VERTICAL = 10f;
    private static final float ESPACO = 12f;

    private final RetanguloDeTela[] botoes;
    private final RetanguloDeTela[] rotulos;
    private final RetanguloDeTela detalheDaCampanha;
    private final RetanguloDeTela conta;
    private final RetanguloDeTela instrucao;

    private LayoutDoMenuHorizontalToque(RetanguloDeTela[] botoes,
            RetanguloDeTela[] rotulos, RetanguloDeTela detalheDaCampanha,
            RetanguloDeTela conta, RetanguloDeTela instrucao) {
        this.botoes = botoes;
        this.rotulos = rotulos;
        this.detalheDaCampanha = detalheDaCampanha;
        this.conta = conta;
        this.instrucao = instrucao;
    }

    public static LayoutDoMenuHorizontalToque calcular(float largura, float altura,
            MargensSeguras margens, MedidasDoMenuHorizontalToque medidas) {
        float escala = Math.min(largura / LARGURA_DE_REFERENCIA, altura / ALTURA_DE_REFERENCIA);
        float margemHorizontal = MARGEM_INTERNA_HORIZONTAL * escala;
        float margemVertical = MARGEM_INTERNA_VERTICAL * escala;
        float espaco = ESPACO * escala;
        float x = margens.esquerda() + 96f * escala;
        float larguraMaxima = Math.min(460f * escala,
                largura - margens.direita() - x - 24f * escala);
        float larguraEmLinha = medidas.largurasDasOpcoes().get(0)
                + medidas.larguraDoDetalhe() + espaco + 2f * margemHorizontal;
        float maiorRotulo = (float) medidas.largurasDasOpcoes().stream()
                .mapToDouble(Float::doubleValue).max().orElse(0d);
        float larguraDesejada = Math.max(320f * escala,
                Math.max(larguraEmLinha, maiorRotulo + 2f * margemHorizontal));
        larguraDesejada = Math.max(larguraDesejada,
                Math.max(medidas.larguraDaConta(), medidas.larguraDaInstrucao()));
        float larguraDoBotao = Math.min(larguraMaxima, larguraDesejada);
        boolean detalheEmLinha = larguraEmLinha <= larguraDoBotao;

        float alturaComum = Math.max(medidas.alvoMinimo(),
                medidas.alturaDaOpcao() + 2f * margemVertical);
        float alturaPrimeiro = detalheEmLinha ? alturaComum
                : Math.max(medidas.alvoMinimo(), medidas.alturaDaOpcao()
                        + medidas.alturaDoDetalhe() + 4f * escala + 2f * margemVertical);
        float topo = altura - margens.cima() - 376f * escala;
        RetanguloDeTela[] botoes = new RetanguloDeTela[4];
        RetanguloDeTela[] rotulos = new RetanguloDeTela[4];
        float cursor = topo;
        for (int i = 0; i < botoes.length; i++) {
            float alturaDoBotao = i == 0 ? alturaPrimeiro : alturaComum;
            botoes[i] = new RetanguloDeTela(x, cursor - alturaDoBotao,
                    larguraDoBotao, alturaDoBotao);
            if (i == 0 && detalheEmLinha) {
                rotulos[i] = new RetanguloDeTela(
                        botoes[i].x() + margemHorizontal,
                        botoes[i].centroY() - medidas.alturaDaOpcao() / 2f,
                        medidas.largurasDasOpcoes().get(i), medidas.alturaDaOpcao());
            } else {
                float topoDoRotulo = botoes[i].topo() - margemVertical;
                rotulos[i] = new RetanguloDeTela(
                        botoes[i].x() + margemHorizontal,
                        topoDoRotulo - medidas.alturaDaOpcao(),
                        botoes[i].largura() - 2f * margemHorizontal,
                        medidas.alturaDaOpcao());
            }
            cursor = botoes[i].y() - espaco;
        }

        RetanguloDeTela detalhe;
        if (detalheEmLinha) {
            detalhe = new RetanguloDeTela(
                    botoes[0].direita() - margemHorizontal - medidas.larguraDoDetalhe(),
                    botoes[0].centroY() - medidas.alturaDoDetalhe() / 2f,
                    medidas.larguraDoDetalhe(), medidas.alturaDoDetalhe());
        } else {
            detalhe = new RetanguloDeTela(
                    botoes[0].x() + margemHorizontal,
                    botoes[0].y() + margemVertical,
                    botoes[0].largura() - 2f * margemHorizontal,
                    medidas.alturaDoDetalhe());
        }

        float base = margens.baixo() + 24f * escala;
        RetanguloDeTela instrucao = new RetanguloDeTela(x, base, larguraDoBotao,
                medidas.alturaDoRodape());
        RetanguloDeTela conta = new RetanguloDeTela(x,
                instrucao.topo() + 4f * escala, larguraDoBotao, medidas.alturaDoRodape());
        return new LayoutDoMenuHorizontalToque(botoes, rotulos, detalhe, conta, instrucao);
    }

    public RetanguloDeTela botao(int indice) { return botoes[indice]; }
    public RetanguloDeTela rotulo(int indice) { return rotulos[indice]; }
    public RetanguloDeTela detalheDaCampanha() { return detalheDaCampanha; }
    public RetanguloDeTela conta() { return conta; }
    public RetanguloDeTela instrucao() { return instrucao; }
}
