package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.List;

/** Geometria responsiva do HUD e do modal de fim, sem dependencias do libGDX. */
public final class LayoutDoHud {
    public static final float LARGURA_MINIMA_DA_COLUNA = 220f;

    private static final float LARGURA_DE_REFERENCIA = 1280f;
    private static final float ALTURA_DE_REFERENCIA = 720f;
    private static final float MARGEM = 24f;
    private static final float ESPACO = 12f;

    private enum Modo { COLUNAS, FAIXAS, RESERVA }

    private final float largura;
    private final float altura;
    private final int celulas;
    private final float escala;
    private final boolean emRetrato;
    private final Modo modo;
    private final float larguraDaColuna;
    private final RetanguloDeTela piloto;
    private final RetanguloDeTela jogador;
    private final RetanguloDeTela objetivo;
    private final RetanguloDeTela textoDoObjetivo;
    private final RetanguloDeTela radar;
    private final RetanguloDeTela radarInterno;
    private final RetanguloDeTela mapa;
    private final RetanguloDeTela areaUtilDoMapa;
    private final RetanguloDeTela rodapeEsquerdo;
    private final RetanguloDeTela rodapeDireito;
    private final RetanguloDeTela aviso;
    private final RetanguloDeTela seloDaTempestade;
    private final RetanguloDeTela controles;
    private final RetanguloDeTela painelDoFim;
    private final RetanguloDeTela[] botoesDoFim;

    private LayoutDoHud(float largura, float altura, int celulas, float escala, boolean emRetrato, Modo modo,
            float larguraDaColuna, RetanguloDeTela piloto, RetanguloDeTela jogador,
            RetanguloDeTela objetivo, RetanguloDeTela textoDoObjetivo, RetanguloDeTela radar,
            RetanguloDeTela radarInterno, RetanguloDeTela mapa, RetanguloDeTela areaUtilDoMapa,
            RetanguloDeTela rodapeEsquerdo,
            RetanguloDeTela rodapeDireito, RetanguloDeTela aviso, RetanguloDeTela seloDaTempestade,
            RetanguloDeTela controles, RetanguloDeTela painelDoFim, RetanguloDeTela[] botoesDoFim) {
        this.largura = largura;
        this.altura = altura;
        this.celulas = celulas;
        this.escala = escala;
        this.emRetrato = emRetrato;
        this.modo = modo;
        this.larguraDaColuna = larguraDaColuna;
        this.piloto = piloto;
        this.jogador = jogador;
        this.objetivo = objetivo;
        this.textoDoObjetivo = textoDoObjetivo;
        this.radar = radar;
        this.radarInterno = radarInterno;
        this.mapa = mapa;
        this.areaUtilDoMapa = areaUtilDoMapa;
        this.rodapeEsquerdo = rodapeEsquerdo;
        this.rodapeDireito = rodapeDireito;
        this.aviso = aviso;
        this.seloDaTempestade = seloDaTempestade;
        this.controles = controles;
        this.painelDoFim = painelDoFim;
        this.botoesDoFim = botoesDoFim;
    }

    /** Mantem o layout anterior para telas que ainda nao forneceram a area real do mapa. */
    public static LayoutDoHud calcular(float largura, float altura, int celulas, boolean toque,
            MargensSeguras margens) {
        return calcularReserva(largura, altura, celulas, toque, margens);
    }

    /** Calcula o HUD nas areas que sobraram ao redor do mapa realmente visivel. */
    public static LayoutDoHud calcular(float largura, float altura, int celulas, boolean toque,
            MargensSeguras margens, RetanguloDeTela mapa) {
        float escala = largura < altura ? Math.min(1f, largura / 800f)
                : Math.min(largura / LARGURA_DE_REFERENCIA, altura / ALTURA_DE_REFERENCIA);
        return calcular(largura, altura, celulas, toque, margens, mapa, MedidasDoHud.padrao(escala));
    }

