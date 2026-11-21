package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Geometria pura do painel de resultado em telas de toque verticais. */
public final class LayoutDoResultadoDaPartida {
    private static final float MARGEM_EXTERNA = 24f;
    private static final float MARGEM_INTERNA = 24f;
    private static final float ESPACO = 12f;
    private static final float ESPACO_ANTES_DOS_BOTOES = 20f;
    private static final float MARGEM_HORIZONTAL_DO_BOTAO = 16f;
    private static final float MARGEM_VERTICAL_DO_BOTAO = 10f;

    private final RetanguloDeTela painel;
    private final RetanguloDeTela titulo;
    private final RetanguloDeTela motivo;
    private final RetanguloDeTela rotuloDosPontos;
    private final RetanguloDeTela pontos;
    private final RetanguloDeTela situacao;
    private final RetanguloDeTela[] botoes;

    private LayoutDoResultadoDaPartida(RetanguloDeTela painel, RetanguloDeTela titulo,
            RetanguloDeTela motivo, RetanguloDeTela rotuloDosPontos, RetanguloDeTela pontos,
            RetanguloDeTela situacao, RetanguloDeTela[] botoes) {
        this.painel = painel;
        this.titulo = titulo;
        this.motivo = motivo;
        this.rotuloDosPontos = rotuloDosPontos;
        this.pontos = pontos;
        this.situacao = situacao;
        this.botoes = botoes;
    }

    public static LayoutDoResultadoDaPartida calcular(float largura, float altura,
            MargensSeguras margens, MedidasDoResultadoDaPartida medidas) {
        float larguraDisponivel = largura - margens.esquerda() - margens.direita()
                - 2f * MARGEM_EXTERNA;
        float larguraPrimeiro = medidas.larguraDoPrimeiroBotao() + 2f * MARGEM_HORIZONTAL_DO_BOTAO;
        float larguraSegundo = medidas.larguraDoSegundoBotao() + 2f * MARGEM_HORIZONTAL_DO_BOTAO;
        float larguraEmLinha = larguraPrimeiro + ESPACO + larguraSegundo;
        float larguraDoConteudo = Math.max(medidas.larguraDoTitulo(), larguraEmLinha);
        float larguraDoPainel = Math.min(larguraDisponivel,
                Math.max(320f, larguraDoConteudo + 2f * MARGEM_INTERNA));
        float larguraInterna = larguraDoPainel - 2f * MARGEM_INTERNA;
        boolean empilhar = larguraEmLinha > larguraInterna;

        float alturaDoBotao = Math.max(medidas.alvoMinimo(),
                medidas.alturaDoTextoDoBotao() + 2f * MARGEM_VERTICAL_DO_BOTAO);
        float alturaDosBotoes = empilhar ? 2f * alturaDoBotao + ESPACO : alturaDoBotao;
        float alturaDoConteudo = medidas.alturaDoTitulo() + ESPACO
                + medidas.alturaDoMotivo() + ESPACO
                + medidas.alturaDoRotulo() + 6f
                + medidas.alturaDosPontos() + ESPACO
                + medidas.alturaDaSituacao() + ESPACO_ANTES_DOS_BOTOES
                + alturaDosBotoes;
        float alturaDisponivel = altura - margens.cima() - margens.baixo() - 2f * MARGEM_EXTERNA;
        float alturaDoPainel = Math.min(alturaDisponivel,
                alturaDoConteudo + 2f * MARGEM_INTERNA);
        RetanguloDeTela painel = new RetanguloDeTela(
                margens.esquerda() + (largura - margens.esquerda() - margens.direita()
                        - larguraDoPainel) / 2f,
                margens.baixo() + (altura - margens.cima() - margens.baixo()
                        - alturaDoPainel) / 2f,
                larguraDoPainel, alturaDoPainel);

        float x = painel.x() + MARGEM_INTERNA;
        float y = painel.topo() - MARGEM_INTERNA;
        RetanguloDeTela titulo = proximaLinha(x, larguraInterna, y, medidas.alturaDoTitulo());
        y = titulo.y() - ESPACO;
        RetanguloDeTela motivo = proximaLinha(x, larguraInterna, y, medidas.alturaDoMotivo());
        y = motivo.y() - ESPACO;
        RetanguloDeTela rotulo = proximaLinha(x, larguraInterna, y, medidas.alturaDoRotulo());
        y = rotulo.y() - 6f;
        RetanguloDeTela pontos = proximaLinha(x, larguraInterna, y, medidas.alturaDosPontos());
        y = pontos.y() - ESPACO;
        RetanguloDeTela situacao = proximaLinha(x, larguraInterna, y, medidas.alturaDaSituacao());
        y = situacao.y() - ESPACO_ANTES_DOS_BOTOES;

        RetanguloDeTela[] botoes = new RetanguloDeTela[2];
        if (empilhar) {
            botoes[0] = new RetanguloDeTela(x, y - alturaDoBotao, larguraInterna, alturaDoBotao);
            botoes[1] = new RetanguloDeTela(x, botoes[0].y() - ESPACO - alturaDoBotao,
                    larguraInterna, alturaDoBotao);
        } else {
            float xDosBotoes = painel.centroX() - larguraEmLinha / 2f;
            botoes[0] = new RetanguloDeTela(xDosBotoes, y - alturaDoBotao,
                    larguraPrimeiro, alturaDoBotao);
            botoes[1] = new RetanguloDeTela(botoes[0].direita() + ESPACO,
                    y - alturaDoBotao, larguraSegundo, alturaDoBotao);
        }
        return new LayoutDoResultadoDaPartida(painel, titulo, motivo, rotulo, pontos, situacao, botoes);
    }

    private static RetanguloDeTela proximaLinha(float x, float largura, float topo, float altura) {
        return new RetanguloDeTela(x, topo - altura, largura, altura);
    }

    public RetanguloDeTela painel() { return painel; }
    public RetanguloDeTela titulo() { return titulo; }
    public RetanguloDeTela motivo() { return motivo; }
    public RetanguloDeTela rotuloDosPontos() { return rotuloDosPontos; }
    public RetanguloDeTela pontos() { return pontos; }
    public RetanguloDeTela situacao() { return situacao; }
    public RetanguloDeTela botao(int indice) { return botoes[indice]; }
}
