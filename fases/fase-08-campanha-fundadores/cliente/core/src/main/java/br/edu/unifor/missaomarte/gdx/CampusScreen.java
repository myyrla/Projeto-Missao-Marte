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
import br.edu.unifor.missaomarte.gdx.campanha.ControleDoCampus;
import br.edu.unifor.missaomarte.gdx.campanha.ResumoVisualDaCampanha;
import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.recursos.CamadaDoCampus;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

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
    private final CampusLayout layout = CampusLayout.padrao();
    private final ControleDoCampus controle = new ControleDoCampus();

    public CampusScreen(MissaoMarteGame jogo) {
        super(jogo);
        this.campanha = jogo.campanha();
        atualizarBotoes();
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
            botoes.add(new BotaoVirtual("confirmar", "Confirmar", 430f, 210f, 150f, 54f));
            botoes.add(new BotaoVirtual("cancelar", "Cancelar", 220f, 210f, 150f, 54f));
            return;
        }
        ResumoVisualDaCampanha resumo = ResumoVisualDaCampanha.de(campanha);
        botoes.add(new BotaoVirtual("principal", resumo.rotuloDaAcao(), 570f, 128f, 200f, 54f));
        botoes.add(new BotaoVirtual("voltar", "Voltar", 570f, 62f, 200f, 48f));
    }

    @Override
    protected void desenhar(float delta) {
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
        formas.rect(0f, 0f, MissaoMarteGame.LARGURA, MissaoMarteGame.ALTURA);
        formas.setColor(PAINEL);
        formas.rect(556f, 42f, 224f, 488f);
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        RetanguloDeTela mapa = layout.mapa();
        jogo.batch().begin();
        jogo.batch().draw(jogo.recursos().texturaDaBaseDoCampus(),
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
        jogo.batch().begin();
        float y = 592f;
        fonte(FonteDoJogo.TITULO);
        cor(AMBAR);
        texto("CAMPUS UNIFOR MARTE", 32f, y);
        y = linhaAbaixo(y, 6f);
        fonte(FonteDoJogo.PEQUENO);
        cor(SECUNDARIO);
        texto(textoLimitado("Mapa autoral inspirado na disposição do campus de Fortaleza", 500f), 34f, y);

        float xPainel = 572f;
        float larguraPainel = 192f;
        y = 498f;
        fonte(FonteDoJogo.SUBTITULO);
        cor(TEXTO);
        texto(textoLimitado("Fundadores: " + resumo.fundadoresResgatados() + "/3", larguraPainel), xPainel, y);
        y = linhaAbaixo(y, 4f);
        fonte(FonteDoJogo.TEXTO);
        texto(textoLimitado("Pontuação: " + resumo.pontuacaoTotal(), larguraPainel), xPainel, y);
        y = linhaAbaixo(y, 5f);
        if (jogo.demonstracaoDaCampanha()) {
            cor(LILAS);
            texto(textoLimitado("Modo demonstração", larguraPainel), xPainel, y);
            y = linhaAbaixo(y, 6f);
        }

        y = Math.min(y, 420f);
        for (ResumoVisualDaCampanha.Etapa etapa : resumo.etapas()) {
            fonte(FonteDoJogo.TEXTO);
            cor(corDaEtapa(etapa.numero()));
            String marcador = etapa.situacao() == ResumoVisualDaCampanha.SituacaoDaEtapa.RESGATADA ? "[OK]"
                    : etapa.situacao() == ResumoVisualDaCampanha.SituacaoDaEtapa.ATUAL ? "[>]" : "[ ]";
            texto(textoLimitado(marcador + " Etapa " + etapa.numero(), larguraPainel), xPainel, y);
            y = linhaAbaixo(y, 2f);
            fonte(FonteDoJogo.PEQUENO);
            cor(SECUNDARIO);
            texto(textoLimitado(etapa.local(), larguraPainel - 16f), xPainel + 16f, y);
            y = linhaAbaixo(y, 1f);
            texto(textoLimitado(etapa.fundador(), larguraPainel - 16f), xPainel + 16f, y);
            y = linhaAbaixo(y, 8f);
        }

        fonte(FonteDoJogo.TEXTO);
        cor(campanha.concluida() ? LILAS : VERDE);
        textoCentralizado(textoLimitado(resumo.rotuloDaAcao(), larguraPainel), 670f, 155f);
        fonte(FonteDoJogo.PEQUENO);
        cor(SECUNDARIO);
        textoCentralizado(textoLimitado(jogo.modoToque() ? "Use os botões abaixo"
                : "Enter: selecionar   Esc: voltar", larguraPainel), 670f, 122f);
        jogo.batch().end();
    }

    private void desenharConfirmacao() {
        ShapeRenderer formas = jogo.shapes();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0f, 0f, 0f, 0.72f);
        formas.rect(0f, 0f, MissaoMarteGame.LARGURA, MissaoMarteGame.ALTURA);
        formas.setColor(PAINEL);
        formas.rect(170f, 190f, 460f, 220f);
        formas.end();
        formas.begin(ShapeType.Line);
        formas.setColor(LILAS);
        formas.rect(170f, 190f, 460f, 220f);
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        jogo.batch().begin();
        float y = 360f;
        fonte(FonteDoJogo.SUBTITULO);
        cor(LILAS);
        textoCentralizado("NOVA CAMPANHA", 400f, y);
        y = linhaAbaixo(y, 10f);
        fonte(FonteDoJogo.TEXTO);
        cor(TEXTO);
        textoCentralizado("O progresso e as pontuações", 400f, y);
        y = linhaAbaixo(y, 4f);
        textoCentralizado("serão apagados.", 400f, y);
        y = linhaAbaixo(y, 8f);
        fonte(FonteDoJogo.PEQUENO);
        cor(SECUNDARIO);
        textoCentralizado(jogo.modoToque() ? "Confirme ou cancele abaixo"
                : "Enter ou S: confirmar   N ou Esc: cancelar", 400f, y);
        jogo.batch().end();
    }

    private static Color corDaEtapa(int etapa) {
        return etapa == 1 ? CIANO : etapa == 2 ? AMBAR : VERDE;
    }
}