    public static LayoutDoHud calcular(float largura, float altura, int celulas, boolean toque,
            MargensSeguras margens, RetanguloDeTela mapa, MedidasDoHud medidas) {
        LayoutDoHud calculado = largura < altura
                ? calcularFaixas(largura, altura, celulas, toque, margens, mapa, medidas)
                : calcularColunas(largura, altura, celulas, toque, margens, mapa, medidas);
        return calculado == null ? calcularReserva(largura, altura, celulas, toque, margens) : calculado;
    }

    private static LayoutDoHud calcularColunas(float largura, float altura, int celulas, boolean toque,
            MargensSeguras margens, RetanguloDeTela mapa, MedidasDoHud medidas) {
        if (toque) {
            return null;
        }
        float escala = Math.min(largura / LARGURA_DE_REFERENCIA, altura / ALTURA_DE_REFERENCIA);
        float margem = MARGEM * escala;
        float espaco = ESPACO * escala;
        float livreEsquerda = mapa.x() - margens.esquerda() - margem - espaco;
        float livreDireita = largura - margens.direita() - margem - mapa.direita() - espaco;
        float coluna = Math.min(livreEsquerda, livreDireita);
        if (coluna < LARGURA_MINIMA_DA_COLUNA * escala) {
            return null;
        }

        float base = margens.baixo() + margem;
        float topo = altura - margens.cima() - margem;
        float alturaPiloto = 116f * escala + 8f * escala + medidas.alturaDoStatus() + 16f * escala;
        float alturaObjetivo = 16f * escala + medidas.alturaDoRotulo() + 8f * escala
                + medidas.alturaDoTextoDoObjetivo() + 12f * escala
                + 3f * 52f * escala + 2f * 8f * escala + 16f * escala;
        if (alturaPiloto + alturaObjetivo + espaco > topo - base) {
            return null;
        }

        float xEsquerda = margens.esquerda() + margem;
        RetanguloDeTela piloto = new RetanguloDeTela(xEsquerda, topo - alturaPiloto, coluna, alturaPiloto);
        RetanguloDeTela jogador = new RetanguloDeTela(xEsquerda, piloto.topo() - 116f * escala,
                coluna, 116f * escala);
        RetanguloDeTela status = new RetanguloDeTela(xEsquerda, piloto.y() + 16f * escala,
                coluna, medidas.alturaDoStatus());
        RetanguloDeTela objetivo = new RetanguloDeTela(xEsquerda,
                piloto.y() - espaco - alturaObjetivo, coluna, alturaObjetivo);
        float topoDoTexto = objetivo.topo() - 16f * escala - medidas.alturaDoRotulo() - 8f * escala;
        RetanguloDeTela textoDoObjetivo = new RetanguloDeTela(objetivo.x() + 16f * escala,
                topoDoTexto - medidas.alturaDoTextoDoObjetivo(), objetivo.largura() - 32f * escala,
                medidas.alturaDoTextoDoObjetivo());

        float xDireita = mapa.direita() + espaco;
        RetanguloDeTela radar = new RetanguloDeTela(xDireita, topo - 150f * escala, coluna, 150f * escala);
        float margemRadar = 14f * escala;
        RetanguloDeTela radarInterno = new RetanguloDeTela(radar.x() + margemRadar,
                radar.y() + margemRadar, radar.largura() - 2f * margemRadar,
                radar.altura() - 50f * escala);
        float alturaDosAtalhos = 24f * escala + medidas.quantidadeDeAtalhos() * 24f * escala
                + Math.max(0, medidas.quantidadeDeAtalhos() - 1) * 14f * escala;
        RetanguloDeTela atalhos = new RetanguloDeTela(xDireita,
                radar.y() - espaco - alturaDosAtalhos, coluna, alturaDosAtalhos);
        float alturaDoAviso = medidas.avisoVisivel()
                ? Math.max(38f * escala, medidas.alturaDoAviso() + 24f * escala) : 0f;
        RetanguloDeTela aviso = alturaDoAviso > 0f
                ? new RetanguloDeTela(xDireita, atalhos.y() - espaco - alturaDoAviso, coluna, alturaDoAviso)
                : vazio(largura, altura);
        float topoDoSelo = alturaDoAviso > 0f ? aviso.y() - espaco : atalhos.y() - espaco;
        RetanguloDeTela selo = new RetanguloDeTela(xDireita,
                topoDoSelo - 34f * escala, coluna, 34f * escala);
        RetanguloDeTela ultimo = medidas.tempestadeVisivel() ? selo : alturaDoAviso > 0f ? aviso : atalhos;
        if (ultimo.y() < base) {
            return null;
        }
        return montarComAreas(largura, altura, celulas, escala, false, Modo.COLUNAS, coluna,
                piloto, jogador, objetivo, textoDoObjetivo, radar, radarInterno, mapa, mapa,
                status, atalhos, aviso, selo, vazio(largura, altura));
    }

