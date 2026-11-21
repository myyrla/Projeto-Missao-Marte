package br.edu.unifor.missaomarte.gdx.campanha;

import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;

/** Geometria responsiva da tela do campus, sem dependencias do libGDX. */
public final class CampusLayout {
    private static final float PROPORCAO_DO_MAPA = 1056f / 864f;

    private final float largura;
    private final float altura;
    private final boolean vertical;
    private final RetanguloDeTela titulo;
    private final RetanguloDeTela mapa;
    private final RetanguloDeTela progresso;
    private final RetanguloDeTela linhaDosFundadores;
    private final RetanguloDeTela linhaDaPontuacao;
    private final RetanguloDeTela linhaDaDemonstracao;
    private final RetanguloDeTela[] etapas;
    private final RetanguloDeTela[] titulosDasEtapas;
    private final RetanguloDeTela[] cenariosDasEtapas;
    private final RetanguloDeTela[] fundadoresDasEtapas;
    private final RetanguloDeTela cartaoDaProximaMissao;
    private final RetanguloDeTela rodape;
    private final RetanguloDeTela botaoPrincipal;
    private final RetanguloDeTela botaoVoltar;
    private final RetanguloDeTela confirmacao;
    private final RetanguloDeTela botaoConfirmar;
    private final RetanguloDeTela botaoCancelar;

    private CampusLayout(float largura, float altura, boolean vertical, RetanguloDeTela titulo,
            RetanguloDeTela mapa, RetanguloDeTela progresso, RetanguloDeTela rodape,
            RetanguloDeTela linhaDosFundadores, RetanguloDeTela linhaDaPontuacao,
            RetanguloDeTela linhaDaDemonstracao, RetanguloDeTela[] etapas,
            RetanguloDeTela[] titulosDasEtapas, RetanguloDeTela[] cenariosDasEtapas,
            RetanguloDeTela[] fundadoresDasEtapas, RetanguloDeTela cartaoDaProximaMissao,
            RetanguloDeTela botaoPrincipal, RetanguloDeTela botaoVoltar,
            RetanguloDeTela confirmacao, RetanguloDeTela botaoConfirmar,
            RetanguloDeTela botaoCancelar) {
        this.largura = largura;
        this.altura = altura;
        this.vertical = vertical;
        this.titulo = titulo;
        this.mapa = mapa;
        this.progresso = progresso;
        this.rodape = rodape;
        this.linhaDosFundadores = linhaDosFundadores;
        this.linhaDaPontuacao = linhaDaPontuacao;
        this.linhaDaDemonstracao = linhaDaDemonstracao;
        this.etapas = etapas;
        this.titulosDasEtapas = titulosDasEtapas;
        this.cenariosDasEtapas = cenariosDasEtapas;
        this.fundadoresDasEtapas = fundadoresDasEtapas;
        this.cartaoDaProximaMissao = cartaoDaProximaMissao;
        this.botaoPrincipal = botaoPrincipal;
        this.botaoVoltar = botaoVoltar;
        this.confirmacao = confirmacao;
        this.botaoConfirmar = botaoConfirmar;
        this.botaoCancelar = botaoCancelar;
    }

    public static CampusLayout padrao() {
        return calcular(800f, 600f, false);
    }

    public static CampusLayout calcular(float largura, float altura, boolean toque) {
        if (toque && altura > largura) {
            return vertical(largura, altura, MedidasDoCampus.padrao());
        }
        return horizontal(largura, altura, MedidasDoCampus.padrao());
    }

    public static CampusLayout calcular(float largura, float altura, boolean toque,
            float alturaDoCartaoConcluido) {
        if (toque && altura > largura) {
            return vertical(largura, altura,
                    MedidasDoCampus.padraoComCartao(alturaDoCartaoConcluido, false, false));
        }
        return horizontal(largura, altura,
                MedidasDoCampus.padraoComCartao(alturaDoCartaoConcluido, false, false));
    }

    public static CampusLayout calcular(float largura, float altura, boolean toque,
            MedidasDoCampus medidas) {
        if (toque && altura > largura) {
            return vertical(largura, altura, medidas);
        }
        return horizontal(largura, altura, medidas);
    }

