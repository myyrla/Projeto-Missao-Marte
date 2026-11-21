package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.ConfiguracaoDificuldade;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import br.edu.unifor.missaomarte.application.RegrasDeTempoReal;
import br.edu.unifor.missaomarte.application.RegrasDificuldade;
import br.edu.unifor.missaomarte.application.pontuacao.RegraDePontuacaoPadrao;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.plataforma.Plataforma.TipoDeTexto;
import br.edu.unifor.missaomarte.gdx.recursos.FonteDoJogo;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import br.edu.unifor.missaomarte.gdx.ui.IconeDaInterface;
import br.edu.unifor.missaomarte.gdx.ui.LayoutDaNovaPartida;
import br.edu.unifor.missaomarte.gdx.ui.MedidasDaNovaPartida;
import br.edu.unifor.missaomarte.gdx.ui.RotulosDaInterface;
import br.edu.unifor.missaomarte.gdx.ui.TextoDoCampoDeNome;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

/**
 * Pede nome, dificuldade e modo de jogo e cria a {@link GameSession} usando a fabrica e as regras ja existentes.
 * <p>Logado: o piloto e o nome da conta. Convidado: o nome e digitado.
 * <p>O nome aceita so ASCII simples: evita caracteres fora da faixa de glifos da fonte padrao (ate Latin-1) e simplifica a validacao.
 * <p>Modo: dinamico (tempo real, padrao) ou classico (por turnos, como nas fases anteriores).
 * <p>Teclas: digitar o nome, BACKSPACE apaga, SETAS esquerda/direita mudam a dificuldade, CIMA/BAIXO mudam o modo, ENTER inicia,
 * ESC (ou Voltar) volta. Modo toque: botoes < e > para dificuldade e modo, Decolar e Voltar; tocar no nome abre a caixa de texto nativa.
 * <p>Esta tela nao usa o atalho global M, porque a letra M faz parte do texto digitado.
 */