    private static LayoutDoHud calcularFaixas(float largura, float altura, int celulas, boolean toque,
            MargensSeguras margens, RetanguloDeTela mapa, MedidasDoHud medidas) {
        float escala = Math.min(1f, largura / 800f);
        float margem = MARGEM * escala;
        float espaco = ESPACO * escala;
        float esquerda = margens.esquerda() + margem;
        float direita = largura - margens.direita() - margem;
        float larguraUtil = direita - esquerda;
        float topoDaFaixaSuperior = altura - margens.cima() - margem;
        float baseDaFaixaInferior = margens.baixo() + margem;
        float espacoDosControles = Math.max(8f * escala, medidas.alvoMinimo() / 6f);
        float alturaFisicaDosControles = medidas.alvoMinimo() > 0f
                ? 3f * Math.max(76f, medidas.alvoMinimo())
                        + 2f * espacoDosControles + 32f * escala
                : 0f;
        float alturaDosControles = toque
                ? Math.max(240f * escala, alturaFisicaDosControles)
                : 0f;
        if (larguraUtil < 700f * escala) {
            return null;
        }

        float alturaDoSelo = medidas.tempestadeVisivel() ? 34f * escala : 0f;
        float respiroEntreLinhas = 6f * escala;
        float alturaJogador = 2f * 8f * escala + 2f * medidas.alturaDoRotulo() + respiroEntreLinhas;
        float alturaPiloto = alturaJogador + (alturaDoSelo > 0f ? espaco + alturaDoSelo : 0f);
        RetanguloDeTela piloto = new RetanguloDeTela(esquerda,
                topoDaFaixaSuperior - alturaPiloto, larguraUtil, alturaPiloto);
        RetanguloDeTela jogador = new RetanguloDeTela(esquerda, piloto.topo() - alturaJogador,
                larguraUtil, alturaJogador);
        RetanguloDeTela status = vazio(largura, altura);
        float larguraDoSelo = Math.min(larguraUtil, 280f * escala);
        RetanguloDeTela selo = alturaDoSelo > 0f
                ? new RetanguloDeTela(esquerda, piloto.y(), larguraDoSelo, alturaDoSelo)
                : vazio(largura, altura);
        float alturaDaFicha = Math.max(52f * escala, medidas.alturaDaLinhaDoTexto()
                + medidas.alturaDaLinhaPequena() + 3f * escala + 16f * escala);
        float alturaObjetivo = 8f * escala + medidas.alturaDoRotulo() + 4f * escala
                + medidas.alturaDoTextoDoObjetivo() + 8f * escala + alturaDaFicha + 8f * escala;
        RetanguloDeTela objetivo = new RetanguloDeTela(esquerda,
                piloto.y() - espaco - alturaObjetivo, larguraUtil, alturaObjetivo);
        float yDosChips = objetivo.y() + 8f * escala;
        RetanguloDeTela textoDoObjetivo = new RetanguloDeTela(objetivo.x() + 16f * escala,
                yDosChips + alturaDaFicha + 8f * escala,
                objetivo.largura() - 32f * escala, medidas.alturaDoTextoDoObjetivo());
        RetanguloDeTela controles = toque
                ? new RetanguloDeTela(esquerda, baseDaFaixaInferior, larguraUtil, alturaDosControles)
                : vazio(largura, altura);
        RetanguloDeTela vazio = vazio(largura, altura);
        RetanguloDeTela areaUtil = new RetanguloDeTela(0f, controles.topo(), largura,
                Math.max(0f, objetivo.y() - controles.topo()));
        if (areaUtil.altura() <= 0f) {
            return null;
        }
        float alturaDoAviso = medidas.avisoVisivel()
                ? Math.max(30f * escala, medidas.alturaDoAviso() + 12f * escala) : 0f;
        float larguraDoAviso = Math.min(larguraUtil, 520f * escala);
        RetanguloDeTela aviso = alturaDoAviso > 0f
                ? new RetanguloDeTela((largura - larguraDoAviso) / 2f,
                        areaUtil.topo() - 8f * escala - alturaDoAviso,
                        larguraDoAviso, alturaDoAviso)
                : vazio(largura, altura);
        return montarComAreas(largura, altura, celulas, escala, true, Modo.FAIXAS, 0f,
                piloto, jogador, objetivo, textoDoObjetivo, vazio, vazio, mapa, areaUtil,
                status, vazio, aviso, selo, controles);
    }