    private static CampusLayout horizontal(float largura, float altura, MedidasDoCampus medidas) {
        float alturaDoMapa = 400f;
        float larguraDoMapa = alturaDoMapa * PROPORCAO_DO_MAPA;
        RetanguloDeTela mapa = new RetanguloDeTela(284f - larguraDoMapa / 2f, 59f,
                larguraDoMapa, alturaDoMapa);
        RetanguloDeTela progresso = new RetanguloDeTela(540f, 21f, 244f, 558f);
        RetanguloDeTela rodape = new RetanguloDeTela(540f, 21f, 244f, 140f);
        RetanguloDeTela principal = new RetanguloDeTela(552f, 96f, 220f, 54f);
        RetanguloDeTela voltar = new RetanguloDeTela(552f, 35f, 220f, 48f);
        RetanguloDeTela confirmacao = new RetanguloDeTela(170f, 190f, 460f, 220f);
        RetanguloDeTela confirmar = new RetanguloDeTela(430f, 210f, 150f, 54f);
        RetanguloDeTela cancelar = new RetanguloDeTela(220f, 210f, 150f, 54f);

        float x = progresso.x() + 12f;
        float larguraDoConteudo = progresso.largura() - 24f;
        float cursor = progresso.topo() - 12f;
        RetanguloDeTela fundadores = linhaAbaixoDoCursor(x, larguraDoConteudo, cursor,
                medidas.alturaDosFundadores());
        cursor = fundadores.y() - 4f;
        RetanguloDeTela pontuacao = medidas.exibePontuacao()
                ? linhaAbaixoDoCursor(x, larguraDoConteudo, cursor, medidas.alturaDaPontuacao())
                : linhaVazia(x, cursor);
        if (medidas.exibePontuacao()) {
            cursor = pontuacao.y() - 4f;
        }
        RetanguloDeTela demonstracao = medidas.exibeDemonstracao()
                ? linhaAbaixoDoCursor(x, larguraDoConteudo, cursor, medidas.alturaDaDemonstracao())
                : linhaVazia(x, cursor);
        if (medidas.exibeDemonstracao()) {
            cursor = demonstracao.y() - 6f;
        }
        cursor -= 8f;

        RetanguloDeTela[] etapas = new RetanguloDeTela[3];
        RetanguloDeTela[] titulos = new RetanguloDeTela[3];
        RetanguloDeTela[] cenarios = new RetanguloDeTela[3];
        RetanguloDeTela[] fundadoresDasEtapas = new RetanguloDeTela[3];
        for (int i = 0; i < etapas.length; i++) {
            MedidasDoCampus.MedidasDaEtapa medida = medidas.etapas().get(i);
            float topoDaEtapa = cursor;
            titulos[i] = linhaAbaixoDoCursor(x + 28f, larguraDoConteudo - 28f, cursor,
                    medida.alturaDoTitulo());
            cursor = titulos[i].y() - 1f;
            cenarios[i] = linhaAbaixoDoCursor(x + 28f, larguraDoConteudo - 28f, cursor,
                    medida.alturaDoCenario());
            cursor = cenarios[i].y() - 1f;
            fundadoresDasEtapas[i] = linhaAbaixoDoCursor(x + 28f, larguraDoConteudo - 28f, cursor,
                    medida.alturaDoFundador());
            cursor = fundadoresDasEtapas[i].y();
            etapas[i] = new RetanguloDeTela(x, cursor, larguraDoConteudo, topoDaEtapa - cursor);
            cursor -= 6f;
        }

        cursor -= 4f;
        RetanguloDeTela cartao = linhaAbaixoDoCursor(x, larguraDoConteudo, cursor,
                medidas.alturaDoCartao());
        return new CampusLayout(largura, altura, false,
                new RetanguloDeTela(32f, 471f, 496f, 70f), mapa, progresso, rodape,
                fundadores, pontuacao, demonstracao, etapas, titulos, cenarios,
                fundadoresDasEtapas, cartao, principal, voltar, confirmacao, confirmar, cancelar);
    }

    private static RetanguloDeTela linhaAbaixoDoCursor(float x, float largura, float cursor,
            float altura) {
        return new RetanguloDeTela(x, cursor - altura, largura, altura);
    }

    private static RetanguloDeTela linhaVazia(float x, float y) {
        return new RetanguloDeTela(x, y, 0f, 0f);
    }

