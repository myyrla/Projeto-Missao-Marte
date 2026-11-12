package missao.engine;
import missao.engine.port.*;
import missao.model.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.UncheckedIOException;
import java.util.*;
/** GRASP Controller: regras de turno e casos de uso, sem entrada ou saída concreta. */
public final class GameEngine {
    private final MissaoFactory factory;
    private final RankingService ranking;
    private final UI ui;
    private final Random random;
    private final Clock clock;
    private Missao missao;
    private String piloto;
    private Dificuldade dificuldade;
    private int tamanho, pontos, movimentos;
    private long inicio, fim;
    private EstadoPartida estado;
    public GameEngine(MissaoFactory factory, RankingRepository repository, UI ui, Random random, Clock clock) {
        this.factory=factory; this.ranking=new RankingService(repository); this.ui=ui; this.random=random; this.clock=clock;
    }
    public void validarMapa(Dificuldade dificuldade, int tamanho) { factory.validar(dificuldade,tamanho); }
    public void iniciarPartida(String piloto,Dificuldade dificuldade,int tamanhoMapa) {
        Missao nova=factory.criar(dificuldade,tamanhoMapa,random);
        this.missao=nova; this.piloto=piloto; this.dificuldade=dificuldade; this.tamanho=tamanhoMapa;
        pontos=dificuldade.getPontuacaoInicial(); movimentos=0; inicio=clock.millis(); fim=inicio;
        estado=EstadoPartida.EM_ANDAMENTO;
    }
    public ResultadoTurno processarAcao(Acao acao) {
        if(missao==null) throw new IllegalStateException("Inicie uma partida.");
        List<String> mensagens=new ArrayList<>();
        if(estado!=EstadoPartida.EM_ANDAMENTO) return new ResultadoTurno(estado,List.of());
        Nave nave=missao.getNave();
        if(acao==Acao.INVALIDA) mensagens.add("Comando inválido.");
        else if(acao==Acao.ABORTAR) { estado=EstadoPartida.ABORTADA; mensagens.add("Missão abortada pelo piloto."); }
        else {
            if(acao==Acao.EMBARCAR) {
                Passageiro p=missao.passageiroNaPosicao();
                if(p==null) mensagens.add("Nenhum passageiro nesta posição.");
                else if(missao.embarcarPassageiroNaPosicao()) {
                    pontos+=p.getPontuacao();
                    mensagens.add("Passageiro "+p.getNome()+" embarcado com sucesso! +"+p.getPontuacao()+" pontos!");
                } else mensagens.add("Nave cheia! Não há espaço para mais passageiros.");
            } else {
                int dx=acao==Acao.DIREITA?1:acao==Acao.ESQUERDA?-1:0;
                int dy=acao==Acao.BAIXO?1:acao==Acao.CIMA?-1:0;
                nave.moverComLimites(dx,dy,-tamanho,tamanho); pontos--; movimentos++;
            }
            missao.moverInimigos(random,-tamanho,tamanho);
            if(missao.verificaColisao()) {
                nave.perderVidas();
                mensagens.add(nave.getVidas()==0 ? "GAME OVER! A nave foi destruída." : "Alerta! Colisão detectada! Vidas restantes: "+nave.getVidas());
                if(nave.getVidas()==0) estado=EstadoPartida.DERROTA;
            }
            if(pontos<=0) { estado=EstadoPartida.DERROTA; mensagens.add("Combustível/Pontuação zerada! Missão perdida."); }
            if(missao.todosEmbarcados() && estado==EstadoPartida.EM_ANDAMENTO) {
                if(nave.getX()==0 && nave.getY()==0) {
                    estado=EstadoPartida.VITORIA;
                    mensagens.add("DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0). Missão cumprida!");
                } else mensagens.add("Todos os passageiros resgatados! Retorne para a Plataforma de Pouso 'L' em (0,0).");
            }
        }
        if(estado!=EstadoPartida.EM_ANDAMENTO) fim=clock.millis();
        if(estado==EstadoPartida.VITORIA) {
            try {
                List<RankingEntry> anteriores=ranking.listar();
                if(!anteriores.isEmpty() && pontos>anteriores.get(0).score()) mensagens.add("Novo recorde absoluto do sistema!");
                else if(!anteriores.isEmpty()) mensagens.add("Recorde atual a ser batido: "+anteriores.get(0).score()+" pontos (Piloto: "+anteriores.get(0).nome()+")");
                RankingEntry entrada=new RankingEntry(piloto,pontos,dificuldade.name(),nave.getPassageiros().size(),
                        LocalDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),estadoAtual().estatisticas().duracaoSegundos());
                if(ranking.registrar(entrada)) mensagens.add("Parabéns! Você entrou para o Top 5 de pilotos!");
            } catch(UncheckedIOException e) { mensagens.add("Não foi possível salvar o ranking: "+e.getCause().getMessage()); }
        }
        ResultadoTurno resultado=new ResultadoTurno(estado,mensagens);
        ui.apresentar(estadoAtual(),resultado);
        return resultado;
    }
    public EstadoJogo estadoAtual() {
        if(missao==null) throw new IllegalStateException("Inicie uma partida.");
        Nave n=missao.getNave();
        List<EstadoJogo.Objeto> objetos=missao.entidades().stream().map(e->new EstadoJogo.Objeto(e.getX(),e.getY(),e.getSimbolo())).toList();
        List<String> passageiros=missao.getPassageiros().stream().map(p->p.getNome()+" ("+p.getTipo()+") em ("+p.getX()+","+p.getY()+")").toList();
        long duracao=Math.max(0,((estado==EstadoPartida.EM_ANDAMENTO?clock.millis():fim)-inicio)/1000);
        return new EstadoJogo(piloto,dificuldade,tamanho,n.getX(),n.getY(),n.getVidas(),n.getCapacidade(),pontos,
                n.getPassageiros().size(),missao.getPassageiros().size(),estado,new EstatisticasMissao(movimentos,duracao,n.getPassageiros().size()),objetos,passageiros);
    }
    public List<RankingEntry> ranking() { return ranking.listar(); }
    public void resetarRanking() { ranking.resetar(); }
}
