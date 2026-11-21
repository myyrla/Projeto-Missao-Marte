package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.hud.PoliticaDeAtalhos;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.layout.TextoResponsivo;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import br.edu.unifor.missaomarte.gdx.ui.LayoutDoMenu;
import br.edu.unifor.missaomarte.gdx.ui.ListaDeBotoes;
import br.edu.unifor.missaomarte.gdx.ui.RodapeDeTeclas;
import br.edu.unifor.missaomarte.gdx.ui.RodapeDeTeclas.Atalho;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/** Opcoes retiradas do menu principal: conta, servidor, som, ranking local e saida. */
public class OpcoesScreen extends TelaBase {
    private enum Confirmacao { NENHUMA, LIMPAR_RANKING, SAIR }

    private static final Color FUNDO = Color.valueOf("0A0612");
    private static final Color TEXTO = Color.valueOf("F2EEF7");
    private static final Color SECUNDARIO = Color.valueOf("B9B2C6");
    private static final Color AMBAR = Color.valueOf("FFB000");
    private static final Color VERDE = Color.valueOf("9EF01A");
    private static final String[] NOMES = {"Conta", "Servidor", "Som", "Ranking local", "Sair do jogo"};

    private final ListaDeBotoes opcoes = new ListaDeBotoes(5);
    private final ListaDeBotoes botoesDaConfirmacao = new ListaDeBotoes(2);
    private final RodapeDeTeclas rodape = new RodapeDeTeclas();
    private final GlyphLayout medidor = new GlyphLayout();
    private LayoutDoMenu layout;
    private Confirmacao confirmacao = Confirmacao.NENHUMA;
    private String mensagem = "";

    public OpcoesScreen(MissaoMarteGame jogo) {
        super(jogo);
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
        if (confirmacao != Confirmacao.NENHUMA) {
            RetanguloDeTela painel = painelDeConfirmacao();
            float largura = (painel.largura() - 60f) / 2f;
            botoes.add(botaoVirtual("confirmar", new RetanguloDeTela(painel.x() + 20f, painel.y() + 24f, largura, 50f)));
            botoes.add(botaoVirtual("cancelar", new RetanguloDeTela(painel.x() + 40f + largura, painel.y() + 24f, largura, 50f)));
            return;
        }
        for (int i = 0; i < 5; i++) {
            botoes.add(botaoVirtual("opcao_" + i, layout.itemOpcoes(i)));
        }
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
        if (confirmacao != Confirmacao.NENHUMA) {
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
            voltar();
            return true;
        }
        return false;
    }