    private static LayoutDoHud calcularReserva(float largura, float altura, int celulas, boolean toque,
            MargensSeguras margens) {
        return largura < altura
                ? calcularReservaRetrato(largura, altura, celulas, toque, margens)
                : calcularReservaPaisagem(largura, altura, celulas, toque, margens);
    }

    private static LayoutDoHud calcularReservaPaisagem(float largura, float altura, int celulas, boolean toque,
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
        float lado = Math.max(celulas * 14f, Math.min(490f * escala, topoDoMapa - baseDoMapa));
        RetanguloDeTela mapa = new RetanguloDeTela((largura - lado) / 2f, baseDoMapa, lado, lado);
        RetanguloDeTela rodapeEsquerdo = new RetanguloDeTela(margens.esquerda() + margem,
                margens.baixo() + margem, 576f * escala, 44f * escala);
        RetanguloDeTela rodapeDireito = PoliticaDeAtalhos.exibeRodapeDeTeclado(toque)
                ? new RetanguloDeTela(largura - margens.direita() - margem - 540f * escala,
                        margens.baixo() + margem, 540f * escala, 44f * escala)
                : vazio(largura, altura);
        RetanguloDeTela aviso = new RetanguloDeTela((largura - 340f * escala) / 2f,
                rodapeEsquerdo.topo() + 14f * escala, 340f * escala, 38f * escala);
        RetanguloDeTela selo = new RetanguloDeTela(objetivo.centroX() - 120f * escala,
                objetivo.y() - 42f * escala, 240f * escala, 34f * escala);
        RetanguloDeTela controles = toque
                ? new RetanguloDeTela(largura - margens.direita() - 260f * escala,
                        margens.baixo() + margem, 260f * escala,
                        Math.max(0f, mapa.topo() - margens.baixo() - margem - 8f * escala))
                : vazio(largura, altura);
        return montar(largura, altura, celulas, escala, false, Modo.RESERVA, 0f, piloto, objetivo,
                radar, radarInterno, mapa, rodapeEsquerdo, rodapeDireito, aviso, selo, controles);
    }

