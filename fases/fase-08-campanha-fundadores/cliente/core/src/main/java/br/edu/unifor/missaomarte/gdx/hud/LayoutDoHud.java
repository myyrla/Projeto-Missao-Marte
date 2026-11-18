package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Geometria responsiva do HUD e do modal de fim, sem dependencias do libGDX. */
public final class LayoutDoHud {
    private static final float LARGURA_DE_REFERENCIA = 1280f;
    private static final float ALTURA_DE_REFERENCIA = 720f;
    private static final float MARGEM = 24f;
    private static final float ESPACO = 12f;

    private final float largura;
    private final float altura;
    private final int celulas;
    private final float escala;
    private final boolean emRetrato;
    private final RetanguloDeTela piloto;
    private final RetanguloDeTela objetivo;
    private final RetanguloDeTela radar;
    private final RetanguloDeTela radarInterno;
    private final RetanguloDeTela mapa;
    private final RetanguloDeTela rodapeEsquerdo;
    private final RetanguloDeTela rodapeDireito;
    private final RetanguloDeTela aviso;
    private final RetanguloDeTela controles;
    private final RetanguloDeTela painelDoFim;
    private final RetanguloDeTela[] botoesDoFim;

    private LayoutDoHud(float largura, float altura, int celulas, float escala, boolean emRetrato,
            RetanguloDeTela piloto, RetanguloDeTela objetivo, RetanguloDeTela radar, RetanguloDeTela radarInterno,
            RetanguloDeTela mapa, RetanguloDeTela rodapeEsquerdo, RetanguloDeTela rodapeDireito,
            RetanguloDeTela aviso, RetanguloDeTela controles, RetanguloDeTela painelDoFim,
            RetanguloDeTela[] botoesDoFim) {
        this.largura = largura;
        this.altura = altura;
        this.celulas = celulas;
        this.escala = escala;
        this.emRetrato = emRetrato;
        this.piloto = piloto;
        this.objetivo = objetivo;
        this.radar = radar;
        this.radarInterno = radarInterno;
        this.mapa = mapa;
        this.rodapeEsquerdo = rodapeEsquerdo;
        this.rodapeDireito = rodapeDireito;
        this.aviso = aviso;
        this.controles = controles;
        this.painelDoFim = painelDoFim;
        this.botoesDoFim = botoesDoFim;
    }

    public static LayoutDoHud calcular(float largura, float altura, int celulas, boolean toque, MargensSeguras margens) {
        if (largura < altura) {
            return calcularRetrato(largura, altura, celulas, toque, margens);
        }
        return calcularPaisagem(largura, altura, celulas, toque, margens);
    }

    private static LayoutDoHud calcularPaisagem(float largura, float altura, int celulas, boolean toque,
            MargensSeguras margens) {
        float escala = Math.min(largura / LARGURA_DE_REFERENCIA, altura / ALTURA_DE_REFERENCIA);
        float margem = MARGEM * escala;
        float xPiloto = margens.esquerda() + margem;
        float yPiloto = altura - margens.cima() - margem - 116f * escala;
        RetanguloDeTela piloto = new RetanguloDeTela(xPiloto, yPiloto, 250f * escala, 116f * escala);
        RetanguloDeTela objetivo = new RetanguloDeTela((largura - 560f * escala) / 2f,
                altura - margens.cima() - margem - 108f * escala, 560f * escala, 108f * escala);
        RetanguloDeTela radar = new RetanguloDeTela(largura - margens.direita() - margem - 220f * escala,
                altura - margens.cima() - margem - 150f * escala, 220f * escala, 150f * escala);
        RetanguloDeTela radarInterno = new RetanguloDeTela(radar.x() + 14f * escala, radar.y() + 14f * escala,
                192f * escala, 100f * escala);

        float baseDoMapa = margens.baixo() + 80f * escala;
        float topoDoMapa = altura - margens.cima() - 150f * escala;
        float lado = Math.max(celulas * 14f,
                Math.min(490f * escala, topoDoMapa - baseDoMapa));
        RetanguloDeTela mapa = new RetanguloDeTela((largura - lado) / 2f, baseDoMapa, lado, lado);

        RetanguloDeTela rodapeEsquerdo = new RetanguloDeTela(margens.esquerda() + margem,
                margens.baixo() + margem, 576f * escala, 44f * escala);
        RetanguloDeTela rodapeDireito = new RetanguloDeTela(largura - margens.direita() - margem - 540f * escala,
                margens.baixo() + margem, 540f * escala, 44f * escala);
        RetanguloDeTela aviso = new RetanguloDeTela((largura - 340f * escala) / 2f,
                rodapeEsquerdo.topo() + 14f * escala, 340f * escala, 38f * escala);
        RetanguloDeTela controles = toque
                ? new RetanguloDeTela(largura - margens.direita() - 260f * escala,
                        rodapeDireito.topo() + 8f * escala, 260f * escala,
                        Math.max(0f, mapa.topo() - rodapeDireito.topo() - 16f * escala))
                : new RetanguloDeTela(largura, 0f, 0f, 0f);
        return montar(largura, altura, celulas, escala, false, piloto, objetivo, radar, radarInterno, mapa,
                rodapeEsquerdo, rodapeDireito, aviso, controles);
    }

