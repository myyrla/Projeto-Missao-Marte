package missao;
import missao.ui.*;
import missao.engine.*;
import missao.suporte.*;
import java.io.*;
import java.util.*;
import java.time.*;
public class ConsoleUITest {
    public static void main(String[] args) throws Exception {
        if(ConsoleUI.traduzirAcao("C")!=Acao.EMBARCAR || ConsoleUI.traduzirAcao("e")!=Acao.INVALIDA || ConsoleUI.traduzirAcao("wxyz")!=Acao.CIMA)
            throw new AssertionError("Tradução de controles da Fase 0");
        var repo=new RankingRepositoryEmMemoria(); repo.salvar(List.of(RankingServiceTest.entrada("Ana",50)));
        String entrada="9\n2\n3\nn\n2\n1\nAna\nmedio\n1\n5\n\nq\n4\n";
        var bytes=new ByteArrayOutputStream();
        var ui=new ConsoleUI(new Scanner(entrada),new PrintStream(bytes,true,"UTF-8"),new MapaRenderer());
        var g=new GameEngine(new MissaoFactory(),repo,ui,new Random(42),Clock.systemUTC());
        ui.executar(g);
        String saida=bytes.toString("UTF-8");
        for(String esperado:List.of("Opção inválida","TOP 5","Operação cancelada","Mapa pequeno","Missão abortada","Obrigado"))
            if(!saida.contains(esperado)) throw new AssertionError("Saída ausente: "+esperado);
        if(repo.carregar().size()!=1) throw new AssertionError("Reset cancelado/abandono alterou ranking");
        var eof=new ConsoleUI(new Scanner("1\nAna\nfacil\n5\n\n"),new PrintStream(bytes),new MapaRenderer());
        var g2=new GameEngine(new MissaoFactory(),repo,eof,new Random(42),Clock.systemUTC()); eof.executar(g2);
        if(g2.estadoAtual().estado()!=EstadoPartida.ABORTADA) throw new AssertionError("EOF não abortou");
        System.out.println("ConsoleUITest: OK");
    }
}
