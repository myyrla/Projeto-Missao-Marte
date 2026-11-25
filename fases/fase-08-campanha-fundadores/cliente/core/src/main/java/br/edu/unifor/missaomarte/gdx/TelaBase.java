package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.entrada.SetasDoControleDeToque;
import br.edu.unifor.missaomarte.gdx.layout.TextoResponsivo;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.ui.IconeDaInterface;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base das telas: limpa a janela, desenha o fundo, aplica o viewport e registra o ciclo de vida da Screen no log.
 * <p>Padrao: Template Method (render() fixa a sequencia e chama {@link #desenhar(float)}). SOLID: SRP (so infraestrutura de tela).
 * <p>Metodos de texto devem ser usados entre batch.begin() e batch.end(). A tecla M (silenciar) vale em todas as telas que usam
 * {@link #definirEntrada(InputProcessor)}.
 * <p>Fase 6: no modo toque a tela publica uma lista de {@link BotaoVirtual}; a base desenha os botoes depois da tela e entrega
 * o toque em {@link #aoTocar(String, int)}. Fora do modo toque nada disso aparece.
 */
abstract class TelaBase implements Screen {
    protected final MissaoMarteGame jogo;
    /** Tempo acumulado em segundos desde que a tela foi criada; usado nas animacoes em laco. */
    protected float tempo;
    /** Botoes tocaveis da tela (so usados e desenhados no modo toque). */
    protected final List<BotaoVirtual> botoes = new ArrayList<>();
    private final GlyphLayout layout = new GlyphLayout();
    private final Vector2 ponto = new Vector2();
    private FonteDoJogo tipoDaFonte = FonteDoJogo.TEXTO;

    protected TelaBase(MissaoMarteGame jogo) {
        this.jogo = jogo;
    }

    protected abstract void desenhar(float delta);

    /**
     * A partida usa o mundo inteiro (a camera fica no centro dele e o layout ocupa toda a area visivel). As demais telas sao
     * desenhadas numa area fixa de 800x600, centralizada na tela (em retrato sobra espaco em cima e embaixo).
     */
    protected boolean usaMundoInteiro() {
        return false;
    }

    /** Telas com arte propria para os botoes desativam o desenho generico, mas preservam as areas de toque. */
    protected boolean desenhaBotoesVirtuais() {
        return true;
    }

    /** Clique de mouse no desktop e opt-in; toque continua disponivel em todas as telas com areas cadastradas. */
    protected boolean aceitaCliqueEsquerdo() {
        return false;
    }

    /** Toque num botao; devolve true se tratou. O ponteiro identifica o dedo (para repetir enquanto apoiado). */
    protected boolean aoTocar(String id, int ponteiro) {
        return false;
    }

    /** Pressao em coordenadas do mundo, antes da procura pelos botoes retangulares. */
    protected boolean aoPressionar(float x, float y, int ponteiro) {
        return false;
    }

    /** Arrasto em coordenadas do mundo para controles que acompanham o dedo. */
    protected boolean aoArrastar(float x, float y, int ponteiro) {
        return false;
    }

    protected void aoSoltar(int ponteiro) {
    }

    /** Tecla Voltar do Android ou Esc. */
    protected static boolean ehVoltar(int keycode) {
        return keycode == Keys.ESCAPE || keycode == Keys.BACK;
    }

    @Override
    public void show() {
        log("show()");
    }

    @Override
    public void render(float delta) {
        tempo += delta;
        ScreenUtils.clear(0.03f, 0.03f, 0.10f, 1f);
        Viewport viewport = jogo.viewport();
        viewport.apply();
        float larguraDoMundo = viewport.getWorldWidth();
        float alturaDoMundo = viewport.getWorldHeight();
        float centroX = usaMundoInteiro() ? larguraDoMundo / 2f : MissaoMarteGame.LARGURA / 2f;
        float centroY = usaMundoInteiro() ? alturaDoMundo / 2f : MissaoMarteGame.ALTURA / 2f;
        jogo.camera().position.set(centroX, centroY, 0f);
        jogo.camera().update();
        jogo.batch().setProjectionMatrix(jogo.camera().combined);
        jogo.shapes().setProjectionMatrix(jogo.camera().combined);
        tipoDaFonte = FonteDoJogo.TEXTO;
        for (FonteDoJogo tipo : FonteDoJogo.values()) {
            jogo.fonte(tipo).setColor(Color.WHITE);
        }

        jogo.batch().begin();
        jogo.batch().draw(jogo.recursos().textura(Imagem.FUNDO), centroX - larguraDoMundo / 2f, centroY - alturaDoMundo / 2f,
                larguraDoMundo, alturaDoMundo);
        jogo.batch().end();

        jogo.fontes().comEscalaTemporaria(jogo.fatorDeTexto(), () -> {
            desenhar(delta);
            desenharBotoes();
        });

        tipoDaFonte = FonteDoJogo.TEXTO;
    }

    private void desenharBotoes() {
        if (!jogo.modoToque() || !desenhaBotoesVirtuais() || botoes.isEmpty()) {
            return;
        }
        ShapeRenderer formas = jogo.shapes();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0.1f, 0.15f, 0.32f, 0.75f);
        for (BotaoVirtual original : botoes) {
            BotaoVirtual b = efetivo(original);
            if (!b.getRotulo().isEmpty()) {
                formas.rect(b.getX(), b.getY(), b.getLargura(), b.getAltura());
            }
        }
        formas.end();
        formas.begin(ShapeType.Line);
        formas.setColor(1f, 0.85f, 0.3f, 0.9f);
        for (BotaoVirtual original : botoes) {
            BotaoVirtual b = efetivo(original);
            formas.rect(b.getX(), b.getY(), b.getLargura(), b.getAltura());
        }
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        jogo.batch().begin();
        for (BotaoVirtual original : botoes) {
            BotaoVirtual b = efetivo(original);
            if (!b.getRotulo().isEmpty()) {
                Optional<Float> rotacao = SetasDoControleDeToque.rotacao(b.getId());
                if (rotacao.isPresent()) {
                    desenharSeta(b, rotacao.orElseThrow());
                } else {
                    fonte(b.getRotulo().length() <= 1 ? FonteDoJogo.SUBTITULO : FonteDoJogo.TEXTO);
                    cor(Color.WHITE);
                    textoCentralizado(b.getRotulo(), b.getX() + b.getLargura() / 2f,
                            b.getY() + b.getAltura() / 2f);
                }
            }
        }
        jogo.batch().end();
    }

    private void desenharSeta(BotaoVirtual botao, float rotacao) {
        float lado = Math.min(32f, Math.min(botao.getLargura(), botao.getAltura()) * 0.56f);
        float x = botao.getX() + (botao.getLargura() - lado) / 2f;
        float y = botao.getY() + (botao.getAltura() - lado) / 2f;
        jogo.batch().setColor(Color.WHITE);
        jogo.batch().draw(jogo.estilo().icone(IconeDaInterface.SETA), x, y,
                lado / 2f, lado / 2f, lado, lado, 1f, 1f, rotacao);
        jogo.batch().setColor(Color.WHITE);
    }

    // Botao com texto nunca fica menor que o alvo minimo de toque (48 dp); campos sem texto mantem o tamanho original.
    private BotaoVirtual efetivo(BotaoVirtual b) {
        float minimo = jogo.alvoMinimo();
        if (b.getRotulo().isEmpty() || (b.getAltura() >= minimo && b.getLargura() >= minimo)) {
            return b;
        }
        float largura = Math.max(b.getLargura(), minimo);
        float altura = Math.max(b.getAltura(), minimo);
        return new BotaoVirtual(b.getId(), b.getRotulo(), b.getX() - (largura - b.getLargura()) / 2f,
                b.getY() - (altura - b.getAltura()) / 2f, largura, altura);
    }

    // Se o toque cai em mais de um botao (alvos ampliados que se encontram), vale o de centro mais proximo do dedo.
    private BotaoVirtual botaoEm(float x, float y) {
        ponto.set(x, y);
        BotaoVirtual melhor = null;
        float menorDistancia = Float.MAX_VALUE;
        for (BotaoVirtual b : botoes) {
            BotaoVirtual ampliado = efetivo(b);
            if (!ampliado.contem(ponto.x, ponto.y)) {
                continue;
            }
            float dx = ponto.x - (ampliado.getX() + ampliado.getLargura() / 2f);
            float dy = ponto.y - (ampliado.getY() + ampliado.getAltura() / 2f);
            float distancia = dx * dx + dy * dy;
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                melhor = b;
            }
        }
        return melhor;
    }

    private Vector2 noMundo(int telaX, int telaY) {
        ponto.set(telaX, telaY);
        jogo.viewport().unproject(ponto);
        return ponto;
    }

    /** Chamado depois que o viewport foi atualizado (tela girou ou mudou de tamanho). */
    protected void aoRedimensionar() {
    }

    @Override
    public void resize(int width, int height) {
        log("resize(" + width + ", " + height + ")");
        jogo.viewport().update(width, height, true);
        aoRedimensionar();
    }

    @Override
    public void pause() {
        log("pause()");
    }

    @Override
    public void resume() {
        log("resume()");
    }

    @Override
    public void hide() {
        log("hide()");
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        log("dispose()");
    }

    protected void log(String metodo) {
        Gdx.app.log(MissaoMarteGame.TAG, getClass().getSimpleName() + "." + metodo);
    }

    /** Instala o processador da tela, precedido pelos atalhos globais (M silencia) e pelos toques nos botoes virtuais. */
    protected void definirEntrada(InputProcessor processador) {
        instalarEntrada(processador, true);
    }

    /** Como {@link #definirEntrada}, mas sem o atalho M, para telas em que a letra M faz parte do texto digitado. */
    protected void definirEntradaDeTexto(InputProcessor processador) {
        instalarEntrada(processador, false);
    }

    private void instalarEntrada(InputProcessor processador, boolean atalhoSom) {
        InputProcessor atalhos = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (atalhoSom && keycode == Keys.M) {
                    boolean mudo = jogo.sons().alternarMudo();
                    log(mudo ? "som desligado" : "som ligado");
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int telaX, int telaY, int ponteiro, int botao) {
                if (!aceitaPonteiro(jogo.modoToque(), aceitaCliqueEsquerdo(), botao)) {
                    return false;
                }
                Vector2 posicao = noMundo(telaX, telaY);
                if (aoPressionar(posicao.x, posicao.y, ponteiro)) {
                    return true;
                }
                BotaoVirtual b = botaoEm(posicao.x, posicao.y);
                return b != null && aoTocar(b.getId(), ponteiro);
            }

            @Override
            public boolean touchDragged(int telaX, int telaY, int ponteiro) {
                Vector2 posicao = noMundo(telaX, telaY);
                return aoArrastar(posicao.x, posicao.y, ponteiro);
            }

            @Override
            public boolean touchUp(int telaX, int telaY, int ponteiro, int botao) {
                aoSoltar(ponteiro);
                return false;
            }

            @Override
            public boolean touchCancelled(int telaX, int telaY, int ponteiro, int botao) {
                aoSoltar(ponteiro);
                return false;
            }
        };
        Gdx.input.setInputProcessor(new InputMultiplexer(atalhos, processador));
    }

    static boolean aceitaPonteiro(boolean modoToque, boolean aceitaCliqueEsquerdo, int botao) {
        return modoToque || aceitaCliqueEsquerdo && botao == Buttons.LEFT;
    }

    protected void cor(Color cor) {
        fonteAtual().setColor(cor);
    }

    protected void fonte(FonteDoJogo tipo) {
        tipoDaFonte = tipo;
    }

    protected void texto(String texto, float x, float y) {
        fonteAtual().draw(jogo.batch(), texto, x, y);
    }

    protected void textoCentralizado(String texto, float centroX, float centroY) {
        layout.setText(fonteAtual(), texto);
        fonteAtual().draw(jogo.batch(), layout, centroX - layout.width / 2f, centroY + layout.height / 2f);
    }

    /** Desenha com quebra de linha e devolve a altura ocupada. */
    protected float textoQuebrado(String texto, float x, float y, float largura) {
        return fonteAtual().draw(jogo.batch(), texto, x, y, largura, Align.left, true).height;
    }

    protected String textoLimitado(String texto, float larguraMaxima) {
        return TextoResponsivo.limitarComReticencias(texto, larguraMaxima, candidato -> {
            layout.setText(fonteAtual(), candidato);
            return layout.width;
        });
    }

    /** Altura da linha da fonte atual, incluindo a escala aplicada no carregamento. */
    protected float alturaDaLinha() {
        return fonteAtual().getLineHeight();
    }

    /** Linha seguinte calculada pela metrica real da fonte, acrescida de uma folga visual. */
    protected float linhaAbaixo(float linhaAtual, float respiro) {
        return linhaAtual - alturaDaLinha() - respiro;
    }

    private BitmapFont fonteAtual() {
        return jogo.fonte(tipoDaFonte);
    }
}
