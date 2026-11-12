package missao;
import missao.engine.*;
import missao.model.*;
import missao.suporte.*;
import java.time.*;
import java.util.*;
public class GameEngineTest {
    static final Clock CLOCK=Clock.fixed(Instant.parse("2026-11-12T12:00:00Z"),ZoneOffset.UTC);
    static GameEngine criar(Missao m, RankingRepositoryEmMemoria repo) {
        return new GameEngine(new MissaoFactory() {
            public Missao criar(Dificuldade d,int tamanho,Random r) { return m; }
        },repo,(s,r)->{},new Random(42),CLOCK);
    }
    public static void main(String[] args) {
        var repo=new RankingRepositoryEmMemoria();
        Missao m=new Missao(new Nave("A",5)); m.addPassageiro(new Professor("P",1,0));
        GameEngine g=criar(m,repo); g.iniciarPartida("Ana",Dificuldade.FACIL,1);
        g.processarAcao(Acao.INVALIDA); check(g.estadoAtual().pontuacao()==30,"Inválida consome turno");
        g.processarAcao(Acao.DIREITA); g.processarAcao(Acao.DIREITA);
        check(g.estadoAtual().x()==1 && g.estadoAtual().pontuacao()==28,"Borda deve custar ponto");
        g.processarAcao(Acao.EMBARCAR); check(g.estadoAtual().pontuacao()==38,"Bônus de embarque");
        check(g.estadoAtual().estado()==EstadoPartida.EM_ANDAMENTO,"Vitória fora da base");
        g.processarAcao(Acao.ESQUERDA); check(g.estadoAtual().estado()==EstadoPartida.VITORIA,"Retorno à base");
        check(repo.carregar().size()==1 && repo.carregar().get(0).score()==37,"Ranking de vitória");
        g.processarAcao(Acao.EMBARCAR); check(repo.carregar().size()==1,"Vitória duplicada");
        try { g.estadoAtual().objetos().clear(); throw new AssertionError("Estado mutável"); } catch(UnsupportedOperationException esperado) { }
        Missao perigo=new Missao(new Nave("A",5)); perigo.addPassageiro(new Professor("P",1,1)); perigo.addAsteroide(new Asteroide(0,0));
        GameEngine h=criar(perigo,new RankingRepositoryEmMemoria()); h.iniciarPartida("B",Dificuldade.FACIL,1);
        h.processarAcao(Acao.EMBARCAR); check(h.estadoAtual().vidas()==2 && h.estadoAtual().pontuacao()==30,"Colisão sem desconto extra");
        h.processarAcao(Acao.EMBARCAR); h.processarAcao(Acao.EMBARCAR);
        check(h.estadoAtual().estado()==EstadoPartida.DERROTA && perigo.getAsteroides().size()==1,"Perigo persistente e derrota");
        Missao zero=new Missao(new Nave("A",5)); zero.addPassageiro(new Professor("P",1,1));
        GameEngine z=criar(zero,new RankingRepositoryEmMemoria()); z.iniciarPartida("Z",Dificuldade.MEDIO,1);
        for(int i=0;i<20;i++) z.processarAcao(Acao.DIREITA);
        check(z.estadoAtual().estado()==EstadoPartida.DERROTA,"Zero pontos");
        GameEngine a=criar(zero,new RankingRepositoryEmMemoria()); a.iniciarPartida("Z",Dificuldade.FACIL,1); a.processarAcao(Acao.ABORTAR);
        check(a.estadoAtual().estado()==EstadoPartida.ABORTADA,"Abandono");
        // Último movimento chega à base com zero pontos: derrota prevalece.
        Missao limite=new Missao(new Nave("A",5)); limite.addPassageiro(new Professor("P",1,0));
        var repoLimite=new RankingRepositoryEmMemoria();
        GameEngine l=criar(limite,repoLimite); l.iniciarPartida("L",Dificuldade.MEDIO,1);
        l.processarAcao(Acao.DIREITA); l.processarAcao(Acao.EMBARCAR);
        for(int i=0;i<28;i++) l.processarAcao(Acao.DIREITA);
        l.processarAcao(Acao.ESQUERDA);
        check(l.estadoAtual().x()==0 && l.estadoAtual().pontuacao()==0 && l.estadoAtual().estado()==EstadoPartida.DERROTA,"Derrota antes da vitória");
        check(repoLimite.carregar().isEmpty(),"Derrota entrou no ranking");
        Missao fatal=new Missao(new Nave("A",5)); fatal.getNave().perderVidas(); fatal.getNave().perderVidas();
        fatal.addPassageiro(new Professor("P",1,0)); fatal.addAsteroide(new Asteroide(0,0));
        GameEngine f=criar(fatal,new RankingRepositoryEmMemoria()); f.iniciarPartida("F",Dificuldade.FACIL,1);
        f.processarAcao(Acao.DIREITA); f.processarAcao(Acao.EMBARCAR); f.processarAcao(Acao.ESQUERDA);
        check(f.estadoAtual().estado()==EstadoPartida.DERROTA,"Colisão fatal na base não pode vencer");
        Missao inimigo=new Missao(new Nave("A",5)); inimigo.addPassageiro(new Professor("P",1,1)); inimigo.addInimigo(new Inimigo(1,0));
        GameEngine e=new GameEngine(new MissaoFactory() { public Missao criar(Dificuldade d,int t,Random r) { return inimigo; } },
                new RankingRepositoryEmMemoria(),(st,res)->{},new Random() { public int nextInt(int bound) { return 1; } },CLOCK);
        e.iniciarPartida("E",Dificuldade.FACIL,1); e.processarAcao(Acao.INVALIDA);
        check(inimigo.getInimigos().get(0).getX()==1,"Inválida moveu inimigo");
        e.processarAcao(Acao.EMBARCAR);
        check(e.estadoAtual().vidas()==2 && e.estadoAtual().pontuacao()==30,"Embarque vazio deve mover inimigos antes da colisão");
        e.processarAcao(Acao.ABORTAR);
        check(inimigo.getInimigos().get(0).getX()==0 && e.estadoAtual().vidas()==2,"Abandono avançou turno");
        var millis=new java.util.concurrent.atomic.AtomicLong(1000);
        Clock controlado=new Clock() {
            public ZoneId getZone() { return ZoneOffset.UTC; }
            public Clock withZone(ZoneId z) { return this; }
            public Instant instant() { return Instant.ofEpochMilli(millis.get()); }
        };
        Missao cronometrada=new Missao(new Nave("A",5)); cronometrada.addPassageiro(new Professor("P",1,0));
        GameEngine tempo=new GameEngine(new MissaoFactory() { public Missao criar(Dificuldade d,int t,Random r) { return cronometrada; } },
                new RankingRepositoryEmMemoria(),(st,res)->{},new Random(1),controlado);
        tempo.iniciarPartida("Tempo",Dificuldade.FACIL,1); millis.set(6000);
        tempo.processarAcao(Acao.ABORTAR); millis.set(16000);
        check(tempo.estadoAtual().estatisticas().duracaoSegundos()==5,"Duração deve parar ao finalizar");
        System.out.println("GameEngineTest: OK");
    }
    static void check(boolean b,String m) { if(!b)throw new AssertionError(m); }
}