public class NovaPartidaScreen extends TelaBase {
    private static final int TAMANHO_MAXIMO_NOME = 12;
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);

    private final Dificuldade[] dificuldades = Dificuldade.values();
    private final ModoDeJogo[] modos = ModoDeJogo.values();
    private final StringBuilder nome = new StringBuilder();
    private int indice;
    private int indiceDoModo = ModoDeJogo.DINAMICO.ordinal();
    private final GlyphLayout medidor = new GlyphLayout();
    private LayoutDaNovaPartida layoutDoToque;

    public NovaPartidaScreen(MissaoMarteGame jogo) {
        super(jogo);
        if (!logado()) {
            botoes.add(new BotaoVirtual("nome", "", 190f, 376f, 460f, 38f));
        }
        botoes.add(new BotaoVirtual("anterior", "<", 560f, 326f, 56f, 44f));
        botoes.add(new BotaoVirtual("proxima", ">", 626f, 326f, 56f, 44f));
        botoes.add(new BotaoVirtual("modo_anterior", "<", 560f, 276f, 56f, 44f));
        botoes.add(new BotaoVirtual("modo_proximo", ">", 626f, 276f, 56f, 44f));
        botoes.add(new BotaoVirtual("decolar", "Decolar", 300f, 150f, 200f, 56f));
        botoes.add(new BotaoVirtual("voltar", "Voltar", 40f, 60f, 140f, 44f));
    }

    private boolean toqueVertical() {
        return jogo.modoToque() && jogo.viewport().getWorldHeight() > jogo.viewport().getWorldWidth();
    }

    @Override
    protected boolean usaMundoInteiro() {
        return toqueVertical();
    }

    @Override
    protected boolean desenhaBotoesVirtuais() {
        return !toqueVertical();
    }

    @Override
    protected void aoRedimensionar() {
        layoutDoToque = null;
    }

    @Override
    public void show() {
        super.show();
        definirEntradaDeTexto(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return tratarTecla(keycode);
            }

            @Override
            public boolean keyTyped(char caractere) {
                if (!logado() && nome.length() < TAMANHO_MAXIMO_NOME && caractereValido(caractere)) {
                    nome.append(caractere);
                    return true;
                }
                return false;
            }
        });
    }

    private boolean logado() {
        return jogo.sessao() != null;
    }

    private static boolean caractereValido(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == ' ' || c == '-' || c == '_';
    }

    @Override
    protected boolean aoTocar(String id, int ponteiro) {
        switch (id) {
            case "nome":
                jogo.plataforma().pedirTexto("Nome do piloto", nome.toString(), TipoDeTexto.NORMAL, this::definirNome);
                return true;
            case "anterior":
                mudarDificuldade(-1);
                return true;
            case "proxima":
                mudarDificuldade(1);
                return true;
            case "modo_anterior":
                mudarModo(-1);
                return true;
            case "modo_proximo":
                mudarModo(1);
                return true;
            case "decolar":
                iniciarPartida();
                return true;
            case "voltar":
                jogo.setScreen(new MenuScreen(jogo));
                return true;
            default:
                return false;
        }
    }

    /** Substitui o nome pelo texto da caixa nativa, descartando o que o nome nao aceita. */
    private void definirNome(String texto) {
        nome.setLength(0);
        for (int i = 0; i < texto.length() && nome.length() < TAMANHO_MAXIMO_NOME; i++) {
            if (caractereValido(texto.charAt(i))) {
                nome.append(texto.charAt(i));
            }
        }
    }

    private void mudarDificuldade(int passo) {
        indice = (indice + passo + dificuldades.length) % dificuldades.length;
        jogo.sons().tocar(Som.SELECIONAR);
    }

    private void mudarModo(int passo) {
        indiceDoModo = (indiceDoModo + passo + modos.length) % modos.length;
        jogo.sons().tocar(Som.SELECIONAR);
    }

    private boolean tratarTecla(int keycode) {
        if (ehVoltar(keycode)) {
            jogo.setScreen(new MenuScreen(jogo));
            return true;
        }
        switch (keycode) {
            case Keys.BACKSPACE:
                if (nome.length() > 0) {
                    nome.deleteCharAt(nome.length() - 1);
                }
                return true;
            case Keys.LEFT:
                mudarDificuldade(-1);
                return true;
            case Keys.RIGHT:
                mudarDificuldade(1);
                return true;
            case Keys.UP:
                mudarModo(-1);
                return true;
            case Keys.DOWN:
            case Keys.TAB:
                mudarModo(1);
                return true;
            case Keys.ENTER:
            case Keys.NUMPAD_ENTER:
                iniciarPartida();
                return true;
            default:
                return false;
        }
    }

    private void iniciarPartida() {
        String piloto = logado() ? jogo.sessao().getNome() : nome.toString().trim();
        if (piloto.isEmpty()) {
            piloto = "Piloto";
        }
        Dificuldade dificuldade = dificuldades[indice];
        ModoDeJogo modo = modos[indiceDoModo];
        MissaoFactory fabrica = jogo.missaoFactory();
        Missao missao = fabrica.criarMissao(dificuldade, MissaoMarteGame.MIN, MissaoMarteGame.MAX, jogo.random(), modo);
        GameSession session = new GameSession(
                piloto, dificuldade, missao, new RegraDePontuacaoPadrao(fabrica.pontuacaoInicial(dificuldade)),
                MissaoMarteGame.MIN, MissaoMarteGame.MAX, jogo.random(), modo);
        jogo.sons().tocar(Som.SELECIONAR);
        jogo.setScreen(new GameScreen(jogo, session));
    }

    @Override
    protected void desenhar(float delta) {
        Dificuldade dificuldade = dificuldades[indice];
        ModoDeJogo modo = modos[indiceDoModo];
        ConfiguracaoDificuldade cfg = RegrasDificuldade.obter(dificuldade);
        String cursor = ((int) (tempo * 2f)) % 2 == 0 ? "_" : " ";

        if (toqueVertical()) {
            desenharNoToque(dificuldade, modo, cfg, cursor);
            return;
        }

        jogo.batch().begin();

        fonte(FonteDoJogo.TITULO);
        cor(DESTAQUE);
        textoCentralizado("NOVA PARTIDA", MissaoMarteGame.LARGURA / 2f, 500f);

        fonte(FonteDoJogo.SUBTITULO);
        cor(Color.WHITE);
        texto(logado() ? "Piloto: " + jogo.sessao().getNome() : "Nome do piloto: " + nome + cursor, 200f, 410f);
        texto("Dificuldade: < " + RotulosDaInterface.dificuldade(dificuldade) + " >", 200f, 360f);
        texto("Modo: < " + (modo == ModoDeJogo.DINAMICO ? "Dinâmico" : "Clássico") + " >", 200f, 310f);

        fonte(FonteDoJogo.TEXTO);
        cor(Color.LIGHT_GRAY);
        int perseguidores = modo == ModoDeJogo.DINAMICO ? RegrasDeTempoReal.obter(dificuldade).getQuantidadeDePerseguidores() : 0;
        texto("Passageiros: " + cfg.getQuantidadePassageiros()
                + "   Asteroides: " + cfg.getQuantidadeAsteroides()
                + "   Inimigos: " + (cfg.getQuantidadeInimigos() + perseguidores)
                + "   Pontos: " + cfg.getPontuacaoInicial(), 200f, 258f);
        cor(DESTAQUE);
        texto(modo == ModoDeJogo.DINAMICO
                ? "Tempo real: segure a direção. Tem tiro."
                : "Por turnos: o jogo espera a sua jogada.", 200f, 234f);

        fonte(FonteDoJogo.PEQUENO);
        cor(Color.LIGHT_GRAY);
        textoCentralizado(jogo.modoToque()
                ? (logado() ? "< >: dificuldade e modo   |   Decolar: iniciar"
                        : "Toque no nome para digitar   |   < >: dificuldade e modo   |   Decolar: iniciar")
                : "Setas: esquerda/direita dificuldade, cima/baixo ou Tab modo   |   Enter: decolar   |   Esc: voltar",
                MissaoMarteGame.LARGURA / 2f, 30f);

        jogo.batch().end();
    }

    private void desenharNoToque(Dificuldade dificuldade, ModoDeJogo modo,
            ConfiguracaoDificuldade cfg, String cursor) {
        String resumo = resumo(cfg, dificuldade, modo);
        String descricao = descricao(modo);
        atualizarLayoutDoToque(resumo, descricao);
        atualizarBotoesDoToque();

        jogo.batch().begin();
        try {
            jogo.estilo().painel(jogo.batch(), layoutDoToque.cartao(0));
            jogo.estilo().painel(jogo.batch(), layoutDoToque.cartao(1));
            jogo.estilo().painel(jogo.batch(), layoutDoToque.cartao(2));
            desenharSeletores();
            jogo.estilo().botao(jogo.batch(), layoutDoToque.decolar(), true);
            jogo.estilo().botao(jogo.batch(), layoutDoToque.voltar(), false);

            desenharCentralizado("NOVA PARTIDA", FonteDoJogo.TITULO,
                    layoutDoToque.titulo(), DESTAQUE);
            String[] rotulos = {"NOME", "DIFICULDADE", "MODO"};
            TextoDoCampoDeNome nomeVisual = TextoDoCampoDeNome.de(logado(),
                    logado() ? jogo.sessao().getNome() : "", nome.toString(), cursor);
            String[] valores = {
                nomeVisual.conteudo(),
                RotulosDaInterface.dificuldade(dificuldade),
                modo == ModoDeJogo.DINAMICO ? "Dinâmico" : "Clássico"
            };
            for (int i = 0; i < 3; i++) {
                desenharCentralizado(rotulos[i], FonteDoJogo.ROTULO,
                        layoutDoToque.rotulo(i), Color.LIGHT_GRAY);
                FonteDoJogo fonteDoValor = i == 0 && nomeVisual.exemplo()
                        ? FonteDoJogo.TEXTO : FonteDoJogo.SUBTITULO;
                Color corDoValor = i == 0 && nomeVisual.exemplo()
                        ? Color.LIGHT_GRAY : Color.WHITE;
                desenharCentralizado(valores[i], fonteDoValor,
                        layoutDoToque.valor(i), corDoValor);
            }
            desenharQuebrado(resumo, FonteDoJogo.TEXTO, layoutDoToque.resumo(), Color.WHITE);
            desenharQuebrado(descricao, FonteDoJogo.TEXTO, layoutDoToque.descricao(), DESTAQUE);
            desenharCentralizado("Decolar", FonteDoJogo.SUBTITULO,
                    layoutDoToque.decolar(), Color.valueOf("0A0612"));
            desenharCentralizado("Voltar", FonteDoJogo.TEXTO,
                    layoutDoToque.voltar(), Color.WHITE);
        } finally {
            jogo.batch().end();
        }
    }

    private void atualizarLayoutDoToque(String resumo, String descricao) {
        float largura = jogo.viewport().getWorldWidth();
        float altura = jogo.viewport().getWorldHeight();
        float larguraDoTexto = Math.max(1f, largura - 64f);
        BitmapFont titulo = jogo.fonte(FonteDoJogo.TITULO);
        BitmapFont rotulo = jogo.fonte(FonteDoJogo.ROTULO);
        BitmapFont valor = jogo.fonte(FonteDoJogo.SUBTITULO);
        BitmapFont texto = jogo.fonte(FonteDoJogo.TEXTO);
        float alturaDoResumo = medirAlturaQuebrada(texto, resumo, larguraDoTexto);
        float alturaDaDescricao = medirAlturaQuebrada(texto, descricao, larguraDoTexto);
        MedidasDaNovaPartida medidas = new MedidasDaNovaPartida(
                titulo.getLineHeight(), rotulo.getLineHeight(), valor.getLineHeight(),
                alturaDoResumo, alturaDaDescricao, jogo.alvoMinimo());
        layoutDoToque = LayoutDaNovaPartida.calcular(
                largura, altura, jogo.margensSeguras(), medidas);
    }

    private float medirAlturaQuebrada(BitmapFont fonte, String texto, float largura) {
        medidor.setText(fonte, texto, Color.WHITE, largura, Align.center, true);
        if (medidor.height <= 0f) return 0f;
        int linhas = Math.max(1,
                Math.round((medidor.height - fonte.getCapHeight()) / fonte.getLineHeight()) + 1);
        return linhas * fonte.getLineHeight();
    }

    private void atualizarBotoesDoToque() {
        botoes.clear();
        if (!logado()) botoes.add(botao("nome", layoutDoToque.valor(0)));
        botoes.add(botao("anterior", layoutDoToque.anterior(1)));
        botoes.add(botao("proxima", layoutDoToque.proximo(1)));
        botoes.add(botao("modo_anterior", layoutDoToque.anterior(2)));
        botoes.add(botao("modo_proximo", layoutDoToque.proximo(2)));
        botoes.add(botao("decolar", layoutDoToque.decolar()));
        botoes.add(botao("voltar", layoutDoToque.voltar()));
    }

    private static BotaoVirtual botao(String id, RetanguloDeTela area) {
        return new BotaoVirtual(id, "", area.x(), area.y(), area.largura(), area.altura());
    }

    private void desenharSeletores() {
        for (int i = 1; i <= 2; i++) {
            desenharSeta(layoutDoToque.anterior(i), 180f);
            desenharSeta(layoutDoToque.proximo(i), 0f);
        }
    }

    private void desenharSeta(RetanguloDeTela area, float rotacao) {
        jogo.estilo().botao(jogo.batch(), area, false);
        float lado = Math.min(34f, Math.min(area.largura(), area.altura()) * 0.48f);
        jogo.batch().setColor(Color.WHITE);
        jogo.batch().draw(jogo.estilo().icone(IconeDaInterface.SETA),
                area.centroX() - lado / 2f, area.centroY() - lado / 2f,
                lado / 2f, lado / 2f, lado, lado, 1f, 1f, rotacao);
        jogo.batch().setColor(Color.WHITE);
    }

    private void desenharCentralizado(String texto, FonteDoJogo tipo,
            RetanguloDeTela area, Color cor) {
        BitmapFont fonte = jogo.fonte(tipo);
        fonte.setColor(cor);
        medidor.setText(fonte, texto);
        fonte.draw(jogo.batch(), medidor, area.centroX() - medidor.width / 2f,
                area.centroY() + medidor.height / 2f);
    }

    private void desenharQuebrado(String texto, FonteDoJogo tipo,
            RetanguloDeTela area, Color cor) {
        BitmapFont fonte = jogo.fonte(tipo);
        fonte.setColor(cor);
        medidor.setText(fonte, texto, cor, area.largura(), Align.center, true);
        fonte.draw(jogo.batch(), medidor, area.x(), area.topo());
    }

    private static String resumo(ConfiguracaoDificuldade cfg, Dificuldade dificuldade, ModoDeJogo modo) {
        int perseguidores = modo == ModoDeJogo.DINAMICO
                ? RegrasDeTempoReal.obter(dificuldade).getQuantidadeDePerseguidores() : 0;
        return "Passageiros: " + cfg.getQuantidadePassageiros()
                + " · Asteroides: " + cfg.getQuantidadeAsteroides()
                + " · Inimigos: " + (cfg.getQuantidadeInimigos() + perseguidores)
                + " · Pontos: " + cfg.getPontuacaoInicial();
    }

    private static String descricao(ModoDeJogo modo) {
        return modo == ModoDeJogo.DINAMICO
                ? "Tempo real: segure a direção. Tem tiro."
                : "Por turnos: o jogo espera a sua jogada.";
    }
}
