package missao.ui;
import missao.engine.*;
import missao.engine.port.UI;
import missao.model.*;
import java.io.*;
import java.util.*;
/** Traduz entrada e apresenta resultados. Regras e persistência ficam na aplicação. */
public final class ConsoleUI implements UI {
    private final Scanner entrada;
    private final PrintStream saida;
    private final MapaRenderer renderer;
    public ConsoleUI(Scanner entrada,PrintStream saida,MapaRenderer renderer) {
        this.entrada=entrada; this.saida=saida; this.renderer=renderer;
    }
    public static Acao traduzirAcao(String texto) {
        if(texto==null || texto.isBlank()) return Acao.INVALIDA;
        return switch(texto.trim().toLowerCase(Locale.ROOT).charAt(0)) {
            case 'w' -> Acao.CIMA; case 's' -> Acao.BAIXO; case 'a' -> Acao.ESQUERDA; case 'd' -> Acao.DIREITA;
            case 'c' -> Acao.EMBARCAR; case 'q' -> Acao.ABORTAR; default -> Acao.INVALIDA;
        };
    }
    public void executar(GameEngine motor) {
        saida.println("MISSÃO MARTE UNIFOR - VERSÃO 10.0\nPilote sua nave, salve os passageiros e desvie dos perigos!");
        while(true) {
            saida.println("\n--- MENU PRINCIPAL ---\n1. Iniciar Nova Missão\n2. Visualizar Ranking Top 5\n3. Resetar Histórico de Ranking\n4. Sair do Jogo");
            String opcao=ler("Escolha uma opção: ");
            if(opcao==null) { saida.println("Entrada encerrada. Obrigado por jogar!"); return; }
            OpcaoMenu escolha=switch(opcao) { case "1" -> OpcaoMenu.JOGAR; case "2" -> OpcaoMenu.RANKING; case "3" -> OpcaoMenu.RESETAR; case "4" -> OpcaoMenu.SAIR; default -> OpcaoMenu.INVALIDA; };
            try {
                switch(escolha) {
                    case JOGAR -> jogar(motor);
                    case RANKING -> mostrarRanking(motor.ranking());
                    case RESETAR -> {
                        String confirmacao=ler("Você realmente deseja limpar o histórico de ranking? (s/n): ");
                        if("s".equalsIgnoreCase(confirmacao) || "sim".equalsIgnoreCase(confirmacao)) {
                            motor.resetarRanking(); saida.println("Ranking resetado com sucesso!");
                        } else saida.println("Operação cancelada.");
                    }
                    case SAIR -> { saida.println("Obrigado por jogar a Missão Marte Unifor!"); return; }
                    default -> saida.println("Opção inválida. Tente novamente.");
                }
            } catch(UncheckedIOException e) { saida.println("Não foi possível acessar o ranking: "+e.getCause().getMessage()); }
        }
    }
    private void jogar(GameEngine motor) {
        String nome=ler("Digite o nome do piloto: "); if(nome==null) return;
        if(nome.isBlank()) nome="Piloto Anônimo";
        String dif=ler("Escolha a Dificuldade (facil/medio/dificil): "); if(dif==null) return;
        Dificuldade dificuldade=switch(dif.toLowerCase(Locale.ROOT)) {
            case "facil","fácil" -> Dificuldade.FACIL; case "dificil","difícil" -> Dificuldade.DIFICIL; default -> Dificuldade.MEDIO;
        };
        int tamanho;
        while(true) {
            String texto=ler("Tamanho do mapa (ex: 5 para mapa de -5 a +5): "); if(texto==null) return;
            try { tamanho=Integer.parseInt(texto); if(tamanho<=0) tamanho=5; }
            catch(NumberFormatException e) { tamanho=5; saida.println("Entrada inválida, usando tamanho padrão (5)."); }
            try { motor.validarMapa(dificuldade,tamanho); break; }
            catch(IllegalArgumentException e) { saida.println(e.getMessage()); }
        }
        saida.println("Iniciando missão na dificuldade "+rotulo(dificuldade.name())+"...");
        if(ler("Pressione Enter para decolar!\n")==null) { saida.println("Entrada encerrada. Missão não iniciada."); return; }
        motor.iniciarPartida(nome,dificuldade,tamanho);
        while(motor.estadoAtual().estado()==EstadoPartida.EM_ANDAMENTO) {
            saida.print(renderer.desenhar(motor.estadoAtual()));
            String comando=ler("Comando (w/s/a/d/c/q): ");
            if(comando==null) { saida.println("Entrada encerrada."); motor.processarAcao(Acao.ABORTAR); return; }
            if(!comando.isEmpty()) motor.processarAcao(traduzirAcao(comando));
        }
    }
    public void apresentar(EstadoJogo s,ResultadoTurno resultado) {
        for(String mensagem:resultado.mensagens()) saida.println(mensagem);
        if(s.estado()==EstadoPartida.VITORIA) {
            saida.println("Estatísticas da Partida:");
            saida.printf(" - Pontuação Final: %d pontos%n - Movimentos Efetuados: %d%n - Tempo de Jogo: %d segundos%n - Passageiros Resgatados: %d%n",
                    s.pontuacao(),s.estatisticas().movimentos(),s.estatisticas().duracaoSegundos(),s.estatisticas().resgatados());
        }
    }
    private void mostrarRanking(List<RankingEntry> ranking) {
        saida.println("\n====== RANKING TOP 5 PILOTOS ======");
        if(ranking.isEmpty()) saida.println(" - Nenhum registro encontrado. Seja o primeiro a jogar!");
        int pos=1;
        for(RankingEntry e:ranking) saida.printf("%d. %s - %d pts | Dificuldade: %s | Coletados: %d | Tempo: %ds | %s%n",
                pos++,e.nome(),e.score(),rotulo(e.dificuldade()),e.passageirosColetados(),e.tempoJogo(),e.dataHora());
    }
    private String ler(String prompt) { saida.print(prompt); return entrada.hasNextLine()?entrada.nextLine().trim():null; }
    private static String rotulo(String d) { return switch(d) { case "FACIL" -> "Fácil"; case "DIFICIL" -> "Difícil"; default -> "Médio"; }; }
}