    private static LayoutDoHud calcularReservaRetrato(float largura, float altura, int celulas, boolean toque,
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
        float lado = Math.max(celulas * 14f, Math.min(larguraUtil, topoDoMapa - reservaInferior));
        RetanguloDeTela mapa = new RetanguloDeTela(esquerda + (larguraUtil - lado) / 2f,
                topoDoMapa - lado, lado, lado);
        RetanguloDeTela rodapeEsquerdo = new RetanguloDeTela(esquerda,
                mapa.y() - 52f * escala, larguraUtil, 44f * escala);
        RetanguloDeTela aviso = new RetanguloDeTela(esquerda + larguraUtil * 0.1f,
                mapa.y() + 10f * escala, larguraUtil * 0.8f, 38f * escala);
        RetanguloDeTela controles = toque
                ? new RetanguloDeTela(margens.esquerda(), margens.baixo() + 36f * escala,
                        largura - margens.esquerda() - margens.direita(),
                        Math.max(0f, rodapeEsquerdo.y() - margens.baixo() - 44f * escala))
                : vazio(largura, altura);
        RetanguloDeTela vazio = vazio(largura, altura);
        RetanguloDeTela selo = new RetanguloDeTela(objetivo.centroX() - 120f * escala,
                objetivo.y() - 42f * escala, 240f * escala, 34f * escala);
        return montar(largura, altura, celulas, escala, true, Modo.RESERVA, 0f, piloto, objetivo,
                vazio, vazio, mapa, rodapeEsquerdo, vazio, aviso, selo, controles);
    }

    private static LayoutDoHud montar(float largura, float altura, int celulas, float escala, boolean emRetrato,
            Modo modo, float larguraDaColuna, RetanguloDeTela piloto, RetanguloDeTela objetivo,
            RetanguloDeTela radar, RetanguloDeTela radarInterno, RetanguloDeTela mapa,
            RetanguloDeTela rodapeEsquerdo, RetanguloDeTela rodapeDireito, RetanguloDeTela aviso,
            RetanguloDeTela seloDaTempestade, RetanguloDeTela controles) {
        RetanguloDeTela textoDoObjetivo = new RetanguloDeTela(objetivo.x() + 16f * escala,
                objetivo.topo() - 46f * escala, Math.max(0f, objetivo.largura() - 32f * escala),
                14f * escala);
        return montarComAreas(largura, altura, celulas, escala, emRetrato, modo, larguraDaColuna,
                piloto, piloto, objetivo, textoDoObjetivo, radar, radarInterno, mapa, mapa,
                rodapeEsquerdo, rodapeDireito, aviso, seloDaTempestade, controles);
    }

    private static LayoutDoHud montarComAreas(float largura, float altura, int celulas, float escala,
            boolean emRetrato, Modo modo, float larguraDaColuna, RetanguloDeTela piloto,
            RetanguloDeTela jogador, RetanguloDeTela objetivo, RetanguloDeTela textoDoObjetivo,
            RetanguloDeTela radar, RetanguloDeTela radarInterno, RetanguloDeTela mapa,
            RetanguloDeTela areaUtilDoMapa, RetanguloDeTela rodapeEsquerdo, RetanguloDeTela rodapeDireito,
            RetanguloDeTela aviso, RetanguloDeTela seloDaTempestade, RetanguloDeTela controles) {
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
        return new LayoutDoHud(largura, altura, celulas, escala, emRetrato, modo, larguraDaColuna,
                piloto, jogador, objetivo, textoDoObjetivo, radar, radarInterno, mapa, areaUtilDoMapa,
                rodapeEsquerdo, rodapeDireito, aviso, seloDaTempestade, controles, painel, botoes);
    }

    private static RetanguloDeTela vazio(float largura, float altura) {
        return new RetanguloDeTela(largura, altura, 0f, 0f);
    }

