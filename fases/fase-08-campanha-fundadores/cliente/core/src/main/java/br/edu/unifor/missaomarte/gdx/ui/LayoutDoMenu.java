package br.edu.unifor.missaomarte.gdx.ui;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Geometria responsiva do menu e da tela de opcoes, sem dependencia do libGDX. */
public final class LayoutDoMenu {
    private static final float LARGURA_DE_REFERENCIA = 1280f;
    private static final float ALTURA_DE_REFERENCIA = 720f;

    private final float largura;
    private final float altura;
    private final float escala;
    private final float deslocamentoX;
    private final boolean retrato;
    private final RetanguloDeTela[] botoesMenu;
    private final RetanguloDeTela painelOpcoes;
    private final RetanguloDeTela[] itensOpcoes;

    private LayoutDoMenu(float largura, float altura, float escala, float deslocamentoX, boolean retrato,
            RetanguloDeTela[] botoesMenu, RetanguloDeTela painelOpcoes, RetanguloDeTela[] itensOpcoes) {
        this.largura = largura;
        this.altura = altura;
        this.escala = escala;
        this.deslocamentoX = deslocamentoX;
        this.retrato = retrato;
        this.botoesMenu = botoesMenu;
        this.painelOpcoes = painelOpcoes;
        this.itensOpcoes = itensOpcoes;
    }

    public static LayoutDoMenu calcular(float largura, float altura, float alvoMinimo) {
        if (largura >= altura) {
            return paisagem(largura, altura);
        }
        return retrato(largura, altura, alvoMinimo);
    }

    private static LayoutDoMenu paisagem(float largura, float altura) {
        float escala = Math.min(largura / LARGURA_DE_REFERENCIA, altura / ALTURA_DE_REFERENCIA);
        float deslocamentoX = (largura - LARGURA_DE_REFERENCIA * escala) / 2f;
        RetanguloDeTela[] botoes = new RetanguloDeTela[4];
        for (int i = 0; i < botoes.length; i++) {
            botoes[i] = daReferencia(deslocamentoX, altura, escala, 96f, 376f + i * 58f, 320f, 50f);
        }
        RetanguloDeTela painel = daReferencia(deslocamentoX, altura, escala, 340f, 120f, 600f, 440f);
        RetanguloDeTela[] itens = new RetanguloDeTela[5];
        for (int i = 0; i < itens.length; i++) {
            itens[i] = daReferencia(deslocamentoX, altura, escala, 380f, 214f + i * 64f, 520f, 56f);
        }
        return new LayoutDoMenu(largura, altura, escala, deslocamentoX, false, botoes, painel, itens);
    }

    private static LayoutDoMenu retrato(float largura, float altura, float alvoMinimo) {
        float margem = Math.max(24f, largura * 0.06f);
        float alturaDoBotao = Math.max(50f, alvoMinimo);
        float passoDoBotao = alturaDoBotao + 8f;
        float primeiroBotaoY = altura / 2f - alturaDoBotao / 2f;
        RetanguloDeTela[] botoes = new RetanguloDeTela[4];
        for (int i = 0; i < botoes.length; i++) {
            botoes[i] = new RetanguloDeTela(margem, primeiroBotaoY - i * passoDoBotao,
                    largura - margem * 2f, alturaDoBotao);
        }

        float alturaDoItem = Math.max(56f, alvoMinimo);
        float passoDoItem = alturaDoItem + 8f;
        float alturaNecessaria = 70f + alturaDoItem * 5f + 8f * 4f + 24f;
        float alturaDoPainel = Math.min(altura - 48f, Math.max(440f, alturaNecessaria));
        RetanguloDeTela painel = new RetanguloDeTela(24f, (altura - alturaDoPainel) / 2f,
                largura - 48f, alturaDoPainel);
        float margemInterna = 24f;
        float primeiroItemY = painel.topo() - 70f - alturaDoItem;
        RetanguloDeTela[] itens = new RetanguloDeTela[5];
        for (int i = 0; i < itens.length; i++) {
            itens[i] = new RetanguloDeTela(painel.x() + margemInterna, primeiroItemY - i * passoDoItem,
                    painel.largura() - margemInterna * 2f, alturaDoItem);
        }
        return new LayoutDoMenu(largura, altura, 1f, 0f, true, botoes, painel, itens);
    }

    private static RetanguloDeTela daReferencia(float deslocamentoX, float altura, float escala,
            float x, float yDoTopo, float largura, float alturaDoRetangulo) {
        return new RetanguloDeTela(deslocamentoX + x * escala,
                altura - (yDoTopo + alturaDoRetangulo) * escala,
                largura * escala, alturaDoRetangulo * escala);
    }

    public RetanguloDeTela botaoMenu(int indice) {
        return botoesMenu[indice];
    }

    public RetanguloDeTela painelOpcoes() {
        return painelOpcoes;
    }

    public RetanguloDeTela itemOpcoes(int indice) {
        return itensOpcoes[indice];
    }

    public boolean emRetrato() {
        return retrato;
    }

    public float largura() {
        return largura;
    }

    public float altura() {
        return altura;
    }

    public float escala() {
        return escala;
    }

    public float xDaReferencia(float x) {
        return deslocamentoX + x * escala;
    }

    public float yDaReferencia(float yDoTopo) {
        return altura - yDoTopo * escala;
    }
}
