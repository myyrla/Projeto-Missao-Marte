package br.edu.unifor.missaomarte.gdx.ui;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Geometria pura do menu principal em toque vertical. */
public final class LayoutDoMenuToque {
    private static final float MARGEM = 32f;
    private static final float ESPACO = 12f;
    private static final float ESPACO_DE_SECAO = 20f;

    private final RetanguloDeTela expedicao;
    private final RetanguloDeTela missao;
    private final RetanguloDeTela marte;
    private final RetanguloDeTela frase;
    private final RetanguloDeTela[] botoes;
    private final RetanguloDeTela conta;
    private final RetanguloDeTela instrucao;

    private LayoutDoMenuToque(RetanguloDeTela expedicao, RetanguloDeTela missao,
            RetanguloDeTela marte, RetanguloDeTela frase, RetanguloDeTela[] botoes,
            RetanguloDeTela conta, RetanguloDeTela instrucao) {
        this.expedicao = expedicao;
        this.missao = missao;
        this.marte = marte;
        this.frase = frase;
        this.botoes = botoes;
        this.conta = conta;
        this.instrucao = instrucao;
    }

    public static LayoutDoMenuToque calcular(float largura, float altura,
            MargensSeguras margens, MedidasDoMenuToque medidas) {
        float x = margens.esquerda() + MARGEM;
        float larguraUtil = largura - x - margens.direita() - MARGEM;
        float topo = altura - margens.cima() - MARGEM;
        RetanguloDeTela expedicao = linha(x, larguraUtil, topo, medidas.alturaDoRotulo());
        RetanguloDeTela missao = linha(x, larguraUtil,
                expedicao.y() - ESPACO, medidas.alturaDoDisplay());
        RetanguloDeTela marte = linha(x, larguraUtil,
                missao.y() - 4f, medidas.alturaDoDisplay());
        RetanguloDeTela frase = linha(x, larguraUtil,
                marte.y() - ESPACO_DE_SECAO, medidas.alturaDaFrase());

        float base = margens.baixo() + MARGEM;
        RetanguloDeTela instrucao = new RetanguloDeTela(x, base, larguraUtil, medidas.alturaDoRodape());
        RetanguloDeTela conta = new RetanguloDeTela(x, instrucao.topo() + 4f,
                larguraUtil, medidas.alturaDoRodape());

        float alturaDoBotao = Math.max(medidas.alvoMinimo(), medidas.alturaDaOpcao() + 20f);
        float alturaDosBotoes = 4f * alturaDoBotao + 3f * ESPACO;
        float baseDisponivel = conta.topo() + ESPACO_DE_SECAO;
        float topoDisponivel = frase.y() - ESPACO_DE_SECAO;
        float primeiroTopo = topoDisponivel
                - Math.max(0f, (topoDisponivel - baseDisponivel - alturaDosBotoes) / 2f);
        RetanguloDeTela[] botoes = new RetanguloDeTela[4];
        for (int i = 0; i < botoes.length; i++) {
            botoes[i] = new RetanguloDeTela(x,
                    primeiroTopo - alturaDoBotao - i * (alturaDoBotao + ESPACO),
                    larguraUtil, alturaDoBotao);
        }
        return new LayoutDoMenuToque(expedicao, missao, marte, frase, botoes, conta, instrucao);
    }

    private static RetanguloDeTela linha(float x, float largura, float topo, float altura) {
        return new RetanguloDeTela(x, topo - altura, largura, altura);
    }

    public RetanguloDeTela expedicao() { return expedicao; }
    public RetanguloDeTela missao() { return missao; }
    public RetanguloDeTela marte() { return marte; }
    public RetanguloDeTela frase() { return frase; }
    public RetanguloDeTela botao(int indice) { return botoes[indice]; }
    public RetanguloDeTela conta() { return conta; }
    public RetanguloDeTela instrucao() { return instrucao; }
}