    private boolean tratarTeclaDaConfirmacao(int keycode) {
        if (keycode == Keys.LEFT || keycode == Keys.A || keycode == Keys.RIGHT || keycode == Keys.D) {
            if (botoesDaConfirmacao.foco() == 0) {
                botoesDaConfirmacao.descer();
            } else {
                botoesDaConfirmacao.subir();
            }
            return true;
        }
        if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.SPACE) {
            confirmarOuCancelar(botoesDaConfirmacao.foco());
            return true;
        }
        if (ehVoltar(keycode) || keycode == Keys.N) {
            cancelarConfirmacao();
            return true;
        }
        return false;
    }

    @Override
    protected boolean aoTocar(String id, int ponteiro) {
        if (id.startsWith("opcao_")) {
            int indice = Integer.parseInt(id.substring("opcao_".length()));
            opcoes.focar(indice);
            executar(indice);
            return true;
        }
        if (id.equals("confirmar")) {
            confirmarOuCancelar(0);
            return true;
        }
        if (id.equals("cancelar")) {
            confirmarOuCancelar(1);
            return true;
        }
        return false;
    }

    private void executar(int indice) {
        jogo.sons().tocar(Som.SELECIONAR);
        switch (indice) {
            case 0:
                acionarConta();
                break;
            case 1:
                jogo.setScreen(new ConfigServidorScreen(jogo, () -> new OpcoesScreen(jogo)));
                break;
            case 2:
                boolean mudo = jogo.sons().alternarMudo();
                mensagem = mudo ? "Som desligado." : "Som ligado.";
                break;
            case 3:
                abrirConfirmacao(Confirmacao.LIMPAR_RANKING);
                break;
            case 4:
                abrirConfirmacao(Confirmacao.SAIR);
                break;
            default:
                throw new IllegalArgumentException("Opção inválida: " + indice);
        }
    }

    private void acionarConta() {
        if (jogo.sessao() == null) {
            jogo.setScreen(new LoginScreen(jogo));
        } else {
            jogo.sair();
            mensagem = "Você saiu da conta.";
        }
    }

    private void abrirConfirmacao(Confirmacao novaConfirmacao) {
        confirmacao = novaConfirmacao;
        botoesDaConfirmacao.focar(0);
        atualizarAreasDeAcao();
    }

    private void cancelarConfirmacao() {
        confirmacao = Confirmacao.NENHUMA;
        atualizarAreasDeAcao();
    }

    private void confirmarOuCancelar(int indice) {
        if (indice != 0) {
            cancelarConfirmacao();
            return;
        }
        if (confirmacao == Confirmacao.LIMPAR_RANKING) {
            jogo.rankingService().limpar();
            mensagem = "Ranking local apagado.";
            cancelarConfirmacao();
        } else if (confirmacao == Confirmacao.SAIR) {
            Gdx.app.exit();
        }
    }

    private void voltar() {
        jogo.setScreen(new MenuScreen(jogo));
    }

    @Override
    protected void desenhar(float delta) {
        jogo.fontes().comEscalaTemporaria(layout.escala(), () -> {
            jogo.batch().begin();
            try {
                desenharPainel();
                desenharRodape();
            } finally {
                jogo.batch().end();
            }
        });
        if (confirmacao != Confirmacao.NENHUMA) {
            desenharConfirmacao();
        }
    }

    private void desenharPainel() {
        RetanguloDeTela painel = layout.painelOpcoes();
        jogo.estilo().painel(jogo.batch(), painel);
        BitmapFont titulo = jogo.fonte(FonteDoJogo.TITULO);
        titulo.setColor(TEXTO);
        medidor.setText(titulo, "OPÇÕES");
        float xTitulo = layout.emRetrato() ? painel.x() + 24f : layout.xDaReferencia(380f);
        titulo.draw(jogo.batch(), medidor, xTitulo, painel.topo() - 30f * layout.escala());

        for (int i = 0; i < 5; i++) {
            desenharItem(i, layout.itemOpcoes(i));
        }
        if (!mensagem.isEmpty()) {
            BitmapFont pequena = jogo.fonte(FonteDoJogo.PEQUENO);
            pequena.setColor(VERDE);
            medidor.setText(pequena, mensagem);
            pequena.draw(jogo.batch(), medidor, painel.centroX() - medidor.width / 2f,
                    painel.y() - 18f * layout.escala());
        }
    }

    private void desenharItem(int indice, RetanguloDeTela area) {
        boolean foco = opcoes.emFoco(indice);
        jogo.estilo().botao(jogo.batch(), area, foco);
        Color corPrincipal = foco ? FUNDO : TEXTO;
        Color corSecundaria = foco ? FUNDO : SECUNDARIO;

        BitmapFont nome = jogo.fonte(FonteDoJogo.SUBTITULO);
        nome.setColor(corPrincipal);
        medidor.setText(nome, NOMES[indice]);
        nome.draw(jogo.batch(), medidor, area.x() + 20f * layout.escala(), area.topo() - 10f * layout.escala());

        String descricao = descricao(indice);
        if (!descricao.isEmpty()) {
            BitmapFont pequena = jogo.fonte(FonteDoJogo.PEQUENO);
            pequena.setColor(corSecundaria);
            medidor.setText(pequena, descricao);
            pequena.draw(jogo.batch(), medidor, area.x() + 20f * layout.escala(), area.y() + 12f * layout.escala()
                    + medidor.height);
        }

        String valor = valor(indice);
        if (!valor.isEmpty()) {
            BitmapFont texto = jogo.fonte(FonteDoJogo.TEXTO);
            texto.setColor(corPrincipal);
            valor = limitar(valor, texto, area.largura() * 0.48f);
            medidor.setText(texto, valor);
            texto.draw(jogo.batch(), medidor, area.direita() - 20f * layout.escala() - medidor.width,
                    area.centroY() + medidor.height / 2f);
        }
    }

    private String descricao(int indice) {
        switch (indice) {
            case 0:
                return jogo.sessao() == null ? "Entrar ou criar conta" : "Sair desta conta";
            case 1:
                return "Endereço do ranking global";
            case 2:
                return "Efeitos e música";
            case 3:
                return "Apagar recordes deste computador";
            default:
                return "";
        }
    }

    private String valor(int indice) {
        switch (indice) {
            case 0:
                return jogo.sessao() == null ? "Convidado" : jogo.sessao().getNome();
            case 1:
                return jogo.endereco().urlBase();
            case 2:
                return jogo.sons().mudo() ? "Desligado" : "Ligado";
            default:
                return "";
        }
    }

    private String limitar(String texto, BitmapFont fonte, float larguraMaxima) {
        return TextoResponsivo.limitarComReticencias(texto, larguraMaxima, candidato -> {
            medidor.setText(fonte, candidato);
            return medidor.width;
        });
    }

    private void desenharRodape() {
        if (!PoliticaDeAtalhos.exibeRodapeDeTeclado(jogo.modoToque())) {
            BitmapFont pequena = jogo.fonte(FonteDoJogo.PEQUENO);
            pequena.setColor(SECUNDARIO);
            pequena.draw(jogo.batch(), "Toque em uma opção · Voltar retorna ao menu", 32f, 44f);
            return;
        }
        rodape.desenhar(jogo.batch(), jogo.estilo(), layout.xDaReferencia(380f), 28f * layout.escala(),
                new Atalho("Setas", "Navegar"), new Atalho("Enter", "Alterar"), new Atalho("Esc", "Voltar"));
    }

    private void desenharConfirmacao() {
        RetanguloDeTela painel = painelDeConfirmacao();
        escurecerTudo();
        jogo.batch().begin();
        jogo.estilo().painel(jogo.batch(), painel);
        BitmapFont titulo = jogo.fonte(FonteDoJogo.TITULO);
        titulo.setColor(AMBAR);
        String pergunta = confirmacao == Confirmacao.LIMPAR_RANKING ? "APAGAR RANKING?" : "SAIR DO JOGO?";
        medidor.setText(titulo, pergunta);
        titulo.draw(jogo.batch(), medidor, painel.centroX() - medidor.width / 2f, painel.topo() - 34f);
        desenharBotaoDeConfirmacao(painel, 0,
                confirmacao == Confirmacao.LIMPAR_RANKING ? "Apagar" : "Sair");
        desenharBotaoDeConfirmacao(painel, 1, "Cancelar");
        jogo.batch().end();
    }

    private void desenharBotaoDeConfirmacao(RetanguloDeTela painel, int indice, String rotulo) {
        float largura = (painel.largura() - 60f) / 2f;
        RetanguloDeTela area = new RetanguloDeTela(painel.x() + 20f + indice * (largura + 20f), painel.y() + 24f,
                largura, 50f);
        boolean foco = botoesDaConfirmacao.emFoco(indice);
        jogo.estilo().botao(jogo.batch(), area, foco);
        BitmapFont fonte = jogo.fonte(FonteDoJogo.TEXTO);
        fonte.setColor(foco ? FUNDO : TEXTO);
        medidor.setText(fonte, rotulo);
        fonte.draw(jogo.batch(), medidor, area.centroX() - medidor.width / 2f, area.centroY() + medidor.height / 2f);
    }

    private RetanguloDeTela painelDeConfirmacao() {
        float largura = Math.min(440f, layout.largura() - 48f);
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
