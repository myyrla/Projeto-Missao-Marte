package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.EventoJogo;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import br.edu.unifor.missaomarte.application.RegistroAlternativoNoRankingLocal;
import br.edu.unifor.missaomarte.application.ResultadoTurno;
import br.edu.unifor.missaomarte.application.campanha.Campanha;
import br.edu.unifor.missaomarte.application.campanha.EtapaDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.Fundador;
import br.edu.unifor.missaomarte.application.campanha.PreparadorDaPartidaDaCampanha;
import br.edu.unifor.missaomarte.application.pontuacao.RegraDePontuacaoPadrao;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.ResultadoDoTiro;
import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.entrada.ControleDeToque;
import br.edu.unifor.missaomarte.gdx.entrada.ControleDeToqueRenderer;
import br.edu.unifor.missaomarte.gdx.entrada.JoystickVirtual;
import br.edu.unifor.missaomarte.gdx.entrada.RepeticaoDoMovimento;
import br.edu.unifor.missaomarte.gdx.camera.CameraDaPartida;
import br.edu.unifor.missaomarte.gdx.camera.EscalaDaCelula;
import br.edu.unifor.missaomarte.gdx.hud.HudRenderer;
import br.edu.unifor.missaomarte.gdx.hud.ApresentacaoDoResultadoDaPartida;
import br.edu.unifor.missaomarte.gdx.hud.EstadoDoEnvioDoResultado;
import br.edu.unifor.missaomarte.gdx.hud.JanelaDoRadar;
import br.edu.unifor.missaomarte.gdx.hud.LayoutDoHud;
import br.edu.unifor.missaomarte.gdx.hud.LayoutDoResultadoDaPartida;
import br.edu.unifor.missaomarte.gdx.hud.MapaNaInterface;
import br.edu.unifor.missaomarte.gdx.hud.MedidasDoHud;
import br.edu.unifor.missaomarte.gdx.hud.ResultadoDaPartidaRenderer;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import br.edu.unifor.missaomarte.gdx.visual.AvisosDaCampanha;
import br.edu.unifor.missaomarte.gdx.visual.DicaDaNami;
import br.edu.unifor.missaomarte.gdx.visual.EfeitosVisuais;
import br.edu.unifor.missaomarte.gdx.visual.MensagensFlutuantes;
import br.edu.unifor.missaomarte.gdx.visual.MundoRenderer;
import br.edu.unifor.missaomarte.gdx.visual.PosicoesVisuais;
import br.edu.unifor.missaomarte.gdx.visual.PosicaoVisual;
import br.edu.unifor.missaomarte.gdx.visual.PoliticaDeDestaque;
import br.edu.unifor.missaomarte.gdx.visual.RastroDaNave;
import br.edu.unifor.missaomarte.gdx.visual.RecorteDoMundo;
import br.edu.unifor.missaomarte.gdx.visual.Tremor;
import br.edu.unifor.missaomarte.gdx.visual.VisibilidadeDaTempestade;
import br.edu.unifor.missaomarte.gdx.ui.ControleDoFimDaPartida;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tela da partida: converte teclas e toques em {@link Acao}, chama a {@link GameSession} e reage ao que aconteceu.
 * <p>GRASP: Controller de interface. Nao tem regra de jogo: o resultado (estado, mensagens e eventos) vem da sessao.
 * <p>Os eventos disparam sons, efeitos visuais, tremor e textos flutuantes (Observer simplificado).
 * <p>Fase 5: ao terminar, o jogador logado tem o resultado enviado ao servidor (assincrono); o convidado grava no ranking local.
 * <p>Fase 6: botoes virtuais ({@link ControleDeToque}); Voltar do Android e Esc abortam.
 * <p>Fase 7: dois modos. No <b>classico</b> cada acao e um turno (a tecla segurada repete). No <b>dinamico</b> o jogo corre sozinho:
 * a tela so repassa o tempo de cada quadro a {@link GameSession#avancar(float)} e avisa quando uma direcao e pressionada ou solta.
 * O HUD e os controles usam {@link LayoutDoHud}; uma câmera independente acompanha o mundo em tela cheia.
 */
public class GameScreen extends TelaBase {
    private static final int CELULAS = MissaoMarteGame.MAX - MissaoMarteGame.MIN + 1;
    private static final float VELOCIDADE_VISUAL = 12f;
    private static final float DURACAO_DO_RAIO = 0.18f;
    private static final float DURACAO_DA_MENSAGEM = 2f;

    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color CAIXA = new Color(0.05f, 0.05f, 0.12f, 1f);

    private final GameSession session;
    private final boolean dinamico;
    private final Campanha campanha;
    private final EtapaDaCampanha etapaDaCampanha;
    private final PosicoesVisuais posicoes = new PosicoesVisuais(VELOCIDADE_VISUAL);
    private final RastroDaNave rastro = new RastroDaNave();
    private final EfeitosVisuais efeitos = new EfeitosVisuais();
    private final MensagensFlutuantes flutuantes = new MensagensFlutuantes();
    private final DicaDaNami dicaDaNami = new DicaDaNami();
    private final Tremor tremor = new Tremor();
    private final RepeticaoDoMovimento repeticaoDoMovimento = new RepeticaoDoMovimento();
    private final ControleDoFimDaPartida controleDoFim = new ControleDoFimDaPartida();
    private final RegistroAlternativoNoRankingLocal registroAlternativo = new RegistroAlternativoNoRankingLocal();
    private final CameraDaPartida acompanhamentoDaCamera = new CameraDaPartida();
    private final OrthographicCamera cameraDoMundo = new OrthographicCamera();
    private final MundoRenderer mundo;
    private final HudRenderer hud;
    private final ResultadoDaPartidaRenderer resultadoRenderer;
    private final ControleDeToqueRenderer controleDeToqueRenderer;
    private final Map<Integer, Acao> setasPorDedo = new HashMap<>();
    private final GlyphLayout medidorDeEtiqueta = new GlyphLayout();

    private LayoutDoHud layout;
    private LayoutDoResultadoDaPartida layoutDoResultado;
    private MapaGeometria mapa;
    private RetanguloDeTela areaVisivelDoMundo;
    private RetanguloDeTela janelaDoRadar;
    private ControleDeToque controles;
    private ControleDeToque.MedidasDosRotulos medidasDosControles;
    private RetanguloDeTela areaDosControles;
    private RetanguloDeTela areaDasAcoes;
    private float alvoDosControles = -1f;
    private boolean controlesEmRetrato;

    private List<String> mensagens = new ArrayList<>();
    private float tempoDaMensagem;
    private EstadoDoEnvioDoResultado envio = EstadoDoEnvioDoResultado.NAO_ENVIA;
    private String erroDeEnvio = "";
    private float anguloNave;
    private float tempoPiscando;
    private float tempoDoRaio;
    private ResultadoDoTiro raio;
    private int teclaSegurada = -1;
    private int ponteiroSegurado = -1;
    private int contagemMostrada = -1;
    private boolean resultadoDaCampanhaRegistrado;

    public GameScreen(MissaoMarteGame jogo, GameSession session) {
        this(jogo, session, null, null);
    }

    public GameScreen(MissaoMarteGame jogo, GameSession session, Campanha campanha, EtapaDaCampanha etapaDaCampanha) {
        super(jogo);
        this.session = session;
        this.dinamico = session.getModo() == ModoDeJogo.DINAMICO;
        this.campanha = campanha;
        this.etapaDaCampanha = etapaDaCampanha;
        Viewport viewport = jogo.viewport();
        this.layout = layoutAtual(viewport);
        this.mapa = geometriaAtual();
        this.mundo = new MundoRenderer(jogo.recursos(), jogo.batch(), jogo.shapes(),
                jogo.fonte(FonteDoJogo.TEXTO), jogo.fonte(FonteDoJogo.ROTULO), jogo.estilo(), mapa);
        this.hud = new HudRenderer(jogo.batch(), jogo.shapes(), jogo.fontes(), jogo.estilo(),
                campanha, etapaDaCampanha);
        this.resultadoRenderer = new ResultadoDaPartidaRenderer(
                jogo.batch(), jogo.shapes(), jogo.fontes(), jogo.estilo());
        this.controleDeToqueRenderer = new ControleDeToqueRenderer(
                jogo.batch(), jogo.shapes(), jogo.fontes());
    }

    private LayoutDoHud layoutAtual(Viewport viewport) {
        return LayoutDoHud.calcular(viewport.getWorldWidth(), viewport.getWorldHeight(), CELULAS, jogo.modoToque(),
                jogo.margensSeguras());
    }

    private MapaGeometria geometriaAtual() {
        float alturaDaJanela = Math.max(1f, Gdx.graphics.getHeight());
        float celula = EscalaDaCelula.calcular(alturaDaJanela, CELULAS);
        return new MapaGeometria(MissaoMarteGame.MIN, MissaoMarteGame.MAX, celula, 0f, 0f);
    }

    @Override
    public void show() {
        super.show();
        definirEntrada(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return dinamico ? teclaDinamica(keycode) : tratarTecla(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                if (dinamico) {
                    session.soltar(TecladoParaAcao.converter(keycode));
                    return false;
                }
                if (keycode == teclaSegurada) {
                    pararRepeticao();
                }
                return false;
            }
        });
    }

    /** O app foi para segundo plano (ou a tela apagou): o jogo dinamico para de correr. */
    @Override
    public void pause() {
        super.pause();
        liberarJoystick();
        if (dinamico) {
            session.pausar();
            setasPorDedo.clear();
        }
    }

    @Override
    public void hide() {
        liberarJoystick();
        super.hide();
    }

    @Override
    protected boolean usaMundoInteiro() {
        return true;
    }

    /** O fim usa os botoes do estilo Switch; a partida desenha o controle adequado a orientacao. */
    @Override
    protected boolean desenhaBotoesVirtuais() {
        return session.getEstado() == EstadoPartida.EM_ANDAMENTO
                && (!jogo.modoToque() || dinamico && session.isPausada());
    }

    // ---------------------------------------------------------------- entrada: modo classico

    private boolean tratarTecla(int keycode) {
        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            return teclaDeFimDePartida(keycode);
        }

        Acao acao = TecladoParaAcao.converter(keycode);
        if (acao == Acao.INVALIDA) {
            return false;
        }

        executar(acao);
        if (ehMovimento(acao) && session.getEstado() == EstadoPartida.EM_ANDAMENTO) {
            repeticaoDoMovimento.iniciar(acao);
            teclaSegurada = keycode;
        }
        return true;
    }

    private boolean teclaDeFimDePartida(int keycode) {
        if (keycode == Keys.R && envio == EstadoDoEnvioDoResultado.FALHOU && jogo.sessao() != null) {
            enviarResultado();
            return true;
        }
        if (keycode == Keys.LEFT || keycode == Keys.A) {
            controleDoFim.esquerda();
            return true;
        }
        if (keycode == Keys.RIGHT || keycode == Keys.D) {
            controleDoFim.direita();
            return true;
        }
        if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.BUTTON_A
                || keycode == Keys.DPAD_CENTER) {
            executarAcaoDoFim(controleDoFim.acaoEmFoco());
            return true;
        }
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
            executarAcaoDoFim(ControleDoFimDaPartida.Acao.SAIR);
            return true;
        }
        return false;
    }

    // ---------------------------------------------------------------- entrada: modo dinamico

    private boolean teclaDinamica(int keycode) {
        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            return teclaDeFimDePartida(keycode);
        }
        if (keycode == Keys.P || keycode == Keys.BACK) {
            alternarPausa();
            return true;
        }
        if (session.isPausada()) {
            if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.BUTTON_A) {
                alternarPausa();
                return true;
            }
            if (TecladoParaAcao.converter(keycode) == Acao.SAIR) {
                executar(Acao.SAIR);
                return true;
            }
            return false;
        }
        if (keycode == Keys.F || keycode == Keys.SPACE) {
            atirar();
            return true;
        }
        Acao acao = TecladoParaAcao.converter(keycode);
        if (acao == Acao.SAIR) {
            executar(Acao.SAIR);
            return true;
        }
        if (acao == Acao.EMBARCAR) {
            atirar();
            return true;
        }
        if (ehMovimento(acao)) {
            session.pressionar(acao);
            return true;
        }
        return false;
    }

    private void alternarPausa() {
        liberarJoystick();
        session.alternarPausa();
        setasPorDedo.clear();
        log(session.isPausada() ? "pausa" : "continua");
    }

    private void atirar() {
        int antes = session.getPontuacao();
        tratarResultado(session.atirar(), antes);
    }

    // ---------------------------------------------------------------- entrada: toque

    @Override
    protected boolean aoPressionar(float x, float y, int ponteiro) {
        if (!usaJoystickDeToque()) {
            return false;
        }
        ControleDeToque.ResultadoDaPressao resultado = controles.pressionar(ponteiro, x, y);
        aplicarComandosDoJoystick(resultado.comandos());
        resultado.botao().ifPresent(id -> aoTocar(id, ponteiro));
        return resultado.tratado();
    }

    @Override
    protected boolean aoArrastar(float x, float y, int ponteiro) {
        if (!usaJoystickDeToque()) {
            return false;
        }
        JoystickVirtual.Resultado resultado = controles.arrastar(ponteiro, x, y);
        aplicarComandosDoJoystick(resultado.comandos());
        return resultado.tratado();
    }

    @Override
    protected boolean aoTocar(String id, int ponteiro) {
        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            if ("tentar_novamente".equals(id)) {
                controleDoFim.focar(ControleDoFimDaPartida.Acao.TENTAR_DE_NOVO);
                executarAcaoDoFim(controleDoFim.acaoEmFoco());
                return true;
            }
            if ("sair_fim".equals(id)) {
                controleDoFim.focar(ControleDoFimDaPartida.Acao.SAIR);
                executarAcaoDoFim(controleDoFim.acaoEmFoco());
                return true;
            }
            return false;
        }
        if (dinamico && session.isPausada()) {
            if ("continuar".equals(id)) {
                alternarPausa();
                return true;
            }
            if ("sair".equals(id)) {
                executar(Acao.SAIR);
                return true;
            }
            return false;
        }
        if (ControleDeToque.SOM.equals(id)) {
            log(jogo.sons().alternarMudo() ? "som desligado" : "som ligado");
            return true;
        }
        if (ControleDeToque.PAUSA.equals(id)) {
            alternarPausa();
            return true;
        }
        Acao acao = ControleDeToque.acaoDe(id);
        if (acao == Acao.INVALIDA) {
            return false;
        }
        if (dinamico) {
            return toqueDinamico(acao, ponteiro);
        }
        executar(acao);
        if (ControleDeToque.repete(id) && session.getEstado() == EstadoPartida.EM_ANDAMENTO) {
            repeticaoDoMovimento.iniciar(acao);
            ponteiroSegurado = ponteiro;
        }
        return true;
    }

    private boolean toqueDinamico(Acao acao, int ponteiro) {
        if (acao == Acao.ATIRAR) {
            atirar();
        } else if (acao == Acao.SAIR) {
            executar(Acao.SAIR);
        } else if (ehMovimento(acao)) {
            setasPorDedo.put(ponteiro, acao);
            session.pressionar(acao);
        }
        return true;
    }

    @Override
    protected void aoSoltar(int ponteiro) {
        if (controles != null && controles.joystick().isPresent()) {
            JoystickVirtual.Resultado resultado = controles.soltar(ponteiro);
            aplicarComandosDoJoystick(resultado.comandos());
            if (resultado.tratado()) {
                return;
            }
        }
        if (dinamico) {
            Acao solta = setasPorDedo.remove(ponteiro);
            if (solta != null && !setasPorDedo.containsValue(solta)) {
                session.soltar(solta);
            }
            return;
        }
        if (ponteiro == ponteiroSegurado) {
            pararRepeticao();
        }
    }

    /** Monta os controles da partida, os botoes da pausa ou as duas areas do modal de encerramento. */
    private void atualizarBotoes() {
        if (!jogo.modoToque()) {
            return;
        }
        botoes.clear();
        RetanguloDeTela m = layout.mapa();
        if (session.getEstado() == EstadoPartida.EM_ANDAMENTO && !(dinamico && session.isPausada())) {
            RetanguloDeTela area = layout.controles();
            RetanguloDeTela acoes = layout.acoes();
            float alvo = jogo.alvoMinimo();
            ControleDeToque.MedidasDosRotulos medidas = controleDeToqueRenderer.medirRotulos(dinamico);
            if (controles == null || alvo != alvoDosControles || controlesEmRetrato != layout.emRetrato()
                    || !mesmoRetangulo(area, areaDosControles)
                    || !mesmoRetangulo(acoes, areaDasAcoes)
                    || !medidas.equals(medidasDosControles)) {
                liberarJoystick();
                controles = new ControleDeToque(
                        area, acoes, layout.emRetrato(), dinamico, alvo, medidas);
                areaDosControles = area;
                areaDasAcoes = acoes;
                alvoDosControles = alvo;
                controlesEmRetrato = layout.emRetrato();
                medidasDosControles = medidas;
            }
            botoes.addAll(controles.getBotoes());
            return;
        }
        float largura = Math.min(220f, m.largura() - 16f);
        float altura = Math.max(52f, jogo.alvoMinimo());
        float x = m.centroX() - largura / 2f;
        float yBase = m.centroY() - 75f - 14f - altura;
        if (session.getEstado() == EstadoPartida.EM_ANDAMENTO) {
            botoes.add(new BotaoVirtual("continuar", "Continuar", x, yBase, largura, altura));
            botoes.add(new BotaoVirtual("sair", "Abortar", x, yBase - 10f - altura, largura, altura));
            return;
        }
        RetanguloDeTela tentar = layoutDoResultado == null
                ? layout.botaoDoFim(0) : layoutDoResultado.botao(0);
        RetanguloDeTela sair = layoutDoResultado == null
                ? layout.botaoDoFim(1) : layoutDoResultado.botao(1);
        botoes.add(new BotaoVirtual("tentar_novamente", "", tentar.x(), tentar.y(), tentar.largura(), tentar.altura()));
        botoes.add(new BotaoVirtual("sair_fim", "", sair.x(), sair.y(), sair.largura(), sair.altura()));
    }

    private static boolean mesmoRetangulo(RetanguloDeTela a, RetanguloDeTela b) {
        return b != null && a.x() == b.x() && a.y() == b.y() && a.largura() == b.largura() && a.altura() == b.altura();
    }

    private boolean usaJoystickDeToque() {
        return jogo.modoToque() && layout != null
                && controles != null && session.getEstado() == EstadoPartida.EM_ANDAMENTO
                && !(dinamico && session.isPausada());
    }

    private void aplicarComandosDoJoystick(List<JoystickVirtual.Comando> comandos) {
        for (JoystickVirtual.Comando comando : comandos) {
            switch (comando.tipo()) {
                case EXECUTAR -> {
                    executar(comando.acao());
                    if (session.getEstado() == EstadoPartida.EM_ANDAMENTO) {
                        repeticaoDoMovimento.iniciar(comando.acao());
                    }
                }
                case PRESSIONAR -> session.pressionar(comando.acao());
                case SOLTAR -> {
                    if (dinamico) {
                        session.soltar(comando.acao());
                    } else {
                        pararRepeticao();
                    }
                }
            }
        }
    }

    private void liberarJoystick() {
        if (controles == null) {
            return;
        }
        controles.joystick().ifPresent(joystick -> {
            if (joystick.ponteiroAtivo() >= 0) {
                aplicarComandosDoJoystick(controles.soltar(joystick.ponteiroAtivo()).comandos());
            }
        });
    }

    // ---------------------------------------------------------------- regras e reacoes

    private void pararRepeticao() {
        teclaSegurada = -1;
        ponteiroSegurado = -1;
        repeticaoDoMovimento.parar();
    }

    private static boolean ehMovimento(Acao acao) {
        return acao == Acao.CIMA || acao == Acao.BAIXO || acao == Acao.ESQUERDA || acao == Acao.DIREITA;
    }

    private static float anguloDe(Acao acao) {
        switch (acao) {
            case DIREITA:
                return -90f;
            case BAIXO:
                return 180f;
            case ESQUERDA:
                return 90f;
            default:
                return 0f;
        }
    }

    private void executar(Acao acao) {
        int pontuacaoAntes = session.getPontuacao();
        ResultadoTurno resultado = session.processarAcao(acao);
        if (ehMovimento(acao)) {
            anguloNave = anguloDe(acao);
        }
        tratarResultado(resultado, pontuacaoAntes);
    }

    private void tratarResultado(ResultadoTurno resultado, int pontuacaoAntes) {
        if (!resultado.getMensagens().isEmpty() || !dinamico) {
            mensagens = new ArrayList<>(resultado.getMensagens());
            tempoDaMensagem = DURACAO_DA_MENSAGEM;
        }
        reagirAosEventos(resultado.getEventos(), session.getPontuacao() - pontuacaoAntes);
        aplicarEstado(resultado.getEstado());
    }

    private void reagirAosEventos(List<EventoJogo> eventos, int variacaoDePontos) {
        Posicao nave = session.getMissao().getNave().getPosicao();
        Posicao base = session.getMissao().getBase().getPosicao();

        for (EventoJogo evento : eventos) {
            if (evento != EventoJogo.MOVIMENTO) {
                log("evento " + evento);
            }
            switch (evento) {
                case MOVIMENTO:
                    jogo.sons().tocar(Som.MOVER);
                    break;
                case EMBARQUE:
                    jogo.sons().tocar(Som.EMBARCAR);
                    efeitos.adicionar(Imagem.BRILHO, nave.getX(), nave.getY(), 0.45f);
                    break;
                case ENTREGA:
                    jogo.sons().tocar(Som.ENTREGAR);
                    efeitos.adicionar(Imagem.BRILHO, base.getX(), base.getY(), 0.5f);
                    flutuantes.adicionar("Entregue!", base.getX(), base.getY() - 0.6f, true);
                    break;
                case COLISAO:
                    jogo.sons().tocar(Som.COLISAO);
                    efeitos.adicionar(Imagem.EXPLOSAO, nave.getX(), nave.getY(), 0.55f);
                    tremor.iniciar(0.35f, 7f);
                    tempoPiscando = 1f;
                    break;
                case TIRO:
                    jogo.sons().tocar(Som.TIRO);
                    raio = session.getUltimoTiro();
                    tempoDoRaio = DURACAO_DO_RAIO;
                    break;
                case PERIGO_DESTRUIDO:
                    if (raio != null) {
                        efeitos.adicionar(Imagem.EXPLOSAO, raio.getDestino().getX(), raio.getDestino().getY(), 0.45f);
                    }
                    jogo.sons().tocar(Som.COLISAO);
                    break;
                case NAMI_ATIVADA:
                    String aviso = AvisosDaCampanha.texto(evento);
                    if (aviso != null) {
                        exibirAviso(aviso);
                        flutuantes.adicionar(aviso, nave.getX(), nave.getY() - 0.6f, true);
                    }
                    efeitos.adicionar(Imagem.BRILHO, nave.getX(), nave.getY(), 0.5f);
                    break;
                case NAMI_CASCO_COMPLETO:
                    exibirAviso(AvisosDaCampanha.texto(evento));
                    break;
                case VITORIA:
                    jogo.sons().pausarMusica();
                    jogo.sons().tocar(Som.VITORIA);
                    break;
                case DERROTA:
                    jogo.sons().pausarMusica();
                    jogo.sons().tocar(Som.DERROTA);
                    break;
                default:
                    break;
            }
        }

        if (Math.abs(variacaoDePontos) > 1) {
            String texto = (variacaoDePontos > 0 ? "+" : "") + variacaoDePontos;
            flutuantes.adicionar(texto, nave.getX(), nave.getY() - 0.4f, variacaoDePontos > 0);
        }
    }

    private void exibirAviso(String aviso) {
        if (aviso == null || aviso.isBlank()) {
            return;
        }
        mensagens = new ArrayList<>(List.of(aviso));
        tempoDaMensagem = DURACAO_DA_MENSAGEM;
    }

    private void aplicarEstado(EstadoPartida estado) {
        registrarResultadoDaCampanha(estado);
        if (estado == EstadoPartida.ABORTADA) {
            pararRepeticao();
            session.soltarTudo();
            jogo.setScreen(emCampanha() ? new CampusScreen(jogo) : new MenuScreen(jogo));
        } else if (estado == EstadoPartida.VITORIA) {
            pararRepeticao();
            session.soltarTudo();
            PoliticaDeRegistroNoRanking.Destino destino = PoliticaDeRegistroNoRanking.destino(
                    emCampanha(), jogo.sessao() != null, estado);
            if (destino == PoliticaDeRegistroNoRanking.Destino.LOCAL) {
                jogo.rankingService().registrarVitoria(session);
            } else if (destino == PoliticaDeRegistroNoRanking.Destino.GLOBAL) {
                enviarResultado();
            }
        } else if (estado == EstadoPartida.DERROTA) {
            pararRepeticao();
            session.soltarTudo();
            if (PoliticaDeRegistroNoRanking.destino(emCampanha(), jogo.sessao() != null, estado)
                    == PoliticaDeRegistroNoRanking.Destino.GLOBAL) {
                enviarResultado();
            }
        }
    }

    private void enviarResultado() {
        envio = EstadoDoEnvioDoResultado.ENVIANDO;
        erroDeEnvio = "";
        log("enviando resultado ao servidor");
        jogo.servidor().enviarPartida(jogo.sessao(), PartidaConcluida.de(session), this::resultadoDoEnvio);
    }

    private void resultadoDoEnvio(Resposta<Void> resposta) {
        if (resposta.isSucesso()) {
            envio = EstadoDoEnvioDoResultado.ENVIADO;
            return;
        }
        envio = EstadoDoEnvioDoResultado.FALHOU;
        boolean salvoLocalmente = registroAlternativo.registrarSeNecessario(
                emCampanha(), session, resposta, jogo.rankingService());
        erroDeEnvio = salvoLocalmente
                ? "Sem conexão: salvo no ranking local."
                : resposta.getMensagem();
        log("falha ao enviar: " + erroDeEnvio);
        if (resposta.isNaoAutorizado()) {
            jogo.sair();
        }
    }

    private void encerrar() {
        if (emCampanha()) {
            jogo.setScreen(new CampusScreen(jogo));
        } else {
            jogo.setScreen(new MenuScreen(jogo));
        }
    }

    private void executarAcaoDoFim(ControleDoFimDaPartida.Acao acao) {
        if (envio == EstadoDoEnvioDoResultado.ENVIANDO) {
            return;
        }
        jogo.sons().tocar(Som.SELECIONAR);
        if (acao == ControleDoFimDaPartida.Acao.TENTAR_DE_NOVO) {
            reiniciarPartida();
        } else {
            encerrar();
        }
    }

    /** Recria a mesma configuracao pelas fabricas existentes, sem reutilizar o estado encerrado. */
    private void reiniciarPartida() {
        MissaoFactory fabrica = jogo.missaoFactory();
        GameSession nova;
        if (emCampanha()) {
            nova = new PreparadorDaPartidaDaCampanha(fabrica).preparar(
                    etapaDaCampanha, session.getPiloto(), MissaoMarteGame.MIN,
                    MissaoMarteGame.MAX, jogo.random());
        } else {
            Missao missao = fabrica.criarMissao(session.getDificuldade(), MissaoMarteGame.MIN, MissaoMarteGame.MAX,
                    jogo.random(), session.getModo());
            nova = new GameSession(session.getPiloto(), session.getDificuldade(), missao,
                    new RegraDePontuacaoPadrao(fabrica.pontuacaoInicial(session.getDificuldade())),
                    MissaoMarteGame.MIN, MissaoMarteGame.MAX,
                    jogo.random(), session.getModo());
        }
        jogo.sons().retomarMusica();
        jogo.setScreen(new GameScreen(jogo, nova, campanha, etapaDaCampanha));
    }

    private boolean emCampanha() {
        return campanha != null && etapaDaCampanha != null;
    }

    private void registrarResultadoDaCampanha(EstadoPartida estado) {
        if (!emCampanha() || resultadoDaCampanhaRegistrado || estado == EstadoPartida.EM_ANDAMENTO) {
            return;
        }
        campanha.registrarResultado(etapaDaCampanha.numero(), estado, session.getPontuacao());
        resultadoDaCampanhaRegistrado = true;
    }

    // ---------------------------------------------------------------- desenho

    @Override
    protected void desenhar(float delta) {
        if (dinamico) {
            avancarTempoReal(delta);
        } else if (session.getEstado() == EstadoPartida.EM_ANDAMENTO) {
            repeticaoDoMovimento.atualizar(delta).ifPresent(this::executar);
        }
        if (jogo.getScreen() != this) {
            return;
        }

        Missao missao = session.getMissao();
        posicoes.sincronizar(todosOsObjetos(missao), delta);
        rastro.registrar(missao.getNave().getPosicao().getX(), missao.getNave().getPosicao().getY());
        efeitos.atualizar(delta);
        flutuantes.atualizar(delta);
        tremor.atualizar(delta);
        tempoPiscando = Math.max(0f, tempoPiscando - delta);
        tempoDoRaio = Math.max(0f, tempoDoRaio - delta);
        if (tempoDaMensagem > 0f) {
            tempoDaMensagem -= delta;
            if (tempoDaMensagem <= 0f) {
                mensagens = new ArrayList<>();
            }
        }
        tentarExibirDicaDaNami(missao);

        boolean naveVisivel = tempoPiscando <= 0f || ((int) (tempoPiscando * 14f)) % 2 == 0;
        boolean tempestadeAtiva = VisibilidadeDaTempestade.estaAtiva(
                session::estaVisivel, MissaoMarteGame.MIN, MissaoMarteGame.MAX);
        boolean mudo = jogo.sons().mudo();
        long segundos = session.getDuracaoSegundos();
        atualizarCameraELayout(missao, mudo, segundos, tempestadeAtiva);
        RetanguloDeTela rodapeEsquerdo = hud.areaOcupadaDoRodape(session, mudo, segundos);
        PoliticaDeDestaque.Resultado destaques = calcularDestaques(missao, tempestadeAtiva);
        try {
            desenharMundoRecortado(() -> {
                mundo.desenhar(missao, posicoes, rastro, efeitos, flutuantes, tempo, anguloNave, naveVisivel,
                        tremor.deslocamentoX(), tremor.deslocamentoY(), areaVisivelDoMundo,
                        session::estaVisivel, tempestadeAtiva, destaques);
                desenharRaio();
                desenharFlashDeColisao();
            });
        } finally {
            restaurarCameraDaInterface();
        }

        boolean fim = session.getEstado() != EstadoPartida.EM_ANDAMENTO;
        hud.desenhar(session, mensagens, mudo, segundos, janelaDoRadar, tempestadeAtiva, destaques);

        if (!fim && usaJoystickDeToque()) {
            controleDeToqueRenderer.desenhar(controles);
        }

        if (fim) {
            desenharFimDePartida();
        } else if (dinamico && session.isPausada()) {
            desenharPausa();
        } else if (dinamico && session.getContagemRestante() > 0f) {
            desenharContagem();
        }
    }

    private void tentarExibirDicaDaNami(Missao missao) {
        if (session.getContagemRestante() > 0f || !mensagens.isEmpty()) {
            return;
        }
        exibirAviso(dicaDaNami.proxima(!missao.getEstacoesNami().isEmpty()));
    }

    private void atualizarCameraELayout(Missao missao, boolean mudo, long segundos, boolean tempestadeAtiva) {
        float larguraDaTela = Math.max(1f, Gdx.graphics.getWidth());
        float alturaDaTela = Math.max(1f, Gdx.graphics.getHeight());
        Viewport viewport = jogo.viewport();
        boolean toqueVertical = jogo.modoToque() && viewport.getWorldHeight() > viewport.getWorldWidth();
        MedidasDoHud medidas = null;
        RetanguloDeTela areaUtilEmPixels = new RetanguloDeTela(0f, 0f, larguraDaTela, alturaDaTela);
        if (toqueVertical) {
            RetanguloDeTela mapaProvisorio = new RetanguloDeTela(0f, 0f,
                    viewport.getWorldWidth(), viewport.getWorldHeight());
            LayoutDoHud referencia = LayoutDoHud.calcular(viewport.getWorldWidth(), viewport.getWorldHeight(),
                    CELULAS, true, jogo.margensSeguras(), mapaProvisorio);
            medidas = hud.medir(session, mensagens, mudo, segundos, tempestadeAtiva, referencia)
                    .comAlvoMinimo(jogo.alvoMinimo());
            LayoutDoHud preliminar = LayoutDoHud.calcular(viewport.getWorldWidth(), viewport.getWorldHeight(),
                    CELULAS, true, jogo.margensSeguras(), mapaProvisorio, medidas);
            areaUtilEmPixels = paraCoordenadasDaJanela(preliminar.areaUtilDoMapa(),
                    viewport, larguraDaTela, alturaDaTela);
        }

        float celula = toqueVertical
                ? EscalaDaCelula.calcularNoToqueVertical(areaUtilEmPixels.largura(), CELULAS)
                : EscalaDaCelula.calcular(alturaDaTela, CELULAS);
        mapa = new MapaGeometria(MissaoMarteGame.MIN, MissaoMarteGame.MAX, celula, 0f, 0f);
        mundo.definirMapa(mapa);

        PosicaoVisual nave = posicoes.de(missao.getNave());
        CameraDaPartida.Centro centro = acompanhamentoDaCamera.atualizar(
                mapa.largura(), mapa.altura(), larguraDaTela, alturaDaTela,
                areaUtilEmPixels, mapa.xCentro(nave.x()), mapa.yCentro(nave.y()));

        cameraDoMundo.setToOrtho(false, larguraDaTela, alturaDaTela);
        cameraDoMundo.position.set(centro.x(), centro.y(), 0f);
        cameraDoMundo.update();
        jogo.batch().setProjectionMatrix(cameraDoMundo.combined);
        jogo.shapes().setProjectionMatrix(cameraDoMundo.combined);

        areaVisivelDoMundo = new RetanguloDeTela(centro.x() - larguraDaTela / 2f,
                centro.y() - alturaDaTela / 2f, larguraDaTela, alturaDaTela);
        RetanguloDeTela mapaNaInterface = MapaNaInterface.projetar(
                new RetanguloDeTela(0f, 0f, mapa.largura(), mapa.altura()), areaVisivelDoMundo,
                viewport.getWorldWidth(), viewport.getWorldHeight());
        LayoutDoHud referencia = LayoutDoHud.calcular(viewport.getWorldWidth(), viewport.getWorldHeight(),
                CELULAS, jogo.modoToque(), jogo.margensSeguras(), mapaNaInterface);
        if (medidas == null) {
            medidas = hud.medir(session, mensagens, mudo, segundos, tempestadeAtiva, referencia)
                    .comAlvoMinimo(jogo.alvoMinimo());
        }
        layout = LayoutDoHud.calcular(viewport.getWorldWidth(), viewport.getWorldHeight(), CELULAS,
                jogo.modoToque(), jogo.margensSeguras(), mapaNaInterface, medidas);
        hud.definirLayout(layout);
        janelaDoRadar = JanelaDoRadar.projetar(layout.radarInterno(), mapa.largura(), mapa.altura(),
                areaVisivelDoMundo.centroX(), areaVisivelDoMundo.centroY(),
                areaVisivelDoMundo.largura(), areaVisivelDoMundo.altura());
        atualizarLayoutDoResultado();
        atualizarBotoes();
    }

    private void atualizarLayoutDoResultado() {
        if (!usaLayoutMedidoDoResultado(jogo.modoToque(), session.getEstado())) {
            layoutDoResultado = null;
            return;
        }
        layoutDoResultado = resultadoRenderer.medirLayout(
                layout.largura(), layout.altura(), jogo.margensSeguras(),
                jogo.alvoMinimo(), apresentacaoDoResultado());
    }

    static boolean usaLayoutMedidoDoResultado(boolean modoToque, EstadoPartida estado) {
        return modoToque && estado != EstadoPartida.EM_ANDAMENTO;
    }

    private static RetanguloDeTela paraCoordenadasDaJanela(RetanguloDeTela area, Viewport viewport,
            float larguraDaTela, float alturaDaTela) {
        float escalaX = larguraDaTela / viewport.getWorldWidth();
        float escalaY = alturaDaTela / viewport.getWorldHeight();
        return new RetanguloDeTela(area.x() * escalaX, area.y() * escalaY,
                area.largura() * escalaX, area.altura() * escalaY);
    }

    private void desenharMundoRecortado(Runnable desenho) {
        if (!jogo.modoToque() || !layout.emRetrato() || layout.usaReserva()) {
            desenho.run();
            return;
        }
        Viewport viewport = jogo.viewport();
        RecorteDoMundo recorte = RecorteDoMundo.calcular(layout.areaUtilDoMapa(),
                viewport.getWorldWidth(), viewport.getWorldHeight(),
                viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight(),
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        flushDosRenderizadores();
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        try {
            Gdx.gl.glScissor(recorte.x(), recorte.y(), recorte.largura(), recorte.altura());
            desenho.run();
        } finally {
            flushDosRenderizadores();
            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        }
    }

    private void flushDosRenderizadores() {
        jogo.batch().flush();
        jogo.shapes().flush();
    }

    private PoliticaDeDestaque.Resultado calcularDestaques(Missao missao, boolean tempestadeAtiva) {
        PoliticaDeDestaque.Resultado resultado = PoliticaDeDestaque.decidir(
                missao.getNave().getPosicao(), missao.getPassageiros(), tipoDoFundador(), tiposResgatados(),
                missao.getInimigos());
        if (resultado.alvo().isEmpty()) {
            return resultado;
        }
        if (!session.estaVisivel(resultado.alvo().orElseThrow().getPosicao())) {
            return resultado.semAlvoDestacado();
        }

        PosicaoVisual visual = posicoes.de(resultado.alvo().orElseThrow());
        float tamanho = mapa.celula() * 0.9f;
        RetanguloDeTela alvo = new RetanguloDeTela(
                mapa.xCentro(visual.x()) - tamanho / 2f + tremor.deslocamentoX(),
                mapa.yCentro(visual.y()) - tamanho / 2f + tremor.deslocamentoY(), tamanho, tamanho);
        medidorDeEtiqueta.setText(jogo.fonte(FonteDoJogo.ROTULO), resultado.textoDaEtiqueta());
        float largura = medidorDeEtiqueta.width + 24f;
        float altura = Math.max(28f, medidorDeEtiqueta.height + 12f);
        PosicaoVisual naveVisual = posicoes.de(missao.getNave());
        RetanguloDeTela nave = new RetanguloDeTela(
                mapa.xCentro(naveVisual.x()) - tamanho / 2f + tremor.deslocamentoX(),
                mapa.yCentro(naveVisual.y()) - tamanho / 2f + tremor.deslocamentoY(), tamanho, tamanho);
        List<RetanguloDeTela> areasEvitadas = PoliticaDeDestaque.areasEvitadas(
                areasDoHudNoMundo(tempestadeAtiva), nave);
        return PoliticaDeDestaque.posicionarEtiqueta(alvo, largura, altura, 8f,
                        areaVisivelDoMundo, areasEvitadas)
                .map(resultado::comEtiqueta)
                .orElse(resultado);
    }

    private List<RetanguloDeTela> areasDoHudNoMundo(boolean tempestadeAtiva) {
        List<RetanguloDeTela> areas = new ArrayList<>();
        for (RetanguloDeTela area : layout.areasDoHud(!mensagens.isEmpty(), tempestadeAtiva)) {
            areas.add(paraCoordenadasDoMundo(area));
        }
        return List.copyOf(areas);
    }

    private RetanguloDeTela paraCoordenadasDoMundo(RetanguloDeTela interfaceArea) {
        float escalaX = areaVisivelDoMundo.largura() / layout.largura();
        float escalaY = areaVisivelDoMundo.altura() / layout.altura();
        return new RetanguloDeTela(
                areaVisivelDoMundo.x() + interfaceArea.x() * escalaX,
                areaVisivelDoMundo.y() + interfaceArea.y() * escalaY,
                interfaceArea.largura() * escalaX, interfaceArea.altura() * escalaY);
    }

    private PoliticaDeDestaque.Tipo tipoDoFundador() {
        return etapaDaCampanha == null ? null : tipo(etapaDaCampanha.fundador());
    }

    private Set<PoliticaDeDestaque.Tipo> tiposResgatados() {
        if (campanha == null) {
            return Set.of();
        }
        java.util.EnumSet<PoliticaDeDestaque.Tipo> tipos = java.util.EnumSet.noneOf(PoliticaDeDestaque.Tipo.class);
        int etapaAtual = campanha.progresso().etapaAtual();
        for (Fundador fundador : Fundador.values()) {
            if (fundador.ordinal() + 1 < etapaAtual) {
                tipos.add(tipo(fundador));
            }
        }
        return Set.copyOf(tipos);
    }

    private static PoliticaDeDestaque.Tipo tipo(Fundador fundador) {
        if (fundador == Fundador.ASTRONAUTA) {
            return PoliticaDeDestaque.Tipo.ASTRONAUTA;
        }
        if (fundador == Fundador.PROFESSOR) {
            return PoliticaDeDestaque.Tipo.PROFESSOR;
        }
        return PoliticaDeDestaque.Tipo.ENGENHEIRO;
    }

    private void restaurarCameraDaInterface() {
        jogo.batch().setProjectionMatrix(jogo.camera().combined);
        jogo.shapes().setProjectionMatrix(jogo.camera().combined);
    }

    private void avancarTempoReal(float delta) {
        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            return;
        }
        int pontuacaoAntes = session.getPontuacao();
        Acao mira = session.getMira();
        anguloNave = anguloDe(mira);
        ResultadoTurno resultado = session.avancar(delta);
        if (!resultado.getEventos().isEmpty() || !resultado.getMensagens().isEmpty()) {
            tratarResultado(resultado, pontuacaoAntes);
        }
        int contagem = (int) Math.ceil(session.getContagemRestante());
        if (contagem != contagemMostrada) {
            if (contagem > 0 && contagemMostrada != -1) {
                jogo.sons().tocar(Som.SELECIONAR);
            }
            contagemMostrada = contagem;
        }
    }

    private List<ObjetoRenderizavel> todosOsObjetos(Missao missao) {
        List<ObjetoRenderizavel> objetos = new ArrayList<>();
        objetos.add(missao.getBase());
        objetos.addAll(missao.getAsteroides());
        objetos.addAll(missao.getInimigos());
        objetos.addAll(missao.getPassageiros());
        objetos.addAll(missao.getEstacoesNami());
        objetos.add(missao.getNave());
        return objetos;
    }

    private void desenharRaio() {
        if (raio == null || tempoDoRaio <= 0f) {
            return;
        }
        ShapeRenderer formas = jogo.shapes();
        float alfa = Math.min(1f, tempoDoRaio / DURACAO_DO_RAIO + 0.2f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(1f, 0.55f, 0.2f, alfa * 0.5f);
        formas.rectLine(mapa.xCentro(raio.getOrigem().getX()), mapa.yCentro(raio.getOrigem().getY()),
                mapa.xCentro(raio.getDestino().getX()), mapa.yCentro(raio.getDestino().getY()), mapa.celula() * 0.28f);
        formas.setColor(1f, 0.95f, 0.5f, alfa);
        formas.rectLine(mapa.xCentro(raio.getOrigem().getX()), mapa.yCentro(raio.getOrigem().getY()),
                mapa.xCentro(raio.getDestino().getX()), mapa.yCentro(raio.getDestino().getY()), mapa.celula() * 0.1f);
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void desenharFlashDeColisao() {
        if (!tremor.ativo()) {
            return;
        }
        ShapeRenderer formas = jogo.shapes();
        RetanguloDeTela m = areaVisivelDoMundo;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(1f, 0.15f, 0.1f, 0.3f * tremor.intensidade());
        formas.rect(m.x(), m.y(), m.largura(), m.altura());
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /** Caixa escura no centro do mapa, usada na pausa e no fim da partida. */
    private RetanguloDeTela desenharCaixa(float larguraDesejada, float altura) {
        RetanguloDeTela m = layout.mapa();
        float largura = Math.min(larguraDesejada, m.largura() - 8f);
        RetanguloDeTela caixa = new RetanguloDeTela(m.centroX() - largura / 2f, m.centroY() - altura / 2f, largura, altura);
        ShapeRenderer formas = jogo.shapes();
        formas.begin(ShapeType.Filled);
        formas.setColor(CAIXA);
        formas.rect(caixa.x(), caixa.y(), caixa.largura(), caixa.altura());
        formas.end();
        formas.begin(ShapeType.Line);
        formas.setColor(DESTAQUE);
        formas.rect(caixa.x(), caixa.y(), caixa.largura(), caixa.altura());
        formas.end();
        return caixa;
    }

    // Usa o subtitulo quando a caixa e estreita, sem ampliar ou reduzir a fonte em tempo de desenho.
    private static FonteDoJogo fonteDoTitulo(RetanguloDeTela caixa) {
        return caixa.largura() < 360f ? FonteDoJogo.SUBTITULO : FonteDoJogo.TITULO;
    }

    private void desenharPausa() {
        RetanguloDeTela caixa = desenharCaixa(380f, 150f);
        jogo.batch().begin();
        fonte(fonteDoTitulo(caixa));
        cor(DESTAQUE);
        textoCentralizado("PAUSADO", caixa.centroX(), caixa.centroY() + 30f);
        fonte(FonteDoJogo.PEQUENO);
        cor(Color.WHITE);
        if (!jogo.modoToque()) {
            textoCentralizado("P ou Enter: continuar", caixa.centroX(), caixa.centroY() - 10f);
            textoCentralizado("Q ou Esc: abortar", caixa.centroX(), caixa.centroY() - 34f);
        }
        jogo.batch().end();
    }

    private void desenharContagem() {
        RetanguloDeTela m = layout.mapa();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        jogo.shapes().begin(ShapeType.Filled);
        jogo.shapes().setColor(0f, 0f, 0.05f, 0.45f);
        jogo.shapes().rect(m.x(), m.y(), m.largura(), m.altura());
        jogo.shapes().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        jogo.batch().begin();
        fonte(FonteDoJogo.TITULO);
        cor(DESTAQUE);
        textoCentralizado(String.valueOf(Math.max(1, (int) Math.ceil(session.getContagemRestante()))), m.centroX(), m.centroY() + 12f);
        fonte(FonteDoJogo.SUBTITULO);
        cor(Color.WHITE);
        textoCentralizado("Prepare-se!", m.centroX(), m.centroY() - 50f);
        jogo.batch().end();
    }

    private void desenharFimDePartida() {
        resultadoRenderer.desenhar(
                apresentacaoDoResultado(), layout, layoutDoResultado, controleDoFim);
    }

    private ApresentacaoDoResultadoDaPartida apresentacaoDoResultado() {
        return ApresentacaoDoResultadoDaPartida.criar(
                session.getEstado(), session.getPontuacao(), mensagens, emCampanha(),
                envio, erroDeEnvio, jogo.sessao() != null, jogo.modoToque());
    }
}
