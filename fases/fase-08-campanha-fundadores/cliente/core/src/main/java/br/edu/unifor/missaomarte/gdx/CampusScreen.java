package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.campanha.Campanha;
import br.edu.unifor.missaomarte.application.campanha.Campus;
import br.edu.unifor.missaomarte.application.campanha.EtapaDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.PreparacaoDaEtapa;
import br.edu.unifor.missaomarte.application.pontuacao.RegraDePontuacaoDaCampanha;
import br.edu.unifor.missaomarte.application.pontuacao.RegraDePontuacaoPadrao;
import br.edu.unifor.missaomarte.gdx.campanha.CampusLayout;
import br.edu.unifor.missaomarte.gdx.campanha.ConteudoDoCartaoDaCampanha;
import br.edu.unifor.missaomarte.gdx.campanha.ControleDoCampus;
import br.edu.unifor.missaomarte.gdx.campanha.MedidasDoCartao;
import br.edu.unifor.missaomarte.gdx.campanha.MedidasDoCampus;
import br.edu.unifor.missaomarte.gdx.campanha.MetricasTipograficasDoCampus;
import br.edu.unifor.missaomarte.gdx.campanha.ResumoVisualDaCampanha;
import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.recursos.CamadaDoCampus;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import br.edu.unifor.missaomarte.gdx.ui.IconeDaInterface;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import java.util.ArrayList;
import java.util.List;

/** Tela de progresso e selecao de missoes da Campanha dos Fundadores. */
public class CampusScreen extends TelaBase {
    private static final Color FUNDO = Color.valueOf("0A0612");
    private static final Color PAINEL = new Color(Color.valueOf("0C0814")).mul(1f, 1f, 1f, 0.82f);
    private static final Color CIANO = Color.valueOf("4CC9F0");
    private static final Color AMBAR = Color.valueOf("FFB000");
    private static final Color VERDE = Color.valueOf("9EF01A");
    private static final Color LILAS = Color.valueOf("B388FF");
    private static final Color TEXTO = Color.valueOf("F2EEF7");
    private static final Color SECUNDARIO = Color.valueOf("B9B2C6");

    private final Campanha campanha;
    private final GlyphLayout medidor = new GlyphLayout();
    private CampusLayout layout;
    private final ControleDoCampus controle = new ControleDoCampus();

    public CampusScreen(MissaoMarteGame jogo) {
        super(jogo);
        this.campanha = jogo.campanha();
        this.layout = layoutAtual();
        atualizarBotoes();
    }

    @Override
    protected boolean usaMundoInteiro() {
        return jogo.modoToque() && jogo.viewport().getWorldHeight() > jogo.viewport().getWorldWidth();
    }

    @Override
    protected void aoRedimensionar() {
        layout = layoutAtual();
        atualizarBotoes();
    }

    private CampusLayout layoutAtual() {
        float largura;
        float altura;
        boolean vertical;
        if (jogo.modoToque() && jogo.viewport().getWorldHeight() > jogo.viewport().getWorldWidth()) {
            largura = jogo.viewport().getWorldWidth();
            altura = jogo.viewport().getWorldHeight();
            vertical = true;
        } else {
            largura = MissaoMarteGame.LARGURA;
            altura = MissaoMarteGame.ALTURA;
            vertical = false;
        }
        CampusLayout base = CampusLayout.calcular(largura, altura, vertical);
        ResumoVisualDaCampanha resumo = ResumoVisualDaCampanha.de(campanha);
        ConteudoDoCartaoDaCampanha conteudo = ConteudoDoCartaoDaCampanha.de(
                resumo, jogo.demonstracaoDaCampanha());
        float alturaDoCartao = medirCartao(
                base.cartaoDaProximaMissao().largura(), conteudo).alturaTotal();
        if (vertical) {
            return CampusLayout.calcular(largura, altura, true,
                    medirConteudoVertical(base, resumo, alturaDoCartao));
        }
        return CampusLayout.calcular(largura, altura, false,
                medirConteudoHorizontal(base, resumo, alturaDoCartao));
    }

