package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.campanha.Campanha;
import br.edu.unifor.missaomarte.application.campanha.Campus;
import br.edu.unifor.missaomarte.application.campanha.Construcao;
import br.edu.unifor.missaomarte.application.campanha.EstadoDaConstrucao;
import br.edu.unifor.missaomarte.application.campanha.EtapaDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.Fundador;
import br.edu.unifor.missaomarte.application.campanha.PreparacaoDaEtapa;
import br.edu.unifor.missaomarte.gdx.campanha.CampusLayout;
import br.edu.unifor.missaomarte.gdx.campanha.ControleDoCampus;
import br.edu.unifor.missaomarte.gdx.campanha.ResumoVisualDaCampanha;
import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
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
    private static final Color TERRENO = Color.valueOf("7A2F16");
    private static final Color AVENIDA = Color.valueOf("3A2630");
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
        GameSession session = new GameSession(piloto, etapa.dificuldade(), preparacao.missao(),
                fabrica.pontuacaoInicial(etapa.dificuldade()), MissaoMarteGame.MIN, MissaoMarteGame.MAX,
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
        RetanguloDeTela mapa = layout.mapa();
        formas.setColor(TERRENO);
        formas.rect(mapa.x(), mapa.y(), mapa.largura(), mapa.altura());
        formas.setColor(AVENIDA);
        formas.rect(mapa.x() + 10f, mapa.y() + 10f, mapa.largura() - 20f, 14f);
        formas.rect(mapa.x() + 10f, mapa.topo() - 24f, mapa.largura() - 20f, 14f);
        formas.rect(mapa.x() + 10f, mapa.y() + 10f, 14f, mapa.altura() - 20f);
        formas.rect(mapa.direita() - 24f, mapa.y() + 10f, 14f, mapa.altura() - 20f);
        formas.rect(164f, 140f, 12f, 340f);
        formas.rect(348f, 140f, 12f, 340f);

        for (CampusLayout.Decoracao decoracao : layout.decoracoes()) {
            formas.setColor("Gelo de água".equals(decoracao.rotulo()) ? CIANO.cpy().mul(0.55f)
                    : SECUNDARIO.cpy().mul(0.35f));
            retangulo(formas, decoracao.retangulo());
        }
        for (CampusLayout.Area area : layout.areas()) {
            EstadoDaConstrucao estado = campus.estado(area.construcao());
            if (estado != EstadoDaConstrucao.PLANTA) {
                Color cor = corDaConstrucao(area.construcao());
                formas.setColor(cor.r, cor.g, cor.b, estado == EstadoDaConstrucao.INAUGURADA ? 0.95f : 0.78f);
                retangulo(formas, area.retangulo());
            }
        }
        formas.end();

        formas.begin(ShapeType.Line);
        formas.setColor(AMBAR);
        formas.rect(mapa.x(), mapa.y(), mapa.largura(), mapa.altura());
        for (CampusLayout.Area area : layout.areas()) {
            if (campus.estado(area.construcao()) == EstadoDaConstrucao.PLANTA) {
                formas.setColor(corDaConstrucao(area.construcao()));
                tracejar(formas, area.retangulo());
            }
        }
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void desenharTextos(ResumoVisualDaCampanha resumo) {
        jogo.batch().begin();
        escala(2f);
        cor(AMBAR);
        texto("CAMPUS UNIFOR MARTE", 32f, 572f);
        escala(0.85f);
        cor(SECUNDARIO);
        texto("Mapa autoral inspirado na disposição do campus de Fortaleza", 34f, 548f);

        escala(0.68f);
        for (CampusLayout.Area area : layout.areas()) {
            cor(TEXTO);
            textoCentralizado(area.rotulo(), area.retangulo().centroX(), area.retangulo().centroY());
        }
        for (CampusLayout.Decoracao decoracao : layout.decoracoes()) {
            cor(SECUNDARIO);
            textoCentralizado(decoracao.rotulo(), decoracao.retangulo().centroX(), decoracao.retangulo().centroY());
        }

        escala(1.25f);
        cor(TEXTO);
        texto("Fundadores: " + resumo.fundadoresResgatados() + "/3", 572f, 498f);
        escala(0.95f);
        texto("Pontuação: " + resumo.pontuacaoTotal(), 572f, 472f);
        if (jogo.demonstracaoDaCampanha()) {
            cor(LILAS);
            texto("Modo demonstração", 572f, 446f);
        }

        float y = 408f;
        for (ResumoVisualDaCampanha.Etapa etapa : resumo.etapas()) {
            cor(corDaEtapa(etapa.numero()));
            String marcador = etapa.situacao() == ResumoVisualDaCampanha.SituacaoDaEtapa.RESGATADA ? "[OK]"
                    : etapa.situacao() == ResumoVisualDaCampanha.SituacaoDaEtapa.ATUAL ? "[>]" : "[ ]";
            texto(marcador + " Etapa " + etapa.numero(), 572f, y);
            escala(0.82f);
            cor(SECUNDARIO);
            texto(etapa.local(), 588f, y - 20f);
            texto(etapa.fundador(), 588f, y - 38f);
            escala(0.95f);
            y -= 88f;
        }

        escala(1f);
        cor(campanha.concluida() ? LILAS : VERDE);
        textoCentralizado(resumo.rotuloDaAcao(), 670f, 155f);
        escala(0.78f);
        cor(SECUNDARIO);
        textoCentralizado(jogo.modoToque() ? "Use os botões abaixo"
                : "Enter: selecionar   Esc: voltar", 670f, 122f);
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
        escala(1.55f);
        cor(LILAS);
        textoCentralizado("NOVA CAMPANHA", 400f, 360f);
        escala(1f);
        cor(TEXTO);
        textoCentralizado("Todo o progresso e as pontuações serão apagados.", 400f, 315f);
        escala(0.9f);
        cor(SECUNDARIO);
        textoCentralizado(jogo.modoToque() ? "Confirme ou cancele abaixo"
                : "Enter ou S: confirmar   N ou Esc: cancelar", 400f, 278f);
        jogo.batch().end();
    }

    private static Color corDaConstrucao(Construcao construcao) {
        Fundador fundador = construcao.responsavel();
        if (fundador == null) {
            return LILAS;
        }
        switch (fundador) {
            case ASTRONAUTA:
                return CIANO;
            case PROFESSOR:
                return AMBAR;
            default:
                return VERDE;
        }
    }

    private static Color corDaEtapa(int etapa) {
        return etapa == 1 ? CIANO : etapa == 2 ? AMBAR : VERDE;
    }

    private static void retangulo(ShapeRenderer formas, RetanguloDeTela r) {
        formas.rect(r.x(), r.y(), r.largura(), r.altura());
    }

    private static void tracejar(ShapeRenderer formas, RetanguloDeTela r) {
        float passo = 12f;
        float traco = 7f;
        for (float x = r.x(); x < r.direita(); x += passo) {
            formas.line(x, r.y(), Math.min(x + traco, r.direita()), r.y());
            formas.line(x, r.topo(), Math.min(x + traco, r.direita()), r.topo());
        }
        for (float y = r.y(); y < r.topo(); y += passo) {
            formas.line(r.x(), y, r.x(), Math.min(y + traco, r.topo()));
            formas.line(r.direita(), y, r.direita(), Math.min(y + traco, r.topo()));
        }
    }
}