    public float escala() { return escala; }
    public boolean emRetrato() { return emRetrato; }
    public boolean exibeRadar() { return radar.largura() > 0f && radar.altura() > 0f; }
    public boolean exibeAtalhos() { return rodapeDireito.largura() > 0f && rodapeDireito.altura() > 0f; }
    public boolean usaReserva() { return modo == Modo.RESERVA; }
    public boolean compacto() { return modo == Modo.FAIXAS; }
    public boolean statusIntegradoAoJogador() { return modo != Modo.RESERVA; }
    public boolean fichasDoObjetivoSaoVerticais() { return modo == Modo.COLUNAS; }
    public boolean textosCabemSemTruncar() { return modo != Modo.RESERVA; }
    public float larguraDaColuna() { return larguraDaColuna; }
    public RetanguloDeTela piloto() { return piloto; }
    public RetanguloDeTela jogador() { return jogador; }
    public RetanguloDeTela linhaPrincipalDoJogador() {
        if (!compacto()) return jogador;
        float margem = 8f * escala;
        float espaco = 6f * escala;
        float alturaDaLinha = Math.max(0f, (jogador.altura() - 2f * margem - espaco) / 2f);
        return new RetanguloDeTela(jogador.x() + 16f * escala,
                jogador.topo() - margem - alturaDaLinha,
                Math.max(0f, jogador.largura() - 32f * escala), alturaDaLinha);
    }
    public RetanguloDeTela linhaSecundariaDoJogador() {
        if (!compacto()) return jogador;
        float margem = 8f * escala;
        float espaco = 6f * escala;
        float alturaDaLinha = Math.max(0f, (jogador.altura() - 2f * margem - espaco) / 2f);
        return new RetanguloDeTela(jogador.x() + 16f * escala, jogador.y() + margem,
                Math.max(0f, jogador.largura() - 32f * escala), alturaDaLinha);
    }
    public RetanguloDeTela inicioDaLinhaPrincipalDoJogador() {
        RetanguloDeTela linha = linhaPrincipalDoJogador();
        return new RetanguloDeTela(linha.x(), linha.y(), linha.largura() * 0.68f, linha.altura());
    }
    public RetanguloDeTela fimDaLinhaPrincipalDoJogador() {
        RetanguloDeTela linha = linhaPrincipalDoJogador();
        return new RetanguloDeTela(linha.x() + linha.largura() * 0.72f, linha.y(),
                linha.largura() * 0.28f, linha.altura());
    }
    public RetanguloDeTela inicioDaLinhaSecundariaDoJogador() {
        RetanguloDeTela linha = linhaSecundariaDoJogador();
        return new RetanguloDeTela(linha.x(), linha.y(), linha.largura() * 0.44f, linha.altura());
    }
    public RetanguloDeTela fimDaLinhaSecundariaDoJogador() {
        RetanguloDeTela linha = linhaSecundariaDoJogador();
        return new RetanguloDeTela(linha.x() + linha.largura() * 0.48f, linha.y(),
                linha.largura() * 0.52f, linha.altura());
    }
    public RetanguloDeTela objetivo() { return objetivo; }
    public RetanguloDeTela textoDoObjetivo() { return textoDoObjetivo; }
    public RetanguloDeTela radar() { return radar; }
    public RetanguloDeTela radarInterno() { return radarInterno; }
    public RetanguloDeTela mapa() { return mapa; }
    public RetanguloDeTela areaUtilDoMapa() { return areaUtilDoMapa; }
    public RetanguloDeTela faixaSuperior() {
        return modo == Modo.FAIXAS
                ? new RetanguloDeTela(0f, areaUtilDoMapa.topo(), largura, altura - areaUtilDoMapa.topo())
                : vazio(largura, altura);
    }
    public RetanguloDeTela faixaInferior() {
        return modo == Modo.FAIXAS
                ? new RetanguloDeTela(0f, 0f, largura, areaUtilDoMapa.y())
                : vazio(largura, altura);
    }
    public RetanguloDeTela rodapeEsquerdo() { return rodapeEsquerdo; }

