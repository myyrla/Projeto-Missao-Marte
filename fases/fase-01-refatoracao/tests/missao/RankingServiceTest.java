package missao;
import missao.engine.*;
import missao.model.*;
import missao.suporte.*;
public class RankingServiceTest {
    static RankingEntry entrada(String nome,int score) { return new RankingEntry(nome,score,"FACIL",4,"2026-11-12 12:00:00",10); }
    public static void main(String[] args) {
        var repo=new RankingRepositoryEmMemoria(); var s=new RankingService(repo);
        for(int i=1;i<=5;i++) s.registrar(entrada("P"+i,i*10));
        if(s.registrar(entrada("Empate",10))) throw new AssertionError("Empate substituiu quinto");
        s.registrar(entrada("Novo",60));
        if(s.listar().size()!=5 || s.listar().get(0).score()!=60 || s.listar().get(4).score()!=20) throw new AssertionError("Top 5");
        if(s.registrar(entrada("Zero",0))) throw new AssertionError("Zero aceito");
        s.resetar(); if(!s.listar().isEmpty()) throw new AssertionError("Reset");
        System.out.println("RankingServiceTest: OK");
    }
}
