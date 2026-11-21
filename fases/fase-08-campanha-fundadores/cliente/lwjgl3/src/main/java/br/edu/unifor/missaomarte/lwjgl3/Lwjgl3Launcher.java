package br.edu.unifor.missaomarte.lwjgl3;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.application.campanha.InicializadorDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.PreparacaoInicialDaCampanha;
import br.edu.unifor.missaomarte.application.campanha.RepositorioDeProgressoEmTexto;
import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoDoServidor;
import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoInvalidaException;
import br.edu.unifor.missaomarte.application.servidor.EnderecoDoServidor;
import br.edu.unifor.missaomarte.application.servidor.RepositorioDeConfiguracaoDoServidor;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.gdx.plataforma.Plataforma;
import br.edu.unifor.missaomarte.gdx.plataforma.PreferenciasComoArmazem;
import br.edu.unifor.missaomarte.gdx.rede.ServidorHttp;
import br.edu.unifor.missaomarte.infrastructure.ranking.FileRankingRepository;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Composition root do desktop: monta as dependencias, configura a janela e inicia o jogo.
 * <p>Padrao: Injecao de Dependencia manual (como o antigo Main). Nao contem regra de jogo.
 * <p>Propriedades: missaomarte.semente (cenario repetivel), missaomarte.servidor (endereco padrao do servidor; o que o jogador
 * salvar na tela Servidor vale mais), missaomarte.semaudio, missaomarte.toque (mouse no lugar do dedo, com botoes virtuais)
 * missaomarte.campanha.etapa (abre o campus diretamente numa etapa de demonstracao, de 1 a 3) e
 * missaomarte.campanha.concluida (abre uma campanha concluida somente em memoria).
 */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        // -Dmissaomarte.semente=123 repete exatamente o mesmo cenario e os mesmos movimentos dos inimigos.
        Long semente = Long.getLong("missaomarte.semente");
        Random random = semente == null ? new Random() : new Random(semente);

        // -Dmissaomarte.servidor=http://host:porta e o endereco padrao; o salvo pelo jogador na tela Servidor prevalece.
        ConfiguracaoDoServidor padrao;
        try {
            padrao = ConfiguracaoDoServidor.deUrl(System.getProperty("missaomarte.servidor", ConfiguracaoDoServidor.PADRAO.urlBase()));
        } catch (ConfiguracaoInvalidaException e) {
            System.err.println("missaomarte.servidor: " + e.getMessage());
            System.exit(1);
            return;
        }
        PreferenciasComoArmazem armazem = new PreferenciasComoArmazem("missaomarte");
        EnderecoDoServidor endereco = new EnderecoDoServidor(
                new RepositorioDeConfiguracaoDoServidor(armazem), padrao);
        PreparacaoInicialDaCampanha inicioDaCampanha = InicializadorDaCampanha.preparar(
                selecaoDaCampanha(), new RepositorioDeProgressoEmTexto(armazem));

        // -Dmissaomarte.toque=true liga o modo toque no desktop: o mouse faz o papel do dedo.
        boolean toque = Boolean.getBoolean("missaomarte.toque");

        MissaoMarteGame jogo = new MissaoMarteGame(
                new MissaoFactory(),
                new RankingService(new FileRankingRepository(Paths.get("ranking.json"))),
                new ServidorHttp(endereco),
                endereco,
                Plataforma.desktop(toque, fatorDeTextoDoDesktop()),
                random,
                inicioDaCampanha);

        Lwjgl3ApplicationConfiguration configuracao = new Lwjgl3ApplicationConfiguration();
        configuracao.setTitle(tituloDaJanela());
        int[] janela = tamanhoDaJanela(toque);
        configuracao.setWindowedMode(janela[0], janela[1]);
        configuracao.useVsync(true);
        configuracao.setForegroundFPS(60);
        configuracao.disableAudio(Boolean.getBoolean("missaomarte.semaudio"));

        new Lwjgl3Application(jogo, configuracao);
    }

    // -Dmissaomarte.janela=540x1200 simula uma tela de celular em retrato; sem a opcao: 1280x720.
    private static int[] tamanhoDaJanela(boolean toque) {
        String pedido = System.getProperty("missaomarte.janela", "");
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d{3,4})x(\\d{3,4})").matcher(pedido);
        if (m.matches()) {
            return new int[] {Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))};
        }
        return new int[] {1280, 720};
    }

    private static String tituloDaJanela() {
        return "Missão Marte";
    }

    // -Dmissaomarte.fatorTexto=1.6 reproduz no desktop a tipografia ampliada de uma tela Android densa.
    private static float fatorDeTextoDoDesktop() {
        try {
            float fator = Float.parseFloat(System.getProperty("missaomarte.fatorTexto", "1.0"));
            return Float.isFinite(fator) && fator > 0f ? fator : 1f;
        } catch (NumberFormatException e) {
            return 1f;
        }
    }

    private static String selecaoDaCampanha() {
        return Boolean.getBoolean("missaomarte.campanha.concluida")
                ? InicializadorDaCampanha.DEMONSTRACAO_CONCLUIDA
                : System.getProperty("missaomarte.campanha.etapa");
    }
}