    public RetanguloDeTela rodapeEsquerdo(float larguraDoTexto) {
        if (!usaReserva()) {
            return rodapeEsquerdo;
        }
        float larguraMedida = larguraDoTexto + 32f * escala;
        return new RetanguloDeTela(rodapeEsquerdo.x(), rodapeEsquerdo.y(),
                Math.min(rodapeEsquerdo.largura(), larguraMedida), rodapeEsquerdo.altura());
    }

    public RetanguloDeTela rodapeDireito() { return rodapeDireito; }
    public RetanguloDeTela aviso() { return aviso; }
    public RetanguloDeTela seloDaTempestade() { return seloDaTempestade; }
    public RetanguloDeTela controles() { return controles; }
    public RetanguloDeTela painelDoFim() { return painelDoFim; }
    public RetanguloDeTela botaoDoFim(int indice) { return botoesDoFim[indice]; }

    public List<RetanguloDeTela> areasDoHud(boolean avisoVisivel, boolean tempestadeVisivel) {
        List<RetanguloDeTela> areas = new ArrayList<>();
        adicionarSeVisivel(areas, piloto);
        adicionarSeVisivel(areas, objetivo);
        if (!statusIntegradoAoJogador()) adicionarSeVisivel(areas, rodapeEsquerdo);
        adicionarSeVisivel(areas, radar);
        adicionarSeVisivel(areas, rodapeDireito);
        if (avisoVisivel) adicionarSeVisivel(areas, aviso);
        if (tempestadeVisivel) adicionarSeVisivel(areas, seloDaTempestade);
        adicionarSeVisivel(areas, controles);
        return List.copyOf(areas);
    }

    private static void adicionarSeVisivel(List<RetanguloDeTela> areas, RetanguloDeTela area) {
        if (area.largura() > 0f && area.altura() > 0f) areas.add(area);
    }

    public RetanguloDeTela fichaDoObjetivo(int indice) {
        if (modo == Modo.COLUNAS) {
            float margem = 12f * escala;
            return new RetanguloDeTela(objetivo.x() + margem,
                    textoDoObjetivo.y() - 12f * escala - 52f * escala - indice * 60f * escala,
                    objetivo.largura() - 2f * margem, 52f * escala);
        }
        if (modo == Modo.FAIXAS) {
            float margem = 12f * escala;
            float espaco = 8f * escala;
            float larguraDaFicha = (objetivo.largura() - 2f * margem - 2f * espaco) / 3f;
            float yDaFicha = objetivo.y() + 8f * escala;
            float alturaDaFicha = textoDoObjetivo.y() - 8f * escala - yDaFicha;
            return new RetanguloDeTela(objetivo.x() + margem + indice * (larguraDaFicha + espaco),
                    yDaFicha, larguraDaFicha, alturaDaFicha);
        }
        float larguraDaFicha = 170f * escala;
        float passo = 178f * escala;
        float larguraDoConjunto = larguraDaFicha + passo * 2f;
        float inicio = emRetrato ? objetivo.centroX() - larguraDoConjunto / 2f : objetivo.x() + 16f * escala;
        return new RetanguloDeTela(inicio + indice * passo, objetivo.y() + 12f * escala,
                larguraDaFicha, 52f * escala);
    }

    public RetanguloDeTela checkDoFundador(int indice) {
        RetanguloDeTela ficha = fichaDoObjetivo(indice);
        return new RetanguloDeTela(ficha.x() + 24f * escala, ficha.y() + 6f * escala,
                14f * escala, 14f * escala);
    }

    public RetanguloDeTela textoDaFichaDoObjetivo(int indice) {
        RetanguloDeTela ficha = fichaDoObjetivo(indice);
        float x = ficha.x() + 44f * escala;
        return new RetanguloDeTela(x, ficha.y(), ficha.direita() - x, ficha.altura());
    }

    public float celula() { return mapa.largura() / celulas; }
    public float origemX() { return mapa.x(); }
    public float origemY() { return mapa.y(); }
    public float largura() { return largura; }
    public float altura() { return altura; }
}