    private static CampusLayout vertical(float largura, float altura, MedidasDoCampus medidas) {
        float margem = 16f;
        float espaco = 8f;
        float alturaDoTitulo = 56f;
        float alturaDoRodape = 90f;
        RetanguloDeTela titulo = new RetanguloDeTela(0f, altura - alturaDoTitulo,
                largura, alturaDoTitulo);
        float alturaDoMapa = largura / PROPORCAO_DO_MAPA;
        RetanguloDeTela mapa = new RetanguloDeTela(0f, titulo.y() - alturaDoMapa,
                largura, alturaDoMapa);
        RetanguloDeTela rodape = new RetanguloDeTela(0f, 0f, largura, alturaDoRodape);
        float topoDoProgresso = mapa.y() - espaco;
        float baseDoProgresso = rodape.topo() + espaco;
        RetanguloDeTela progresso = new RetanguloDeTela(margem, baseDoProgresso,
                largura - 2f * margem, Math.max(0f, topoDoProgresso - baseDoProgresso));
        float espacoDasEtapas = 6f;
        float margemDoPainel = 12f;
        float larguraDoConteudo = progresso.largura() - 2f * margemDoPainel;
        float xDoCabecalho = progresso.x() + margemDoPainel;
        float cursorDoCabecalho = progresso.topo() - 4f;
        float espacoDoCabecalho = menorAlturaVisivel(medidas) * 0.2f;
        RetanguloDeTela fundadores = linhaAbaixoDoCursor(xDoCabecalho, larguraDoConteudo,
                cursorDoCabecalho, medidas.alturaDosFundadores());
        cursorDoCabecalho = fundadores.y();
        RetanguloDeTela pontuacao = medidas.exibePontuacao()
                ? linhaAbaixoDoCursor(xDoCabecalho, larguraDoConteudo,
                        cursorDoCabecalho - espacoDoCabecalho,
                        medidas.alturaDaPontuacao())
                : linhaVazia(xDoCabecalho, cursorDoCabecalho);
        if (medidas.exibePontuacao()) {
            cursorDoCabecalho = pontuacao.y();
        }
        RetanguloDeTela demonstracao = medidas.exibeDemonstracao()
                ? linhaAbaixoDoCursor(xDoCabecalho, larguraDoConteudo,
                        cursorDoCabecalho - espacoDoCabecalho,
                        medidas.alturaDaDemonstracao())
                : linhaVazia(xDoCabecalho, cursorDoCabecalho);
        if (medidas.exibeDemonstracao()) {
            cursorDoCabecalho = demonstracao.y();
        }
        float topoDasEtapas = cursorDoCabecalho - 4f;
        RetanguloDeTela[] etapas = new RetanguloDeTela[3];
        RetanguloDeTela[] titulos = new RetanguloDeTela[3];
        RetanguloDeTela[] cenarios = new RetanguloDeTela[3];
        RetanguloDeTela[] fundadoresDasEtapas = new RetanguloDeTela[3];
        float cursor = topoDasEtapas;
        for (int i = 0; i < etapas.length; i++) {
            MedidasDoCampus.MedidasDaEtapa medida = medidas.etapas().get(i);
            float alturaDoSubtitulo = Math.max(medida.alturaDoCenario(), medida.alturaDoFundador());
            float respiro = alturaDoSubtitulo > 0f
                    ? Math.min(medida.alturaDoTitulo(), alturaDoSubtitulo) * 0.18f : 0f;
            float alturaDaEtapa = Math.max(32f,
                    medida.alturaDoTitulo() + respiro + alturaDoSubtitulo);
            etapas[i] = new RetanguloDeTela(progresso.x() + 12f,
                    cursor - alturaDaEtapa,
                    progresso.largura() - 24f, alturaDaEtapa);
            float xDoTexto = etapas[i].x() + 28f;
            float larguraDoTexto = etapas[i].largura() - 28f;
            titulos[i] = new RetanguloDeTela(xDoTexto,
                    etapas[i].topo() - medida.alturaDoTitulo(), larguraDoTexto,
                    medida.alturaDoTitulo());
            cenarios[i] = new RetanguloDeTela(xDoTexto, etapas[i].y(), larguraDoTexto, alturaDoSubtitulo);
            fundadoresDasEtapas[i] = linhaVazia(xDoTexto, etapas[i].y());
            cursor = etapas[i].y() - espacoDasEtapas;
        }
        float topoDoCartao = etapas[2].y() - 10f;
        float alturaDoCartao = medidas.alturaDoCartao();
        float yDoCartao = topoDoCartao - alturaDoCartao;
        RetanguloDeTela cartao = new RetanguloDeTela(progresso.x() + 12f, yDoCartao,
                progresso.largura() - 24f, alturaDoCartao);

        float alturaDoBotao = 56f;
        float yDoBotao = 24f;
        float larguraVoltar = Math.min(178f, largura * 0.33f);
        float larguraPrincipal = largura - 2f * margem - espaco - larguraVoltar;
        RetanguloDeTela principal = new RetanguloDeTela(margem, yDoBotao,
                larguraPrincipal, alturaDoBotao);
        RetanguloDeTela voltar = new RetanguloDeTela(principal.direita() + espaco,
                yDoBotao, larguraVoltar, alturaDoBotao);

        float larguraDaConfirmacao = Math.min(460f, largura - 2f * margem);
        float alturaDaConfirmacao = 220f;
        RetanguloDeTela confirmacao = new RetanguloDeTela((largura - larguraDaConfirmacao) / 2f,
                (altura - alturaDaConfirmacao) / 2f, larguraDaConfirmacao, alturaDaConfirmacao);
        float larguraDoBotaoModal = Math.min(150f, (larguraDaConfirmacao - 60f) / 2f);
        float espacoModal = 20f;
        float inicioModal = confirmacao.centroX() - (2f * larguraDoBotaoModal + espacoModal) / 2f;
        RetanguloDeTela cancelar = new RetanguloDeTela(inicioModal, confirmacao.y() + 20f,
                larguraDoBotaoModal, 54f);
        RetanguloDeTela confirmar = new RetanguloDeTela(cancelar.direita() + espacoModal,
                cancelar.y(), larguraDoBotaoModal, 54f);
        return new CampusLayout(largura, altura, true, titulo, mapa, progresso, rodape,
                fundadores, pontuacao, demonstracao, etapas, titulos, cenarios,
                fundadoresDasEtapas, cartao,
                principal, voltar, confirmacao, confirmar, cancelar);
    }

