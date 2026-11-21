package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.campanha.ProgressoDaCampanha;
import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.hud.PoliticaDeAtalhos;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import br.edu.unifor.missaomarte.gdx.ui.LayoutDoMenu;
import br.edu.unifor.missaomarte.gdx.ui.LayoutDoMenuToque;
import br.edu.unifor.missaomarte.gdx.ui.ListaDeBotoes;
import br.edu.unifor.missaomarte.gdx.ui.MedidasDoMenuToque;
import br.edu.unifor.missaomarte.gdx.ui.RodapeDeTeclas;
import br.edu.unifor.missaomarte.gdx.ui.RodapeDeTeclas.Atalho;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/** Menu principal no estilo Switch: quatro acoes, foco por teclado e selecao por clique ou toque. */
public class MenuScreen extends TelaBase {
    private static final Color FUNDO = Color.valueOf("0A0612");
    private static final Color TEXTO = Color.valueOf("F2EEF7");
    private static final Color SECUNDARIO = Color.valueOf("B9B2C6");
    private static final Color FRASE = Color.valueOf("E4DCEA");
    private static final Color AMBAR = Color.valueOf("FFB000");
    private static final int QUANTIDADE_DE_OPCOES = 4;

    private final Animation<TextureRegion> propulsor;
    private final ListaDeBotoes opcoes = new ListaDeBotoes(QUANTIDADE_DE_OPCOES);
    private final ListaDeBotoes confirmacao = new ListaDeBotoes(2);
    private final RodapeDeTeclas rodape = new RodapeDeTeclas();
    private final GlyphLayout medidor = new GlyphLayout();
    private LayoutDoMenu layout;
    private LayoutDoMenuToque layoutDoToque;
    private boolean confirmandoSaida;

    public MenuScreen(MissaoMarteGame jogo) {
        super(jogo);
        propulsor = jogo.recursos().animacao(Imagem.PROPULSOR, 0.08f, PlayMode.LOOP);
        atualizarLayout();
    }

    @Override
    protected boolean usaMundoInteiro() {
        return true;
    }

    @Override
    protected boolean desenhaBotoesVirtuais() {
        return false;
    }

    @Override
    protected boolean aceitaCliqueEsquerdo() {
        return true;
    }

    @Override
    protected void aoRedimensionar() {
        atualizarLayout();
    }

    private void atualizarLayout() {
        layout = LayoutDoMenu.calcular(jogo.viewport().getWorldWidth(), jogo.viewport().getWorldHeight(), jogo.alvoMinimo());
        atualizarAreasDeAcao();
    }

    private void atualizarAreasDeAcao() {
        botoes.clear();
        if (confirmandoSaida) {
            RetanguloDeTela painel = painelDeConfirmacao();
            float largura = (painel.largura() - 60f) / 2f;
            botoes.add(botaoVirtual("sair", new RetanguloDeTela(painel.x() + 20f, painel.y() + 24f, largura, 50f)));
            botoes.add(botaoVirtual("cancelar", new RetanguloDeTela(painel.x() + 40f + largura, painel.y() + 24f, largura, 50f)));
            return;
        }
        for (int i = 0; i < QUANTIDADE_DE_OPCOES; i++) {
            botoes.add(botaoVirtual("menu_" + i, botaoDoMenu(i)));
        }
    }

    private RetanguloDeTela botaoDoMenu(int indice) {
        return layoutDoToque != null ? layoutDoToque.botao(indice) : layout.botaoMenu(indice);
    }

