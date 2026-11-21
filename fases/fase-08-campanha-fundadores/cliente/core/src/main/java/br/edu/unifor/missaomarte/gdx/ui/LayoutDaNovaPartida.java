package br.edu.unifor.missaomarte.gdx.ui;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Geometria pura da tela de nova partida em toque vertical. */
public final class LayoutDaNovaPartida {
    private static final float MARGEM = 32f;
    private static final float ESPACO = 12f;
    private static final float RESPIRO = 24f;
    private static final float MARGEM_DO_CARTAO = 14f;

    private final RetanguloDeTela titulo;
    private final RetanguloDeTela[] cartoes;
    private final RetanguloDeTela[] rotulos;
    private final RetanguloDeTela[] valores;
    private final RetanguloDeTela[] anteriores;
    private final RetanguloDeTela[] proximos;
    private final RetanguloDeTela resumo;
    private final RetanguloDeTela descricao;
    private final RetanguloDeTela decolar;
    private final RetanguloDeTela voltar;

    private LayoutDaNovaPartida(RetanguloDeTela titulo, RetanguloDeTela[] cartoes,
            RetanguloDeTela[] rotulos, RetanguloDeTela[] valores,
            RetanguloDeTela[] anteriores, RetanguloDeTela[] proximos,
            RetanguloDeTela resumo, RetanguloDeTela descricao,
            RetanguloDeTela decolar, RetanguloDeTela voltar) {
        this.titulo = titulo;
        this.cartoes = cartoes;
        this.rotulos = rotulos;
        this.valores = valores;
        this.anteriores = anteriores;
        this.proximos = proximos;
        this.resumo = resumo;
        this.descricao = descricao;
        this.decolar = decolar;
        this.voltar = voltar;
    }

    public static LayoutDaNovaPartida calcular(float largura, float altura,
            MargensSeguras margens, MedidasDaNovaPartida medidas) {
        float esquerda = margens.esquerda() + MARGEM;
        float larguraUtil = largura - esquerda - margens.direita() - MARGEM;
        float topo = altura - margens.cima() - MARGEM;
        RetanguloDeTela titulo = new RetanguloDeTela(esquerda,
                topo - medidas.alturaDoTitulo(), larguraUtil, medidas.alturaDoTitulo());

        float alturaDoValor = Math.max(medidas.alturaDoValor(), medidas.alvoMinimo());
        float alturaDoCartao = 2f * MARGEM_DO_CARTAO + medidas.alturaDoRotulo()
                + 6f + alturaDoValor;
        RetanguloDeTela[] cartoes = new RetanguloDeTela[3];
        RetanguloDeTela[] rotulos = new RetanguloDeTela[3];
        RetanguloDeTela[] valores = new RetanguloDeTela[3];
        RetanguloDeTela[] anteriores = new RetanguloDeTela[3];
        RetanguloDeTela[] proximos = new RetanguloDeTela[3];
        float y = titulo.y() - RESPIRO;
        for (int i = 0; i < 3; i++) {
            cartoes[i] = new RetanguloDeTela(esquerda, y - alturaDoCartao, larguraUtil, alturaDoCartao);
            rotulos[i] = new RetanguloDeTela(esquerda + MARGEM_DO_CARTAO,
                    cartoes[i].topo() - MARGEM_DO_CARTAO - medidas.alturaDoRotulo(),
                    larguraUtil - 2f * MARGEM_DO_CARTAO, medidas.alturaDoRotulo());
            float yDoValor = cartoes[i].y() + MARGEM_DO_CARTAO;
            if (i == 0) {
                anteriores[i] = vazio(largura, altura);
                proximos[i] = vazio(largura, altura);
                valores[i] = new RetanguloDeTela(esquerda + MARGEM_DO_CARTAO, yDoValor,
                        larguraUtil - 2f * MARGEM_DO_CARTAO, alturaDoValor);
            } else {
                float lado = medidas.alvoMinimo();
                anteriores[i] = new RetanguloDeTela(esquerda + MARGEM_DO_CARTAO, yDoValor, lado, alturaDoValor);
                proximos[i] = new RetanguloDeTela(esquerda + larguraUtil - MARGEM_DO_CARTAO - lado,
                        yDoValor, lado, alturaDoValor);
                float xDoValor = anteriores[i].direita() + ESPACO;
                valores[i] = new RetanguloDeTela(xDoValor, yDoValor,
                        proximos[i].x() - ESPACO - xDoValor, alturaDoValor);
            }
            y = cartoes[i].y() - ESPACO;
        }

        y -= RESPIRO - ESPACO;
        RetanguloDeTela resumo = new RetanguloDeTela(esquerda, y - medidas.alturaDoResumo(),
                larguraUtil, medidas.alturaDoResumo());
        RetanguloDeTela descricao = new RetanguloDeTela(esquerda,
                resumo.y() - ESPACO - medidas.alturaDaDescricao(),
                larguraUtil, medidas.alturaDaDescricao());

        float alturaDoBotao = Math.max(56f, medidas.alvoMinimo());
        RetanguloDeTela voltar = new RetanguloDeTela(esquerda, margens.baixo() + MARGEM,
                larguraUtil, alturaDoBotao);
        RetanguloDeTela decolar = new RetanguloDeTela(esquerda, voltar.topo() + ESPACO,
                larguraUtil, alturaDoBotao);
        return new LayoutDaNovaPartida(titulo, cartoes, rotulos, valores, anteriores, proximos,
                resumo, descricao, decolar, voltar);
    }

    private static RetanguloDeTela vazio(float largura, float altura) {
        return new RetanguloDeTela(largura, altura, 0f, 0f);
    }

    public RetanguloDeTela titulo() { return titulo; }
    public RetanguloDeTela cartao(int indice) { return cartoes[indice]; }
    public RetanguloDeTela rotulo(int indice) { return rotulos[indice]; }
    public RetanguloDeTela valor(int indice) { return valores[indice]; }
    public RetanguloDeTela anterior(int indice) { return anteriores[indice]; }
    public RetanguloDeTela proximo(int indice) { return proximos[indice]; }
    public RetanguloDeTela resumo() { return resumo; }
    public RetanguloDeTela descricao() { return descricao; }
    public RetanguloDeTela decolar() { return decolar; }
    public RetanguloDeTela voltar() { return voltar; }
}