    private static LayoutDoHud calcularRetrato(float largura, float altura, int celulas, boolean toque,
            MargensSeguras margens) {
        float escala = Math.min(1f, largura / 800f);
        float margem = MARGEM * escala;
        float esquerda = margens.esquerda() + margem;
        float direita = margens.direita() + margem;
        float larguraUtil = Math.max(1f, largura - esquerda - direita);
        float yPiloto = altura - margens.cima() - 16f * escala - 116f * escala;
        RetanguloDeTela piloto = new RetanguloDeTela(esquerda, yPiloto, larguraUtil, 116f * escala);
        RetanguloDeTela objetivo = new RetanguloDeTela(esquerda,
                piloto.y() - ESPACO * escala - 108f * escala, larguraUtil, 108f * escala);

        float topoDoMapa = objetivo.y() - ESPACO * escala;
        float reservaInferior = margens.baixo() + (toque ? 380f : 92f) * escala;
        float lado = Math.max(celulas * 14f,
                Math.min(larguraUtil, topoDoMapa - reservaInferior));
        RetanguloDeTela mapa = new RetanguloDeTela(esquerda + (larguraUtil - lado) / 2f,
                topoDoMapa - lado, lado, lado);
        RetanguloDeTela rodapeEsquerdo = new RetanguloDeTela(esquerda,
                mapa.y() - 52f * escala, larguraUtil, 44f * escala);
        RetanguloDeTela rodapeDireito = new RetanguloDeTela(largura, 0f, 0f, 0f);
        RetanguloDeTela aviso = new RetanguloDeTela(esquerda + larguraUtil * 0.1f,
                mapa.y() + 10f * escala, larguraUtil * 0.8f, 38f * escala);
        RetanguloDeTela controles = toque
                ? new RetanguloDeTela(margens.esquerda(), margens.baixo() + 36f * escala,
                        largura - margens.esquerda() - margens.direita(),
                        Math.max(0f, rodapeEsquerdo.y() - margens.baixo() - 44f * escala))
                : new RetanguloDeTela(0f, 0f, 0f, 0f);
        RetanguloDeTela vazio = new RetanguloDeTela(largura, altura, 0f, 0f);
        return montar(largura, altura, celulas, escala, true, piloto, objetivo, vazio, vazio, mapa,
                rodapeEsquerdo, rodapeDireito, aviso, controles);
    }

    private static LayoutDoHud montar(float largura, float altura, int celulas, float escala, boolean emRetrato,
            RetanguloDeTela piloto, RetanguloDeTela objetivo, RetanguloDeTela radar, RetanguloDeTela radarInterno,
            RetanguloDeTela mapa, RetanguloDeTela rodapeEsquerdo, RetanguloDeTela rodapeDireito,
            RetanguloDeTela aviso, RetanguloDeTela controles) {
        float larguraDoPainel = Math.min(480f * escala, largura - 24f * escala);
        float alturaDoPainel = Math.min(300f * escala, altura - 24f * escala);
        RetanguloDeTela painel = new RetanguloDeTela((largura - larguraDoPainel) / 2f,
                (altura - alturaDoPainel) / 2f, larguraDoPainel, alturaDoPainel);
        float larguraDoBotao = Math.min(180f * escala, (painel.largura() - 40f * escala) / 2f);
        float espaco = 20f * escala;
        float larguraDosBotoes = larguraDoBotao * 2f + espaco;
        float x = painel.centroX() - larguraDosBotoes / 2f;
        float y = painel.y() + 30f * escala;
        RetanguloDeTela[] botoes = {
            new RetanguloDeTela(x, y, larguraDoBotao, 50f * escala),
            new RetanguloDeTela(x + larguraDoBotao + espaco, y, larguraDoBotao, 50f * escala)
        };
        return new LayoutDoHud(largura, altura, celulas, escala, emRetrato, piloto, objetivo, radar,
                radarInterno, mapa, rodapeEsquerdo, rodapeDireito, aviso, controles, painel, botoes);
    }

    public float escala() { return escala; }
    public boolean emRetrato() { return emRetrato; }
    public boolean exibeRadar() { return !emRetrato && radar.largura() > 0f; }
    public boolean exibeAtalhos() { return !emRetrato && rodapeDireito.largura() > 0f; }
    public RetanguloDeTela piloto() { return piloto; }
    public RetanguloDeTela objetivo() { return objetivo; }
    public RetanguloDeTela radar() { return radar; }
    public RetanguloDeTela radarInterno() { return radarInterno; }
    public RetanguloDeTela mapa() { return mapa; }
    public RetanguloDeTela rodapeEsquerdo() { return rodapeEsquerdo; }
    public RetanguloDeTela rodapeEsquerdo(float larguraDoTexto) {
        float larguraMedida = larguraDoTexto + 32f * escala;
        return new RetanguloDeTela(rodapeEsquerdo.x(), rodapeEsquerdo.y(),
                Math.min(rodapeEsquerdo.largura(), larguraMedida), rodapeEsquerdo.altura());
    }
    public RetanguloDeTela rodapeDireito() { return rodapeDireito; }
    public RetanguloDeTela aviso() { return aviso; }
    public RetanguloDeTela controles() { return controles; }
    public RetanguloDeTela painelDoFim() { return painelDoFim; }
    public RetanguloDeTela botaoDoFim(int indice) { return botoesDoFim[indice]; }

    public RetanguloDeTela fichaDoObjetivo(int indice) {
        float larguraDaFicha = 170f * escala;
        float passo = 178f * escala;
        float larguraDoConjunto = larguraDaFicha + passo * 2f;
        float inicio = emRetrato ? objetivo.centroX() - larguraDoConjunto / 2f : objetivo.x() + 16f * escala;
        return new RetanguloDeTela(inicio + indice * passo, objetivo.y() + 12f * escala,
                larguraDaFicha, 52f * escala);
    }

    public float celula() { return mapa.largura() / celulas; }
    public float origemX() { return mapa.x(); }
    public float origemY() { return mapa.y(); }
    public float largura() { return largura; }
    public float altura() { return altura; }
}