    private MedidasDoCampus medirConteudoHorizontal(CampusLayout base,
            ResumoVisualDaCampanha resumo, float alturaDoCartao) {
        float largura = base.progresso().largura() - 24f;
        float larguraDaEtapa = largura - 28f;
        List<MedidasDoCampus.MedidasDaEtapa> etapas = new ArrayList<>();
        for (ResumoVisualDaCampanha.Etapa etapa : resumo.etapas()) {
            etapas.add(new MedidasDoCampus.MedidasDaEtapa(
                    medirAltura(FonteDoJogo.TEXTO, "Etapa " + etapa.numero(), larguraDaEtapa),
                    medirAltura(FonteDoJogo.PEQUENO, etapa.local(), larguraDaEtapa),
                    medirAltura(FonteDoJogo.PEQUENO, etapa.fundador(), larguraDaEtapa)));
        }
        boolean exibePontuacao = resumo.exibePontuacaoNoCabecalho();
        boolean exibeDemonstracao = jogo.demonstracaoDaCampanha();
        return new MedidasDoCampus(
                medirAltura(FonteDoJogo.SUBTITULO,
                        "Fundadores: " + resumo.fundadoresResgatados() + "/3", largura),
                exibePontuacao ? medirAltura(FonteDoJogo.TEXTO,
                        "Pontuação: " + resumo.pontuacaoTotal(), largura) : 0f,
                exibeDemonstracao ? medirAltura(FonteDoJogo.PEQUENO,
                        "Modo demonstração", largura) : 0f,
                exibePontuacao, exibeDemonstracao, etapas, alturaDoCartao);
    }

    private MedidasDoCampus medirConteudoVertical(CampusLayout base,
            ResumoVisualDaCampanha resumo, float alturaDoCartao) {
        float largura = base.progresso().largura() - 24f;
        boolean exibePontuacao = resumo.exibePontuacaoNoCabecalho();
        boolean exibeDemonstracao = jogo.demonstracaoDaCampanha();
        float larguraDoCabecalho = largura;
        float larguraDaEtapa = largura - 28f;
        List<MedidasDoCampus.MedidasDaEtapa> etapas = new ArrayList<>();
        for (ResumoVisualDaCampanha.Etapa etapa : resumo.etapas()) {
            etapas.add(new MedidasDoCampus.MedidasDaEtapa(
                    medirAltura(FonteDoJogo.TEXTO, "Etapa " + etapa.numero(), larguraDaEtapa),
                    medirAltura(FonteDoJogo.PEQUENO,
                            etapa.local() + " · " + etapa.fundador(), larguraDaEtapa),
                    0f));
        }
        return new MedidasDoCampus(
                medirAltura(FonteDoJogo.SUBTITULO,
                        "Fundadores: " + resumo.fundadoresResgatados() + "/3", larguraDoCabecalho),
                exibePontuacao ? medirAltura(FonteDoJogo.TEXTO,
                        "Pontuação: " + resumo.pontuacaoTotal(), larguraDoCabecalho) : 0f,
                exibeDemonstracao ? medirAltura(FonteDoJogo.PEQUENO,
                        "Modo demonstração", larguraDoCabecalho) : 0f,
                exibePontuacao, exibeDemonstracao, etapas, alturaDoCartao);
    }

    private MedidasDoCartao medirCartao(float largura, ConteudoDoCartaoDaCampanha conteudo) {
        float larguraDoTexto = Math.max(1f, largura - 24f);
        BitmapFont fonteDoTitulo = jogo.fonte(FonteDoJogo.ROTULO);
        medidor.setText(fonteDoTitulo, conteudo.titulo(), TEXTO,
                larguraDoTexto, Align.left, true);
        float alturaDoLayoutDoTitulo = medidor.height;
        BitmapFont fonteDoTexto = jogo.fonte(FonteDoJogo.TEXTO);
        List<Float> alturasDosLayouts = new ArrayList<>();
        for (String linha : conteudo.linhas()) {
            medidor.setText(fonteDoTexto, linha, TEXTO, larguraDoTexto, Align.left, true);
            alturasDosLayouts.add(medidor.height);
        }
        return MetricasTipograficasDoCampus.medidasDoCartao(
                alturaDoLayoutDoTitulo, fonteDoTitulo.getCapHeight(), fonteDoTitulo.getLineHeight(),
                alturasDosLayouts, fonteDoTexto.getCapHeight(), fonteDoTexto.getLineHeight());
    }