    private static float menorAlturaVisivel(MedidasDoCampus medidas) {
        float menor = medidas.alturaDosFundadores();
        if (medidas.exibePontuacao()) {
            menor = Math.min(menor, medidas.alturaDaPontuacao());
        }
        if (medidas.exibeDemonstracao()) {
            menor = Math.min(menor, medidas.alturaDaDemonstracao());
        }
        return menor;
    }

    public float largura() { return largura; }
    public float altura() { return altura; }
    public boolean vertical() { return vertical; }
    public RetanguloDeTela titulo() { return titulo; }
    public RetanguloDeTela mapa() { return mapa; }
    public RetanguloDeTela progresso() { return progresso; }
    public RetanguloDeTela linhaDosFundadores() { return linhaDosFundadores; }
    public RetanguloDeTela linhaDaPontuacao() { return linhaDaPontuacao; }
    public RetanguloDeTela linhaDaDemonstracao() { return linhaDaDemonstracao; }
    public RetanguloDeTela etapa(int indice) { return etapas[indice]; }
    public RetanguloDeTela tituloDaEtapa(int indice) { return titulosDasEtapas[indice]; }
    public RetanguloDeTela cenarioDaEtapa(int indice) { return cenariosDasEtapas[indice]; }
    public RetanguloDeTela fundadorDaEtapa(int indice) { return fundadoresDasEtapas[indice]; }
    public RetanguloDeTela cartaoDaProximaMissao() { return cartaoDaProximaMissao; }
    public RetanguloDeTela rodape() { return rodape; }
    public RetanguloDeTela botaoPrincipal() { return botaoPrincipal; }
    public RetanguloDeTela botaoVoltar() { return botaoVoltar; }
    public RetanguloDeTela confirmacao() { return confirmacao; }
    public RetanguloDeTela botaoConfirmar() { return botaoConfirmar; }
    public RetanguloDeTela botaoCancelar() { return botaoCancelar; }
}