    private static BotaoVirtual botaoVirtual(String id, RetanguloDeTela area) {
        return new BotaoVirtual(id, "", area.x(), area.y(), area.largura(), area.altura());
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
        if (confirmandoSaida) {
            return tratarTeclaDaConfirmacao(keycode);
        }
        if (keycode == Keys.UP || keycode == Keys.W) {
            opcoes.subir();
            return true;
        }
        if (keycode == Keys.DOWN || keycode == Keys.S) {
            opcoes.descer();
            return true;
        }
        if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.SPACE || keycode == Keys.BUTTON_A) {
            executar(opcoes.foco());
            return true;
        }
        if (ehVoltar(keycode)) {
            abrirConfirmacaoDeSaida();
            return true;
        }
        if (keycode >= Keys.NUM_1 && keycode <= Keys.NUM_4) {
            executar(keycode - Keys.NUM_1);
            return true;
        }
        if (keycode >= Keys.NUMPAD_1 && keycode <= Keys.NUMPAD_4) {
            executar(keycode - Keys.NUMPAD_1);
            return true;
        }
        return false;
    }

    private boolean tratarTeclaDaConfirmacao(int keycode) {
        if (keycode == Keys.LEFT || keycode == Keys.A || keycode == Keys.RIGHT || keycode == Keys.D) {
            if (confirmacao.foco() == 0) {
                confirmacao.descer();
            } else {
                confirmacao.subir();
            }
            return true;
        }
        if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.SPACE) {
            confirmarOuCancelarSaida(confirmacao.foco());
            return true;
        }
        if (ehVoltar(keycode) || keycode == Keys.N) {
            fecharConfirmacaoDeSaida();
            return true;
        }
        return false;
    }

    @Override
    protected boolean aoTocar(String id, int ponteiro) {
        if (id.startsWith("menu_")) {
            int indice = Integer.parseInt(id.substring("menu_".length()));
            opcoes.focar(indice);
            executar(indice);
            return true;
        }
        if (id.equals("sair")) {
            confirmarOuCancelarSaida(0);
            return true;
        }
        if (id.equals("cancelar")) {
            confirmarOuCancelarSaida(1);
            return true;
        }
        return false;
    }

    private void executar(int indice) {
        jogo.sons().tocar(Som.SELECIONAR);
        switch (indice) {
            case 0:
                jogo.setScreen(new CampusScreen(jogo));
                break;
            case 1:
                jogo.setScreen(new NovaPartidaScreen(jogo));
                break;
            case 2:
                jogo.setScreen(new RankingScreen(jogo, null, false));
                break;
            case 3:
                jogo.setScreen(new OpcoesScreen(jogo));
                break;
            default:
                throw new IllegalArgumentException("Opção de menu inválida: " + indice);
        }
    }

    private void abrirConfirmacaoDeSaida() {
        confirmandoSaida = true;
        confirmacao.focar(0);
        atualizarAreasDeAcao();
    }

    private void fecharConfirmacaoDeSaida() {
        confirmandoSaida = false;
        atualizarAreasDeAcao();
    }

    private void confirmarOuCancelarSaida(int indice) {
        if (indice == 0) {
            Gdx.app.exit();
        } else {
            fecharConfirmacaoDeSaida();
        }
    }

    @Override
    protected void desenhar(float delta) {
        desenharEscurecimento();
        jogo.fontes().comEscalaTemporaria(layout.escala(), () -> {
            atualizarLayoutDoToque();
            jogo.batch().begin();
            try {
                desenharTituloEFrase();
                desenharNave();
                desenharOpcoes();
                desenharRodape();
            } finally {
                jogo.batch().end();
            }
        });
        if (confirmandoSaida) {
            desenharConfirmacao();
        }
    }

    private void atualizarLayoutDoToque() {
        if (!jogo.modoToque() || !layout.emRetrato()) {
            layoutDoToque = null;
            return;
        }
        BitmapFont rotulo = jogo.fonte(FonteDoJogo.ROTULO);
        BitmapFont display = jogo.fonte(FonteDoJogo.DISPLAY);
        BitmapFont texto = jogo.fonte(FonteDoJogo.TEXTO);
        BitmapFont opcao = jogo.fonte(FonteDoJogo.SUBTITULO);
        BitmapFont pequeno = jogo.fonte(FonteDoJogo.PEQUENO);
        float larguraDaFrase = Math.max(1f, layout.largura() - 64f);
        medidor.setText(texto,
                "Resgate os três fundadores e construa o primeiro campus interplanetário.",
                FRASE, larguraDaFrase, com.badlogic.gdx.utils.Align.left, true);
        int linhas = Math.max(1,
                Math.round((medidor.height - texto.getCapHeight()) / texto.getLineHeight()) + 1);
        MedidasDoMenuToque medidas = new MedidasDoMenuToque(
                rotulo.getLineHeight(), display.getLineHeight(), linhas * texto.getLineHeight(),
                opcao.getLineHeight(), pequeno.getLineHeight(), jogo.alvoMinimo());
        layoutDoToque = LayoutDoMenuToque.calcular(layout.largura(), layout.altura(),
                jogo.margensSeguras(), medidas);
        atualizarAreasDeAcao();
    }

    private void desenharEscurecimento() {
        ShapeRenderer formas = jogo.shapes();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        Color escuro = new Color(FUNDO).mul(1f, 1f, 1f, 0.90f);
        Color transparente = new Color(FUNDO).mul(1f, 1f, 1f, layout.emRetrato() ? 0.38f : 0f);
        float largura = layout.emRetrato() ? layout.largura() : layout.xDaReferencia(760f);
        formas.rect(0f, 0f, largura, layout.altura(), escuro, transparente, transparente, escuro);
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void desenharTituloEFrase() {
        if (layoutDoToque != null) {
            desenharLinhaEsquerda("UNIFOR · EXPEDIÇÃO 2087", FonteDoJogo.ROTULO,
                    AMBAR, layoutDoToque.expedicao());
            desenharLinhaEsquerda("MISSÃO", FonteDoJogo.DISPLAY, TEXTO, layoutDoToque.missao());
            desenharLinhaEsquerda("MARTE", FonteDoJogo.DISPLAY, TEXTO, layoutDoToque.marte());
            fonte(FonteDoJogo.TEXTO);
            cor(FRASE);
            textoQuebrado("Resgate os três fundadores e construa o primeiro campus interplanetário.",
                    layoutDoToque.frase().x(), layoutDoToque.frase().topo(),
                    layoutDoToque.frase().largura());
            return;
        }
        float x = layout.emRetrato() ? 48f : layout.xDaReferencia(96f);
        fonte(FonteDoJogo.ROTULO);
        cor(AMBAR);
        texto("UNIFOR · EXPEDIÇÃO 2087", x, layout.emRetrato() ? layout.altura() - 64f : layout.yDaReferencia(92f));

        fonte(FonteDoJogo.DISPLAY);
        cor(TEXTO);
        float xTitulo = layout.emRetrato() ? 44f : layout.xDaReferencia(92f);
        texto("MISSÃO", xTitulo, layout.emRetrato() ? layout.altura() - 118f : layout.yDaReferencia(112f));
        texto("MARTE", xTitulo, layout.emRetrato() ? layout.altura() - 208f : layout.yDaReferencia(192f));

        fonte(FonteDoJogo.TEXTO);
        cor(FRASE);
        float yFrase = layout.emRetrato() ? layout.altura() - 330f : layout.yDaReferencia(304f);
        float largura = layout.emRetrato() ? layout.largura() - 96f : 420f * layout.escala();
        textoQuebrado("Resgate os três fundadores e construa o primeiro campus interplanetário.", x, yFrase, largura);
    }

    private void desenharLinhaEsquerda(String texto, FonteDoJogo tipo, Color cor,
            RetanguloDeTela area) {
        BitmapFont fonte = jogo.fonte(tipo);
        fonte.setColor(cor);
        medidor.setText(fonte, texto);
        fonte.draw(jogo.batch(), medidor, area.x(), area.centroY() + medidor.height / 2f);
    }

    private void desenharNave() {
        if (layout.emRetrato()) {
            return;
        }
        float flutuar = (float) Math.sin(tempo * 2f) * 6f * layout.escala();
        float x = layout.xDaReferencia(1030f);
        float y = layout.altura() - (150f + 96f) * layout.escala() + flutuar;
        float tamanho = 96f * layout.escala();
        jogo.batch().draw(propulsor.getKeyFrame(tempo, true), x, y, tamanho, tamanho);
        jogo.batch().draw(jogo.recursos().quadro(Imagem.NAVE), x, y, tamanho, tamanho);
    }

    private void desenharOpcoes() {
        String[] rotulos = {rotuloDaCampanha(), "Partida rápida", "Ranking", "Opções"};
        String detalhe = detalheDaCampanha();
        for (int i = 0; i < rotulos.length; i++) {
            RetanguloDeTela area = botaoDoMenu(i);
            boolean foco = opcoes.emFoco(i);
            jogo.estilo().botao(jogo.batch(), area, foco);
            BitmapFont fonte = jogo.fonte(FonteDoJogo.SUBTITULO);
            fonte.setColor(foco ? FUNDO : TEXTO);
            medidor.setText(fonte, rotulos[i]);
            fonte.draw(jogo.batch(), medidor, area.x() + 20f * layout.escala(), area.centroY() + medidor.height / 2f);
            if (i == 0 && detalhe != null) {
                BitmapFont pequena = jogo.fonte(FonteDoJogo.PEQUENO);
                pequena.setColor(foco ? FUNDO : SECUNDARIO);
                medidor.setText(pequena, detalhe);
                pequena.draw(jogo.batch(), medidor, area.direita() - 18f * layout.escala() - medidor.width,
                        area.centroY() + medidor.height / 2f);
            }
        }
    }

    private String rotuloDaCampanha() {
        if (jogo.campanha().concluida()) {
            return "Nova campanha";
        }
        ProgressoDaCampanha progresso = jogo.campanha().progresso();
        if (progresso.etapaAtual() == 1 && progresso.pontuacaoTotal() == 0) {
            return "Campanha";
        }
        return "Continuar campanha";
    }

    private String detalheDaCampanha() {
        String rotulo = rotuloDaCampanha();
        if (jogo.campanha().concluida() || rotulo.equals("Campanha")) {
            return null;
        }
        return "Etapa " + jogo.campanha().progresso().etapaAtual();
    }

    private void desenharRodape() {
        BitmapFont pequena = jogo.fonte(FonteDoJogo.PEQUENO);
        pequena.setColor(SECUNDARIO);
        String conta = jogo.sessao() == null ? "Convidado · ranking local"
                : jogo.sessao().getNome() + " · ranking global";
        if (layoutDoToque != null) {
            desenharLinhaEsquerda(conta, FonteDoJogo.PEQUENO, SECUNDARIO, layoutDoToque.conta());
            desenharLinhaEsquerda("Toque em uma opção para selecionar", FonteDoJogo.PEQUENO,
                    SECUNDARIO, layoutDoToque.instrucao());
            return;
        }
        medidor.setText(pequena, conta);
        float margem = layout.emRetrato() ? 32f : 96f * layout.escala();
        pequena.draw(jogo.batch(), medidor, layout.largura() - margem - medidor.width, margem + medidor.height);
        if (!PoliticaDeAtalhos.exibeRodapeDeTeclado(jogo.modoToque())) {
            pequena.draw(jogo.batch(), "Toque em uma opção para selecionar", margem, margem + 44f);
            return;
        }
        rodape.desenhar(jogo.batch(), jogo.estilo(), layout.xDaReferencia(96f), 28f * layout.escala(),
                new Atalho("Setas", "Navegar"), new Atalho("Enter", "Selecionar"), new Atalho("Esc", "Sair"));
    }

    private void desenharConfirmacao() {
        RetanguloDeTela painel = painelDeConfirmacao();
        escurecerTudo();
        jogo.batch().begin();
        jogo.estilo().painel(jogo.batch(), painel);
        BitmapFont titulo = jogo.fonte(FonteDoJogo.TITULO);
        titulo.setColor(AMBAR);
        medidor.setText(titulo, "SAIR DO JOGO?");
        titulo.draw(jogo.batch(), medidor, painel.centroX() - medidor.width / 2f, painel.topo() - 34f);
        desenharBotaoDeConfirmacao(painel, 0, "Sair");
        desenharBotaoDeConfirmacao(painel, 1, "Cancelar");
        jogo.batch().end();
    }

    private void desenharBotaoDeConfirmacao(RetanguloDeTela painel, int indice, String rotulo) {
        float largura = (painel.largura() - 60f) / 2f;
        RetanguloDeTela area = new RetanguloDeTela(painel.x() + 20f + indice * (largura + 20f), painel.y() + 24f,
                largura, 50f);
        boolean foco = confirmacao.emFoco(indice);
        jogo.estilo().botao(jogo.batch(), area, foco);
        BitmapFont fonte = jogo.fonte(FonteDoJogo.TEXTO);
        fonte.setColor(foco ? FUNDO : TEXTO);
        medidor.setText(fonte, rotulo);
        fonte.draw(jogo.batch(), medidor, area.centroX() - medidor.width / 2f, area.centroY() + medidor.height / 2f);
    }

    private RetanguloDeTela painelDeConfirmacao() {
        float largura = Math.min(420f, layout.largura() - 48f);
        return new RetanguloDeTela((layout.largura() - largura) / 2f, (layout.altura() - 190f) / 2f, largura, 190f);
    }

    private void escurecerTudo() {
        ShapeRenderer formas = jogo.shapes();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0f, 0f, 0f, 0.72f);
        formas.rect(0f, 0f, layout.largura(), layout.altura());
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

}