    private float medirAltura(FonteDoJogo fonte, String texto, float largura) {
        BitmapFont bitmapFont = jogo.fonte(fonte);
        medidor.setText(bitmapFont, texto, TEXTO, Math.max(1f, largura), Align.left, true);
        return MetricasTipograficasDoCampus.alturaEmLinhas(
                medidor.height, bitmapFont.getCapHeight(), bitmapFont.getLineHeight());
    }

    @Override
    public void show() {
        super.show();
        jogo.sons().retomarMusica();
        definirEntrada(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return tratarTecla(keycode);
            }
        });
    }

    private boolean tratarTecla(int keycode) {
        if (controle.confirmandoNovaCampanha()) {
            if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.S) {
                confirmarNovaCampanha();
                return true;
            }
            if (ehVoltar(keycode) || keycode == Keys.N) {
                cancelarNovaCampanha();
                return true;
            }
            return false;
        }
        if (ehVoltar(keycode)) {
            jogo.setScreen(new MenuScreen(jogo));
            return true;
        }
        if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.BUTTON_A) {
            acionarPrincipal();
            return true;
        }
        return false;
    }

    @Override
    protected boolean aoTocar(String id, int ponteiro) {
        switch (id) {
            case "principal":
                acionarPrincipal();
                return true;
            case "voltar":
                jogo.setScreen(new MenuScreen(jogo));
                return true;
            case "confirmar":
                confirmarNovaCampanha();
                return true;
            case "cancelar":
                cancelarNovaCampanha();
                return true;
            default:
                return false;
        }
    }

    private void acionarPrincipal() {
        jogo.sons().tocar(Som.SELECIONAR);
        if (controle.acionarPrincipal(campanha) == ControleDoCampus.AcaoPrincipal.INICIAR_MISSAO) {
            iniciarMissao();
        }
        atualizarBotoes();
    }

    private void confirmarNovaCampanha() {
        controle.confirmarNovaCampanha(campanha);
        jogo.sons().tocar(Som.SELECIONAR);
        atualizarBotoes();
    }

    private void cancelarNovaCampanha() {
        controle.cancelarNovaCampanha();
        atualizarBotoes();
    }

    private void iniciarMissao() {
        EtapaDaCampanha etapa = campanha.etapaAtual().orElseThrow();
        MissaoFactory fabrica = jogo.missaoFactory();
        PreparacaoDaEtapa preparacao = fabrica.criarEtapaDaCampanha(
                etapa, MissaoMarteGame.MIN, MissaoMarteGame.MAX, jogo.random());
        String piloto = jogo.sessao() == null ? "Piloto" : jogo.sessao().getNome();
        RegraDePontuacaoDaCampanha pontuacao = new RegraDePontuacaoDaCampanha(
                new RegraDePontuacaoPadrao(fabrica.pontuacaoInicial(etapa.dificuldade())));
        GameSession session = new GameSession(piloto, etapa.dificuldade(), preparacao.missao(),
                pontuacao, MissaoMarteGame.MIN, MissaoMarteGame.MAX,
                jogo.random(), etapa.modo(), preparacao.mecanicas());
        jogo.setScreen(new GameScreen(jogo, session, campanha, etapa));
    }

    private void atualizarBotoes() {
        botoes.clear();
        if (controle.confirmandoNovaCampanha()) {
            botoes.add(botao("confirmar", "Confirmar", layout.botaoConfirmar()));
            botoes.add(botao("cancelar", "Cancelar", layout.botaoCancelar()));
            return;
        }
        ResumoVisualDaCampanha resumo = ResumoVisualDaCampanha.de(campanha);
        botoes.add(botao("principal", resumo.rotuloDaAcao(), layout.botaoPrincipal()));
        botoes.add(botao("voltar", "Voltar", layout.botaoVoltar()));
    }

    private static BotaoVirtual botao(String id, String rotulo, RetanguloDeTela area) {
        return new BotaoVirtual(id, rotulo, area.x(), area.y(), area.largura(), area.altura());
    }

    @Override
    protected void desenhar(float delta) {
        CampusLayout atual = layoutAtual();
        if (atual.vertical() != layout.vertical()
                || atual.largura() != layout.largura() || atual.altura() != layout.altura()
                || atual.cartaoDaProximaMissao().altura()
                        != layout.cartaoDaProximaMissao().altura()
                || atual.linhaDaPontuacao().altura() != layout.linhaDaPontuacao().altura()
                || atual.linhaDaDemonstracao().altura() != layout.linhaDaDemonstracao().altura()
                || atual.etapa(0).altura() != layout.etapa(0).altura()) {
            layout = atual;
            atualizarBotoes();
        }
        ResumoVisualDaCampanha resumo = ResumoVisualDaCampanha.de(campanha);
        desenharCampus(campanha.campus());
        desenharTextos(resumo);
        if (controle.confirmandoNovaCampanha()) {
            desenharConfirmacao();
        }
    }

    private void desenharCampus(Campus campus) {
        ShapeRenderer formas = jogo.shapes();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(FUNDO);
        formas.rect(0f, 0f, layout.largura(), layout.altura());
        formas.setColor(PAINEL);
        RetanguloDeTela progresso = layout.progresso();
        formas.rect(progresso.x(), progresso.y(), progresso.largura(), progresso.altura());
        if (layout.vertical()) {
            formas.setColor(FUNDO);
            RetanguloDeTela rodape = layout.rodape();
            formas.rect(rodape.x(), rodape.y(), rodape.largura(), rodape.altura());
        }
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        RetanguloDeTela mapa = layout.mapa();
        jogo.batch().begin();
        jogo.batch().draw(jogo.recursos().texturaDaBaseDoCampus(campanha.concluida()),
                mapa.x(), mapa.y(), mapa.largura(), mapa.altura());
        for (CamadaDoCampus camada : CamadaDoCampus.values()) {
            jogo.batch().draw(jogo.recursos().textura(camada, campus.estado(camada.construcao())),
                    mapa.x(), mapa.y(), mapa.largura(), mapa.altura());
        }
        jogo.batch().end();

        formas.begin(ShapeType.Line);
        formas.setColor(AMBAR);
        formas.rect(mapa.x(), mapa.y(), mapa.largura(), mapa.altura());
        formas.end();
    }

    private void desenharTextos(ResumoVisualDaCampanha resumo) {
        if (layout.vertical()) {
            desenharTextosVerticais(resumo);
            return;
        }
        jogo.batch().begin();
        RetanguloDeTela titulo = layout.titulo();
        float y = titulo.topo() - 4f;
        fonte(FonteDoJogo.TITULO);
        cor(AMBAR);
        texto("CAMPUS UNIFOR MARTE", titulo.x(), y);
        y = linhaAbaixo(y, 6f);
        fonte(FonteDoJogo.PEQUENO);
        cor(SECUNDARIO);
        texto("Mapa autoral inspirado na disposição do campus de Fortaleza", titulo.x() + 2f, y);

        desenharTextoNaArea("Fundadores: " + resumo.fundadoresResgatados() + "/3",
                layout.linhaDosFundadores(), FonteDoJogo.SUBTITULO, TEXTO);
        if (resumo.exibePontuacaoNoCabecalho()) {
            desenharTextoNaArea("Pontuação: " + resumo.pontuacaoTotal(),
                    layout.linhaDaPontuacao(), FonteDoJogo.TEXTO, TEXTO);
        }
        if (jogo.demonstracaoDaCampanha()) {
            desenharTextoNaArea("Modo demonstração", layout.linhaDaDemonstracao(),
                    FonteDoJogo.PEQUENO, LILAS);
        }

        desenharEtapasHorizontais(resumo);
        RetanguloDeTela cartao = layout.cartaoDaProximaMissao();
        jogo.estilo().ficha(jogo.batch(), cartao,
                resumo.conclusao().isPresent() ? LILAS : corDaEtapa(etapaAtual(resumo)));
        desenharConteudoDoCartao(resumo, cartao);

        if (!jogo.modoToque()) {
            fonte(FonteDoJogo.TEXTO);
            cor(campanha.concluida() ? LILAS : VERDE);
            textoCentralizado(textoLimitado(resumo.rotuloDaAcao(),
                            layout.botaoPrincipal().largura()),
                    layout.botaoPrincipal().centroX(), layout.botaoPrincipal().centroY());
            fonte(FonteDoJogo.PEQUENO);
            cor(SECUNDARIO);
            textoCentralizado(textoLimitado("Enter: selecionar   Esc: voltar",
                            layout.botaoVoltar().largura()),
                    layout.botaoVoltar().centroX(), layout.botaoVoltar().centroY());
        }
        jogo.batch().end();
    }

    private void desenharEtapasHorizontais(ResumoVisualDaCampanha resumo) {
        int indice = 0;
        for (ResumoVisualDaCampanha.Etapa etapa : resumo.etapas()) {
            desenharIndicador(etapa, layout.etapa(indice).x(), layout.tituloDaEtapa(indice).topo());
            desenharTextoNaArea("Etapa " + etapa.numero(), layout.tituloDaEtapa(indice),
                    FonteDoJogo.TEXTO, corDaEtapa(etapa.numero()));
            desenharTextoNaArea(etapa.local(), layout.cenarioDaEtapa(indice),
                    FonteDoJogo.PEQUENO, SECUNDARIO);
            desenharTextoNaArea(etapa.fundador(), layout.fundadorDaEtapa(indice),
                    FonteDoJogo.PEQUENO, SECUNDARIO);
            indice++;
        }
    }

    private void desenharTextoNaArea(String texto, RetanguloDeTela area,
            FonteDoJogo fonte, Color cor) {
        desenharTextoNaArea(texto, area, fonte, cor, Align.left);
    }

    private void desenharTextoNaArea(String texto, RetanguloDeTela area,
            FonteDoJogo fonte, Color cor, int alinhamento) {
        BitmapFont bitmapFont = jogo.fonte(fonte);
        bitmapFont.setColor(cor);
        bitmapFont.draw(jogo.batch(), texto, area.x(), area.topo(), area.largura(), alinhamento, true);
    }

    private void desenharTextosVerticais(ResumoVisualDaCampanha resumo) {
        RetanguloDeTela titulo = layout.titulo();
        jogo.batch().begin();
        RetanguloDeTela cartao = layout.cartaoDaProximaMissao();
        jogo.estilo().ficha(jogo.batch(), cartao,
                resumo.conclusao().isPresent() ? LILAS : corDaEtapa(etapaAtual(resumo)));
        fonte(FonteDoJogo.TITULO);
        cor(AMBAR);
        texto("CAMPUS UNIFOR MARTE", titulo.x() + 16f, titulo.topo() - 16f);

        desenharTextoNaArea("Fundadores: " + resumo.fundadoresResgatados() + "/3",
                layout.linhaDosFundadores(), FonteDoJogo.SUBTITULO, TEXTO, Align.left);
        if (resumo.exibePontuacaoNoCabecalho()) {
            desenharTextoNaArea("Pontuação: " + resumo.pontuacaoTotal(),
                    layout.linhaDaPontuacao(), FonteDoJogo.TEXTO, TEXTO, Align.left);
        }
        if (jogo.demonstracaoDaCampanha()) {
            desenharTextoNaArea("Modo demonstração", layout.linhaDaDemonstracao(),
                    FonteDoJogo.PEQUENO, LILAS, Align.left);
        }

        int indice = 0;
        for (ResumoVisualDaCampanha.Etapa etapa : resumo.etapas()) {
            RetanguloDeTela linhaDaEtapa = layout.etapa(indice);
            desenharIndicador(etapa, linhaDaEtapa.x(), layout.tituloDaEtapa(indice).topo());
            desenharTextoNaArea("Etapa " + etapa.numero(), layout.tituloDaEtapa(indice),
                    FonteDoJogo.TEXTO, corDaEtapa(etapa.numero()));
            desenharTextoNaArea(etapa.local() + " · " + etapa.fundador(),
                    layout.cenarioDaEtapa(indice), FonteDoJogo.PEQUENO, SECUNDARIO);
            indice++;
        }
        desenharConteudoDoCartao(resumo, cartao);
        jogo.batch().end();
    }

    private void desenharConteudoDoCartao(ResumoVisualDaCampanha resumo,
            RetanguloDeTela cartao) {
        ConteudoDoCartaoDaCampanha conteudo = ConteudoDoCartaoDaCampanha.de(
                resumo, jogo.demonstracaoDaCampanha());
        MedidasDoCartao medidas = medirCartao(cartao.largura(), conteudo);
        float x = cartao.x() + 12f;
        float largura = cartao.largura() - 24f;
        desenharLinhaQuebrada(conteudo.titulo(), FonteDoJogo.ROTULO,
                resumo.conclusao().isPresent() ? LILAS : SECUNDARIO,
                x, medidas.topoDoTitulo(cartao.topo()), largura);
        for (int i = 0; i < conteudo.linhas().size(); i++) {
            desenharLinhaQuebrada(conteudo.linhas().get(i), FonteDoJogo.TEXTO,
                    TEXTO, x, medidas.topoDoConteudo(cartao.topo(), i), largura);
        }
    }

    private void desenharLinhaQuebrada(String texto, FonteDoJogo fonte, Color cor,
            float x, float y, float largura) {
        BitmapFont bitmapFont = jogo.fonte(fonte);
        bitmapFont.setColor(cor);
        medidor.setText(bitmapFont, texto, cor, Math.max(1f, largura), Align.left, true);
        bitmapFont.draw(jogo.batch(), medidor, x, y);
    }

    private static int etapaAtual(ResumoVisualDaCampanha resumo) {
        return resumo.etapas().stream()
                .filter(etapa -> etapa.situacao() == ResumoVisualDaCampanha.SituacaoDaEtapa.ATUAL)
                .mapToInt(ResumoVisualDaCampanha.Etapa::numero)
                .findFirst()
                .orElse(3);
    }

    private void desenharIndicador(ResumoVisualDaCampanha.Etapa etapa, float x, float linha) {
        IconeDaInterface icone = etapa.situacao() == ResumoVisualDaCampanha.SituacaoDaEtapa.RESGATADA
                ? IconeDaInterface.CHECK
                : etapa.situacao() == ResumoVisualDaCampanha.SituacaoDaEtapa.ATUAL
                        ? IconeDaInterface.SETA : null;
        if (icone != null) {
            jogo.batch().draw(jogo.estilo().icone(icone), x, linha - 18f, 20f, 20f);
        }
    }

    private void desenharConfirmacao() {
        ShapeRenderer formas = jogo.shapes();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0f, 0f, 0f, 0.72f);
        formas.rect(0f, 0f, layout.largura(), layout.altura());
        formas.setColor(PAINEL);
        RetanguloDeTela modal = layout.confirmacao();
        formas.rect(modal.x(), modal.y(), modal.largura(), modal.altura());
        formas.end();
        formas.begin(ShapeType.Line);
        formas.setColor(LILAS);
        formas.rect(modal.x(), modal.y(), modal.largura(), modal.altura());
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        jogo.batch().begin();
        float y = modal.topo() - 50f;
        fonte(FonteDoJogo.SUBTITULO);
        cor(LILAS);
        textoCentralizado("NOVA CAMPANHA", modal.centroX(), y);
        y = linhaAbaixo(y, 10f);
        fonte(FonteDoJogo.TEXTO);
        cor(TEXTO);
        textoCentralizado("O progresso e as pontuações", modal.centroX(), y);
        y = linhaAbaixo(y, 4f);
        textoCentralizado("serão apagados.", modal.centroX(), y);
        y = linhaAbaixo(y, 8f);
        fonte(FonteDoJogo.PEQUENO);
        cor(SECUNDARIO);
        if (!jogo.modoToque()) {
            textoCentralizado("Enter ou S: confirmar   N ou Esc: cancelar", modal.centroX(), y);
        }
        jogo.batch().end();
    }

    private static Color corDaEtapa(int etapa) {
        return etapa == 1 ? CIANO : etapa == 2 ? AMBAR : VERDE;
    }
}
